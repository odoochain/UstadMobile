package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.DefaultOneToManyJoinEditHelper
import com.ustadmobile.core.util.ext.putEntityAsJson
import com.ustadmobile.core.view.SelQuestionAndOptionsEditView
import com.ustadmobile.core.view.UstadView
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.DoorLiveData
import com.ustadmobile.door.doorMainDispatcher
import com.ustadmobile.lib.db.entities.SelQuestionAndOptions

import com.ustadmobile.lib.db.entities.UmAccount
import io.ktor.client.features.json.defaultSerializer
import io.ktor.http.content.TextContent
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.core.view.UstadEditView.Companion.ARG_ENTITY_JSON
import com.ustadmobile.lib.db.entities.SelQuestion
import com.ustadmobile.lib.db.entities.SelQuestionOption


class SelQuestionAndOptionsEditPresenter(context: Any,
                          arguments: Map<String, String>, view: SelQuestionAndOptionsEditView,
                          lifecycleOwner: DoorLifecycleOwner,
                          systemImpl: UstadMobileSystemImpl,
                          db: UmAppDatabase, repo: UmAppDatabase,
                          activeAccount: DoorLiveData<UmAccount?> = UmAccountManager.activeAccountLiveData)
    : UstadEditPresenter<SelQuestionAndOptionsEditView, SelQuestionAndOptions>(context, arguments, view, lifecycleOwner, systemImpl,
        db, repo, activeAccount) {

    override val persistenceMode: PersistenceMode
        get() = PersistenceMode.JSON

    /*
     * TODO: Add any required one to many join helpers here - use these templates (type then hit tab)
     * onetomanyhelper: Adds a one to many relationship using OneToManyJoinEditHelper
     */
    val selQuestionOptionOneToManyJoinEditHelper =
            DefaultOneToManyJoinEditHelper<SelQuestionOption>(SelQuestionOption::selQuestionOptionUid,
            "state_EntityClass_list", SelQuestionOption.serializer().list,
            SelQuestionOption.serializer().list, this) { selQuestionOptionUid = it }

    fun handleAddOrEditSelQuestionOption(entityClass: SelQuestionOption) {
        selQuestionOptionOneToManyJoinEditHelper.onEditResult(entityClass)
    }

    fun handleRemoveSelQuestionOption(entityClass: SelQuestionOption) {
        selQuestionOptionOneToManyJoinEditHelper.onDeactivateEntity(entityClass)
    }


    //

    //TODO: Add code to handleClickSave to save the result to the database
    // entityClass.commitToDatabase(repo.entityClassDao) {
    //   it.fk = entity.fk
    // }
    //

    enum class QuestionOptions(val optionVal: Int, val messageId: Int){

    }

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)

        view.selQuestionOptionList = selQuestionOptionOneToManyJoinEditHelper.liveList

        selQuestionOptionOneToManyJoinEditHelper.liveList.sendValue(
                entity?.options?: mutableListOf())

    }

    override suspend fun onLoadEntityFromDb(db: UmAppDatabase): SelQuestionAndOptions? {
        val entityUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L

        //TODO: Load the list for any one to many join helper here
        /* e.g.
         val selQuestionAndOptions = withTimeoutOrNull {
             db.selQuestionAndOptions.findByUid(entityUid)
         } ?: SelQuestionAndOptions()
         return selQuestionAndOptions
         */
        return TODO("Implement load from Database or return null if using PERSISTENCE_MODE.JSON")
    }

    override fun onLoadFromJson(bundle: Map<String, String>): SelQuestionAndOptions? {
        super.onLoadFromJson(bundle)

        val entityJsonStr = bundle[ARG_ENTITY_JSON]
        var editEntity: SelQuestionAndOptions? = null
        if(entityJsonStr != null) {
            editEntity = Json.parse(SelQuestionAndOptions.serializer(), entityJsonStr)
        }else {
            editEntity = SelQuestionAndOptions(SelQuestion().apply { questionActive = true },
                    listOf())
        }

        return editEntity
    }

    override fun onSaveInstanceState(savedState: MutableMap<String, String>) {
        super.onSaveInstanceState(savedState)
        val entityVal = entity
        savedState.putEntityAsJson(ARG_ENTITY_JSON, null,
                entityVal)
    }

    fun updateQuestionOptionTitle(questionOption: SelQuestionOption, title: String){
        questionOption.optionText = title
        handleAddOrEditSelQuestionOption(questionOption)

    }

    fun addNewBlankQuestionOption(){
        val newQuestionOption = SelQuestionOption()
        newQuestionOption.optionText = ""
        newQuestionOption.selQuestionOptionQuestionUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L
        newQuestionOption.optionActive = true

        handleAddOrEditSelQuestionOption(newQuestionOption)

    }

    fun removeQuestionOption(selQuestionOption: SelQuestionOption){
        selQuestionOption.optionActive = false
        GlobalScope.launch {
            if(selQuestionOption.selQuestionOptionUid != 0L) {
                repo.selQuestionOptionDao.updateAsync(selQuestionOption)
            }
        }
    }

    override fun handleClickSave(entity: SelQuestionAndOptions) {

        GlobalScope.launch {
            //Build options
            val eti = selQuestionOptionOneToManyJoinEditHelper.entitiesToInsert
            entity.options = eti

            view.finishWithResult(entity)
        }
    }


    companion object {

        //TODO: Add constants for keys that would be used for any One To Many Join helpers

    }

}