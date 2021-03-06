package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.util.DefaultOneToManyJoinEditHelper
import com.ustadmobile.core.util.ext.putEntityAsJson
import com.ustadmobile.core.util.safeParse
import com.ustadmobile.core.view.HolidayCalendarEditView
import com.ustadmobile.core.view.UstadEditView.Companion.ARG_ENTITY_JSON
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.doorMainDispatcher
import com.ustadmobile.lib.db.entities.Holiday
import com.ustadmobile.lib.db.entities.HolidayCalendar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseJson
import org.kodein.di.DI


class HolidayCalendarEditPresenter(context: Any,
                          arguments: Map<String, String>, view: HolidayCalendarEditView,
                          lifecycleOwner: DoorLifecycleOwner,
                          di: DI)
    : UstadEditPresenter<HolidayCalendarEditView, HolidayCalendar>(context, arguments, view, di, lifecycleOwner) {

    override val persistenceMode: PersistenceMode
        get() = PersistenceMode.DB


    val holidayOneToManyJoinEditHelper = DefaultOneToManyJoinEditHelper<Holiday>(Holiday::holUid,
            "state_Holiday_list", Holiday.serializer().list,
            Holiday.serializer().list, this, Holiday::class) { holUid = it }

    fun handleAddOrEditHoliday(holiday: Holiday) {
        holidayOneToManyJoinEditHelper.onEditResult(holiday)
    }

    fun handleRemoveHoliday(holiday: Holiday) {
        holidayOneToManyJoinEditHelper.onDeactivateEntity(holiday)
    }


    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)

        view.holidayList = holidayOneToManyJoinEditHelper.liveList
    }

    override suspend fun onLoadEntityFromDb(db: UmAppDatabase): HolidayCalendar? {
        val entityUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L
        val holidayCalendar = withTimeoutOrNull(2000) {
            db.holidayCalendarDao.findByUid(entityUid)
        } ?: HolidayCalendar()

        val holidayList = withTimeoutOrNull(2000) {
            db.holidayDao.findByHolidayCalendaUidAsync(entityUid)
        } ?: listOf()
        holidayOneToManyJoinEditHelper.liveList.sendValue(holidayList)

        return holidayCalendar
    }

    override fun onLoadFromJson(bundle: Map<String, String>): HolidayCalendar? {
        super.onLoadFromJson(bundle)

        val entityJsonStr = bundle[ARG_ENTITY_JSON]
        var editEntity: HolidayCalendar? = null
        if(entityJsonStr != null) {
            editEntity = safeParse(di, HolidayCalendar.serializer(), entityJsonStr)
        }else {
            editEntity = HolidayCalendar()
        }

        return editEntity
    }

    override fun onSaveInstanceState(savedState: MutableMap<String, String>) {
        super.onSaveInstanceState(savedState)
        val entityVal = entity
        savedState.putEntityAsJson(ARG_ENTITY_JSON, null,
                entityVal)
    }

    override fun handleClickSave(entity: HolidayCalendar) {
        GlobalScope.launch(doorMainDispatcher()) {
            if(entity.umCalendarUid == 0L) {
                entity.umCalendarUid = repo.holidayCalendarDao.insertAsync(entity)
            }else {
                repo.holidayCalendarDao.updateAsync(entity)
            }

            holidayOneToManyJoinEditHelper.commitToDatabase(repo.holidayDao) {
                it.holHolidayCalendarUid = entity.umCalendarUid
            }

            view.finishWithResult(listOf(entity))
        }
    }


    companion object {

        //TODO: Add constants for keys that would be used for any One To Many Join helpers

    }

}