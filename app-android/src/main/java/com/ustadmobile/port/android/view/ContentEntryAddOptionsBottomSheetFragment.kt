package com.ustadmobile.port.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.toughra.ustadmobile.R
import com.ustadmobile.core.impl.UMAndroidUtil
import com.ustadmobile.core.view.ContentEntryAddOptionsView
import com.ustadmobile.core.view.UstadView.Companion.ARG_LEAF
import com.ustadmobile.core.view.UstadView.Companion.ARG_PARENT_ENTRY_UID
import kotlinx.android.synthetic.main.fragment_content_entry_add_options.*
import kotlinx.android.synthetic.main.fragment_content_entry_add_options.view.*

/**
 * Fragment class responsible for content creation selection, you can create content by one of the following
 * CONTENT_CREATE_FOLDER = Create new content category
 * CONTENT_IMPORT_FILE = Create content from file (epub, h5p e.t.c)
 * CONTENT_CREATE_CONTENT = create content from out content editor
 */

class ContentEntryAddOptionsBottomSheetFragment : BottomSheetDialogFragment(), ContentEntryAddOptionsView, View.OnClickListener {


    private var createFolderOptionView: View? = null

    private var importContentOptionView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_content_entry_add_options, container, false)
        createFolderOptionView = rootView.content_create_folder
        importContentOptionView = rootView.content_import_content
        createFolderOptionView?.setOnClickListener(this)
        importContentOptionView?.setOnClickListener(this)
        return rootView
    }

    override fun onClick(view: View?) {
        val isShowing = this.dialog?.isShowing
       val contentType =  when(view?.id){
            R.id.content_create_folder -> false
            R.id.content_import_content -> true
           else -> -1
       }

        findNavController().navigate(R.id.content_entry_edit_dest, UMAndroidUtil.mapToBundle(mapOf(
                ARG_PARENT_ENTRY_UID to arguments?.get(ARG_PARENT_ENTRY_UID).toString(),
                ARG_LEAF to contentType.toString())))
        if(isShowing != null && isShowing){
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        importContentOptionView?.setOnClickListener(null)
        createFolderOptionView?.setOnClickListener(null)
        importContentOptionView = null
        createFolderOptionView = null
    }

}
