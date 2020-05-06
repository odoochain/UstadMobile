package com.ustadmobile.core.view

import com.ustadmobile.lib.db.entities.School


interface SchoolDetailView: UstadDetailView<School> {

    fun setSettingsVisible(visible: Boolean)

    fun setTitle(title: String)

    companion object {

        const val VIEW_NAME = "SchoolDetailView"

    }

}