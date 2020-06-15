package com.ustadmobile.core.controller

import com.ustadmobile.core.db.JobStatus
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.AppConfig
import com.ustadmobile.core.impl.NoAppFoundException
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.networkmanager.downloadmanager.ContainerDownloadManager
import com.ustadmobile.core.util.GoToEntryFn
import com.ustadmobile.core.util.ext.observeWithLifecycleOwner
import com.ustadmobile.core.util.ext.observeWithPresenter
import com.ustadmobile.core.util.goToContentEntry
import com.ustadmobile.core.view.ContentEntry2DetailView
import com.ustadmobile.core.view.ContentEntryEdit2View
import com.ustadmobile.core.view.LoginView
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.core.view.UstadView.Companion.ARG_NO_IFRAMES
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.DoorLiveData
import com.ustadmobile.door.doorMainDispatcher
import com.ustadmobile.lib.db.entities.ContentEntryWithMostRecentContainer
import com.ustadmobile.lib.db.entities.DownloadJobItem
import com.ustadmobile.lib.db.entities.UmAccount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull


class ContentEntry2DetailPresenter(context: Any,
                                   arguments: Map<String, String>, view: ContentEntry2DetailView,
                                   lifecycleOwner: DoorLifecycleOwner,
                                   systemImpl: UstadMobileSystemImpl,
                                   private val isDownloadEnabled: Boolean,
                                   db: UmAppDatabase, repo: UmAppDatabase,
                                   private val containerDownloadManager: ContainerDownloadManager?,
                                   activeAccount: DoorLiveData<UmAccount?> = UmAccountManager.activeAccountLiveData,
                                   private val goToEntryFn: GoToEntryFn = ::goToContentEntry)
    : UstadDetailPresenter<ContentEntry2DetailView, ContentEntryWithMostRecentContainer>(context, arguments, view, lifecycleOwner, systemImpl,
        db, repo, activeAccount) {

    override val persistenceMode: PersistenceMode
        get() = PersistenceMode.DB

    private var downloadJobItemLiveData: DoorLiveData<DownloadJobItem?>? = null

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)
        val entryUuid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L
        if (containerDownloadManager != null) {
            GlobalScope.launch(doorMainDispatcher()) {
                downloadJobItemLiveData = containerDownloadManager.getDownloadJobItemByContentEntryUid(entryUuid).apply {
                    observeWithLifecycleOwner(lifecycleOwner, this@ContentEntry2DetailPresenter::onDownloadJobItemChanged)
                }
            }
        }
    }

    override suspend fun onLoadEntityFromDb(db: UmAppDatabase): ContentEntryWithMostRecentContainer? {
        val entityUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L
        val entity = withTimeoutOrNull(2000) {
            db.contentEntryDao.findEntryWithContainerByEntryId(entityUid)
        } ?: ContentEntryWithMostRecentContainer()

        val result = db.contentEntryRelatedEntryJoinDao.findAllTranslationsWithContentEntryUid(entityUid)
        view.availableTranslationsList = result
        return entity
    }

    override fun handleClickEdit() {
        systemImpl.go(ContentEntryEdit2View.VIEW_NAME,
                mapOf(ARG_ENTITY_UID to entity?.contentEntryUid.toString()), context)
    }

    fun handleOnClickOpenDownloadButton(){
        val canOpen = !isDownloadEnabled || downloadJobItemLiveData?.getValue()?.djiStatus == JobStatus.COMPLETE
        if (canOpen) {
            val loginFirst = systemImpl.getAppConfigString(AppConfig.KEY_LOGIN_REQUIRED_FOR_CONTENT_OPEN,
                    "false", context)!!.toBoolean()
            val account = UmAccountManager.getActiveAccount(context)
            if (loginFirst && (account == null || account.personUid == 0L)) {
                systemImpl.go(LoginView.VIEW_NAME, arguments, context)
            } else {
                openContentEntry()
            }
        } else if (isDownloadEnabled) {
            view.downloadOptions = arguments
        }
    }

    private fun onDownloadJobItemChanged(downloadJobItem: DownloadJobItem?) {
        view.downloadJobItem = downloadJobItem
    }

    private fun openContentEntry() {
        GlobalScope.launch {
            try {
                entity?.contentEntryUid?.let { goToEntryFn(it, db, context, systemImpl, isDownloadEnabled,
                            false,
                            arguments[ARG_NO_IFRAMES]
                                    ?.toBoolean() ?: false) }
            } catch (e: Exception) {
                if (e is NoAppFoundException) {
                    view.showSnackBar(systemImpl.getString(MessageID.no_app_found,context))
                } else {
                    val message = e.message
                    if(message != null){
                        view.showSnackBar(message)
                    }
                }
            }
        }
    }

    fun handleOnTranslationClicked(entryUid: Long){
        systemImpl.go(ContentEntry2DetailView.VIEW_NAME, mapOf(ARG_ENTITY_UID to entryUid.toString()), context)
    }

    override suspend fun onCheckEditPermission(account: UmAccount?): Boolean {
        return true
    }

}