
package com.ustadmobile.core.controller

import com.nhaarman.mockitokotlin2.*
import com.ustadmobile.core.account.UstadAccountManager
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.dao.CommentsDao
import com.ustadmobile.core.db.dao.EntityRoleDao
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.UstadTestRule
import com.ustadmobile.core.util.directActiveRepoInstance
import com.ustadmobile.core.util.ext.insertPersonAndGroup
import com.ustadmobile.core.util.ext.insertPersonOnlyAndGroup
import com.ustadmobile.core.view.PersonDetailView
import com.ustadmobile.core.view.UstadView
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.DoorMutableLiveData
import com.ustadmobile.lib.db.entities.ClazzWork
import com.ustadmobile.lib.db.entities.Person
import com.ustadmobile.lib.db.entities.UmAccount
import com.ustadmobile.util.test.ext.insertPersonWithRole
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

class PersonDetailPresenterTest {

    @JvmField
    @Rule
    var ustadTestRule = UstadTestRule()

    private lateinit var mockView: PersonDetailView

    private lateinit var context: Any

    private lateinit var accountManager: UstadAccountManager

    private val defaultTimeout:Long = 5000

    private lateinit var impl: UstadMobileSystemImpl

    private val  activeAccountLive = DoorMutableLiveData<UmAccount>()

    private lateinit var mockLifecycleOwner: DoorLifecycleOwner

    private lateinit var di: DI

    private lateinit var mockWebServer: MockWebServer

    private lateinit var repo: UmAppDatabase

    private lateinit var serverUrl: String

    private lateinit var repoEntityRoleDao: EntityRoleDao

    @Before
    fun setup() {
        context = Any()
        mockLifecycleOwner = mock { }

        mockView = mock{}
        impl = mock()

        mockWebServer = MockWebServer()
        mockWebServer.start()

        serverUrl = mockWebServer.url("/").toString()

        di = DI {
            import(ustadTestRule.diModule)
        }

        repo = di.directActiveRepoInstance()

        repoEntityRoleDao = spy(repo.entityRoleDao)
        whenever(repo.entityRoleDao).thenReturn(repoEntityRoleDao)

    }

    private fun createPerson(isAdmin: Boolean = true, withUsername: Boolean = true, sameUser: Boolean = false): Person{
        val mPersonUid:Long = 121212
        val loggedPersonUid: Long = 42

        val activeAccountUid = if(sameUser) mPersonUid else loggedPersonUid
        accountManager = mock{
            on{activeAccount}.thenReturn(UmAccount(activeAccountUid,"","",serverUrl))
            on{activeAccountLive}.thenReturn(activeAccountLive)
        }

        di = DI {
            import(ustadTestRule.diModule)
            bind<UstadAccountManager>(overrides = true) with singleton { accountManager }
            bind<UstadMobileSystemImpl>(overrides = true) with singleton { impl }
        }

        repo = di.directActiveRepoInstance()


        repoEntityRoleDao = spy(repo.entityRoleDao)
        whenever(repo.entityRoleDao).thenReturn(repoEntityRoleDao)

        val person = Person().apply {
            fatherName = "Doe"
            firstNames = "Jane"
            lastName = "Doe"
            username = if(withUsername) "jane.Doe" else null
            personUid = mPersonUid

            //repo.personDao.insert(this)
        }

        //GlobalScope.launch {
            repo.insertPersonOnlyAndGroup(person)
        //}

        if(!sameUser){
            Person().apply {
                admin = isAdmin
                username = "Admin"
                lastName = "User"
                personUid = activeAccountUid
                repo.personDao.insert(this)
            }
        }
        return person
    }

    @Test
    fun givenPersonDetailsWithRoles_whenLoaded_thenRolesCalled(){
        val person = createPerson(withUsername = false, isAdmin = false)
        val args = mapOf(UstadView.ARG_ENTITY_UID to person.personUid.toString())
        val presenter = PersonDetailPresenter(context, args,mockView,di, mockLifecycleOwner)
        presenter.onCreate(null)

        verify(repoEntityRoleDao, timeout(5000).atLeastOnce()).filterByPersonWithExtra(
                person.personGroupUid)
        verify(mockView, timeout(5000).atLeastOnce()).rolesAndPermissions = any()

    }

    @Test
    fun givenPersonDetails_whenPersonUsernameIsNullAndCanManageAccount_thenCreateAccountShouldBeHidden(){
        val person = createPerson(withUsername = false, isAdmin = false)
        val args = mapOf(UstadView.ARG_ENTITY_UID to person.personUid.toString())
        val presenter = PersonDetailPresenter(context, args,mockView,di, mockLifecycleOwner)
        presenter.onCreate(null)

        verify(mockView, timeout(defaultTimeout).atLeastOnce()).changePasswordVisible = eq(false)
        verify(mockView, timeout(defaultTimeout).atLeastOnce()).showCreateAccountVisible = eq(false)
    }

    @Test
    fun givenPersonDetailsAndAdminLogged_whenPersonUsernameIsNullAndCanManageAccount_thenCreateAccountShouldBeShown(){
        val person = createPerson(withUsername = false)
        val args = mapOf(UstadView.ARG_ENTITY_UID to person.personUid.toString())
        val presenter = PersonDetailPresenter(context, args,mockView,di, mockLifecycleOwner)
        presenter.onCreate(null)

        verify(mockView, timeout(defaultTimeout).atLeastOnce()).changePasswordVisible = eq(false)
        verify(mockView, timeout(defaultTimeout).atLeastOnce()).showCreateAccountVisible = eq(true)
    }

    @Test
    fun givenPersonDetailsAndAdminLogged_whenPersonUsernameIsNotNullAndCanManageAccount_thenChangePasswordShouldBeShown(){
        val person = createPerson(isAdmin = true)
        val args = mapOf(UstadView.ARG_ENTITY_UID to person.personUid.toString())
        val presenter = PersonDetailPresenter(context, args,mockView,di, mockLifecycleOwner)
        presenter.onCreate(null)

        verify(mockView, timeout(defaultTimeout).atLeastOnce()).changePasswordVisible = eq(true)
        verify(mockView, timeout(defaultTimeout).atLeastOnce()).showCreateAccountVisible = eq(false)
    }

    @Test
    fun givenPersonDetails_whenOpenedActivePersonDetailPersonAndCanManageAccount_thenChangePasswordShouldBeShown(){
        val person = createPerson(isAdmin = false, sameUser = true)
        val args = mapOf(UstadView.ARG_ENTITY_UID to person.personUid.toString())
        val presenter = PersonDetailPresenter(context, args,mockView,di, mockLifecycleOwner)
        presenter.onCreate(null)

        verify(mockView, timeout(defaultTimeout).atLeastOnce()).changePasswordVisible = eq(true)
        verify(mockView, timeout(defaultTimeout).atLeastOnce()).showCreateAccountVisible = eq(false)
    }
}