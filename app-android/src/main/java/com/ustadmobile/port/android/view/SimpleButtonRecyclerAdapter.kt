package com.ustadmobile.port.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toughra.ustadmobile.databinding.ItemSimpleButtonBinding
import com.ustadmobile.port.android.view.util.SingleItemRecyclerViewAdapter

//TODO: Why not use the normal View.OnClickListener for this?
class SimpleButtonRecyclerAdapter(heading: String, val buttonHandler: SimpleButtonHandler)
    : SingleItemRecyclerViewAdapter<SimpleButtonRecyclerAdapter.SimpleHeadingViewHolder>() {

    var buttonText: String? = heading
        set(value) {
            field = value
            viewHolder?.itemBinding?.buttonText = value
        }

    var isOutline: Boolean? = false
        set(value){
            field = value
            viewHolder?.itemBinding?.outline = value
        }

    class SimpleHeadingViewHolder(var itemBinding: ItemSimpleButtonBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    //TODO: the variable ViewHolder is never set
    private var viewHolder: SimpleHeadingViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHeadingViewHolder {
        return SimpleHeadingViewHolder(
                ItemSimpleButtonBinding.inflate(LayoutInflater.from(parent.context),
                        parent, false).also {
                    it.buttonText = buttonText
                    it.mHandler = buttonHandler
                    it.outline = isOutline
                })
    }

    override fun onBindViewHolder(holder: SimpleHeadingViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.tag = buttonText
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        viewHolder = null
    }


}