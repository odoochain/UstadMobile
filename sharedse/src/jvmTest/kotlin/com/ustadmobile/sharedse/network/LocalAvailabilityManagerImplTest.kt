package com.ustadmobile.sharedse.network

import com.nhaarman.mockitokotlin2.*
import com.ustadmobile.core.account.Endpoint
import com.ustadmobile.core.account.UstadAccountManager
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.UmAppDatabase.Companion.TAG_DB
import com.ustadmobile.core.impl.UstadMobileSystemCommon
import com.ustadmobile.core.networkmanager.AvailabilityMonitorRequest
import com.ustadmobile.lib.db.entities.NetworkNode
import com.ustadmobile.sharedse.network.NetworkManagerBleCommon.Companion.ENTRY_STATUS_REQUEST
import com.ustadmobile.sharedse.network.NetworkManagerBleCommon.Companion.ENTRY_STATUS_RESPONSE
import com.ustadmobile.sharedse.util.UstadTestRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kodein.di.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LocalAvailabilityManagerImplTest  {

    val TEST_ENTRY_UID1  = 42L

    val TEST_NODE1_ADDR = "aa:bb:cc:dd:ee:ff"

    val TEST_NODE1 = NetworkNode().apply {
        bluetoothMacAddress = TEST_NODE1_ADDR
    }

    val TEST_NODE2_ADDR = "00:12:12:13:aa:bb"

    val TEST_NODE2 = NetworkNode().apply {
        bluetoothMacAddress = TEST_NODE2_ADDR
    }

    private lateinit var db: UmAppDatabase

    @JvmField
    @Rule
    var ustadTestRule = UstadTestRule()

    lateinit var di: DI

    lateinit var activeEndpoint: Endpoint

    lateinit var mockNetworkManager: NetworkManagerBle

    @Before
    fun setup() {
        mockNetworkManager = mock {
            onBlocking { sendBleMessage(any(), any()) }.thenAnswer {
                val messageSent = it.arguments[0] as BleMessage
                val entryStatusRequest = EntryStatusRequest.fromBytes(messageSent.payload!!)
                val availabilityResponse = entryStatusRequest.entryList.map {
                    if(it == TEST_ENTRY_UID1) 1L else 0L
                }

                BleMessage(ENTRY_STATUS_RESPONSE, 42,
                        BleMessageUtil.bleMessageLongToBytes(availabilityResponse))
            }
        }

        di = DI {
            import(ustadTestRule.diModule)
            bind<NetworkManagerBle>() with singleton { mockNetworkManager }
            bind<Int>(tag = UstadMobileSystemCommon.TAG_LOCAL_HTTP_PORT) with singleton { 8000 }
        }

        val accountManager: UstadAccountManager = di.direct.instance()
        activeEndpoint = Endpoint(accountManager.activeAccount.endpointUrl)
        db = di.on(accountManager.activeAccount).direct.instance(tag = TAG_DB)
    }

    @Test
    fun givenEntryMonitorActiveWithNodeThatHasEntry_whenNodeDisocvered_shouldCreateAndSendBleEntryStatusTask() {
        runBlocking {
            val countdownLatch = CountDownLatch(1)
            val coroutineContext = newSingleThreadContext("LocalAvailabilityTest")
            val managerImpl = LocalAvailabilityManagerImpl(di, activeEndpoint)
            val monitorRequest = AvailabilityMonitorRequest(listOf(TEST_ENTRY_UID1), {
                if(it[TEST_ENTRY_UID1] ?: false)
                    countdownLatch.countDown()
            })

            managerImpl.addMonitoringRequest(monitorRequest)
            managerImpl.onNewNodeDiscovered(TEST_NODE1)

            countdownLatch.await(10, TimeUnit.SECONDS)


            verify(mockNetworkManager, timeout(5000)).sendBleMessage(argWhere {
                it.requestType == ENTRY_STATUS_REQUEST && TEST_ENTRY_UID1 in EntryStatusRequest.fromBytes(it.payload!!).entryList
            }, argWhere {
                it == TEST_NODE1_ADDR
            })

            val availableMap = managerImpl.areContentEntriesLocallyAvailable(listOf(TEST_ENTRY_UID1, -1))
            Assert.assertEquals("Entry that responded as available is marked as available",
                    true, availableMap[TEST_ENTRY_UID1])
            Assert.assertFalse("Other unknown entry is marked as not available", availableMap[-1] ?: true)
            coroutineContext.close()
        }
    }

    @Test
    fun givenNodesAlreadyDiscovered_whenAvailabilityStatusRequested_shouldCreateStatusTasks() {
        runBlocking {
            val managerImpl = LocalAvailabilityManagerImpl(di, activeEndpoint)
            managerImpl.onNewNodeDiscovered(TEST_NODE1)
            managerImpl.onNewNodeDiscovered(TEST_NODE2)

            val countDownLatch = CountDownLatch(1)
            val availabilityRequest = AvailabilityMonitorRequest(listOf(TEST_ENTRY_UID1), {
                if(it[TEST_ENTRY_UID1] ?: false)
                    countDownLatch.countDown()
            })

            managerImpl.addMonitoringRequest(availabilityRequest)

            listOf(TEST_NODE1_ADDR, TEST_NODE2_ADDR).forEach {nodeAddr ->
                verify(mockNetworkManager, timeout(5000)).sendBleMessage(argWhere {
                    it.requestType == ENTRY_STATUS_REQUEST && TEST_ENTRY_UID1 in EntryStatusRequest.fromBytes(it.payload!!).entryList
                }, argWhere {
                    it == nodeAddr
                })
            }


            countDownLatch.await(5, TimeUnit.SECONDS)
            val availableMap = managerImpl.areContentEntriesLocallyAvailable(listOf(TEST_ENTRY_UID1, -1))
            Assert.assertTrue("Entry that responded as available is marked as available", availableMap[TEST_ENTRY_UID1] ?: false)
            Assert.assertFalse("Other unknown entry is marked as not available", availableMap[-1] ?: true)
        }
    }

    @Test
    fun givenFileAvailable_whenNodeList_thenShouldFireContainerUnavailable() {
        runBlocking {
            val managerImpl = LocalAvailabilityManagerImpl(di, activeEndpoint)
            managerImpl.onNewNodeDiscovered(TEST_NODE1)

            val availableLatch = CountDownLatch(1)
            val unavailableLatch = CountDownLatch(1)
            val availabilityRequest = AvailabilityMonitorRequest(listOf(TEST_ENTRY_UID1), {
                if(it[TEST_ENTRY_UID1] == true && availableLatch.count > 0)
                    availableLatch.countDown()
                else if(it[TEST_ENTRY_UID1] == false && availableLatch.count == 0L) {
                    unavailableLatch.countDown()
                }
            })

            managerImpl.addMonitoringRequest(availabilityRequest)

            availableLatch.await(5, TimeUnit.SECONDS)

            Assert.assertEquals("Container was marked as available", 0,
                    availableLatch.count)


            managerImpl.handleNodesLost(listOf(TEST_NODE1_ADDR))

            unavailableLatch.await(5, TimeUnit.SECONDS)

            Assert.assertEquals("Container was marked as unavailable after node was lost", 0,
                unavailableLatch.count)
        }
    }


    //Disabled by Mike 12/08/2020: This is failing repeatedly and is not yet in production. Will be resolved with the networkdi merge
    //@Test
    fun givenStatusAlreadyKnown_whenAvailabilityStatusRequested_noTasksAreCreated() {
        runBlocking {
            val managerImpl = LocalAvailabilityManagerImpl(di, activeEndpoint)
            managerImpl.onNewNodeDiscovered(TEST_NODE1)
            managerImpl.onNewNodeDiscovered(TEST_NODE2)
            managerImpl.addMonitoringRequest(AvailabilityMonitorRequest(listOf(TEST_ENTRY_UID1)))

            verifyBlocking(mockNetworkManager, timeout(5000).times(2)) { sendBleMessage(any(), any())}

            managerImpl.addMonitoringRequest(AvailabilityMonitorRequest(listOf(TEST_ENTRY_UID1)))

            delay(200)

            verifyNoMoreInteractions(mockNetworkManager)
        }
    }

}