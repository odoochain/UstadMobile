package com.ustadmobile.port.android.view

import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.gcacace.signaturepad.views.SignaturePad
import com.toughra.ustadmobile.R
import com.toughra.ustadmobile.databinding.FragmentSaleDeliveryEditBinding
import com.ustadmobile.core.controller.SaleDeliveryEditPresenter
import com.ustadmobile.core.controller.UstadEditPresenter
import com.ustadmobile.core.util.ext.toStringMap
import com.ustadmobile.core.view.SaleDeliveryEditView
import com.ustadmobile.lib.db.entities.ProductDeliveryWithProductAndTransactions
import com.ustadmobile.lib.db.entities.SaleDeliveryAndItems
import com.ustadmobile.port.android.util.ext.currentBackStackEntrySavedStateMap
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException

interface SaleDeliveryEditFragmentEventHandler: SignaturePad.OnSignedListener {

    fun clearAll()

}

class SaleDeliveryEditFragment: UstadEditFragment<SaleDeliveryAndItems>(), SaleDeliveryEditView,
        SaleDeliveryEditFragmentEventHandler {

    private var mBinding: FragmentSaleDeliveryEditBinding? = null

    private var mPresenter: SaleDeliveryEditPresenter? = null

    override val mEditPresenter: UstadEditPresenter<*, SaleDeliveryAndItems>?
        get() = mPresenter

    private var deliveryListMergerRecyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView: View
        mBinding = FragmentSaleDeliveryEditBinding.inflate(inflater, container,
                false).also {
            rootView = it.root
            it.activityEventHandler = this
            it.fragmentSaleDeliveryEditSignature.setOnSignedListener(this)

        }
        mBinding?.fragmentSaleDeliveryEditSignature?.setOnSignedListener(this)

        deliveryListMergerRecyclerView = rootView.findViewById(
                R.id.fragment_sale_delivery_edit_items_rv)

        mPresenter = SaleDeliveryEditPresenter(requireContext(), arguments.toStringMap(),
                this, di, viewLifecycleOwner)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()


        mPresenter?.onCreate(navController.currentBackStackEntrySavedStateMap())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mPresenter = null
        entity = null
    }

    override fun onResume() {
        super.onResume()
        setEditFragmentTitle(R.string.delivery, R.string.delivery)
    }

    override var entity: SaleDeliveryAndItems? = null
        get() = field
        set(value) {
            field = value
            mBinding?.saleDelivery = value

            try {

                if(value?.delivery?.saleDeliverySignature != null && value.delivery?.saleDeliverySignature.isNotBlank()) {
                    val svg = SVG.getFromString(value?.delivery?.saleDeliverySignature ?: "")

                    val signPic = svg.renderToPicture()

                    val picW = signPic.getWidth()
                    val picH = signPic.getHeight()
                    var adjustedPic = signPic
                    if (picH > picW) {
                        adjustedPic = rotatePicture(0f, signPic)
                    }
                    val pd = PictureDrawable(adjustedPic)

                    mBinding?.fragmentSaleDeliveryEditSignature?.background = pd
                }

            } catch (spe: SVGParseException) {
                spe.printStackTrace()
            }


        }

    fun rotatePicture(degrees: Float, picture: Picture): Picture {
        val width = picture.width
        val height = picture.height

        val rotatedPicture = Picture()
        val canvas = rotatedPicture.beginRecording(width, height)
        canvas.save()
        canvas.rotate(degrees, width.toFloat(), height.toFloat())
        picture.draw(canvas)
        canvas.restore()
        rotatedPicture.endRecording()

        return rotatedPicture
    }

    override var productWithDeliveriesList
            : List<ProductDeliveryWithProductAndTransactions> = mutableListOf()
        set(value) {

            field = value
            //Make the merger
            val allAdapters = mutableListOf<Any>()
            val mergeAdapter = MergeAdapter()
            for(product in value){

                val productTitleRecyclerAdapter =
                        ProductDeliveryWithProducersSelectionRecyclerAdapter()
                productTitleRecyclerAdapter?.submitList(listOf(product))
                mergeAdapter.addAdapter(productTitleRecyclerAdapter)

                val deliverySelectionRecyclerAdapter = PersonWithInventoryListRecyclerAdapter(
                        true)
                deliverySelectionRecyclerAdapter?.submitList(product.transactions)
                mergeAdapter?.addAdapter(deliverySelectionRecyclerAdapter)

            }

            deliveryListMergerRecyclerView?.adapter = mergeAdapter
            deliveryListMergerRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        }

    override var fieldsEnabled: Boolean = false
        set(value) {
            field = value
            mBinding?.fieldsEnabled = value
        }

    override fun clearAll() {
        mBinding?.fragmentSaleDeliveryEditSignature?.clear()
        print("hie")
    }


    override fun onStartSigning() {
        //Event triggered when the pad is touched
        print("hsss")
    }

    override fun onSigned() {
        //Event triggered when the pad is signed
        print("hi")
        val signSvg = mBinding?.fragmentSaleDeliveryEditSignature?.getSignatureSvg()
        entity?.delivery?.saleDeliverySignature = signSvg?:""


    }

    override fun onClear() {
    }

}