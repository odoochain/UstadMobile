package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.view.ListViewMode
import com.ustadmobile.core.view.SelQuestionSetEditView
import com.ustadmobile.core.view.SelQuestionSetListView
import com.ustadmobile.core.view.UstadView
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.DoorLiveData
import com.ustadmobile.lib.db.entities.SelQuestionSet
import com.ustadmobile.lib.db.entities.UmAccount

class SelQuestionSetListPresenter(context: Any, arguments: Map<String, String>,
                          view: SelQuestionSetListView,
                          lifecycleOwner: DoorLifecycleOwner, systemImpl: UstadMobileSystemImpl,
                          db: UmAppDatabase, repo: UmAppDatabase,
                          activeAccount: DoorLiveData<UmAccount?>)
    : UstadListPresenter<SelQuestionSetListView, SelQuestionSet>(context, arguments, view,
        lifecycleOwner, systemImpl, db, repo, activeAccount) {

    var currentSortOrder: SortOrder = SortOrder.ORDER_NAME_ASC

    enum class SortOrder(val messageId: Int) {
        ORDER_NAME_ASC(MessageID.sort_by_name_asc),
        ORDER_NAME_DSC(MessageID.sort_by_name_desc)
    }

    class SelQuestionSetListSortOption(val sortOrder: SortOrder, context: Any)
        : MessageIdOption(sortOrder.messageId, context)

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)
        updateListOnView()
        view.sortOptions = SortOrder.values().toList().map {
            SelQuestionSetListSortOption(it, context) }
    }

    override suspend fun onCheckAddPermission(account: UmAccount?): Boolean {
        //TODO: Add permission
        return true
    }

    private fun updateListOnView() {
        view.list = when(currentSortOrder){
            SortOrder.ORDER_NAME_ASC ->  repo.selQuestionSetDao.findAllQuestionSetsWithNumQuestionsNameAsc()
            SortOrder.ORDER_NAME_DSC ->  repo.selQuestionSetDao.findAllQuestionSetsWithNumQuestionsNameDesc()
        }
    }

    override fun handleClickEntry(entry: SelQuestionSet) {
        when(mListMode) {
            ListViewMode.PICKER -> view.finishWithResult(listOf(entry))
            ListViewMode.BROWSER -> systemImpl.go(SelQuestionSetEditView.VIEW_NAME,
                mapOf(UstadView.ARG_ENTITY_UID to entry.selQuestionSetUid.toString()), context)
        }

    }

    override fun handleClickCreateNewFab() {
        systemImpl.go(SelQuestionSetEditView.VIEW_NAME, mapOf(), context)
    }

    override fun handleClickSortOrder(sortOption: MessageIdOption) {
    }
}