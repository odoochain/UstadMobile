package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.DefaultOneToManyJoinEditHelper
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.util.ext.putEntityAsJson
import com.ustadmobile.core.view.SchoolEditView
import com.ustadmobile.core.view.UstadEditView.Companion.ARG_ENTITY_JSON
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.DoorLiveData
import com.ustadmobile.door.doorMainDispatcher
import com.ustadmobile.lib.db.entities.Clazz
import com.ustadmobile.lib.db.entities.School
import com.ustadmobile.lib.db.entities.SchoolWithHolidayCalendar
import com.ustadmobile.lib.db.entities.UmAccount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json


class SchoolEditPresenter(context: Any,
                          arguments: Map<String, String>, view: SchoolEditView,
                          lifecycleOwner: DoorLifecycleOwner,
                          systemImpl: UstadMobileSystemImpl,
                          db: UmAppDatabase, repo: UmAppDatabase,
                          activeAccount: DoorLiveData<UmAccount?> = UmAccountManager.activeAccountLiveData)
    : UstadEditPresenter<SchoolEditView, SchoolWithHolidayCalendar>(context, arguments, view, lifecycleOwner, systemImpl,
        db, repo, activeAccount) {

    enum class GenderOptions(val optionVal: Int, val messageId: Int){
        MIXED(School.SCHOOL_GENDER_MIXED,
                MessageID.mixed),
        FEMALE(School.SCHOOL_GENDER_FEMALE,
                MessageID.female),
        MALE(School.SCHOOL_GENDER_MALE,
                MessageID.male)
    }

    class GenderTypeMessageIdOption(day: GenderOptions, context: Any)
        : MessageIdOption(day.messageId, context, day.optionVal)

    override val persistenceMode: PersistenceMode
        get() = PersistenceMode.DB

    private val clazzOneToManyJoinEditHelper = DefaultOneToManyJoinEditHelper<Clazz>(
            Clazz::clazzUid,"state_Clazz_list", Clazz.serializer().list,
            Clazz.serializer().list, this) { clazzUid = it }

    fun handleAddOrEditClazz(clazz: Clazz) {
        clazzOneToManyJoinEditHelper.onEditResult(clazz)
    }

    fun handleRemoveSchedule(clazz: Clazz) {
        clazzOneToManyJoinEditHelper.onDeactivateEntity(clazz)
    }

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)

        view.schoolClazzes = clazzOneToManyJoinEditHelper.liveList
        view.genderOptions = GenderOptions.values().map { GenderTypeMessageIdOption(it, context) }


    }

    override suspend fun onLoadEntityFromDb(db: UmAppDatabase): SchoolWithHolidayCalendar? {
        val entityUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L

        val school = withTimeoutOrNull(2000) {
            db.schoolDao.findByUidWithHolidayCalendarAsync(entityUid)
        } ?: SchoolWithHolidayCalendar()

        val clazzes = withTimeoutOrNull(2000){
            db.clazzDao.findAllClazzesBySchool(entityUid)
        }?: listOf()

        clazzOneToManyJoinEditHelper.liveList.sendValue(clazzes)

        return school

    }

    override fun onLoadFromJson(bundle: Map<String, String>): SchoolWithHolidayCalendar? {
        super.onLoadFromJson(bundle)

        val entityJsonStr = bundle[ARG_ENTITY_JSON]
        var editEntity: SchoolWithHolidayCalendar? = null
        if(entityJsonStr != null) {
            editEntity = Json.parse(SchoolWithHolidayCalendar.serializer(), entityJsonStr)
        }else {
            editEntity = SchoolWithHolidayCalendar()
        }

        return editEntity
    }

    override fun onSaveInstanceState(savedState: MutableMap<String, String>) {
        super.onSaveInstanceState(savedState)
        val entityVal = entity
        savedState.putEntityAsJson(ARG_ENTITY_JSON, null,
                entityVal)
    }

    override fun handleClickSave(entity: SchoolWithHolidayCalendar) {

        GlobalScope.launch(doorMainDispatcher()) {
            if(entity.schoolUid == 0L) {
                entity.schoolActive = true
                entity.schoolUid = repo.schoolDao.insertAsync(entity)
            }else {
                repo.schoolDao.updateAsync(entity)
            }

            clazzOneToManyJoinEditHelper.commitToDatabase(repo.clazzDao){
                it.clazzSchoolUid = entity.schoolUid
            }
            view.finishWithResult(listOf(entity))
        }
    }

    companion object {
    }

}