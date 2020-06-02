
package com.ustadmobile.core.controller

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.ustadmobile.core.view.ClazzWorkDetailOverviewView
import com.ustadmobile.core.view.ClazzWorkDetailView
import com.ustadmobile.core.view.ClazzWorkEditView
import com.nhaarman.mockitokotlin2.*
import com.ustadmobile.core.util.SystemImplRule
import com.ustadmobile.core.util.UmAppDatabaseClientRule
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.core.db.dao.ClazzWorkDao
import com.ustadmobile.core.db.dao.CommentsDao
import com.ustadmobile.door.DoorLifecycleObserver
import com.ustadmobile.lib.db.entities.ClazzWork
import com.ustadmobile.lib.db.entities.ClazzWorkWithSubmission
import com.ustadmobile.core.util.ext.waitForListToBeSet
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.lib.db.entities.Clazz
import com.ustadmobile.util.test.ext.insertTestClazzWork
import kotlinx.coroutines.runBlocking
import org.junit.Assert

/**
 * The Presenter test for list items is generally intended to be a sanity check on the underlying code.
 *
 * Note:
 */
class ClazzWorkDetailOverviewPresenterTest {

    @JvmField
    @Rule
    var systemImplRule = SystemImplRule()

    @JvmField
    @Rule
    var clientDbRule = UmAppDatabaseClientRule(useDbAsRepo = true)

    private lateinit var mockView: ClazzWorkDetailOverviewView

    private lateinit var context: Any

    private lateinit var mockLifecycleOwner: DoorLifecycleOwner

    private lateinit var repoClazzWorkDaoSpy: ClazzWorkDao

    private lateinit var repoCommentsDaoSpy: CommentsDao

    @Before
    fun setup() {
        mockView = mock { }
        mockLifecycleOwner = mock {
            on { currentState }.thenReturn(DoorLifecycleObserver.RESUMED)
        }
        context = Any()
        repoClazzWorkDaoSpy = spy(clientDbRule.db.clazzWorkDao)
        whenever(clientDbRule.db.clazzWorkDao).thenReturn(repoClazzWorkDaoSpy)

        repoCommentsDaoSpy = spy(clientDbRule.db.commentsDao)
        whenever(clientDbRule.db.commentsDao).thenReturn(repoCommentsDaoSpy)

        //TODO: insert any entities required for all tests
    }

    @Test
    fun givenClazzWorkExists_whenOnCreateCalled_thenClazzWorkIsSetOnView() {

        val testClazzWork = runBlocking {
            clientDbRule.db.insertTestClazzWork(ClazzWork())
        }


        val presenterArgs = mapOf(ARG_ENTITY_UID to testClazzWork.clazzWork.clazzWorkUid.toString())
        val presenter = ClazzWorkDetailOverviewPresenter(context,
                presenterArgs, mockView, mockLifecycleOwner,
                systemImplRule.systemImpl, clientDbRule.db, clientDbRule.repo,
                clientDbRule.accountLiveData)

        presenter.onCreate(null)

        nullableArgumentCaptor<ClazzWorkWithSubmission>().apply {
            verify(mockView, timeout(5000).atLeastOnce()).entity = capture()

            Assert.assertEquals("Expected entity was set on view",
                    testClazzWork.clazzWork.clazzWorkUid, lastValue!!.clazzWorkUid)
        }

        verify(repoCommentsDaoSpy, timeout(5000).atLeastOnce()).findPublicByEntityTypeAndUidLive(
                ClazzWork.CLAZZ_WORK_TABLE_ID, testClazzWork.clazzWork.clazzWorkUid)
        verify(mockView, timeout(5000).atLeastOnce()).clazzWorkPublicComments = any()

        verify(repoCommentsDaoSpy, timeout(5000).atLeastOnce()).findPrivateByEntityTypeAndUidLive(
                ClazzWork.CLAZZ_WORK_TABLE_ID, testClazzWork.clazzWork.clazzWorkUid)
        verify(mockView, timeout(5000).atLeastOnce()).clazzWorkPrivateComments = any()

        verify(mockView, timeout(5000).atLeastOnce()).timeZone =
                testClazzWork.clazzAndMembers.clazz.clazzTimeZone!!

        verify(mockView, timeout(5000).atLeastOnce()).studentMode = false


    }


    @Test
    fun givenClazzWorkExists_whenClickAddPublicComment_thenShouldPersistComment(){
        val testClazzWork = runBlocking {
            clientDbRule.db.insertTestClazzWork(ClazzWork())
        }


        val presenterArgs = mapOf(ARG_ENTITY_UID to testClazzWork.clazzWork.clazzWorkUid.toString())
        val presenter = ClazzWorkDetailOverviewPresenter(context,
                presenterArgs, mockView, mockLifecycleOwner,
                systemImplRule.systemImpl, clientDbRule.db, clientDbRule.repo,
                clientDbRule.accountLiveData)

        presenter.onCreate(null)

        verify(mockView, timeout(5000).atLeastOnce()).entity = any()

        presenter.addComment("Hello World", true)

        verifyBlocking(repoCommentsDaoSpy, timeout(5000)){
            insertAsync(argThat{ this.commentsText == "Hello World"})
        }


    }


    @Test
    fun givenClazzWorkExists_whenHandleOnClickEditCalled_thenSystemImplGoToEditViewIsCalled() {
        val testEntity = ClazzWork().apply {
            //set variables here
            clazzWorkUid = clientDbRule.db.clazzWorkDao.insert(this)
        }
        val presenterArgs = mapOf(ARG_ENTITY_UID to testEntity.clazzWorkUid.toString())
        val presenter = ClazzWorkDetailOverviewPresenter(context,
                presenterArgs, mockView, mockLifecycleOwner,
                systemImplRule.systemImpl, clientDbRule.db, clientDbRule.repo,
                clientDbRule.accountLiveData)

        presenter.onCreate(null)

        presenter.handleClickEdit()

        verify(systemImplRule.systemImpl, timeout(5000)).go(eq(ClazzWorkEditView.VIEW_NAME),
            eq(mapOf(ARG_ENTITY_UID to testEntity.clazzWorkUid.toString())), any())
    }

}