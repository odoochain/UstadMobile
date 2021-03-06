package com.ustadmobile.sharedse.network.containeruploader

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.ustadmobile.core.account.Endpoint
import com.ustadmobile.core.account.EndpointScope
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.networkmanager.ContainerUploaderRequest
import com.ustadmobile.door.ext.bindNewSqliteDataSourceIfNotExisting
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContainerImportJob
import com.ustadmobile.lib.util.sanitizeDbNameFromUrl
import com.ustadmobile.port.sharedse.ext.generateConcatenatedFilesResponse
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.sharedse.network.containeruploader.ContainerUploader.Companion.DEFAULT_CHUNK_SIZE
import com.ustadmobile.sharedse.network.NetworkManagerBle
import com.ustadmobile.sharedse.util.UstadTestRule
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kodein.di.*
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.naming.InitialContext

class ContainerUploaderTest {

    private lateinit var epubContainer: Container
    private lateinit var entryListStr: String
    private lateinit var containerManager: ContainerManager

    private lateinit var appDb: UmAppDatabase

    private lateinit var appRepo: UmAppDatabase

    @JvmField
    @Rule
    var tmpFolderRule = TemporaryFolder()

    private lateinit var tmpFolder: File


    private lateinit var fileToUpload: File
    private lateinit var mockWebServer: MockWebServer

    private lateinit var di: DI

    private lateinit var networkManager: NetworkManagerBle

    private val context = Any()

    @JvmField
    @Rule
    var ustadTestRule = UstadTestRule()

    @Before
    fun setup() {
        networkManager = mock()
        val endpointScope = EndpointScope()
        di = DI {
            bind<NetworkManagerBle>() with singleton { networkManager }
            import(ustadTestRule.diModule)
        }
        appDb = di.on(Endpoint(TEST_ENDPOINT)).direct.instance(tag = UmAppDatabase.TAG_DB)
        appRepo = di.on(Endpoint(TEST_ENDPOINT)).direct.instance(tag = UmAppDatabase.TAG_REPO)

        tmpFolder = tmpFolderRule.newFolder()
        fileToUpload = File(tmpFolder, "thelittlechicks.epub")
        UmFileUtilSe.extractResourceToFile(
                "/com/ustadmobile/port/sharedse/networkmanager/thelittlechicks.epub",
                fileToUpload)


        epubContainer = Container()
        epubContainer.containerUid = appRepo.containerDao.insert(epubContainer)
        containerManager = ContainerManager(epubContainer, appDb, appRepo, tmpFolder.absolutePath)
        runBlocking {
            addEntriesFromZipToContainer(fileToUpload.absolutePath, containerManager)
            val entryList = containerManager.allEntries.distinctBy { it.containerEntryFile!!.cefMd5 }
            entryListStr = entryList.joinToString(separator = ";") { it.ceCefUid.toString() }
        }
    }

    @Test
    fun givenValidHttpUrl_whenUploadCalled_thenShouldUploadFileAndReturnSuccess() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val concatenatedResponse = appDb.containerEntryFileDao.generateConcatenatedFilesResponse(entryListStr)
        val inStream = concatenatedResponse.dataSrc

        val job = ContainerImportJob()
        job.cijContainerUid = epubContainer.containerUid
        job.cijUid = appDb.containerImportJobDao.insert(job)

        val sessionId = UUID.randomUUID().toString()
        mockWebServer.enqueue(MockResponse().setBody(sessionId))
        for (i in 0..concatenatedResponse.contentLength step DEFAULT_CHUNK_SIZE.toLong()) {
            mockWebServer.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent.value))
        }


        val request = ContainerUploaderRequest(job.cijUid,
                entryListStr, mockWebServer.url("/upload/").toString(), TEST_ENDPOINT
        )

        val uploader = ContainerUploader(request, di = di)
        val uploadResult = runBlocking { uploader.upload() }

        val uploadedFile = File(tmpFolder, "UploadedFile")
        val fileOut = FileOutputStream(uploadedFile)
        val requestCount = mockWebServer.requestCount
        repeat(requestCount) {
            val request = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
            if (request.method == "GET") {
                return@repeat
            }
            request.body.writeTo(fileOut)
        }

        Assert.assertEquals("Upload result is successful", JobStatus.COMPLETE,
                uploadResult)
        Assert.assertArrayEquals("byte array of file matches", inStream!!.readBytes(), uploadedFile.readBytes())

    }

    companion object {

        const val TEST_ENDPOINT = "http://test.localhost.com/"

    }

}