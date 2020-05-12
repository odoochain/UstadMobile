package com.ustadmobile.core.util

import com.ustadmobile.core.controller.UstadEditPresenter
import com.ustadmobile.core.db.dao.OneToManyJoinDao
import com.ustadmobile.door.DoorMutableLiveData
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

/**
 * This class is designed to help manager a one to many join in edit mode. E.g. Clazz has a 1:n
 * join with Schedule. The editing of that entity is done in memory and passed to/from presenters
 * using JSON.
 *
 * At the end of editing mode newly created entities must be joined and inserted, changed entities
 * must be updated and some entities will need to be deactivated (e.g. the active field is set to
 * false).
 */
open class OneToManyJoinEditHelper<T, K>(val pkGetter: (T) -> K,
                                         val serializationKey: String,
                                         val serializationStrategy: SerializationStrategy<List<T>>? = null,
                                         val deserializationStrategy: DeserializationStrategy<List<T>>? = null,
                                         val newPk: K,
                                         editPresenter: UstadEditPresenter<*, *>?,
                                    val pkSetter: T.(K) -> Unit,
    open protected val fakePkGenerator: () -> K): UstadEditPresenter.JsonLoadListener  {

    val liveList: DoorMutableLiveData<List<T>> = DoorMutableLiveData(listOf())

    private val pksToInsert = mutableListOf<K>()

    private val pksToDeactivate = mutableListOf<K>()

    init {
        editPresenter?.addJsonLoadListener(this)
    }

    fun onEditResult(entity: T) {
        val pk = pkGetter(entity)
        if(pk as? Long == 0L || pk as? Int == 0){
            val newFakePk = fakePkGenerator()
            pkSetter(entity, newFakePk)
            pksToInsert += newFakePk

            val listVal = liveList.getValue() ?: return
            val newList = listVal + entity
            liveList.sendValue(newList)
        }else {
            val newList = liveList.getValue()?.toMutableList() ?: return
            val editedPk = pkGetter(entity)
            val entityIndex = newList.indexOfFirst { pkGetter(it) == editedPk }
            newList[entityIndex] = entity
            liveList.sendValue(newList)
        }
    }

    fun onDeactivateEntity(entity: T) {
        val listVal = liveList.getValue()?.toMutableList() ?: return
        val pkToRemove = pkGetter(entity)
        liveList.sendValue(listVal.filter { pkGetter(it) != pkToRemove} )
        pksToDeactivate += pkToRemove
    }

    val entitiesToInsert: List<T>
        get() {
            val listVal = liveList.getValue() ?: return listOf()
            return listVal.filter {pkGetter(it) in pksToInsert}
        }

    //TODO: Track which entities have been edited and return only those (e.g. don't update those that weren't actually changed)
    val entitiesToUpdate: List<T>
        get() = liveList.getValue()?.filter { pkGetter(it) !in pksToInsert } ?: listOf()

    val entitiesToDeactivate: List<T>
        get() = liveList.getValue()?.filter { pkGetter(it) in pksToDeactivate } ?: listOf()

    override fun onSaveState(outState: MutableMap<String, String>) {
        val listVal = liveList.getValue() ?: return
        val serializer = serializationStrategy ?: return
        outState[serializationKey] = Json.stringify(serializationStrategy, listVal)
    }

    override fun onLoadFromJsonSavedState(savedState: Map<String, String>?) {
        val listJsonStr = savedState?.get(serializationKey) ?: return
        val deserializer = deserializationStrategy ?: return
        val listVal = Json.parse(deserializer, listJsonStr)
        liveList.setVal(listVal)
    }

    /**
     * Commits the results of the editing to the database
     */
    open suspend fun commitToDatabase(dao: OneToManyJoinDao<T>, fkSetter: (T) -> Unit) {
        dao.insertListAsync(entitiesToInsert.also { it.forEach {
            fkSetter(it)
            pkSetter(it, newPk)
        }  })
        dao.updateListAsync(entitiesToUpdate.also { it.forEach(fkSetter) })
    }

}