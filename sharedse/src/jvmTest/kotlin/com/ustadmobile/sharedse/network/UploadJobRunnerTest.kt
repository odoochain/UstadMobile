package com.ustadmobile.sharedse.network

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.ustadmobile.core.account.Endpoint
import com.ustadmobile.core.account.EndpointScope
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.door.DatabaseBuilder
import com.ustadmobile.door.DoorMutableLiveData
import com.ustadmobile.lib.db.entities.ConnectivityStatus
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContainerUploadJob
import com.ustadmobile.lib.rest.ContainerUpload
import com.ustadmobile.lib.rest.ResumableUploadRoute
import com.ustadmobile.lib.util.sanitizeDbNameFromUrl
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.sharedse.ext.TestContainer.assertContainersHaveSameContent
import com.ustadmobile.sharedse.network.containeruploader.ContainerUploader.Companion.DEFAULT_CHUNK_SIZE
import com.ustadmobile.sharedse.network.containeruploader.ContainerUploaderCommon
import com.ustadmobile.sharedse.network.containeruploader.ContainerUploaderCommonJvm
import com.ustadmobile.sharedse.network.containeruploader.UploadJobRunner
import com.ustadmobile.util.test.ext.bindNewSqliteDataSourceIfNotExisting
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kodein.di.*
import java.io.File
import java.util.*
import javax.naming.InitialContext

class UploadJobRunnerTest {

    private lateinit var endpoint: String
    private lateinit var serverFolder: File
    private lateinit var repo: UmAppDatabase
    lateinit var server: ApplicationEngine

    private val defaultPort = 8098

    private lateinit var epubContainer: Container
    private lateinit var containerManager: ContainerManager
    private lateinit var appDb: UmAppDatabase
    private lateinit var clientFolder: File
    private lateinit var fileToUpload: File

    private lateinit var connectivityStatusLiveData: DoorMutableLiveData<ConnectivityStatus>

    private lateinit var di: DI

    lateinit var mockNetworkManager: NetworkManagerBle

    private lateinit var networkManager: NetworkManagerBle

    private val context = Any()

    private lateinit var containerUploadJob: ContainerUploadJob

    @Before
    fun setup() {
        repo = DatabaseBuilder.databaseBuilder(Any(), UmAppDatabase::class, "UmAppDatabase").build()
        repo.clearAllTables()

        val endpointScope = EndpointScope()

        connectivityStatusLiveData = DoorMutableLiveData(ConnectivityStatus().apply {
            connectedOrConnecting = true
            connectivityState = ConnectivityStatus.STATE_UNMETERED
            wifiSsid = "wifi-mock"
        })

        mockNetworkManager = mock {
            on { connectivityStatus }.thenReturn(connectivityStatusLiveData)
        }

        di = DI {
            bind<UmAppDatabase>(tag = UmAppDatabase.TAG_DB) with scoped(endpointScope).singleton {
                val dbName = sanitizeDbNameFromUrl(context.url)
                InitialContext().bindNewSqliteDataSourceIfNotExisting(dbName)
                spy(UmAppDatabase.getInstance(Any(), dbName).also {
                    it.clearAllTables()
                })
            }
            bind<ContainerUploaderCommon>() with singleton { ContainerUploaderCommonJvm(di) }
            bind<NetworkManagerBle>() with singleton { mockNetworkManager }
        }

        connectivityStatusLiveData.sendValue(ConnectivityStatus(ConnectivityStatus.STATE_METERED, true, null))

        clientFolder = UmFileUtilSe.makeTempDir("upload", "")
        serverFolder = UmFileUtilSe.makeTempDir("server", "")

        server = embeddedServer(Netty, port = defaultPort) {
            install(ContentNegotiation) {
                gson {
                    register(ContentType.Application.Json, GsonConverter())
                    register(ContentType.Any, GsonConverter())
                }
            }
            install(Routing) {
                ContainerUpload(repo, serverFolder)
                ResumableUploadRoute(serverFolder)
            }
        }.start(wait = false)

        fileToUpload = File(clientFolder, "tincan.zip")
        UmFileUtilSe.extractResourceToFile(
                "/com/ustadmobile/port/sharedse/contentformats/ustad-tincan.zip",
                fileToUpload)
    }

    private fun createContainer(appDb: UmAppDatabase) {
        epubContainer = Container()
        epubContainer.containerUid = appDb.containerDao.insert(epubContainer)
        containerManager = ContainerManager(epubContainer, appDb, appDb, clientFolder.absolutePath)
        runBlocking {
            addEntriesFromZipToContainer(fileToUpload.absolutePath, containerManager)
        }
        containerUploadJob = ContainerUploadJob().apply {
            this.cujContainerUid = epubContainer.containerUid
            this.cujUid = appDb.containerUploadJobDao.insert(this)
        }
    }

    @After
    fun tearDown() {
        server.stop(0, 7000)
    }

    @Test
    fun givenAnUploadJob_whenRunnerUploads_thenContentFromServerIsSameAsDb() {

        endpoint = "http://localhost:$defaultPort/"
        appDb = di.on(Endpoint(endpoint)).direct.instance(tag = UmAppDatabase.TAG_DB)
        createContainer(appDb)

        val runner = UploadJobRunner(containerUploadJob, retryDelay, endpoint, di)
        runBlocking {
            runner.startUpload()
        }

        assertContainersHaveSameContent(epubContainer.containerUid, appDb, repo)
    }

    @Test
    fun givenRunnerStarts_whenFailExceedsMaxAttempt_thenShouldStopAndSetStatusToFail() {
        val mockWebServer = MockWebServer()
        mockWebServer.start()

        endpoint = mockWebServer.url("").toString()
        appDb = di.on(Endpoint(endpoint)).direct.instance(tag = UmAppDatabase.TAG_DB)
        createContainer(appDb)

        // existing md5Sum response
        val md5List = appDb.containerEntryDao.findByContainerWithMd5(containerUploadJob.cujContainerUid).map { it.cefMd5 }
        mockWebServer.enqueue(MockResponse().addHeader("Content-Type", "application/json")
                .setBody(Gson().toJson(md5List)))

        // create session
        val sessionId = UUID.randomUUID().toString()
        mockWebServer.enqueue(MockResponse().setBody(sessionId))

        // fail to upload - server problem
        for (i in 0..(fileToUpload.length()) step (1024 * 8)) {
            mockWebServer.enqueue(MockResponse().setResponseCode(HttpStatusCode.InternalServerError.value).setBody("Server error"))
        }

        val runner = UploadJobRunner(containerUploadJob, retryDelay, mockWebServer.url("").toString(), di)
        var status = 0
        runBlocking {
            status = runner.startUpload()
        }

        Assert.assertEquals("Runner failed", JobStatus.FAILED, status)

    }

    @Test
    fun givenRunnerStarts_whenServerHasAllMd5_thenShouldCallFinalizeWithoutSession() {
        endpoint = "http://localhost:$defaultPort/"
        appDb = di.on(Endpoint(endpoint)).direct.instance(tag = UmAppDatabase.TAG_DB)
        createContainer(repo)
        createContainer(appDb)

        val runner = UploadJobRunner(containerUploadJob, retryDelay, endpoint, di)
        var status = 0
        runBlocking {
            status = runner.startUpload()
        }

        assertContainersHaveSameContent(epubContainer.containerUid, appDb, repo)

    }

    companion object{
        const val retryDelay = 10L
    }


}