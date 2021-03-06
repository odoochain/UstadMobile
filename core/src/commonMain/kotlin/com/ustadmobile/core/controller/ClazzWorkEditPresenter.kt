package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.util.DefaultOneToManyJoinEditHelper
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.util.ext.effectiveTimeZone
import com.ustadmobile.core.util.ext.putEntityAsJson
import com.ustadmobile.core.util.safeParse
import com.ustadmobile.core.view.ClazzWorkDetailView
import com.ustadmobile.core.view.ClazzWorkEditView
import com.ustadmobile.core.view.UstadEditView.Companion.ARG_ENTITY_JSON
import com.ustadmobile.core.view.UstadView.Companion.ARG_ENTITY_UID
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.doorMainDispatcher
import com.ustadmobile.door.util.systemTimeInMillis
import com.ustadmobile.lib.db.entities.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import org.kodein.di.DI


class ClazzWorkEditPresenter(context: Any,
                          arguments: Map<String, String>, view: ClazzWorkEditView, di: DI,
                          lifecycleOwner: DoorLifecycleOwner)
    : UstadEditPresenter<ClazzWorkEditView, ClazzWork>(context, arguments, view, di, lifecycleOwner) {

    enum class SubmissionOptions(val optionVal: Int, val messageId: Int){
        NO_SUBMISSION_REQUIRED(ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_NONE,
                MessageID.no_submission_required),
        SHORT_TEXT(ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_SHORT_TEXT,
                MessageID.short_text),
        QUIZ(ClazzWork.CLAZZ_WORK_SUBMISSION_TYPE_QUIZ,
                MessageID.quiz),
    }

    class SubmissionOptionsMessageIdOption(day: SubmissionOptions, context: Any)
        : MessageIdOption(day.messageId, context, day.optionVal)

    override val persistenceMode: PersistenceMode
        get() = PersistenceMode.DB


    private val contentJoinEditHelper = DefaultOneToManyJoinEditHelper(
            ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer::contentEntryUid,
            "state_ContentEntryWithMetrics_list",
            ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer.serializer().list,
            ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer.serializer().list,
            this, ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer::class) { contentEntryUid = it }

    fun handleAddOrEditContent(entityClass: ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer) {
        contentJoinEditHelper.onEditResult(entityClass)
    }

    fun handleRemoveContent(entityClass: ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer) {
        contentJoinEditHelper.onDeactivateEntity(entityClass)
    }

    private val questionAndOptionsEditHelper =
            DefaultOneToManyJoinEditHelper<ClazzWorkQuestionAndOptions>(
            {it.clazzWorkQuestion.clazzWorkQuestionUid},
            "state_ClazzWorkQuestionAndOption_list",
                    ClazzWorkQuestionAndOptions.serializer().list,
            ClazzWorkQuestionAndOptions.serializer().list, this,
                    ClazzWorkQuestionAndOptions::class)
    { clazzWorkQuestion.clazzWorkQuestionUid = it }

    fun handleAddOrEditClazzQuestionAndOptions(entityClass: ClazzWorkQuestionAndOptions) {
        questionAndOptionsEditHelper.onEditResult(entityClass)
    }

    fun handleRemoveQuestionAndOptions(entityClass: ClazzWorkQuestionAndOptions) {
        questionAndOptionsEditHelper.onDeactivateEntity(entityClass)
    }

    override fun onCreate(savedState: Map<String, String>?) {
        super.onCreate(savedState)

        view.clazzWorkQuizQuestionsAndOptions = questionAndOptionsEditHelper.liveList
        view.clazzWorkContent = contentJoinEditHelper.liveList
        view.submissionTypeOptions = SubmissionOptions.values().map{SubmissionOptionsMessageIdOption(it, context)}
    }

    override suspend fun onLoadEntityFromDb(db: UmAppDatabase): ClazzWork? {
        val entityUid = arguments[ARG_ENTITY_UID]?.toLong() ?: 0L

        val clazzWork = withTimeoutOrNull(2000){
            db.clazzWorkDao.findByUidAsync(entityUid)
        }?:ClazzWork()

        val clazzWithSchool = withTimeoutOrNull(2000){
            db.clazzDao.getClazzWithSchool(clazzWork.clazzWorkClazzUid)
        }?: ClazzWithSchool()

        view.timeZone = clazzWithSchool.effectiveTimeZone()

        val loggedInPersonUid = accountManager.activeAccount.personUid

        val contentList = withTimeoutOrNull(2000) {
            db.clazzWorkContentJoinDao.findAllContentByClazzWorkUidAsync(
                    clazzWork.clazzWorkUid, loggedInPersonUid
            )
         }?: listOf()

        contentJoinEditHelper.liveList.sendValue(contentList)

        val questionAndOptions: List<ClazzWorkQuestionAndOptionRow> =
                withTimeoutOrNull(2000) {
            db.clazzWorkQuestionDao.findAllActiveQuestionsWithOptionsInClazzWorkAsList(entityUid)
        }?: listOf()

        val questionsWithOptionsList: List<ClazzWorkQuestionAndOptions> =
                questionAndOptions.groupBy { it.clazzWorkQuestion }.entries
                        .map { ClazzWorkQuestionAndOptions(
                            it.key?: ClazzWorkQuestion(),
                            it.value.map { it.clazzWorkQuestionOption?: ClazzWorkQuestionOption() },
                                listOf()) }

        questionAndOptionsEditHelper.liveList.sendValue(questionsWithOptionsList)

        return clazzWork
    }

    override fun onLoadFromJson(bundle: Map<String, String>): ClazzWork? {
        super.onLoadFromJson(bundle)

        val entityJsonStr = bundle[ARG_ENTITY_JSON]
        var editEntity: ClazzWork? = null
        if(entityJsonStr != null) {
            editEntity = safeParse(di, ClazzWork.serializer(), entityJsonStr)
        }else {
            editEntity = ClazzWork()
        }

        GlobalScope.launch {
            val clazzWithSchool = withTimeoutOrNull(2000) {
                db.clazzDao.getClazzWithSchool(editEntity.clazzWorkClazzUid)
            } ?: ClazzWithSchool()

            view.timeZone = clazzWithSchool.effectiveTimeZone()
        }

        view.clazzWorkQuizQuestionsAndOptions = questionAndOptionsEditHelper.liveList
        view.submissionTypeOptions = SubmissionOptions.values().map{SubmissionOptionsMessageIdOption(it, context)}

        questionAndOptionsEditHelper.onLoadFromJsonSavedState(bundle)

        return editEntity
    }

    override fun onSaveInstanceState(savedState: MutableMap<String, String>) {
        super.onSaveInstanceState(savedState)
        val entityVal = entity
        savedState.putEntityAsJson(ARG_ENTITY_JSON, null,
                entityVal)
    }

    override fun handleClickSave(entity: ClazzWork) {

        GlobalScope.launch(doorMainDispatcher()) {
            if(entity.clazzWorkUid == 0L) {
                entity.clazzWorkUid = repo.clazzWorkDao.insertAsync(entity)
            }else {
                repo.clazzWorkDao.updateAsync(entity)
            }

            val contentToInsert = contentJoinEditHelper.entitiesToInsert
            val contentToDelete = contentJoinEditHelper.primaryKeysToDeactivate

            repo.clazzWorkContentJoinDao.insertListAsync(contentToInsert.map {
                ClazzWorkContentJoin().apply {
                    clazzWorkContentJoinContentUid = it.contentEntryUid
                    clazzWorkContentJoinClazzWorkUid = entity?.clazzWorkUid?:0L
                    clazzWorkContentJoinDateAdded = systemTimeInMillis()
                }
            })

            repo.clazzWorkContentJoinDao.deactivateByUids(contentToDelete)


            val eti : List<ClazzWorkQuestionAndOptions> =
                    questionAndOptionsEditHelper.entitiesToInsert
            val etu : List<ClazzWorkQuestionAndOptions> =
                    questionAndOptionsEditHelper.entitiesToUpdate
            etu.forEach {
                val questionUid = it.clazzWorkQuestion.clazzWorkQuestionUid
                it.clazzWorkQuestion.clazzWorkQuestionClazzWorkUid = entity.clazzWorkUid
                it.options.forEach {
                    it.clazzWorkQuestionOptionActive = true
                    it.clazzWorkQuestionOptionQuestionUid = questionUid

                }
            }
            eti.forEach {
                it.clazzWorkQuestion.clazzWorkQuestionClazzWorkUid = entity.clazzWorkUid
                it.clazzWorkQuestion.clazzWorkQuestionUid = 0L
                val questionUid = repo.clazzWorkQuestionDao.insertAsync(it.clazzWorkQuestion)
                it.clazzWorkQuestion.clazzWorkQuestionUid = questionUid
                it.options.forEach {
                    it.clazzWorkQuestionOptionActive = true
                    it.clazzWorkQuestionOptionQuestionUid = questionUid
                }
            }

            repo.clazzWorkQuestionDao.updateListAsync(etu.map { it.clazzWorkQuestion })

            val allQuestions: List<ClazzWorkQuestionAndOptions> = (eti + etu)
            val allOptions = allQuestions.flatMap { it.options }
            val splitList = allOptions.partition { it.clazzWorkQuestionOptionUid == 0L }
            repo.clazzWorkQuestionOptionDao.insertListAsync(splitList.first)
            repo.clazzWorkQuestionOptionDao.updateListAsync(splitList.second)

            val deactivateOptions = allQuestions.flatMap { it.optionsToDeactivate }
            repo.clazzWorkQuestionOptionDao.deactivateByUids(deactivateOptions)

            repo.clazzWorkQuestionDao.deactivateByUids(questionAndOptionsEditHelper.primaryKeysToDeactivate)

            onFinish(ClazzWorkDetailView.VIEW_NAME, entity.clazzWorkUid, entity)
        }
    }

    companion object {
    }

}