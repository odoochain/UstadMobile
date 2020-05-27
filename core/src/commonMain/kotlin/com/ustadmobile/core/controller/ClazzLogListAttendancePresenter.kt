package com.ustadmobile.core.controller

import com.soywiz.klock.days
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.util.ext.attendancePercentage
import com.ustadmobile.core.view.*
import com.ustadmobile.door.*
import com.ustadmobile.lib.db.entities.ClazzLog
import com.ustadmobile.lib.db.entities.ClazzWithSchool
import com.ustadmobile.lib.db.entities.UmAccount
import com.ustadmobile.lib.util.getSystemTimeInMillis
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClazzLogListAttendancePresenter(context: Any, arguments: Map<String, String>, view: ClazzLogListAttendanceView,
                          lifecycleOwner: DoorLifecycleOwner, systemImpl: UstadMobileSystemImpl,
                          db: UmAppDatabase, repo: UmAppDatabase,
                          activeAccount: DoorLiveData<UmAccount?>)
    : UstadListPresenter<ClazzLogListAttendanceView, ClazzLog>(context, arguments, view, lifecycleOwner, systemImpl,
        db, repo, activeAccount) {


    data class AttendanceGraphData(val graphData: List<Pair<Long, Float>>, val graphDateRange: Pair<Long, Long>)

    var currentSortOrder: SortOrder = SortOrder.ORDER_NAME_ASC

    var clazzUidFilter: Long = 0

    private var clazzWithSchool: ClazzWithSchool? = null

    private var graphDbData: DoorLiveData<List<ClazzLog>>? = null

    private var graphDisplayData = DoorMutableLiveData<AttendanceGraphData>()

    private var graphDateRange = Pair(0L, 0L)

    private val graphObserver = object: DoorObserver<List<ClazzLog>> {
        override fun onChanged(t: List<ClazzLog>) {
            graphDisplayData.sendValue(AttendanceGraphData(
                t.map { it.logDate to it.attendancePercentage() },
                graphDateRange
            ))
        }
    }

    enum class SortOrder(val messageId: Int) {
        ORDER_NAME_ASC(MessageID.sort_by_name_asc),
        ORDER_NAME_DSC(MessageID.sort_by_name_desc)
    }

    class ClazzLogListSortOption(val sortOrder: SortOrder, context: Any) : MessageIdOption(sortOrder.messageId, context)

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)
        clazzUidFilter = arguments[UstadView.ARG_FILTER_BY_CLAZZUID]?.toLong() ?: 0
        updateListOnView()
        view.sortOptions = SortOrder.values().toList().map { ClazzLogListSortOption(it, context) }
        view.graphData = graphDisplayData
        handleClickGraphDuration(7)
    }

    override suspend fun onCheckAddPermission(account: UmAccount?): Boolean {
        //ClazzLogs are always created by the scheduling system
        return false
    }

    private fun updateListOnView() {
        GlobalScope.launch {
            clazzWithSchool = repo.clazzDao.getClazzWithSchool(clazzUidFilter)
            withContext((doorMainDispatcher())) {
                view.clazzTimeZone = clazzWithSchool?.clazzTimeZone ?: clazzWithSchool?.school?.schoolTimeZone ?: "UTC"
                view.list = repo.clazzLogDao.findByClazzUidAsFactory(clazzUidFilter)
            }
        }

    }

    override fun handleClickEntry(entry: ClazzLog) {
        systemImpl.go(ClazzLogEditAttendanceView.VIEW_NAME,
            mapOf(UstadView.ARG_ENTITY_UID to entry.clazzLogUid.toString()), context)
    }

    override fun handleClickCreateNewFab() {
        //in this instance we should open up the most recent clazzlog for clazz log detail
    }

    fun handleClickGraphDuration(days: Int) {
        val timeNow = getSystemTimeInMillis()
        graphDateRange = timeNow - (days.days.millisecondsLong) to timeNow
        graphDbData?.removeObserver(graphObserver)
        graphDbData = repo.clazzLogDao.findByClazzUidWithinTimeRangeLive(clazzUidFilter,
                graphDateRange.first, graphDateRange.second, ClazzLog.STATUS_RECORDED)
        graphDbData?.observe(lifecycleOwner, graphObserver)
    }

    override fun handleClickSortOrder(sortOption: MessageIdOption) {
        val sortOrder = (sortOption as? ClazzLogListSortOption)?.sortOrder ?: return
        if(sortOrder != currentSortOrder) {
            currentSortOrder = sortOrder
            updateListOnView()
        }
    }
}