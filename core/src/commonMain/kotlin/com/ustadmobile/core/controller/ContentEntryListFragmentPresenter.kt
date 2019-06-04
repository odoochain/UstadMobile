package com.ustadmobile.core.controller

import com.ustadmobile.core.db.dao.ContentEntryDao
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemCommon
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.view.ContentEntryDetailView
import com.ustadmobile.core.view.ContentEntryListFragmentView
import com.ustadmobile.core.view.DummyView
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.lib.db.entities.DistinctCategorySchema
import com.ustadmobile.lib.db.entities.Language
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlin.js.JsName

class ContentEntryListFragmentPresenter(context: Any, arguments: Map<String, String>?, private val fragmentViewContract: ContentEntryListFragmentView)
    : UstadBaseController<ContentEntryListFragmentView>(context, arguments!!, fragmentViewContract) {

    private var contentEntryDao: ContentEntryDao? = null

    private var filterByLang: Long = 0

    private var filterByCategory: Long = 0

    private var parentUid: Long? = null


    override fun onCreate(savedState: Map<String, String?>?) {
        super.onCreate(savedState)
        val  appDatabase =  UmAccountManager.getRepositoryForActiveAccount(context)

        contentEntryDao = appDatabase.contentEntryDao
        if (arguments.containsKey(ARG_CONTENT_ENTRY_UID)) {
            showContentByParent()
        } else if (arguments.containsKey(ARG_DOWNLOADED_CONTENT)) {
            showDownloadedContent()
        }
    }

    private fun showContentByParent() {
        parentUid = arguments.getValue(ARG_CONTENT_ENTRY_UID)!!.toLong()
        fragmentViewContract.setContentEntryProvider(contentEntryDao!!.getChildrenByParentUidWithCategoryFilter(parentUid!!, 0, 0))

        GlobalScope.launch {
            try {
                val result = contentEntryDao!!.getContentByUuidAsync(parentUid!!)
                if (result == null) {
                    fragmentViewContract.runOnUiThread(Runnable { fragmentViewContract.showError() })
                    return@launch
                }
                val resultTitle = result.title
                if (resultTitle != null)
                    fragmentViewContract.setToolbarTitle(resultTitle)
            } catch (e: Exception) {
                fragmentViewContract.runOnUiThread(Runnable { fragmentViewContract.showError() })
            }
        }

        GlobalScope.launch {
            val result = contentEntryDao!!.findUniqueLanguagesInListAsync(parentUid!!).toMutableList()
            if (result.size > 1) {
                val selectLang = Language()
                selectLang.name = "Language"
                selectLang.langUid = 0
                result.add(0, selectLang)

                val allLang = Language()
                allLang.name = "All"
                allLang.langUid = 0
                result.add(1, allLang)

                fragmentViewContract.setLanguageOptions(result)
            }
        }

        GlobalScope.launch {
            val result = contentEntryDao!!.findListOfCategoriesAsync(parentUid!!)
            val schemaMap = HashMap<Long, List<DistinctCategorySchema>>()
            for (schema in result) {
                var data: MutableList<DistinctCategorySchema>? =
                        schemaMap[schema.contentCategorySchemaUid] as MutableList<DistinctCategorySchema>?
                if (data == null) {
                    data = ArrayList()
                    val schemaTitle = DistinctCategorySchema()
                    schemaTitle.categoryName = schema.schemaName
                    schemaTitle.contentCategoryUid = 0
                    schemaTitle.contentCategorySchemaUid = 0
                    data.add(0, schemaTitle)

                    val allSchema = DistinctCategorySchema()
                    allSchema.categoryName = "All"
                    allSchema.contentCategoryUid = 0
                    allSchema.contentCategorySchemaUid = 0
                    data.add(1, allSchema)

                }
                data.add(schema)
                schemaMap[schema.contentCategorySchemaUid] = data
            }
            fragmentViewContract.setCategorySchemaSpinner(schemaMap)
        }
    }

    private fun showDownloadedContent() {
        fragmentViewContract.setContentEntryProvider(contentEntryDao!!.downloadedRootItems())
    }


    @JsName("handleContentEntryClicked")
    fun handleContentEntryClicked(entry: ContentEntry) {
        val impl = UstadMobileSystemImpl.instance
        val args = hashMapOf<String, String?>()
        args.putAll(arguments)
        val entryUid = entry.contentEntryUid

        GlobalScope.launch {
            try {
                val result = contentEntryDao!!.findByUidAsync(entryUid)
                if (result == null) {
                    fragmentViewContract.runOnUiThread(Runnable { fragmentViewContract.showError() })
                    return@launch
                }
                if (result.leaf) {
                    args[ARG_CONTENT_ENTRY_UID] = entryUid.toString()
                    impl.go(ContentEntryDetailView.VIEW_NAME, args, view.viewContext)
                } else {
                    args[ARG_CONTENT_ENTRY_UID] = entryUid.toString()
                    impl.go(ContentEntryListFragmentView.VIEW_NAME, args, view.viewContext)
                }
            } catch (e: Exception) {
                fragmentViewContract.runOnUiThread(Runnable { fragmentViewContract.showError() })
            }

        }
    }

    @JsName("handleClickFilterByLanguage")
    fun handleClickFilterByLanguage(langUid: Long) {
        this.filterByLang = langUid
        fragmentViewContract.setContentEntryProvider(contentEntryDao!!.getChildrenByParentUidWithCategoryFilter(parentUid!!, filterByLang, filterByCategory))
    }

    @JsName("handleClickFilterByCategory")
    fun handleClickFilterByCategory(contentCategoryUid: Long) {
        this.filterByCategory = contentCategoryUid
        fragmentViewContract.setContentEntryProvider(contentEntryDao!!.getChildrenByParentUidWithCategoryFilter(parentUid!!, filterByLang, filterByCategory))
    }

    @JsName("handleUpNavigation")
    fun handleUpNavigation() {
        val impl = UstadMobileSystemImpl.instance
        impl.go(DummyView.VIEW_NAME, mapOf(), view.viewContext,
                UstadMobileSystemCommon.GO_FLAG_CLEAR_TOP or UstadMobileSystemCommon.GO_FLAG_SINGLE_TOP)

    }

    @JsName("handleDownloadStatusButtonClicked")
    fun handleDownloadStatusButtonClicked(entry: ContentEntry) {
        val impl = UstadMobileSystemImpl.instance
        val args = HashMap<String, String>()
        args["contentEntryUid"] = entry.contentEntryUid.toString()
        impl.go("DownloadDialog", args, context)
    }

    companion object {

        const val ARG_CONTENT_ENTRY_UID = "entryid"

        const val ARG_DOWNLOADED_CONTENT = "downloaded"
    }
}
