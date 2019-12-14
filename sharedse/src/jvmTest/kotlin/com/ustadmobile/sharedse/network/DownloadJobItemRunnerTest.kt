package com.ustadmobile.sharedse.network

import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier
import com.nhaarman.mockitokotlin2.*
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.waitForLiveData
import com.ustadmobile.core.impl.UMLog
import com.ustadmobile.core.networkmanager.LocalAvailabilityManager
import com.ustadmobile.core.util.UMIOUtils
import com.ustadmobile.lib.db.entities.*
import com.ustadmobile.lib.db.entities.ConnectivityStatus.Companion.STATE_METERED
import com.ustadmobile.port.sharedse.impl.http.EmbeddedHTTPD
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.sharedse.network.NetworkManagerBleCommon.Companion.WIFI_GROUP_CREATION_RESPONSE
import com.ustadmobile.sharedse.network.containerfetcher.ContainerFetcherBuilder
import com.ustadmobile.sharedse.util.ReverseProxyDispatcher
import com.ustadmobile.util.test.checkJndiSetup
import com.ustadmobile.util.test.extractTestResourceToFile
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipFile


class DownloadJobItemRunnerTest {

    private lateinit var cloudServer: EmbeddedHTTPD

    private lateinit var cloudMockWebServer: MockWebServer

    private lateinit var cloudMockDispatcher: ReverseProxyDispatcher

    private lateinit var peerServer: EmbeddedHTTPD

    private lateinit var mockedNetworkManager: NetworkManagerBle

    private val mockedDownloadJobItemManagerMap = mutableMapOf<Int, DownloadJobItemManager>()

    private var mockedNetworkManagerBleWorking = AtomicBoolean()

    private val mockedNetworkManagerWifiConnectWorking = AtomicBoolean()

    private lateinit var clientDb: UmAppDatabase

    private lateinit var clientRepo: UmAppDatabase

    private lateinit var serverDb: UmAppDatabase

    private lateinit var serverRepo: UmAppDatabase

    private lateinit var cloudEndPoint: String

    private lateinit var webServerTmpDir: File

    private lateinit var containerTmpDir: File

    private lateinit var webServerTmpContentEntryFile: File

    private lateinit var webServerTmpContentEntryWithDuplicateFile: File

    private lateinit var peerTmpContentEntryFile: File

    private lateinit var peerContainerFileTmpDir: File

    private lateinit var clientContainerDir: File

    private val TEST_CONTENT_ENTRY_FILE_UID = 1000L

    private val TEST_FILE_RESOURCE_PATH = "/com/ustadmobile/port/sharedse/networkmanager/thelittlechicks.epub"

    private val TEST_FILE_WITH_DUPLICATES = "/com/ustadmobile/port/sharedse/networkmanager/thebigchicken.epub"

    private var context = Any()

    private lateinit var entryStatusResponse: EntryStatusResponse

    private lateinit var mockedEntryStatusTask: BleEntryStatusTask

    private lateinit var networkNode: NetworkNode

    private lateinit var connectivityStatus: ConnectivityStatus

    private lateinit var wifiDirectGroupInfoMessage: BleMessage

    private lateinit var groupBle: WiFiDirectGroupBle

    private lateinit var downloadJobItem: DownloadJobItem

    private lateinit var container: Container

    private lateinit var containerWDuplicates: Container

    private lateinit var containerManager: ContainerManager

    private val MAX_LATCH_WAITING_TIME = 15000L

    private val MAX_THREAD_SLEEP_TIME = 2000L

    private lateinit var downloadJobItemManager: DownloadJobItemManager


    @Before
    @Throws(IOException::class)
    fun setup() {
        checkJndiSetup()
        clientDb = UmAppDatabase.getInstance(context, "clientdb")
        clientDb.clearAllTables()

        groupBle = WiFiDirectGroupBle("DIRECT-PeerNode", "networkPass123")


        clientContainerDir = UmFileUtilSe.makeTempDir("clientContainerDir", "" + System.currentTimeMillis())

        clientRepo = clientDb//.getRepository("http://localhost/dummy/", "")
        networkNode = NetworkNode()
        networkNode.bluetoothMacAddress = "00:3F:2F:64:C6:4F"
        networkNode.lastUpdateTimeStamp = System.currentTimeMillis()
        networkNode.nodeId = clientDb.networkNodeDao.replace(networkNode)

        serverDb = UmAppDatabase.getInstance(context)
        serverDb.clearAllTables()
        serverRepo = serverDb//.getRepository("http://localhost/dummy/", "")

        webServerTmpDir = UmFileUtilSe.makeTempDir("webServerTmpDir",
                "" + System.currentTimeMillis())
        webServerTmpContentEntryFile = File(webServerTmpDir, "" + TEST_CONTENT_ENTRY_FILE_UID)

        extractTestResourceToFile(TEST_FILE_RESOURCE_PATH, webServerTmpContentEntryFile)

        containerTmpDir = UmFileUtilSe.makeTempDir("containerTmpDir",
                "" + System.currentTimeMillis())

        //mockedNetworkManager = spy<NetworkManagerBleCommon>(NetworkManagerBleCommon::class.java!!)

        val contentEntry = ContentEntry()
        contentEntry.title = "Test entry"
        contentEntry.contentEntryUid = clientDb.contentEntryDao.insert(contentEntry)

        container = Container(contentEntry)
        container.containerUid = serverDb.containerDao.insert(container)
        containerManager = ContainerManager(container, serverDb, serverRepo,
                webServerTmpDir.absolutePath)
        addEntriesFromZipToContainer(webServerTmpContentEntryFile.absolutePath, containerManager)

        //add the container itself to the client database (would normally happen via sync/preload)
        clientRepo.containerDao.insert(container)



        val downloadJob = DownloadJob(contentEntry.contentEntryUid,
                System.currentTimeMillis())
        downloadJob.timeRequested = System.currentTimeMillis()
        downloadJob.djStatus = JobStatus.QUEUED
        downloadJob.djDestinationDir = clientContainerDir.absolutePath
        downloadJob.djUid = clientDb.downloadJobDao.insert(downloadJob).toInt()

        downloadJobItem = DownloadJobItem(downloadJob, contentEntry.contentEntryUid,
                container.containerUid, container.fileSize)
        downloadJobItem.djiStatus = JobStatus.QUEUED
        downloadJobItem.downloadedSoFar = 0
        downloadJobItem.destinationFile = File(clientContainerDir,
                TEST_CONTENT_ENTRY_FILE_UID.toString()).absolutePath

        runBlocking {
            downloadJobItemManager = DownloadJobItemManager(clientDb, downloadJob.djUid,
                    newSingleThreadContext("DownloadJobItemRunnerTestManager"))
            downloadJobItemManager.insertDownloadJobItems(listOf(downloadJobItem))
        }

        val peerDb = UmAppDatabase.getInstance(context, "peerdb")
        peerDb.clearAllTables()
        peerServer = EmbeddedHTTPD(0, context, peerDb)
        mockedEntryStatusTask = mock<BleEntryStatusTask> {}
        mockedDownloadJobItemManagerMap.clear()
        mockedDownloadJobItemManagerMap[downloadJob.djUid] = downloadJobItemManager
        mockedNetworkManager = mock {
            on { sendMessage(any(), any(), any(), any()) } doAnswer {invocation ->
                val bleResponseListener = invocation.arguments[3] as BleMessageResponseListener
                val destinationNode = invocation.arguments[2] as NetworkNode
                peerServer.start()
                groupBle.port = peerServer.listeningPort
                groupBle.ipAddress = "127.0.0.1"
                Thread.sleep(2000)


                wifiDirectGroupInfoMessage = BleMessage(WIFI_GROUP_CREATION_RESPONSE, 42.toByte(),
                    groupBle.toBytes())

                val bleWorking = mockedNetworkManagerBleWorking.get()
                val messageResponse = if (bleWorking) wifiDirectGroupInfoMessage else null
                val messageErr = if(bleWorking) null else IOException("BLE Group details request failed")
                bleResponseListener.onResponseReceived(destinationNode.bluetoothMacAddress!!,
                        messageResponse, messageErr)
                Unit
            }

            on { makeEntryStatusTask(any(), any(), any(), any()) } doReturn(mockedEntryStatusTask)

            on { connectToWiFi(groupBle.ssid, groupBle.passphrase) } doAnswer {invocation ->
                runBlocking {
                    clientDb.connectivityStatusDao
                            .updateState(ConnectivityStatus.STATE_CONNECTING_LOCAL, groupBle.ssid)
                }

                Thread.sleep(1000)
                if(mockedNetworkManagerWifiConnectWorking.get()) {
                    clientDb.connectivityStatusDao.insert(ConnectivityStatus(ConnectivityStatus.STATE_CONNECTED_LOCAL,
                            true, groupBle.ssid))
                }else {
                    clientDb.connectivityStatusDao.insert(ConnectivityStatus(ConnectivityStatus.STATE_DISCONNECTED,
                            false, null))
                }

                Unit
            }

            on { restoreWifi() } doAnswer { invocation ->
                GlobalScope.launch {
                    delay(1000)
                    clientDb.connectivityStatusDao.updateState(ConnectivityStatus.STATE_UNMETERED,
                            "normalwifi")
                }
                Unit
            }

            onBlocking { openDownloadJobItemManager(any()) }.thenAnswer {
                mockedDownloadJobItemManagerMap.get(it.arguments[0] as Int)
            }
        }
        val containerDownloaderImpl = ContainerFetcherBuilder(mockedNetworkManager).build()
        whenever(mockedNetworkManager.containerFetcher).thenReturn(containerDownloaderImpl)

        mockedNetworkManagerBleWorking.set(true)

        mockedNetworkManagerWifiConnectWorking.set(true)



        val httpd = EmbeddedHTTPD(0, context)
        httpd.start()

        connectivityStatus = ConnectivityStatus()
        connectivityStatus.connectedOrConnecting = true
        connectivityStatus.connectivityState = ConnectivityStatus.STATE_UNMETERED
        connectivityStatus.csUid = 1
        clientDb.connectivityStatusDao.insert(connectivityStatus)

        entryStatusResponse = EntryStatusResponse()
        entryStatusResponse.erContainerUid = container.containerUid
        entryStatusResponse.erNodeId = networkNode.nodeId
        entryStatusResponse.available = true
        entryStatusResponse.responseTime = System.currentTimeMillis()

        cloudServer = EmbeddedHTTPD(0, context,
                serverDb)
        cloudServer.start()

        cloudMockWebServer = MockWebServer()
        cloudMockDispatcher = ReverseProxyDispatcher(HttpUrl.parse(cloudServer.localURL)!!)
        cloudMockWebServer.setDispatcher(cloudMockDispatcher)

        cloudEndPoint = cloudMockWebServer.url("/").toString()


        val peerRepo = peerDb//.getRepository("http://localhost/dummy/", "")
        peerRepo.containerDao.insert(container)
        peerContainerFileTmpDir = UmFileUtilSe.makeTempDir("peerContainerFileTmpDir",
                "" + System.currentTimeMillis())
        val peerContainerManager = ContainerManager(container,
                peerDb, peerRepo, peerContainerFileTmpDir.absolutePath)

        peerTmpContentEntryFile = File.createTempFile("peerTmpContentEntryFile",
                "" + System.currentTimeMillis() + ".zip")
        extractTestResourceToFile(TEST_FILE_RESOURCE_PATH, peerTmpContentEntryFile)
        val peerZipFile = ZipFile(peerTmpContentEntryFile)
        addEntriesFromZipToContainer(peerTmpContentEntryFile.absolutePath, peerContainerManager)
        peerZipFile.close()
    }

    fun assertContainersHaveSameContent(containerUid1: Long, containerUid2: Long,
                                        db1: UmAppDatabase, repo1: UmAppDatabase,
                                        db2: UmAppDatabase, repo2: UmAppDatabase) {

        val container1 = repo1.containerDao.findByUid(containerUid1)!!
        val manager1 = ContainerManager(container1, db1, repo1)

        val container2 = repo2.containerDao.findByUid(containerUid2)!!
        val manager2 = ContainerManager(container2, db2, repo2)

        Assert.assertEquals("Containers have same number of entries",
                container1.cntNumEntries.toLong(),
                db2.containerEntryDao.findByContainer(containerUid2).size.toLong())

        for (entry in manager1.allEntries) {
            val entry2 = manager2.getEntry(entry.cePath!!)
            Assert.assertNotNull("Client container also contains " + entry.cePath!!,
                    entry2)


            val e1Contents: ByteArray
            try {
                e1Contents = UMIOUtils.readStreamToByteArray(manager1.getInputStream(entry))
            } catch(e1: Exception) {
                throw IOException("Exception reading entry 1 ${entry.cePath} from ${entry.containerEntryFile?.cefPath}",
                        e1)
            }


            val e2Contents: ByteArray

            try {
                e2Contents = UMIOUtils.readStreamToByteArray(manager2.getInputStream(entry2!!))
            } catch(e2: Exception) {
                throw IOException("Exception reading entry 2 ${entry.cePath} from ${entry2!!.containerEntryFile?.cefPath}",
                        e2)
            }

            Assert.assertArrayEquals("${entry.cePath} contents are the same",
                    e1Contents, e2Contents)
        }
    }

    @Test
    fun givenDownload_whenRun_shouldDownloadAndComplete() {
        runBlocking {
            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mock<LocalAvailabilityManager>())

            val startTime = System.currentTimeMillis()
            jobItemRunner.download()
            val runTime = System.currentTimeMillis() - startTime
            println("Download job completed in $runTime ms")

            item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!

            Assert.assertEquals("File download task completed successfully",
                    JobStatus.COMPLETE, item.djiStatus)

            Assert.assertEquals("Correct number of ContentEntry items available in client db",
                    container.cntNumEntries,
                    clientDb.containerEntryDao.findByContainer(item.djiContainerUid).size)

            assertContainersHaveSameContent(item.djiContainerUid, item.djiContainerUid,
                    serverDb, serverRepo, clientDb, clientRepo)
        }
    }


    @Test
    @Throws(IOException::class)
    fun givenDownloadStarted_whenFailsOnce_shouldRetryAndComplete() {
        runBlocking {
            cloudMockDispatcher.numTimesToFail.set(1)

            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mock<LocalAvailabilityManager>())

            jobItemRunner.download()

            item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!

            Assert.assertEquals("File download task retried and completed successfully",
                    JobStatus.COMPLETE.toLong(), item.djiStatus.toLong())

            //Assert.assertEquals("Number of attempts = 2", 2, item.numAttempts.toLong())
            Assert.assertTrue("Number of file get requests > 2",
                    cloudMockWebServer.requestCount > 2)

            assertContainersHaveSameContent(item.djiContainerUid, item.djiContainerUid,
                    serverDb, serverRepo, clientDb, clientRepo)
        }
    }

    @Test
    fun givenDownloadStarted_whenFailsExceedMaxAttempts_shouldStopAndSetStatusToFailed() {
        runBlocking {
            cloudMockDispatcher.numTimesToFail.set(10)

            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, retryDelay = 100L,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())

            jobItemRunner.download()

            item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!

            Assert.assertEquals("File download task retried and completed with failure status",
                    JobStatus.FAILED, item.djiStatus)
        }
    }

    @Test
    fun givenDownloadUnmeteredConnectivityOnly_whenConnectivitySwitchesToMetered_shouldStopAndSetStatusToWaiting() {
        var item = clientDb.downloadJobItemDao.findByUid(
                downloadJobItem.djiUid)!!
        runBlocking {
            //set speed to 512kbps (period unit by default is milliseconds)
            cloudMockDispatcher.throttleBytesPerPeriod = (64 * 1000)
            cloudMockDispatcher.throttlePeriod = 1000


            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())

            launch {
                jobItemRunner.download()
            }

            delay(2000) //wait for 2 seconds

            jobItemRunner.handleConnectivityStatusChanged(
                    ConnectivityStatus(STATE_METERED, true, null))

            try {
                waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                        item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                    status == JobStatus.WAITING_FOR_CONNECTION
                }
            } catch (e: Exception) {
                println("Exception with live data cancel?")
                e.printStackTrace()
            }

        }

        item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!

        Assert.assertEquals("File download task stopped after network status " + "change and set status to waiting",
                JobStatus.WAITING_FOR_CONNECTION, item.djiStatus)
        Thread.sleep(15000)
    }

    //@Test
    fun givenDownloadStarted_whenJobIsStopped_shouldStopAndSetStatus() {
        runBlocking {
            cloudMockDispatcher.throttleBytesPerPeriod = (128 * 1000)
            cloudMockDispatcher.throttlePeriod = 1000

            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())

            launch {
                jobItemRunner.download()
            }

            delay(1000)

            clientDb.downloadJobItemDao.updateStatus(item.djiUid, JobStatus.STOPPING)

            waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                    item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                status == JobStatus.STOPPED
            }

            item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!

            Assert.assertEquals("File download job was stopped and status was updated",
                    JobStatus.STOPPED, item.djiStatus)
        }
    }

    /**
     * DownloadJobItemRunner should stop when connectivity is lost, and then a new
     * DownloadJobItemRunner will be created when connectivity is restored. A DownloadJobItemRunner
     * must be capable of picking up from where the last one left off.
     */
    //TODO: IMPORTANT: this case is broken. Corrupt files are being delivered in this case.
    //@Test
    fun givenDownloadJobItemRunnerStartedAndStopped_whenNextJobItemRunnerRuns_shouldFinishAndContentShouldMatch() {
        runBlocking {
            cloudMockDispatcher.throttleBytesPerPeriod = (128 * 1000)
            cloudMockDispatcher.throttlePeriod = 1000

            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner1 = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())

            launch {
                jobItemRunner1.download()
            }

            delay(2000)

            clientDb.connectivityStatusDao.updateStateAsync(ConnectivityStatus.STATE_DISCONNECTED)

            waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                    item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                status == JobStatus.WAITING_FOR_CONNECTION
            }

            val statusAfterDisconnect = clientDb.downloadJobItemDao.findByUid(item.djiUid)!!.djiStatus

            clientDb.connectivityStatusDao.updateState(ConnectivityStatus.STATE_UNMETERED, "Dummy-Wifi")

            val downloadJobItemRunner2 = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())
            downloadJobItemRunner2.download()

            item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!

            Assert.assertEquals("First download job item runner status was WAITING_FOR_CONNECTION after disconnect",
                    JobStatus.WAITING_FOR_CONNECTION, statusAfterDisconnect)

            Assert.assertEquals("File download task completed successfully",
                    JobStatus.COMPLETE, item.djiStatus)

            Assert.assertEquals("Correct number of ContainerEntry items available in client db",
                    container.cntNumEntries,
                    clientDb.containerEntryDao.findByContainer(item.djiContainerUid).size)

            //The assertion below was flaky if run immediately. This should NOT be the case.
            assertContainersHaveSameContent(item.djiContainerUid, item.djiContainerUid,
                    serverDb, serverRepo, clientDb, clientRepo)
        }
    }


    //@Test
    fun givenDownloadStartsOnMeteredConnection_whenJobSetChangedToDisableMeteredConnection_shouldStopAndSetStatus() {
        var item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!
        var statusAfterWaitingForDownload = -1
        runBlocking {
            cloudMockDispatcher.throttleBytesPerPeriod = (128 * 1000)
            cloudMockDispatcher.throttlePeriod = 1000


            clientDb.downloadJobDao.setMeteredConnectionAllowedByJobUidSync(
                    item.djiDjUid, true)

            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mock<LocalAvailabilityManager>())

            clientDb.connectivityStatusDao.updateState(ConnectivityStatus.STATE_METERED, "")

            UMLog.l(UMLog.DEBUG, 699,
                    " Running DownloadJobItemRunner for " + item.djiUid)

            launch { jobItemRunner.download() }

            waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                    item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                status >= JobStatus.RUNNING_MIN
            }

            delay(MAX_THREAD_SLEEP_TIME)
            statusAfterWaitingForDownload = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!.djiStatus

            clientDb.downloadJobDao.setMeteredConnectionAllowedByJobUidSync(
                    item.djiDjUid, false)

            waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                    item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                status == JobStatus.WAITING_FOR_CONNECTION
            }
        }

        item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!
        assertTrue("After starting download, download was in running state before changing metered connection option",
                statusAfterWaitingForDownload >= JobStatus.RUNNING_MIN)
        assertEquals("File download job is waiting for network after changing download" +
                " set setting to use unmetered connection only",
                JobStatus.WAITING_FOR_CONNECTION, item.djiStatus)
    }

    //@Test
    fun givenDownloadStarted_whenConnectionGoesOff_shouldStopAndSetStatusToWaiting() {
        var item = clientDb.downloadJobItemDao.findByUid(
                downloadJobItem.djiUid)!!
        var stateBeforeDisconnect = -1

        runBlocking {
            cloudMockDispatcher.throttleBytesPerPeriod = (128 * 1000)
            cloudMockDispatcher.throttlePeriod = 1000


            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mock<LocalAvailabilityManager>())

            launch { jobItemRunner.download() }

            delay(MAX_THREAD_SLEEP_TIME)
            stateBeforeDisconnect = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!.djiStatus

            clientDb.connectivityStatusDao.updateState(ConnectivityStatus.STATE_DISCONNECTED, "")

            waitForLiveData(clientDb.downloadJobItemDao.getLiveStatus(
                    item.djiUid), MAX_LATCH_WAITING_TIME) { status ->
                status == JobStatus.WAITING_FOR_CONNECTION
            }
        }

        item = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!
        assertEquals("Before disconnect, job was running", JobStatus.RUNNING,
                stateBeforeDisconnect)
        assertEquals("File download job is waiting for network after the network goes off",
                JobStatus.WAITING_FOR_CONNECTION, item.djiStatus)
    }



    //@Test
    fun givenDownloadLocallyAvailable_whenRun_shouldDownloadFromLocalPeer() {
        runBlocking {
            var item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val mockAvailabilityManager = mock<LocalAvailabilityManager> {
                onBlocking { findBestLocalNodeForContentEntryDownload(any()) }.thenReturn(networkNode)
            }
            val jobItemRunner = DownloadJobItemRunner(context, item, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mockAvailabilityManager)

            val startTime = System.currentTimeMillis()
            jobItemRunner.download()
            val runTime = System.currentTimeMillis() - startTime
            println("Download job completed in $runTime ms")

            item = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!

            val downloadJobItemHistory = clientDb.downloadJobItemHistoryDao
                    .findHistoryItemsByDownloadJobItem(downloadJobItem.djiUid)
            Assert.assertEquals("Download history recorded as being local download",
                    DownloadJobItemHistory.MODE_LOCAL, downloadJobItemHistory[0].mode)

            //TODO: verify the correct message was sent
            verify(mockedNetworkManager).connectToWiFi(groupBle.ssid, groupBle.passphrase)

            Assert.assertEquals("File download task completed successfully",
                    JobStatus.COMPLETE.toLong(), item.djiStatus.toLong())

            Assert.assertEquals("Correct number of ContentEntry items available in client db",
                    container.cntNumEntries.toLong(),
                    clientDb.containerEntryDao.findByContainer(item.djiContainerUid).size.toLong())

            assertContainersHaveSameContent(item.djiContainerUid, item.djiContainerUid,
                    serverDb, serverRepo, clientDb, clientRepo)
        }

    }

    /**
     * The container system avoids the need to download duplicate entries (files that are already
     * present in other containers). When a download contains entries that the device already has
     * from other containers, these items should be linked and only the new files should be downloaded.
     */
    @Test
    fun givenOneItemDownloaded_whenSecondItemWithDuplicatesIsDownloaded_thenOnlyNewEntriesAreDownloadeAndBothShouldHaveContentMatchingOriginalContainers() {
        runBlocking {
            val container2TmpFile = File.createTempFile("DownloadJobItemRunnerTest", "c2")
            val container2TmpDir = Files.createTempDirectory("DownloadJobItemRunnerTestC2Tmp").toFile()
            extractTestResourceToFile(TEST_FILE_WITH_DUPLICATES, container2TmpFile)

            val contentEntry2 = ContentEntry("The big chicken", "The big chicken", true, true)
            contentEntry2.contentEntryUid = serverDb.contentEntryDao.insert(contentEntry2)
            var container2 = Container(contentEntry2)
            container2.containerUid = serverDb.containerDao.insert(container2)
            val containerManager2 = ContainerManager(container2, serverDb, serverDb,
                    container2TmpDir.absolutePath)
            addEntriesFromZipToContainer(container2TmpFile.absolutePath, containerManager2)
            container2 = serverDb.containerDao.findByUid(container2.containerUid)!!

            clientDb.contentEntryDao.insert(contentEntry2)
            clientDb.containerDao.insert(container2)

            //Create a DownloadJob and DownloadJobItem
            val downloadJob2 = DownloadJob(contentEntry2.contentEntryUid,
                    System.currentTimeMillis()).also {
                it.timeRequested = System.currentTimeMillis()
                it.djStatus = JobStatus.QUEUED
                it.djDestinationDir = clientContainerDir.absolutePath
                it.djUid = clientDb.downloadJobDao.insert(it).toInt()
            }

            val downloadJobItem2 = DownloadJobItem(downloadJob2, contentEntry2.contentEntryUid,
                    container2.containerUid, container2.fileSize).also {
                it.djiStatus = JobStatus.QUEUED
                it.destinationFile = File(clientContainerDir,
                        contentEntry2.contentEntryUid.toString()).absolutePath
            }

            val downloadJobItemManager2 = DownloadJobItemManager(clientDb, downloadJob2.djUid,
                newSingleThreadContext("DownloadJobItemRunnerTestManager"))
            downloadJobItemManager2.insertDownloadJobItems(listOf(downloadJobItem2))
            mockedDownloadJobItemManagerMap[downloadJob2.djUid] = downloadJobItemManager2

            val item1 = clientDb.downloadJobItemDao.findByUid(
                    downloadJobItem.djiUid)!!
            val jobItemRunner1 = DownloadJobItemRunner(context, item1, mockedNetworkManager, clientDb,
                    cloudEndPoint, connectivityStatus, localAvailabilityManager = mock<LocalAvailabilityManager>())

            jobItemRunner1.download()

            val jobItem1Status = clientDb.downloadJobItemDao.findByUid(downloadJobItem.djiUid)!!.djiStatus

            val jobItemRunner2 = DownloadJobItemRunner(context, downloadJobItem2, mockedNetworkManager,
                    clientDb, cloudEndPoint, connectivityStatus,
                    localAvailabilityManager = mock<LocalAvailabilityManager>())

            jobItemRunner2.download()

            assertContainersHaveSameContent(container.containerUid, container.containerUid,
                    serverDb, serverDb, clientDb, clientDb)
            assertContainersHaveSameContent(container2.containerUid, container2.containerUid,
                    serverDb, serverDb, clientDb, clientDb)
        }
    }


    companion object {

        @BeforeClass
        @JvmStatic
        fun setupLog() {
            Napier.base(DebugAntilog())
        }
    }
}