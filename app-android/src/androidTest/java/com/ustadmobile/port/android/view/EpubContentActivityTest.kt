package com.ustadmobile.port.android.view

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.toughra.ustadmobile.R
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecord
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecordRule
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.view.UstadView.Companion.ARG_CONTAINER_UID
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.test.core.impl.DataBindingIdlingResource
import com.ustadmobile.test.port.android.util.clickOptionMenu
import com.ustadmobile.test.rules.ScenarioIdlingResourceRule
import com.ustadmobile.test.rules.UmAppDatabaseAndroidClientRule
import com.ustadmobile.test.rules.withScenarioIdlingResourceRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@AdbScreenRecord("Epub content screen test")
@RunWith(AndroidJUnit4::class)
class EpubContentActivityTest {

    @JvmField
    @Rule
    var dbRule = UmAppDatabaseAndroidClientRule(useDbAsRepo = true)

    @JvmField
    @Rule
    val dataBindingIdlingResourceRule = ScenarioIdlingResourceRule(DataBindingIdlingResource())

    @JvmField
    @Rule
    val adbScreenRecordRule = AdbScreenRecordRule()

    private lateinit var container: Container

    private val context: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp(){
        val contentEntry = ContentEntry().apply {
            leaf = true
            contentEntryUid = dbRule.db.contentEntryDao.insert(this)
        }

        container = Container().apply {
            containerContentEntryUid = contentEntry.contentEntryUid
            containerUid  = 1000
            dbRule.db.containerDao.insert(this)
        }
        val containerTmpDir = UmFileUtilSe.makeTempDir("containerTmpDir",
                "${System.currentTimeMillis()}")
        val testFile = File.createTempFile("xapicontent", "xapifile", containerTmpDir)

        val input = javaClass.getResourceAsStream("/com/ustadmobile/app/android/test.epub")
        testFile.outputStream().use { input?.copyTo(it) }

        val containerManager = ContainerManager(container, dbRule.db, dbRule.repo,containerTmpDir.absolutePath)
        addEntriesFromZipToContainer(testFile.absolutePath, containerManager)
        containerTmpDir.deleteRecursively()
    }

    @AdbScreenRecord("Given valid epub content when created should be loaded to the view")
    @Test
    fun givenValidEpubContent_whenCreated_shouldBeLoadedToTheView(){
        val intent = Intent(context, EpubContentActivity::class.java)
        intent.putExtra(ARG_CONTAINER_UID , container.containerUid.toString())
        val activityScenario = launch<EpubContentActivity>(intent)
                .withScenarioIdlingResourceRule(dataBindingIdlingResourceRule)

        activityScenario.clickOptionMenu(R.id.menu_epub_content_showtoc)

        onView(allOf(withId(R.id.item_basepoint_cover_title),withText("ರುಮ್ನಿಯಾ"))).check(matches(isDisplayed()))

        onWebView(allOf(isDisplayed(), isJavascriptEnabled()))
                .withElement(findElement(Locator.CLASS_NAME, "authors"))
                .check(webMatches(getText(), containsString("Rukmini Banerji")));
    }
}