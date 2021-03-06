package com.ustadmobile.core.view

import com.ustadmobile.lib.db.entities.Clazz
import com.ustadmobile.lib.db.entities.ClazzWithListDisplayDetails

interface ClazzList2View: UstadListView<Clazz, ClazzWithListDisplayDetails> {

    var newClazzListOptionVisible: Boolean

    companion object {
        const val VIEW_NAME = "ClazzList2"
    }

}