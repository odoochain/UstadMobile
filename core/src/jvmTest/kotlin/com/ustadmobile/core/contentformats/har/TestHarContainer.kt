package com.ustadmobile.core.contentformats.har

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import com.ustadmobile.core.account.UstadAccountManager
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.dao.ReportDao
import com.ustadmobile.core.util.*
import com.ustadmobile.core.view.ReportListView
import com.ustadmobile.door.DoorLifecycleObserver
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.lib.db.entities.UmAccount
import com.ustadmobile.port.sharedse.impl.http.EmbeddedHTTPD
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.util.test.ext.insertTestStatements
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.io.File

@ExperimentalStdlibApi
class TestHarContainer {

    private lateinit var container: Container
    var harContainer: HarContainer? = null

    @JvmField
    @Rule
    var ustadTestRule = UstadTestRule()

    private lateinit var mockView: ReportListView

    private lateinit var context: Any

    private lateinit var mockLifecycleOwner: DoorLifecycleOwner

    private lateinit var di: DI


    @Before
    fun setup() {

        mockView = mock { }
        mockLifecycleOwner = mock {
            on { currentState }.thenReturn(DoorLifecycleObserver.RESUMED)
        }
        context = Any()

        di = DI {
            import(ustadTestRule.diModule)
        }

        val repo: UmAppDatabase by di.activeRepoInstance()
        val db: UmAppDatabase by di.activeDbInstance()
        val accountManager: UstadAccountManager by di.instance()

        val httpd = EmbeddedHTTPD(0, di)
        httpd.start()

        val tmpDir = UmFileUtilSe.makeTempDir("testHar",
                "" + System.currentTimeMillis())

        val chunkCountingOut = File(tmpDir, "har.zip")

        FileUtils.copyInputStreamToFile(
                javaClass.getResourceAsStream("/com/ustadmobile/core/contentformats/har.zip"),
                chunkCountingOut)

        val targetEntry = ContentEntry()
        targetEntry.title = "tiempo de prueba"
        targetEntry.thumbnailUrl = "https://www.africanstorybook.org/img/asb120.png"
        targetEntry.description = "todo el contenido"
        targetEntry.publisher = "CK12"
        targetEntry.author = "borrachera"
        targetEntry.primaryLanguageUid = 53
        targetEntry.leaf = true
        targetEntry.contentEntryUid = repo.contentEntryDao.insert(targetEntry)

        container = Container()
        container?.mimeType = "application/har+zip"
        container?.containerContentEntryUid = targetEntry.contentEntryUid
        container?.containerUid = repo.containerDao.insert(container!!)

        val containerManager = ContainerManager(container!!, db, repo,
                tmpDir.absolutePath)
        addEntriesFromZipToContainer(chunkCountingOut.absolutePath, containerManager)

        harContainer = HarContainer(containerManager, targetEntry, accountManager.activeAccount, context, httpd.localHttpUrl){

        }
    }

    @Test
    fun givenRequest_whenServedByContainer_thenSameResponse() {
        val response = harContainer?.serve(HarRequest().apply {
            this.url = "http://www.ustadmobile.com/index.html"
            this.body = "index.html"
            this.method = "GET"
        })

        Assert.assertEquals("index html was found", 200, response!!.status)
    }

    @Test
    fun givenUrlLoaded_whenNotInIndex_Return404ErrorResponse() {
        val response = harContainer?.serve(HarRequest().apply {
            this.url = "http://www.ustadmobile.com/faketest.html"
            this.body = "faketest.html"
            this.method = "GET"
        })

        Assert.assertEquals("index html was found", 404, response!!.status)
    }

    @Test
    fun givenUrlLoaded_whenInIndexButContainerMissing_thenReturn404ErrorResponse() {
        val response = harContainer?.serve(HarRequest().apply {
            this.url = "http://www.ustadmobile.com/favicon.ico"
            this.body = "favicon.ico"
            this.method = "GET"
        })

        Assert.assertEquals("index html was found", 402, response!!.status)
    }

}