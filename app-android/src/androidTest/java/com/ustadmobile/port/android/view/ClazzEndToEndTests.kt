package com.ustadmobile.port.android.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.agoda.kakao.common.views.KView
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.toughra.ustadmobile.R
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecord
import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecordRule
import com.ustadmobile.lib.db.entities.HolidayCalendar
import com.ustadmobile.lib.db.entities.Person
import com.ustadmobile.port.android.screen.*
import com.ustadmobile.test.port.android.util.setDateField
import com.ustadmobile.test.rules.UmAppDatabaseAndroidClientRule
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@AdbScreenRecord("Class end-to-end test")
class ClazzEndToEndTests : TestCase() {

    @JvmField
    @Rule
    var dbRule = UmAppDatabaseAndroidClientRule()

    @JvmField
    @Rule
    val screenRecordRule = AdbScreenRecordRule()

    @AdbScreenRecord("Given an empty class list, when the user clicks add class and " +
            "fills in form, then it should go to the new class")
    @Test
    fun givenEmptyClazzList_whenUserClicksAddAndFillsInForm_thenClassIsCreatedAndGoneInto() {
        var calendarUid = 0L
        init {
            calendarUid = dbRule.repo.holidayCalendarDao.insert(HolidayCalendar().apply {
                this.umCalendarName = "Test Calendar"
            })

            dbRule.insertPersonForActiveUser(Person().apply {
                firstNames = "Bob"
                lastName = "Jones"
                admin = true
            })
            launchActivity<MainActivity>()

        }.run {

            MainScreen {

                bottomNav{
                    setSelectedItem(R.id.home_clazzlist_dest)
                }

                fab.click()

                val context = ApplicationProvider.getApplicationContext<Context>()
                val newClazzText = context.getString(R.string.add_a_new_class)
                KView{
                    withText(newClazzText)
                } perform{
                    click()
                }

            }

            ClazzEditScreen{

                editNameLayout{
                    edit{
                        typeText("Test Class")
                    }
                }
                closeSoftKeyboard()


                holidayCalendarTextInput{
                    click()
                }

            }

            HolidayCalendarListScreen{

                recycler{

                    childWith<HolidayCalendarListScreen.Holiday> {
                        withTag(calendarUid)
                    }perform {
                        click()
                    }

                }

            }

            ClazzEditScreen{

                val cal = Calendar.getInstance()
                cal.set(2020,5,31)
                setDateField(R.id.start_date_text, cal.timeInMillis)

                KView{
                    withId(R.id.menu_done)
                } perform {
                    click()
                }

            }

            MainScreen{

                toolBarTitle{
                    hasDescendant { withText("Test Class") }
                    isDisplayed()
                }

            }

        }
    }

}
