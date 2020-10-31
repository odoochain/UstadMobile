package com.ustadmobile.door

import com.ustadmobile.door.daos.ISyncHelperEntitiesDao

/**
 * Interface implemented by the repository for any database that implements SyncableDoorDatabase.
 */
interface DoorDatabaseSyncRepository: DoorDatabaseRepository {

    /**
     * Syncs the given tables. This is implemented by generated code
     *
     * @param tablesToSync a list of the table ids to sync. If null, then sync all tables.
     */
    suspend fun sync(tablesToSync: List<Int>?) : List<SyncResult>

    /**
     * Interface that provides access to the functions in SyncHelperEntitiesDao
     *
     * @see ISyncHelperEntitiesDao
     */
    val syncHelperEntitiesDao: ISyncHelperEntitiesDao

    suspend fun getAndIncrementSqlitePk(tableId: Int, increment: Int): Long

    /**
     * Listen for incoming sync changes. This can be used to trigger logic that is required to
     * update clients (e.g. when a permission change happens granting a client access to an entity
     * it didn't have access to before).
     */
    //fun <T : Any> addSyncListener(entityClass: KClass<T>, listener: SyncListener<T>)

    /**
     * This is to be called from generated code on the SyncDao's HTTP Endpoint (e.g.
     * DbNameSyncDao_KtorRoute). It is called after entities are received from an incoming sync. It
     * will trigger any SyncListeners that were added using addSyncListener
     *
     * @param entityClass
     */
    //fun <T: Any> handleSyncEntitiesReceived(entityClass: KClass<T>, entities: List<T>)

    /**
     * Do not call this on the main thread: it might run a query
     */
    val clientId: Int
}