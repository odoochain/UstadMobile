package com.ustadmobile.core.view

import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.lib.db.entities.ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer

interface ContentEntryList2View: UstadListView<ContentEntry, ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer> {

    fun showContentEntryAddOptions(parentEntryUid: Long)

    /**
     * Show the download dialog button. If required by the OS, show a permission dialog first
     */
    fun showDownloadDialog(args: Map<String, String>)

    /**
     * Show ContentEntryList in picker mode so the user can select a folder to move entries to.
     *
     * @param selectedContentEntryParentChildJoinUids a string which is a comma separated list of
     * the selected ContentEntryParentChildJoinUids (e.g. that should be saved to the savedStateHandle)
     */
    fun showMoveEntriesFolderPicker(selectedContentEntryParentChildJoinUids: String)

    var title: String?

    var editOptionVisible: Boolean

    companion object {

        const val ARG_DOWNLOADED_CONTENT = "downloaded"

        const val ARG_CONTENT_FILTER = "filter"

        const val ARG_FOLDER_FILTER = "folder"

        const val ARG_LIBRARIES_CONTENT = "libraries"

        const val ARG_RECYCLED_CONTENT = "recycled"

        const val ARG_CLAZZWORK_FILTER = "clazzworkFilter"

        const val VIEW_NAME = "ContentEntryListView"

        const val ARG_MOVING_CONTENT = "SelectedItems"

        const val ARG_MOVING_COUNT = "moveCount"

    }

}