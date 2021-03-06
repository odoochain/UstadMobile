package com.ustadmobile.core.view

import androidx.paging.DataSource
import com.ustadmobile.door.DoorMutableLiveData
import com.ustadmobile.lib.db.entities.Clazz
import com.ustadmobile.lib.db.entities.SchoolWithHolidayCalendar


interface SchoolDetailOverviewView: UstadDetailView<SchoolWithHolidayCalendar> {

    var schoolClazzes : DataSource.Factory<Int, Clazz>?

    var schoolCodeVisible: Boolean

    companion object {

        const val VIEW_NAME = "SchoolWithHolidayCalendarDetailView"

    }

}