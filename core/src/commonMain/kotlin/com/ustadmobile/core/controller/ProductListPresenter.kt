package com.ustadmobile.core.controller

import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.util.ext.toQueryLikeParam
import com.ustadmobile.core.view.*
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.lib.db.entities.ClazzWork
import com.ustadmobile.lib.db.entities.Product
import com.ustadmobile.lib.db.entities.ProductWithInventoryCount
import com.ustadmobile.lib.db.entities.UmAccount
import kotlinx.serialization.json.Json
import org.kodein.di.DI

class ProductListPresenter(context: Any, arguments: Map<String, String>, view: ProductListView,
                           di: DI, lifecycleOwner: DoorLifecycleOwner)
    : UstadListPresenter<ProductListView, Product>(context, arguments, view, di, lifecycleOwner),
        ProductListItemListener, OnSearchSubmitted  {

    var currentSortOrder: SortOrder = SortOrder.ORDER_NAME_ASC

    var searchText: String? = null

    enum class SortOrder(val messageId: Int) {
        ORDER_NAME_ASC(MessageID.sort_by_name_asc),
        ORDER_NAME_DSC(MessageID.sort_by_name_desc)
    }


    override fun onSearchSubmitted(text: String?) {
        searchText = text
        updateListOnView()
    }

    class ProductListSortOption(val sortOrder: SortOrder, context: Any) :
            MessageIdOption(sortOrder.messageId, context)

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)
        updateListOnView()
        view.sortOptions = SortOrder.values().toList().map { ProductListSortOption(it, context) }
    }

    override suspend fun onCheckAddPermission(account: UmAccount?): Boolean {
        return true
    }

    private fun updateListOnView() {

        view.list = repo.productDao.findAllActiveProductWithInventoryCount(
                accountManager.activeAccount.personUid, searchText.toQueryLikeParam())
    }

    override fun handleClickCreateNewFab() {
        systemImpl.go(ProductEditView.VIEW_NAME, mapOf(), context)

    }

    override fun handleClickSortOrder(sortOption: MessageIdOption) {
        val sortOrder = (sortOption as? ProductListSortOption)?.sortOrder ?: return
        if(sortOrder != currentSortOrder) {
            currentSortOrder = sortOrder
            updateListOnView()
        }
    }

    override fun handleClickEntry(entry: Product) {
        when(mListMode) {
            ListViewMode.PICKER -> {
                if(arguments.containsKey(UstadView.ARG_CREATE_SALE) && arguments[UstadView.ARG_CREATE_SALE].equals("true")){
                    systemImpl.go(SaleItemEditView.VIEW_NAME, mapOf(
                            UstadView.ARG_PRODUCT_UID to entry.productUid.toString()
                    ), context)
                }else {
                    view.finishWithResult(listOf(entry))
                }
            }
            ListViewMode.BROWSER -> {
                systemImpl.go(ProductDetailView.VIEW_NAME,
                        mapOf(UstadView.ARG_ENTITY_UID to entry.productUid.toString()), context)
            }
        }
    }

    override fun onClickProduct(product: ProductWithInventoryCount) {


        when(mListMode) {
            ListViewMode.PICKER -> {
                if(arguments.containsKey(UstadView.ARG_CREATE_SALE) && arguments[UstadView.ARG_CREATE_SALE].equals("true")){

                    view.goToSaleItem(product)
                }else {
                    view.finishWithResult(listOf(product))
                }
            }
            ListViewMode.BROWSER -> {
                systemImpl.go(ProductDetailView.VIEW_NAME,
                        mapOf(UstadView.ARG_ENTITY_UID to product.productUid.toString()), context)
            }
        }
    }
}