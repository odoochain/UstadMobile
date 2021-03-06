package com.ustadmobile.port.android.view

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.agoda.kakao.common.views.KView
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.toughra.ustadmobile.R
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecord
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecordRule
import com.ustadmobile.core.container.ContainerManager
import com.ustadmobile.core.container.addEntriesFromZipToContainer
import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.view.ContentEntry2DetailView
import com.ustadmobile.core.view.UstadView
import com.ustadmobile.lib.db.entities.Container
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.lib.db.entities.DownloadJobItem
import com.ustadmobile.lib.db.entities.Person
import com.ustadmobile.port.android.screen.*
import com.ustadmobile.port.sharedse.util.UmFileUtilSe
import com.ustadmobile.test.port.android.util.waitUntilWithActivityScenario
import com.ustadmobile.test.rules.UmAppDatabaseAndroidClientRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File


@AdbScreenRecord("Learner Group End To End")
class LearnerGroupEndToEndTest : TestCase() {


    @JvmField
    @Rule
    var dbRule = UmAppDatabaseAndroidClientRule()

    @JvmField
    @Rule
    val screenRecordRule = AdbScreenRecordRule()

    @Before
    fun setup() {
        dbRule.insertPersonForActiveUser(Person().apply {
            admin = true
            firstNames = "Test"
            lastName = "Teacher"
            username = "Ms Teach"
        })

        Person().apply {
            firstNames = "New"
            lastName = "Student"
            username = "Star"
            personUid = dbRule.repo.personDao.insert(this)
        }

        ContentEntry().apply {
            title = "Hello World Example"
            contentEntryUid = 1
            dbRule.repo.contentEntryDao.insert(this)
        }

        val container = Container().apply {
            containerContentEntryUid = 1
            mimeType = "application/tincan+zip"
            containerUid = dbRule.repo.containerDao.insert(this)
        }
        val containerTmpDir = UmFileUtilSe.makeTempDir("xapicontent", "${System.currentTimeMillis()}")
        val testFile = File.createTempFile("xapicontent", "xapifile", containerTmpDir)
        val input = javaClass.getResourceAsStream("/com/ustadmobile/port/android/view/tincan-group.zip")
        testFile.outputStream().use { input?.copyTo(it) }

        val containerManager = ContainerManager(container, dbRule.db, dbRule.repo, containerTmpDir.absolutePath)
        addEntriesFromZipToContainer(testFile.absolutePath, containerManager)

        DownloadJobItem().apply {
            djiContentEntryUid = 1
            djiContainerUid = container.containerUid
            djiStatus = JobStatus.COMPLETE
            djiUid = dbRule.db.downloadJobItemDao.insert(this).toInt()
        }

    }

    @AdbScreenRecord("given Downloaded Entry when GroupActivity Clicked then Learner Group Created And Content Viewed As Group")
    @Test
    fun givenDownloadedEntry_whenGroupActivityClicked_thenLearnerGroupCreatedAndContentViewedAsGroup() {
        var activityScenario: ActivityScenario<MainActivity>? = null
        init {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val launchIntent = Intent(context, MainActivity::class.java).also {
                it.putExtra(UstadView.ARG_NEXT,
                        "${ContentEntry2DetailView.VIEW_NAME}?${UstadView.ARG_ENTITY_UID}=1")
            }
            activityScenario = launchActivity(intent = launchIntent)
        }.run {

            ContentEntryDetailScreen {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                KView {
                    withText(R.string.group_activity)
                } perform {
                    click()
                }
            }

            LearnerGroupMemberListScreen {
                recycler {
                    hasSize(1)
                    childAt<LearnerGroupMemberListScreen.LearnerGroupMember>(0) {
                        memberName {
                            isDisplayed()
                            hasText("Test Teacher")
                        }
                    }
                }
            }

            MainScreen {
                fab {
                    click()
                }
            }

            PersonListScreen {
                recycler {
                    childWith<PersonListScreen.Person> {
                        withDescendant { withId(R.id.item_person_text) }
                    } perform {
                        personName{
                            hasText("New Student")
                            click()
                        }
                    }
                }
            }
            LearnerGroupMemberListScreen {
                recycler {
                    hasSize(2)
                    childAt<LearnerGroupMemberListScreen.LearnerGroupMember>(0) {
                        memberName {
                            isDisplayed()
                            hasText("Test Teacher")
                        }
                        memberRole {
                            hasText("Primary user")
                        }
                    }
                    childAt<LearnerGroupMemberListScreen.LearnerGroupMember>(1) {
                        memberName {
                            isDisplayed()
                            hasText("New Student")
                        }
                        memberRole {
                            hasText("Participant")
                        }
                    }
                    KView{
                        withId(R.id.action_selection_done)
                    } perform {
                        click()
                    }
                }
            }
            MainScreen{
                toolBarTitle{
                    hasDescendant { withText("Hello World Example") }
                    isDisplayed()
                }
            }
            XapiContentScreen{
                webView{
                    withElement(Locator.CSS_SELECTOR, "h2"){
                        hasText("Hello World")
                    }
                }
                val statement = dbRule.repo.statementDao.getOneStatement()
                        .waitUntilWithActivityScenario(activityScenario!!){
                            it?.fullStatement?.contains("Group") == true
                        }
                val groupLearnerUid = statement!!.statementLearnerGroupUid
                val groupActor = dbRule.repo.agentDao.getAgentByAnyId(account = "group:$groupLearnerUid", homepage = "http://localhost/")
                Assert.assertEquals("Agent id matches on statement", groupActor!!.agentUid, statement!!.agentUid)
                Assert.assertEquals("group id is same account name", "group:$groupLearnerUid", groupActor!!.agentAccountName)
            }
            pressBack()
            ContentEntryDetailScreen {
                entryTitleTextView {
                    isVisible()
                    isDisplayed()
                }
            }

        }

    }


}
