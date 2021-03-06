//package com.ustadmobile.port.android.view
//
//import androidx.core.os.bundleOf
//import androidx.fragment.app.testing.FragmentScenario
//import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.Espresso.closeSoftKeyboard
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
//import androidx.test.espresso.matcher.BoundedMatcher
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withText
//import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
//import com.toughra.ustadmobile.R
//import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecord
//import com.ustadmobile.adbscreenrecorder.client.AdbScreenRecordRule
//import com.ustadmobile.core.impl.UstadMobileSystemImpl
//import com.ustadmobile.core.util.UMCalendarUtil
//import com.ustadmobile.core.view.UstadView
//import com.ustadmobile.lib.db.entities.ClazzWork
//import com.ustadmobile.lib.db.entities.Comments
//import com.ustadmobile.lib.db.entities.ContentEntryProgress
//import com.ustadmobile.port.android.screen.ClazzWorkDetailOverviewScreen
//import com.ustadmobile.port.android.screen.ClazzWorkMarkingFragmentScreen
//import com.ustadmobile.test.core.impl.DataBindingIdlingResource
//import com.ustadmobile.test.port.android.util.installNavController
//import com.ustadmobile.test.rules.ScenarioIdlingResourceRule
//import com.ustadmobile.test.rules.SystemImplTestNavHostRule
//import com.ustadmobile.test.rules.UmAppDatabaseAndroidClientRule
//import com.ustadmobile.test.rules.withScenarioIdlingResourceRule
//import com.ustadmobile.util.test.ext.TestClazzWork
//import com.ustadmobile.util.test.ext.createTestContentEntriesAndJoinToClazzWork
//import com.ustadmobile.util.test.ext.insertTestClazzWorkAndQuestionsAndOptionsWithResponse
//import kotlinx.coroutines.runBlocking
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.junit.*
//
//@AdbScreenRecord("ClazzWork (Assignments) Teacher Marking tests")
//class ClazzWorkSubmissionMarkingFragmentTest : TestCase() {
//
//    @JvmField
//    @Rule
//    var dbRule = UmAppDatabaseAndroidClientRule(useDbAsRepo = true)
//
//    @JvmField
//    @Rule
//    var systemImplNavRule = SystemImplTestNavHostRule()
//
//    @JvmField
//    @Rule
//    val screenRecordRule = AdbScreenRecordRule()
//
//    @JvmField
//    @Rule
//    val dataBindingIdlingResourceRule =
//            ScenarioIdlingResourceRule(DataBindingIdlingResource())
//
//    @After
//    fun tearDown() {
//        UstadMobileSystemImpl.instance.navController = null
//    }
//
//    private fun createQuizDbScenario(): TestClazzWork {
//        val clazzWork = ClazzWork().apply {
//            clazzWorkTitle = "Test ClazzWork A"
//            clazzWorkSubmissionType = ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_NONE
//            clazzWorkInstructions = "Pass espresso test for ClazzWork"
//            clazzWorkStartDateTime = UMCalendarUtil.getDateInMilliPlusDays(0)
//            clazzWorkDueDateTime = UMCalendarUtil.getDateInMilliPlusDays(10)
//            clazzWorkCommentsEnabled = true
//            clazzWorkMaximumScore = 120
//            clazzWorkActive = true
//        }
//
//        val dateNow: Long = UMCalendarUtil.getDateInMilliPlusDays(0)
//
//        val testClazzWork = runBlocking {
//            dbRule.db.insertTestClazzWorkAndQuestionsAndOptionsWithResponse(
//                    clazzWork, true, -1,
//                    true, 0, submitted = true,
//                    isStudentToClazz = true, dateNow = dateNow, marked = false)
//        }
//
//        //Add content
//        runBlocking {
//            dbRule.db.createTestContentEntriesAndJoinToClazzWork(testClazzWork.clazzWork, 2)
//        }
//
//        val teacherMember = testClazzWork.clazzAndMembers.teacherList.get(0)
//        dbRule.account.personUid = teacherMember.clazzMemberPersonUid
//
//
//        return testClazzWork
//    }
//
//    private fun createQuizDbScenarioWith2Submissions(): TestClazzWork {
//        val clazzWork = ClazzWork().apply {
//            clazzWorkTitle = "Test ClazzWork A"
//            clazzWorkSubmissionType = ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_NONE
//            clazzWorkInstructions = "Pass espresso test for ClazzWork"
//            clazzWorkStartDateTime = UMCalendarUtil.getDateInMilliPlusDays(0)
//            clazzWorkDueDateTime = UMCalendarUtil.getDateInMilliPlusDays(10)
//            clazzWorkCommentsEnabled = true
//            clazzWorkMaximumScore = 120
//            clazzWorkActive = true
//        }
//
//        val dateNow: Long = UMCalendarUtil.getDateInMilliPlusDays(0)
//
//        val testClazzWork = runBlocking {
//            dbRule.db.insertTestClazzWorkAndQuestionsAndOptionsWithResponse(
//                    clazzWork, true, -1,
//                    true, 0, submitted = true,
//                    isStudentToClazz = true, dateNow = dateNow, marked = false,
//                    multipleSubmissions = true)
//        }
//
//        //Add content
//        val contentList = runBlocking {
//            dbRule.db.createTestContentEntriesAndJoinToClazzWork(testClazzWork.clazzWork, 2).contentList
//        }
//
//        val student1 = testClazzWork.clazzAndMembers.studentList.get(0)
//        val student3 = testClazzWork.clazzAndMembers.studentList.get(2)
//        val student4 = testClazzWork.clazzAndMembers.studentList.get(3)
//
//        contentList.forEach {
//            runBlocking {
//                ContentEntryProgress().apply {
//                    contentEntryProgressActive = true
//                    contentEntryProgressContentEntryUid = it.contentEntryUid
//                    contentEntryProgressPersonUid = student1.clazzMemberPersonUid
//                    contentEntryProgressProgress = 42
//                    contentEntryProgressStatusFlag = ContentEntryProgress.CONTENT_ENTRY_PROGRESS_FLAG_COMPLETED
//                    contentEntryProgressUid = dbRule.db.contentEntryProgressDao.insertAsync(this)
//                }
//
//                ContentEntryProgress().apply {
//                    contentEntryProgressActive = true
//                    contentEntryProgressContentEntryUid = it.contentEntryUid
//                    contentEntryProgressPersonUid = student3.clazzMemberPersonUid
//                    contentEntryProgressProgress = 24
//                    contentEntryProgressStatusFlag = ContentEntryProgress.CONTENT_ENTRY_PROGRESS_FLAG_COMPLETED
//                    contentEntryProgressUid = dbRule.db.contentEntryProgressDao.insertAsync(this)
//                }
//
//                ContentEntryProgress().apply {
//                    contentEntryProgressActive = true
//                    contentEntryProgressContentEntryUid = it.contentEntryUid
//                    contentEntryProgressPersonUid = student4.clazzMemberPersonUid
//                    contentEntryProgressProgress = 100
//                    contentEntryProgressStatusFlag = ContentEntryProgress.CONTENT_ENTRY_PROGRESS_FLAG_COMPLETED
//                    contentEntryProgressUid = dbRule.db.contentEntryProgressDao.insertAsync(this)
//                }
//            }
//        }
//
//
//        val teacherMember = testClazzWork.clazzAndMembers.teacherList.get(0)
//        dbRule.account.personUid = teacherMember.clazzMemberPersonUid
//
//
//        return testClazzWork
//    }
//
//    private fun createQuizDbPartialScenario(): TestClazzWork {
//        val clazzWork = ClazzWork().apply {
//            clazzWorkTitle = "Test ClazzWork A"
//            clazzWorkSubmissionType = ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_NONE
//            clazzWorkInstructions = "Pass espresso test for ClazzWork"
//            clazzWorkStartDateTime = UMCalendarUtil.getDateInMilliPlusDays(0)
//            clazzWorkDueDateTime = UMCalendarUtil.getDateInMilliPlusDays(10)
//            clazzWorkCommentsEnabled = true
//            clazzWorkMaximumScore = 120
//            clazzWorkActive = true
//        }
//
//        val dateNow: Long = UMCalendarUtil.getDateInMilliPlusDays(0)
//
//        val testClazzWork = runBlocking {
//            dbRule.db.insertTestClazzWorkAndQuestionsAndOptionsWithResponse(
//                    clazzWork, true, -1,
//                    true, 0, submitted = true,
//                    isStudentToClazz = true, dateNow = dateNow, marked = false, partialFilled = true)
//        }
//
//        //Add content
//        runBlocking {
//            dbRule.db.createTestContentEntriesAndJoinToClazzWork(testClazzWork.clazzWork, 2)
//        }
//
//        val teacherMember = testClazzWork.clazzAndMembers.teacherList.get(0)
//        dbRule.account.personUid = teacherMember.clazzMemberPersonUid
//
//
//        return testClazzWork
//    }
//
//    private fun createFreeTextDbScenario(): TestClazzWork {
//        val clazzWork = ClazzWork().apply {
//            clazzWorkTitle = "Test ClazzWork A"
//            clazzWorkSubmissionType = ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_NONE
//            clazzWorkInstructions = "Pass espresso test for ClazzWork"
//            clazzWorkStartDateTime = UMCalendarUtil.getDateInMilliPlusDays(0)
//            clazzWorkDueDateTime = UMCalendarUtil.getDateInMilliPlusDays(10)
//            clazzWorkCommentsEnabled = true
//            clazzWorkMaximumScore = 120
//            clazzWorkActive = true
//        }
//
//        val dateNow: Long = UMCalendarUtil.getDateInMilliPlusDays(0)
//
//        val testClazzWork = runBlocking {
//            dbRule.db.insertTestClazzWorkAndQuestionsAndOptionsWithResponse(
//                    clazzWork, true, ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_SHORT_TEXT,
//                    true, 0, submitted = true,
//                    isStudentToClazz = true, dateNow = dateNow, marked = false)
//        }
//
//        //Add content
//        runBlocking {
//            dbRule.db.createTestContentEntriesAndJoinToClazzWork(testClazzWork.clazzWork, 2)
//        }
//
//        val teacherMember = testClazzWork.clazzAndMembers.teacherList.get(0)
//        dbRule.account.personUid = teacherMember.clazzMemberPersonUid
//
//
//        return testClazzWork
//    }
//
//    private fun reloadFragment(clazzWorkUid: Long, clazzMemberUid: Long)
//            : FragmentScenario<ClazzWorkSubmissionMarkingFragment> {
//
//        return launchFragmentInContainer(
//                fragmentArgs = bundleOf(UstadView.ARG_CLAZZWORK_UID to clazzWorkUid.toString(),
//                        UstadView.ARG_CLAZZMEMBER_UID to clazzMemberUid.toString()),
//                themeResId = R.style.UmTheme_App) {
//            ClazzWorkSubmissionMarkingFragment().also {
//                it.installNavController(systemImplNavRule.navController)
//                it.arguments = bundleOf(UstadView.ARG_CLAZZWORK_UID to clazzWorkUid.toString(),
//                        UstadView.ARG_CLAZZMEMBER_UID to clazzMemberUid.toString())
//            }
//        }.withScenarioIdlingResourceRule(dataBindingIdlingResourceRule)
//    }
//
//    @AdbScreenRecord("ClazzWorkSubmissionMarking: Should show marking (Quiz scenario) ")
//    @Test
//    fun givenNoClazzWorkSubmissionMarkingPresentYetForQuiz_whenFilledInAndSaveClicked_thenShouldSaveToDatabase() {
//
//        var testClazzWork: TestClazzWork? = null
//        before{
//            testClazzWork = createQuizDbScenario()
//            val clazzWorkUid: Long = testClazzWork?.clazzWork?.clazzWorkUid?:0L
//            val clazzMemberUid: Long = testClazzWork?.submissions!!.get(0).clazzWorkSubmissionClazzMemberUid
//
//            reloadFragment(clazzWorkUid, clazzMemberUid)
//        }.after {
//
//        }.run {
//            fillMarkingAndReturn(testClazzWork!!)
//            //Check database
//            val submissionPostSubmit = runBlocking {
//                dbRule.db.clazzWorkSubmissionDao.findByUidAsync(
//                        testClazzWork?.submissions!!.get(0).clazzWorkSubmissionUid)
//            }
//            Assert.assertEquals("Marked OK", 42,
//                    submissionPostSubmit?.clazzWorkSubmissionScore)
//        }
//
//    }
//
//    @AdbScreenRecord("ClazzWorkSubmissionMarking: Should show marking (partially filled Quiz) ")
//    @Test
//    fun givenNoClazzWorkSubmissionMarkingPresentYetForPartiallyFilledQuiz_whenFilledInAndSaveClicked_thenShouldSaveToDatabase() {
//
//        val testClazzWork = createQuizDbPartialScenario()
//        val clazzWorkUid: Long = testClazzWork.clazzWork.clazzWorkUid
//        val clazzMemberUid: Long = testClazzWork.submissions!!.get(0).clazzWorkSubmissionClazzMemberUid
//
//        reloadFragment(clazzWorkUid, clazzMemberUid)
//
//        fillMarkingAndReturn(testClazzWork)
//
//        //Check database
//        val submissionPostSubmit = runBlocking {
//            dbRule.db.clazzWorkSubmissionDao.findByUidAsync(
//                    testClazzWork.submissions!!.get(0).clazzWorkSubmissionUid)
//        }
//        Assert.assertEquals("Marked OK", 42,
//                submissionPostSubmit?.clazzWorkSubmissionScore)
//    }
//
//    @AdbScreenRecord("ClazzWorkSubmissionMarking: Should show marking (for Free Text Submission) ")
//    @Test
//    fun givenNoClazzWorkSubmissionMarkingPresentYetForFreeText_whenFilledInAndSaveClicked_thenShouldSaveToDatabase() {
//
//        var testClazzWork: TestClazzWork? = null
//        before{
//            testClazzWork = createFreeTextDbScenario()
//            val clazzWorkUid: Long = testClazzWork?.clazzWork?.clazzWorkUid?:0L
//            val clazzMemberUid: Long = testClazzWork?.submissions!!.get(0).clazzWorkSubmissionClazzMemberUid
//
//            reloadFragment(clazzWorkUid, clazzMemberUid)
//        }.after {
//
//        }.run {
//            fillMarkingAndReturn(testClazzWork!!)
//
//            //Check database
//            val submissionPostSubmit = runBlocking {
//                dbRule.db.clazzWorkSubmissionDao.findByUidAsync(
//                        testClazzWork?.submissions!!.get(0).clazzWorkSubmissionUid)
//            }
//            Assert.assertEquals("Marked OK", 42,
//                    submissionPostSubmit?.clazzWorkSubmissionScore)
//        }
//
//    }
//
//
//    private fun fillMarkingAndReturn(testClazzWork: TestClazzWork, hitReturn: Boolean = true) {
//
//
//        ClazzWorkMarkingFragmentScreen {
//
//            recycler {
//
//                scrollToHolder(withTagInMarking(testClazzWork.submissions!![0].clazzWorkSubmissionUid))
//
//                childWith<ClazzWorkMarkingFragmentScreen.Submission> {
//                    withDescendant { withId(R.id.item_clazzwork_submission_score_edit_et) }
//                } perform {
//                    submissionEditText {
//                        clearText()
//                        typeText("42")
//                    }
//                }
//
//                ViewActions.closeSoftKeyboard()
//                scrollTo {
//                    withTag(testClazzWork.clazzWork.clazzWorkUid)
//                }
//                if (hitReturn) {
//                    scrollToHolder(withTagInMarkingSubmit(testClazzWork.clazzWork.clazzWorkUid))
//                    childWith<ClazzWorkMarkingFragmentScreen.SubmitWithMetrics> {
//                        withDescendant { withId(R.id.item_clazzworksubmission_marking_button_with_extra_button) }
//                    } perform {
//                        scrollToHolder(withTagInMarkingSubmit(testClazzWork.clazzWork.clazzWorkUid))
//                        click()
//                    }
//
//                }
//
//
//            }
//
//        }
//    }
//
//    private fun addPrivateComment(comment: String, hitReturn: Boolean = true) {
//
//        ClazzWorkDetailOverviewScreen {
//
//
//            recycler {
//                scrollTo {
//                    withTag("Private comments")
//                }
//                childWith<ClazzWorkDetailOverviewScreen.SimpleHeading> {
//                    withDescendant { withText("Private comments") }
//                } perform {
//                    isEnabled()
//                }
//                childWith<ClazzWorkDetailOverviewScreen.SubmitComment> {
//                    withDescendant { withId(R.id.item_comment_new_comment_et) }
//                } perform {
//                    newCommentEditText {
//                        clearText()
//                        typeText(comment)
//                        ViewActions.closeSoftKeyboard()
//                    }
//                    if (hitReturn) {
//                        submitCommentButton {
//                            click()
//                        }
//                    }
//
//                }
//
//
//            }
//
//        }
//    }
//
//    private fun withTagInMarking(quid: Long): Matcher<RecyclerView.ViewHolder?>? {
//        return object : BoundedMatcher<RecyclerView.ViewHolder?,
//                ClazzWorkSubmissionScoreEditRecyclerAdapter.ScoreEditViewHolder>(
//                ClazzWorkSubmissionScoreEditRecyclerAdapter.ScoreEditViewHolder::class.java) {
//            override fun matchesSafely(
//                    item: ClazzWorkSubmissionScoreEditRecyclerAdapter.ScoreEditViewHolder): Boolean {
//                return item.itemView.tag.equals(quid)
//            }
//
//            override fun describeTo(description: Description) {
//                description.appendText("view holder with title: $quid")
//            }
//        }
//    }
//
//    fun withTagInSimpleHeading(title: String): Matcher<RecyclerView.ViewHolder?>? {
//        return object : BoundedMatcher<RecyclerView.ViewHolder?,
//                SimpleHeadingRecyclerAdapter.SimpleHeadingViewHolder>(SimpleHeadingRecyclerAdapter.SimpleHeadingViewHolder::class.java) {
//            override fun matchesSafely(item: SimpleHeadingRecyclerAdapter.SimpleHeadingViewHolder): Boolean {
//                return item.itemView.tag.equals(title)
//            }
//
//            override fun describeTo(description: Description) {
//                description.appendText("view holder with title: $title")
//            }
//        }
//    }
//
//    private fun withTagInMarkingSubmit(clazzWorkUid: Long): Matcher<RecyclerView.ViewHolder?>? {
//        return object : BoundedMatcher<RecyclerView.ViewHolder?,
//                ClazzWorkSubmissionMarkingSubmitWithMetricsRecyclerAdapter.ClazzWorkProgressViewHolder>(
//                ClazzWorkSubmissionMarkingSubmitWithMetricsRecyclerAdapter.ClazzWorkProgressViewHolder::class.java) {
//            override fun matchesSafely(
//                    item: ClazzWorkSubmissionMarkingSubmitWithMetricsRecyclerAdapter.ClazzWorkProgressViewHolder): Boolean {
//                return item.itemView.tag.equals(clazzWorkUid)
//            }
//
//            override fun describeTo(description: Description) {
//                description.appendText("view holder with title: $clazzWorkUid")
//            }
//        }
//    }
//
//    private fun withTagInComment(commentUid: Long): Matcher<RecyclerView.ViewHolder?>? {
//        return object : BoundedMatcher<RecyclerView.ViewHolder?,
//                CommentsRecyclerAdapter.CommentsWithPersonViewHolder>(
//                CommentsRecyclerAdapter.CommentsWithPersonViewHolder::class.java) {
//            override fun matchesSafely(
//                    item: CommentsRecyclerAdapter.CommentsWithPersonViewHolder): Boolean {
//                return item.itemView.tag.equals(commentUid)
//            }
//
//            override fun describeTo(description: Description) {
//                description.appendText("view holder with comment uid: $commentUid")
//            }
//        }
//    }
//
//}
