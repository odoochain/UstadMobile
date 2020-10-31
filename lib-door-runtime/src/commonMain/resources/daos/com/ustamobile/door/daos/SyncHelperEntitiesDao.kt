package com.ustadmobile.door.daos

import androidx.room.Dao
import androidx.room.Query
import com.ustadmobile.door.entities.TableSyncStatus
import com.ustadmobile.door.entities.UpdateNotification

/**
 * This DAO contains various queries that are needed for sync related code to work. They are used
 * by other parts of lib-door-runtime and also by generated code.
 *
 * This DAO source is kept here because of a compiler bug. Ideally we would have simply put this
 * in the normal source in lib-door-runtime alongside the entities etc. Unfortunately the compiler,
 * even if given explicit arguments otherwise, will not preserve the name of function parameters.
 * This leads to the queries failing to compile.
 */
@Dao
abstract class SyncHelperEntitiesDao : ISyncHelperEntitiesDao {

    /**
     * This will be implemented by generated code to run the query. It will find a list of all
     * pending UpdateNotification entities for the given deviceId (e.g. used to find the backlog
     * of notifications when a client subscribes to events).
     */
    @Query("SELECT * FROM UpdateNotification WHERE pnDeviceId = :deviceId")
    override abstract suspend fun findPendingUpdateNotifications(deviceId: Int): List<UpdateNotification>

    /**
     * This will delete the matching UpdateNotification. It should be called after an update notification
     * has been delivered to the client (via the client making an http request answered by
     * lib-door-runtime RespondUpdateNotifications.respondUpdateNotificationReceived).
     *
     * Note this is not done using the actual notification uid because this is not known when
     * the server sends it live
     *
     * @param deviceId The deviceid as per the UpdateNotification
     * @param tableId The tableId as per the UpdateNotification
     * @param lastModTimestamp The pnTimestamp as per the UpdateNotification
     */
    @Query("DELETE FROM UpdateNotification WHERE pnDeviceId = :deviceId AND pnTableId = :tableId AND pnTimestamp = :lastModTimestamp")
    override abstract suspend fun deleteUpdateNotification(deviceId: Int, tableId: Int, lastModTimestamp: Long)

    /**
     * This will be implemented by generated code to run the query. It will find a list of any
     * tableIds that have pending ChangeLog items that should be sent to dispatchUpdateNotifications.
     * This is used on startup to find any changes that happen when ChangeLogMonitor was not running.
     *
     * @return A list of tableIds for which there are pending ChangeLogs
     */
    @Query("SELECT DISTINCT chTableId FROM ChangeLog WHERE CAST(dispatched AS INTEGER) = 0")
    override abstract suspend fun findTablesWithPendingChangeLogs(): List<Int>


    /**
     * Find a list of tables that need to be sync'd (e.g. those that have changed more recently than
     * a sync has been completed)
     */
    @Query("SELECT TableSyncStatus.* FROM TableSyncStatus WHERE tsLastChanged > tsLastSynced")
    override abstract fun findTablesToSync(): List<TableSyncStatus>

    /**
     * Mark the given table id as having been changed at the specified time. This will be used by
     * the ClientSyncManager to determine which tables need to be synced.
     */
    @Query("UPDATE TableSyncStatus SET tsLastChanged = :lastChanged WHERE tsTableId = :tableId")
    override abstract suspend fun updateTableSyncStatusLastChanged(tableId: Int, lastChanged: Long)

    /**
     * Mark the given table id as having been synced at the specified time. This will be used by
     * the ClientSyncManager to determine which tables need to be synced.
     */
    @Query(value = "UPDATE TableSyncStatus SET tsLastSynced = :lastSynced WHERE tsTableId = :tableId")
    override abstract suspend fun updateTableSyncStatusLastSynced(tableId: Int, lastSynced: Long)

    @Query("SELECT COALESCE((SELECT sspNextPrimaryKey FROM SqliteSyncablePrimaryKey WHERE sspTableId = :tableId), 1)")
    override abstract suspend fun selectNextSqliteSyncablePk(tableId: Int): Long

    @Query("UPDATE SqliteSyncablePrimaryKey SET sspNextPrimaryKey = sspNextPrimaryKey + :increment WHERE sspTableId = :tableId")
    override abstract suspend fun incrementNextSqliteSyncablePk(tableId: Int, increment: Int)

}