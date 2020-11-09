package com.ustadmobile.core.db

import com.ustadmobile.door.DoorDbType
import com.ustadmobile.door.DoorMigration
import com.ustadmobile.door.DoorSqlDatabase
import com.ustadmobile.door.ext.dbType
import com.ustadmobile.door.util.systemTimeInMillis

/**
 * This class was generated 9/Nov by DbProcessorSyncPush. It migrates UmAppDatabase to the new
 * syncpush system.
 */
class UmAppDatabase_SyncPushMigration : DoorMigration(42, 43) {
  override fun migrate(database: DoorSqlDatabase) {
    if(database.dbType() == DoorDbType.SQLITE) {
      database.execSQL("CREATE TABLE IF NOT EXISTS ChangeLog (  chTableId  INTEGER  NOT NULL , chEntityPk  INTEGER  NOT NULL , dispatched  INTEGER  NOT NULL , chTime  INTEGER  NOT NULL , changeLogUid  INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL )")
      database.execSQL("CREATE TABLE IF NOT EXISTS SqliteChangeSeqNums (  sCsnTableId  INTEGER  PRIMARY KEY  NOT NULL , sCsnNextLocal  INTEGER  NOT NULL , sCsnNextPrimary  INTEGER  NOT NULL )")
      database.execSQL("""
      |CREATE 
      | INDEX index_SqliteChangeSeqNums_sCsnNextLocal 
      |ON SqliteChangeSeqNums (sCsnNextLocal)
      """.trimMargin())
      database.execSQL("""
      |CREATE 
      | INDEX index_SqliteChangeSeqNums_sCsnNextPrimary 
      |ON SqliteChangeSeqNums (sCsnNextPrimary)
      """.trimMargin())
      database.execSQL("CREATE TABLE IF NOT EXISTS TableSyncStatus (  tsTableId  INTEGER  PRIMARY KEY  NOT NULL , tsLastChanged  INTEGER  NOT NULL , tsLastSynced  INTEGER  NOT NULL )")
      database.execSQL("CREATE TABLE IF NOT EXISTS UpdateNotification (  pnDeviceId  INTEGER  NOT NULL , pnTableId  INTEGER  NOT NULL , pnTimestamp  INTEGER  NOT NULL , pnUid  INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL )")
      database.execSQL("""
      |CREATE 
      |UNIQUE INDEX index_UpdateNotification_pnDeviceId_pnTableId 
      |ON UpdateNotification (pnDeviceId, pnTableId)
      """.trimMargin())
      database.execSQL("""
      |CREATE 
      | INDEX index_UpdateNotification_pnDeviceId_pnTimestamp 
      |ON UpdateNotification (pnDeviceId, pnTimestamp)
      """.trimMargin())
      database.execSQL("DROP TRIGGER IF EXISTS INS_14")
      database.execSQL("DROP TRIGGER IF EXISTS INS_14")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_14
      |AFTER INSERT ON ClazzLog
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzLogLCSN = 0)
      |BEGIN
      |    UPDATE ClazzLog
      |    SET clazzLogMSQN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 14)
      |    WHERE clazzLogUid = NEW.clazzLogUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 14;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_14
      |            AFTER INSERT ON ClazzLog
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzLogMSQN = 0)
      |            BEGIN
      |                UPDATE ClazzLog
      |                SET clazzLogMSQN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 14)
      |                WHERE clazzLogUid = NEW.clazzLogUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 14;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 14, NEW.clazzLogUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_14
      |AFTER UPDATE ON ClazzLog
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzLogLCSN == OLD.clazzLogLCSN OR
      |        NEW.clazzLogLCSN == 0))
      |BEGIN
      |    UPDATE ClazzLog
      |    SET clazzLogLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 14) 
      |    WHERE clazzLogUid = NEW.clazzLogUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 14;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_14
      |            AFTER UPDATE ON ClazzLog
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzLogMSQN == OLD.clazzLogMSQN OR
      |                    NEW.clazzLogMSQN == 0))
      |            BEGIN
      |                UPDATE ClazzLog
      |                SET clazzLogMSQN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 14)
      |                WHERE clazzLogUid = NEW.clazzLogUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 14;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 14, NEW.clazzLogUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(14, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(14, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzLog_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzLog_trk_clientId_epk_csn  ON ClazzLog_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzLog_trk_epk_clientId ON ClazzLog_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_15")
      database.execSQL("DROP TRIGGER IF EXISTS INS_15")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_15
      |AFTER INSERT ON ClazzLogAttendanceRecord
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzLogAttendanceRecordLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ClazzLogAttendanceRecord
      |    SET clazzLogAttendanceRecordMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 15)
      |    WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 15;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_15
      |            AFTER INSERT ON ClazzLogAttendanceRecord
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzLogAttendanceRecordMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ClazzLogAttendanceRecord
      |                SET clazzLogAttendanceRecordMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 15)
      |                WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 15;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 15, NEW.clazzLogAttendanceRecordUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_15
      |AFTER UPDATE ON ClazzLogAttendanceRecord
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzLogAttendanceRecordLocalChangeSeqNum == OLD.clazzLogAttendanceRecordLocalChangeSeqNum OR
      |        NEW.clazzLogAttendanceRecordLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ClazzLogAttendanceRecord
      |    SET clazzLogAttendanceRecordLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 15) 
      |    WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 15;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_15
      |            AFTER UPDATE ON ClazzLogAttendanceRecord
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzLogAttendanceRecordMasterChangeSeqNum == OLD.clazzLogAttendanceRecordMasterChangeSeqNum OR
      |                    NEW.clazzLogAttendanceRecordMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ClazzLogAttendanceRecord
      |                SET clazzLogAttendanceRecordMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 15)
      |                WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 15;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 15, NEW.clazzLogAttendanceRecordUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(15, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(15, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzLogAttendanceRecord_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzLogAttendanceRecord_trk_clientId_epk_csn  ON ClazzLogAttendanceRecord_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzLogAttendanceRecord_trk_epk_clientId ON ClazzLogAttendanceRecord_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_21")
      database.execSQL("DROP TRIGGER IF EXISTS INS_21")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_21
      |AFTER INSERT ON Schedule
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.scheduleLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE Schedule
      |    SET scheduleMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 21)
      |    WHERE scheduleUid = NEW.scheduleUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 21;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_21
      |            AFTER INSERT ON Schedule
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.scheduleMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE Schedule
      |                SET scheduleMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 21)
      |                WHERE scheduleUid = NEW.scheduleUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 21;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 21, NEW.scheduleUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_21
      |AFTER UPDATE ON Schedule
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.scheduleLocalChangeSeqNum == OLD.scheduleLocalChangeSeqNum OR
      |        NEW.scheduleLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE Schedule
      |    SET scheduleLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 21) 
      |    WHERE scheduleUid = NEW.scheduleUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 21;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_21
      |            AFTER UPDATE ON Schedule
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.scheduleMasterChangeSeqNum == OLD.scheduleMasterChangeSeqNum OR
      |                    NEW.scheduleMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE Schedule
      |                SET scheduleMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 21)
      |                WHERE scheduleUid = NEW.scheduleUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 21;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 21, NEW.scheduleUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(21, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(21, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Schedule_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Schedule_trk_clientId_epk_csn  ON Schedule_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Schedule_trk_epk_clientId ON Schedule_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_17")
      database.execSQL("DROP TRIGGER IF EXISTS INS_17")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_17
      |AFTER INSERT ON DateRange
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.dateRangeLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE DateRange
      |    SET dateRangeMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 17)
      |    WHERE dateRangeUid = NEW.dateRangeUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 17;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_17
      |            AFTER INSERT ON DateRange
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.dateRangeMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE DateRange
      |                SET dateRangeMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 17)
      |                WHERE dateRangeUid = NEW.dateRangeUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 17;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 17, NEW.dateRangeUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_17
      |AFTER UPDATE ON DateRange
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.dateRangeLocalChangeSeqNum == OLD.dateRangeLocalChangeSeqNum OR
      |        NEW.dateRangeLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE DateRange
      |    SET dateRangeLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 17) 
      |    WHERE dateRangeUid = NEW.dateRangeUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 17;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_17
      |            AFTER UPDATE ON DateRange
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.dateRangeMasterChangeSeqNum == OLD.dateRangeMasterChangeSeqNum OR
      |                    NEW.dateRangeMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE DateRange
      |                SET dateRangeMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 17)
      |                WHERE dateRangeUid = NEW.dateRangeUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 17;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 17, NEW.dateRangeUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(17, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(17, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_DateRange_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_DateRange_trk_clientId_epk_csn  ON DateRange_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_DateRange_trk_epk_clientId ON DateRange_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_28")
      database.execSQL("DROP TRIGGER IF EXISTS INS_28")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_28
      |AFTER INSERT ON HolidayCalendar
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.umCalendarLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE HolidayCalendar
      |    SET umCalendarMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 28)
      |    WHERE umCalendarUid = NEW.umCalendarUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 28;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_28
      |            AFTER INSERT ON HolidayCalendar
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.umCalendarMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE HolidayCalendar
      |                SET umCalendarMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 28)
      |                WHERE umCalendarUid = NEW.umCalendarUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 28;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 28, NEW.umCalendarUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_28
      |AFTER UPDATE ON HolidayCalendar
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.umCalendarLocalChangeSeqNum == OLD.umCalendarLocalChangeSeqNum OR
      |        NEW.umCalendarLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE HolidayCalendar
      |    SET umCalendarLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 28) 
      |    WHERE umCalendarUid = NEW.umCalendarUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 28;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_28
      |            AFTER UPDATE ON HolidayCalendar
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.umCalendarMasterChangeSeqNum == OLD.umCalendarMasterChangeSeqNum OR
      |                    NEW.umCalendarMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE HolidayCalendar
      |                SET umCalendarMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 28)
      |                WHERE umCalendarUid = NEW.umCalendarUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 28;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 28, NEW.umCalendarUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(28, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(28, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_HolidayCalendar_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_HolidayCalendar_trk_clientId_epk_csn  ON HolidayCalendar_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_HolidayCalendar_trk_epk_clientId ON HolidayCalendar_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_99")
      database.execSQL("DROP TRIGGER IF EXISTS INS_99")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_99
      |AFTER INSERT ON Holiday
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.holLocalCsn = 0)
      |BEGIN
      |    UPDATE Holiday
      |    SET holMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 99)
      |    WHERE holUid = NEW.holUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 99;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_99
      |            AFTER INSERT ON Holiday
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.holMasterCsn = 0)
      |            BEGIN
      |                UPDATE Holiday
      |                SET holMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 99)
      |                WHERE holUid = NEW.holUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 99;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 99, NEW.holUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_99
      |AFTER UPDATE ON Holiday
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.holLocalCsn == OLD.holLocalCsn OR
      |        NEW.holLocalCsn == 0))
      |BEGIN
      |    UPDATE Holiday
      |    SET holLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 99) 
      |    WHERE holUid = NEW.holUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 99;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_99
      |            AFTER UPDATE ON Holiday
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.holMasterCsn == OLD.holMasterCsn OR
      |                    NEW.holMasterCsn == 0))
      |            BEGIN
      |                UPDATE Holiday
      |                SET holMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 99)
      |                WHERE holUid = NEW.holUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 99;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 99, NEW.holUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(99, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(99, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Holiday_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Holiday_trk_clientId_epk_csn  ON Holiday_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Holiday_trk_epk_clientId ON Holiday_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_173")
      database.execSQL("DROP TRIGGER IF EXISTS INS_173")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_173
      |AFTER INSERT ON ScheduledCheck
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.scheduledCheckLocalCsn = 0)
      |BEGIN
      |    UPDATE ScheduledCheck
      |    SET scheduledCheckMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 173)
      |    WHERE scheduledCheckUid = NEW.scheduledCheckUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 173;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_173
      |            AFTER INSERT ON ScheduledCheck
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.scheduledCheckMasterCsn = 0)
      |            BEGIN
      |                UPDATE ScheduledCheck
      |                SET scheduledCheckMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 173)
      |                WHERE scheduledCheckUid = NEW.scheduledCheckUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 173;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 173, NEW.scheduledCheckUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_173
      |AFTER UPDATE ON ScheduledCheck
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.scheduledCheckLocalCsn == OLD.scheduledCheckLocalCsn OR
      |        NEW.scheduledCheckLocalCsn == 0))
      |BEGIN
      |    UPDATE ScheduledCheck
      |    SET scheduledCheckLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 173) 
      |    WHERE scheduledCheckUid = NEW.scheduledCheckUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 173;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_173
      |            AFTER UPDATE ON ScheduledCheck
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.scheduledCheckMasterCsn == OLD.scheduledCheckMasterCsn OR
      |                    NEW.scheduledCheckMasterCsn == 0))
      |            BEGIN
      |                UPDATE ScheduledCheck
      |                SET scheduledCheckMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 173)
      |                WHERE scheduledCheckUid = NEW.scheduledCheckUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 173;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 173, NEW.scheduledCheckUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(173, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(173, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ScheduledCheck_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ScheduledCheck_trk_clientId_epk_csn  ON ScheduledCheck_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ScheduledCheck_trk_epk_clientId ON ScheduledCheck_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_53")
      database.execSQL("DROP TRIGGER IF EXISTS INS_53")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_53
      |AFTER INSERT ON AuditLog
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.auditLogLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE AuditLog
      |    SET auditLogMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 53)
      |    WHERE auditLogUid = NEW.auditLogUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 53;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_53
      |            AFTER INSERT ON AuditLog
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.auditLogMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE AuditLog
      |                SET auditLogMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 53)
      |                WHERE auditLogUid = NEW.auditLogUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 53;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 53, NEW.auditLogUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_53
      |AFTER UPDATE ON AuditLog
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.auditLogLocalChangeSeqNum == OLD.auditLogLocalChangeSeqNum OR
      |        NEW.auditLogLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE AuditLog
      |    SET auditLogLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 53) 
      |    WHERE auditLogUid = NEW.auditLogUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 53;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_53
      |            AFTER UPDATE ON AuditLog
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.auditLogMasterChangeSeqNum == OLD.auditLogMasterChangeSeqNum OR
      |                    NEW.auditLogMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE AuditLog
      |                SET auditLogMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 53)
      |                WHERE auditLogUid = NEW.auditLogUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 53;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 53, NEW.auditLogUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(53, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(53, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_AuditLog_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_AuditLog_trk_clientId_epk_csn  ON AuditLog_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_AuditLog_trk_epk_clientId ON AuditLog_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_56")
      database.execSQL("DROP TRIGGER IF EXISTS INS_56")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_56
      |AFTER INSERT ON CustomField
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.customFieldLCSN = 0)
      |BEGIN
      |    UPDATE CustomField
      |    SET customFieldMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 56)
      |    WHERE customFieldUid = NEW.customFieldUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 56;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_56
      |            AFTER INSERT ON CustomField
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.customFieldMCSN = 0)
      |            BEGIN
      |                UPDATE CustomField
      |                SET customFieldMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 56)
      |                WHERE customFieldUid = NEW.customFieldUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 56;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 56, NEW.customFieldUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_56
      |AFTER UPDATE ON CustomField
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.customFieldLCSN == OLD.customFieldLCSN OR
      |        NEW.customFieldLCSN == 0))
      |BEGIN
      |    UPDATE CustomField
      |    SET customFieldLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 56) 
      |    WHERE customFieldUid = NEW.customFieldUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 56;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_56
      |            AFTER UPDATE ON CustomField
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.customFieldMCSN == OLD.customFieldMCSN OR
      |                    NEW.customFieldMCSN == 0))
      |            BEGIN
      |                UPDATE CustomField
      |                SET customFieldMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 56)
      |                WHERE customFieldUid = NEW.customFieldUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 56;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 56, NEW.customFieldUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(56, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(56, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_CustomField_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_CustomField_trk_clientId_epk_csn  ON CustomField_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_CustomField_trk_epk_clientId ON CustomField_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_57")
      database.execSQL("DROP TRIGGER IF EXISTS INS_57")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_57
      |AFTER INSERT ON CustomFieldValue
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.customFieldValueLCSN = 0)
      |BEGIN
      |    UPDATE CustomFieldValue
      |    SET customFieldValueMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 57)
      |    WHERE customFieldValueUid = NEW.customFieldValueUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 57;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_57
      |            AFTER INSERT ON CustomFieldValue
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.customFieldValueMCSN = 0)
      |            BEGIN
      |                UPDATE CustomFieldValue
      |                SET customFieldValueMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 57)
      |                WHERE customFieldValueUid = NEW.customFieldValueUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 57;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 57, NEW.customFieldValueUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_57
      |AFTER UPDATE ON CustomFieldValue
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.customFieldValueLCSN == OLD.customFieldValueLCSN OR
      |        NEW.customFieldValueLCSN == 0))
      |BEGIN
      |    UPDATE CustomFieldValue
      |    SET customFieldValueLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 57) 
      |    WHERE customFieldValueUid = NEW.customFieldValueUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 57;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_57
      |            AFTER UPDATE ON CustomFieldValue
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.customFieldValueMCSN == OLD.customFieldValueMCSN OR
      |                    NEW.customFieldValueMCSN == 0))
      |            BEGIN
      |                UPDATE CustomFieldValue
      |                SET customFieldValueMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 57)
      |                WHERE customFieldValueUid = NEW.customFieldValueUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 57;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 57, NEW.customFieldValueUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(57, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(57, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_CustomFieldValue_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_CustomFieldValue_trk_clientId_epk_csn  ON CustomFieldValue_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_CustomFieldValue_trk_epk_clientId ON CustomFieldValue_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_55")
      database.execSQL("DROP TRIGGER IF EXISTS INS_55")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_55
      |AFTER INSERT ON CustomFieldValueOption
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.customFieldValueOptionLCSN = 0)
      |BEGIN
      |    UPDATE CustomFieldValueOption
      |    SET customFieldValueOptionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 55)
      |    WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 55;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_55
      |            AFTER INSERT ON CustomFieldValueOption
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.customFieldValueOptionMCSN = 0)
      |            BEGIN
      |                UPDATE CustomFieldValueOption
      |                SET customFieldValueOptionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 55)
      |                WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 55;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 55, NEW.customFieldValueOptionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_55
      |AFTER UPDATE ON CustomFieldValueOption
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.customFieldValueOptionLCSN == OLD.customFieldValueOptionLCSN OR
      |        NEW.customFieldValueOptionLCSN == 0))
      |BEGIN
      |    UPDATE CustomFieldValueOption
      |    SET customFieldValueOptionLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 55) 
      |    WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 55;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_55
      |            AFTER UPDATE ON CustomFieldValueOption
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.customFieldValueOptionMCSN == OLD.customFieldValueOptionMCSN OR
      |                    NEW.customFieldValueOptionMCSN == 0))
      |            BEGIN
      |                UPDATE CustomFieldValueOption
      |                SET customFieldValueOptionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 55)
      |                WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 55;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 55, NEW.customFieldValueOptionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(55, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(55, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_CustomFieldValueOption_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_CustomFieldValueOption_trk_clientId_epk_csn  ON CustomFieldValueOption_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_CustomFieldValueOption_trk_epk_clientId ON CustomFieldValueOption_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_9")
      database.execSQL("DROP TRIGGER IF EXISTS INS_9")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_9
      |AFTER INSERT ON Person
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.personLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE Person
      |    SET personMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 9)
      |    WHERE personUid = NEW.personUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 9;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_9
      |            AFTER INSERT ON Person
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.personMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE Person
      |                SET personMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 9)
      |                WHERE personUid = NEW.personUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 9;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 9, NEW.personUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_9
      |AFTER UPDATE ON Person
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.personLocalChangeSeqNum == OLD.personLocalChangeSeqNum OR
      |        NEW.personLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE Person
      |    SET personLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 9) 
      |    WHERE personUid = NEW.personUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 9;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_9
      |            AFTER UPDATE ON Person
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.personMasterChangeSeqNum == OLD.personMasterChangeSeqNum OR
      |                    NEW.personMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE Person
      |                SET personMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 9)
      |                WHERE personUid = NEW.personUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 9;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 9, NEW.personUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(9, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(9, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Person_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Person_trk_clientId_epk_csn  ON Person_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Person_trk_epk_clientId ON Person_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_6")
      database.execSQL("DROP TRIGGER IF EXISTS INS_6")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_6
      |AFTER INSERT ON Clazz
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE Clazz
      |    SET clazzMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 6)
      |    WHERE clazzUid = NEW.clazzUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 6;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_6
      |            AFTER INSERT ON Clazz
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE Clazz
      |                SET clazzMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 6)
      |                WHERE clazzUid = NEW.clazzUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 6;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 6, NEW.clazzUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_6
      |AFTER UPDATE ON Clazz
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzLocalChangeSeqNum == OLD.clazzLocalChangeSeqNum OR
      |        NEW.clazzLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE Clazz
      |    SET clazzLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 6) 
      |    WHERE clazzUid = NEW.clazzUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 6;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_6
      |            AFTER UPDATE ON Clazz
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzMasterChangeSeqNum == OLD.clazzMasterChangeSeqNum OR
      |                    NEW.clazzMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE Clazz
      |                SET clazzMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 6)
      |                WHERE clazzUid = NEW.clazzUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 6;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 6, NEW.clazzUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(6, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(6, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Clazz_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Clazz_trk_clientId_epk_csn  ON Clazz_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Clazz_trk_epk_clientId ON Clazz_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_65")
      database.execSQL("DROP TRIGGER IF EXISTS INS_65")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_65
      |AFTER INSERT ON ClazzMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzMemberLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ClazzMember
      |    SET clazzMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 65)
      |    WHERE clazzMemberUid = NEW.clazzMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 65;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_65
      |            AFTER INSERT ON ClazzMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzMemberMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ClazzMember
      |                SET clazzMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 65)
      |                WHERE clazzMemberUid = NEW.clazzMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 65;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 65, NEW.clazzMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_65
      |AFTER UPDATE ON ClazzMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzMemberLocalChangeSeqNum == OLD.clazzMemberLocalChangeSeqNum OR
      |        NEW.clazzMemberLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ClazzMember
      |    SET clazzMemberLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 65) 
      |    WHERE clazzMemberUid = NEW.clazzMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 65;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_65
      |            AFTER UPDATE ON ClazzMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzMemberMasterChangeSeqNum == OLD.clazzMemberMasterChangeSeqNum OR
      |                    NEW.clazzMemberMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ClazzMember
      |                SET clazzMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 65)
      |                WHERE clazzMemberUid = NEW.clazzMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 65;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 65, NEW.clazzMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(65, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(65, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzMember_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzMember_trk_clientId_epk_csn  ON ClazzMember_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzMember_trk_epk_clientId ON ClazzMember_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_178")
      database.execSQL("DROP TRIGGER IF EXISTS INS_178")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_178
      |AFTER INSERT ON PersonCustomFieldValue
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.personCustomFieldValueLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE PersonCustomFieldValue
      |    SET personCustomFieldValueMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 178)
      |    WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 178;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_178
      |            AFTER INSERT ON PersonCustomFieldValue
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.personCustomFieldValueMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE PersonCustomFieldValue
      |                SET personCustomFieldValueMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 178)
      |                WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 178;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 178, NEW.personCustomFieldValueUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_178
      |AFTER UPDATE ON PersonCustomFieldValue
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.personCustomFieldValueLocalChangeSeqNum == OLD.personCustomFieldValueLocalChangeSeqNum OR
      |        NEW.personCustomFieldValueLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE PersonCustomFieldValue
      |    SET personCustomFieldValueLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 178) 
      |    WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 178;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_178
      |            AFTER UPDATE ON PersonCustomFieldValue
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.personCustomFieldValueMasterChangeSeqNum == OLD.personCustomFieldValueMasterChangeSeqNum OR
      |                    NEW.personCustomFieldValueMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE PersonCustomFieldValue
      |                SET personCustomFieldValueMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 178)
      |                WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 178;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 178, NEW.personCustomFieldValueUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(178, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(178, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_PersonCustomFieldValue_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_PersonCustomFieldValue_trk_clientId_epk_csn  ON PersonCustomFieldValue_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_PersonCustomFieldValue_trk_epk_clientId ON PersonCustomFieldValue_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_42")
      database.execSQL("DROP TRIGGER IF EXISTS INS_42")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_42
      |AFTER INSERT ON ContentEntry
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.contentEntryLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentEntry
      |    SET contentEntryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 42)
      |    WHERE contentEntryUid = NEW.contentEntryUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 42;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_42
      |            AFTER INSERT ON ContentEntry
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.contentEntryMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentEntry
      |                SET contentEntryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 42)
      |                WHERE contentEntryUid = NEW.contentEntryUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 42;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 42, NEW.contentEntryUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_42
      |AFTER UPDATE ON ContentEntry
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.contentEntryLocalChangeSeqNum == OLD.contentEntryLocalChangeSeqNum OR
      |        NEW.contentEntryLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentEntry
      |    SET contentEntryLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 42) 
      |    WHERE contentEntryUid = NEW.contentEntryUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 42;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_42
      |            AFTER UPDATE ON ContentEntry
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.contentEntryMasterChangeSeqNum == OLD.contentEntryMasterChangeSeqNum OR
      |                    NEW.contentEntryMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentEntry
      |                SET contentEntryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 42)
      |                WHERE contentEntryUid = NEW.contentEntryUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 42;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 42, NEW.contentEntryUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(42, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(42, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentEntry_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentEntry_trk_clientId_epk_csn  ON ContentEntry_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentEntry_trk_epk_clientId ON ContentEntry_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_3")
      database.execSQL("DROP TRIGGER IF EXISTS INS_3")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_3
      |AFTER INSERT ON ContentEntryContentCategoryJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.ceccjLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentEntryContentCategoryJoin
      |    SET ceccjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 3)
      |    WHERE ceccjUid = NEW.ceccjUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 3;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_3
      |            AFTER INSERT ON ContentEntryContentCategoryJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.ceccjMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentEntryContentCategoryJoin
      |                SET ceccjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 3)
      |                WHERE ceccjUid = NEW.ceccjUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 3;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 3, NEW.ceccjUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_3
      |AFTER UPDATE ON ContentEntryContentCategoryJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.ceccjLocalChangeSeqNum == OLD.ceccjLocalChangeSeqNum OR
      |        NEW.ceccjLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentEntryContentCategoryJoin
      |    SET ceccjLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 3) 
      |    WHERE ceccjUid = NEW.ceccjUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 3;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_3
      |            AFTER UPDATE ON ContentEntryContentCategoryJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.ceccjMasterChangeSeqNum == OLD.ceccjMasterChangeSeqNum OR
      |                    NEW.ceccjMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentEntryContentCategoryJoin
      |                SET ceccjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 3)
      |                WHERE ceccjUid = NEW.ceccjUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 3;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 3, NEW.ceccjUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(3, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(3, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentEntryContentCategoryJoin_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentEntryContentCategoryJoin_trk_clientId_epk_csn  ON ContentEntryContentCategoryJoin_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentEntryContentCategoryJoin_trk_epk_clientId ON ContentEntryContentCategoryJoin_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_7")
      database.execSQL("DROP TRIGGER IF EXISTS INS_7")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_7
      |AFTER INSERT ON ContentEntryParentChildJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.cepcjLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentEntryParentChildJoin
      |    SET cepcjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 7)
      |    WHERE cepcjUid = NEW.cepcjUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 7;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_7
      |            AFTER INSERT ON ContentEntryParentChildJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.cepcjMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentEntryParentChildJoin
      |                SET cepcjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 7)
      |                WHERE cepcjUid = NEW.cepcjUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 7;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 7, NEW.cepcjUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_7
      |AFTER UPDATE ON ContentEntryParentChildJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.cepcjLocalChangeSeqNum == OLD.cepcjLocalChangeSeqNum OR
      |        NEW.cepcjLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentEntryParentChildJoin
      |    SET cepcjLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 7) 
      |    WHERE cepcjUid = NEW.cepcjUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 7;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_7
      |            AFTER UPDATE ON ContentEntryParentChildJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.cepcjMasterChangeSeqNum == OLD.cepcjMasterChangeSeqNum OR
      |                    NEW.cepcjMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentEntryParentChildJoin
      |                SET cepcjMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 7)
      |                WHERE cepcjUid = NEW.cepcjUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 7;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 7, NEW.cepcjUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(7, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(7, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentEntryParentChildJoin_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentEntryParentChildJoin_trk_clientId_epk_csn  ON ContentEntryParentChildJoin_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentEntryParentChildJoin_trk_epk_clientId ON ContentEntryParentChildJoin_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_8")
      database.execSQL("DROP TRIGGER IF EXISTS INS_8")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_8
      |AFTER INSERT ON ContentEntryRelatedEntryJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.cerejLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentEntryRelatedEntryJoin
      |    SET cerejMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 8)
      |    WHERE cerejUid = NEW.cerejUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 8;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_8
      |            AFTER INSERT ON ContentEntryRelatedEntryJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.cerejMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentEntryRelatedEntryJoin
      |                SET cerejMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 8)
      |                WHERE cerejUid = NEW.cerejUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 8;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 8, NEW.cerejUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_8
      |AFTER UPDATE ON ContentEntryRelatedEntryJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.cerejLocalChangeSeqNum == OLD.cerejLocalChangeSeqNum OR
      |        NEW.cerejLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentEntryRelatedEntryJoin
      |    SET cerejLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 8) 
      |    WHERE cerejUid = NEW.cerejUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 8;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_8
      |            AFTER UPDATE ON ContentEntryRelatedEntryJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.cerejMasterChangeSeqNum == OLD.cerejMasterChangeSeqNum OR
      |                    NEW.cerejMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentEntryRelatedEntryJoin
      |                SET cerejMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 8)
      |                WHERE cerejUid = NEW.cerejUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 8;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 8, NEW.cerejUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(8, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(8, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentEntryRelatedEntryJoin_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentEntryRelatedEntryJoin_trk_clientId_epk_csn  ON ContentEntryRelatedEntryJoin_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentEntryRelatedEntryJoin_trk_epk_clientId ON ContentEntryRelatedEntryJoin_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_2")
      database.execSQL("DROP TRIGGER IF EXISTS INS_2")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_2
      |AFTER INSERT ON ContentCategorySchema
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.contentCategorySchemaLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentCategorySchema
      |    SET contentCategorySchemaMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 2)
      |    WHERE contentCategorySchemaUid = NEW.contentCategorySchemaUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 2;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_2
      |            AFTER INSERT ON ContentCategorySchema
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.contentCategorySchemaMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentCategorySchema
      |                SET contentCategorySchemaMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 2)
      |                WHERE contentCategorySchemaUid = NEW.contentCategorySchemaUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 2;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 2, NEW.contentCategorySchemaUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_2
      |AFTER UPDATE ON ContentCategorySchema
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.contentCategorySchemaLocalChangeSeqNum == OLD.contentCategorySchemaLocalChangeSeqNum OR
      |        NEW.contentCategorySchemaLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentCategorySchema
      |    SET contentCategorySchemaLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 2) 
      |    WHERE contentCategorySchemaUid = NEW.contentCategorySchemaUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 2;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_2
      |            AFTER UPDATE ON ContentCategorySchema
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.contentCategorySchemaMasterChangeSeqNum == OLD.contentCategorySchemaMasterChangeSeqNum OR
      |                    NEW.contentCategorySchemaMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentCategorySchema
      |                SET contentCategorySchemaMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 2)
      |                WHERE contentCategorySchemaUid = NEW.contentCategorySchemaUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 2;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 2, NEW.contentCategorySchemaUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(2, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(2, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentCategorySchema_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentCategorySchema_trk_clientId_epk_csn  ON ContentCategorySchema_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentCategorySchema_trk_epk_clientId ON ContentCategorySchema_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_1")
      database.execSQL("DROP TRIGGER IF EXISTS INS_1")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_1
      |AFTER INSERT ON ContentCategory
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.contentCategoryLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentCategory
      |    SET contentCategoryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 1)
      |    WHERE contentCategoryUid = NEW.contentCategoryUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 1;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_1
      |            AFTER INSERT ON ContentCategory
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.contentCategoryMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentCategory
      |                SET contentCategoryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 1)
      |                WHERE contentCategoryUid = NEW.contentCategoryUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 1;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 1, NEW.contentCategoryUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_1
      |AFTER UPDATE ON ContentCategory
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.contentCategoryLocalChangeSeqNum == OLD.contentCategoryLocalChangeSeqNum OR
      |        NEW.contentCategoryLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentCategory
      |    SET contentCategoryLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 1) 
      |    WHERE contentCategoryUid = NEW.contentCategoryUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 1;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_1
      |            AFTER UPDATE ON ContentCategory
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.contentCategoryMasterChangeSeqNum == OLD.contentCategoryMasterChangeSeqNum OR
      |                    NEW.contentCategoryMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentCategory
      |                SET contentCategoryMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 1)
      |                WHERE contentCategoryUid = NEW.contentCategoryUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 1;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 1, NEW.contentCategoryUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(1, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(1, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentCategory_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentCategory_trk_clientId_epk_csn  ON ContentCategory_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentCategory_trk_epk_clientId ON ContentCategory_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_13")
      database.execSQL("DROP TRIGGER IF EXISTS INS_13")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_13
      |AFTER INSERT ON Language
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.langLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE Language
      |    SET langMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 13)
      |    WHERE langUid = NEW.langUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 13;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_13
      |            AFTER INSERT ON Language
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.langMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE Language
      |                SET langMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 13)
      |                WHERE langUid = NEW.langUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 13;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 13, NEW.langUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_13
      |AFTER UPDATE ON Language
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.langLocalChangeSeqNum == OLD.langLocalChangeSeqNum OR
      |        NEW.langLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE Language
      |    SET langLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 13) 
      |    WHERE langUid = NEW.langUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 13;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_13
      |            AFTER UPDATE ON Language
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.langMasterChangeSeqNum == OLD.langMasterChangeSeqNum OR
      |                    NEW.langMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE Language
      |                SET langMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 13)
      |                WHERE langUid = NEW.langUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 13;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 13, NEW.langUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(13, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(13, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Language_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Language_trk_clientId_epk_csn  ON Language_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Language_trk_epk_clientId ON Language_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_10")
      database.execSQL("DROP TRIGGER IF EXISTS INS_10")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_10
      |AFTER INSERT ON LanguageVariant
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.langVariantLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE LanguageVariant
      |    SET langVariantMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 10)
      |    WHERE langVariantUid = NEW.langVariantUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 10;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_10
      |            AFTER INSERT ON LanguageVariant
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.langVariantMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE LanguageVariant
      |                SET langVariantMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 10)
      |                WHERE langVariantUid = NEW.langVariantUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 10;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 10, NEW.langVariantUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_10
      |AFTER UPDATE ON LanguageVariant
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.langVariantLocalChangeSeqNum == OLD.langVariantLocalChangeSeqNum OR
      |        NEW.langVariantLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE LanguageVariant
      |    SET langVariantLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 10) 
      |    WHERE langVariantUid = NEW.langVariantUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 10;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_10
      |            AFTER UPDATE ON LanguageVariant
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.langVariantMasterChangeSeqNum == OLD.langVariantMasterChangeSeqNum OR
      |                    NEW.langVariantMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE LanguageVariant
      |                SET langVariantMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 10)
      |                WHERE langVariantUid = NEW.langVariantUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 10;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 10, NEW.langVariantUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(10, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(10, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_LanguageVariant_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_LanguageVariant_trk_clientId_epk_csn  ON LanguageVariant_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_LanguageVariant_trk_epk_clientId ON LanguageVariant_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_45")
      database.execSQL("DROP TRIGGER IF EXISTS INS_45")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_45
      |AFTER INSERT ON Role
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.roleLocalCsn = 0)
      |BEGIN
      |    UPDATE Role
      |    SET roleMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 45)
      |    WHERE roleUid = NEW.roleUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 45;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_45
      |            AFTER INSERT ON Role
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.roleMasterCsn = 0)
      |            BEGIN
      |                UPDATE Role
      |                SET roleMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 45)
      |                WHERE roleUid = NEW.roleUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 45;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 45, NEW.roleUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_45
      |AFTER UPDATE ON Role
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.roleLocalCsn == OLD.roleLocalCsn OR
      |        NEW.roleLocalCsn == 0))
      |BEGIN
      |    UPDATE Role
      |    SET roleLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 45) 
      |    WHERE roleUid = NEW.roleUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 45;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_45
      |            AFTER UPDATE ON Role
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.roleMasterCsn == OLD.roleMasterCsn OR
      |                    NEW.roleMasterCsn == 0))
      |            BEGIN
      |                UPDATE Role
      |                SET roleMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 45)
      |                WHERE roleUid = NEW.roleUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 45;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 45, NEW.roleUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(45, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(45, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Role_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Role_trk_clientId_epk_csn  ON Role_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Role_trk_epk_clientId ON Role_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_47")
      database.execSQL("DROP TRIGGER IF EXISTS INS_47")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_47
      |AFTER INSERT ON EntityRole
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.erLocalCsn = 0)
      |BEGIN
      |    UPDATE EntityRole
      |    SET erMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 47)
      |    WHERE erUid = NEW.erUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 47;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_47
      |            AFTER INSERT ON EntityRole
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.erMasterCsn = 0)
      |            BEGIN
      |                UPDATE EntityRole
      |                SET erMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 47)
      |                WHERE erUid = NEW.erUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 47;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 47, NEW.erUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_47
      |AFTER UPDATE ON EntityRole
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.erLocalCsn == OLD.erLocalCsn OR
      |        NEW.erLocalCsn == 0))
      |BEGIN
      |    UPDATE EntityRole
      |    SET erLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 47) 
      |    WHERE erUid = NEW.erUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 47;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_47
      |            AFTER UPDATE ON EntityRole
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.erMasterCsn == OLD.erMasterCsn OR
      |                    NEW.erMasterCsn == 0))
      |            BEGIN
      |                UPDATE EntityRole
      |                SET erMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 47)
      |                WHERE erUid = NEW.erUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 47;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 47, NEW.erUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(47, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(47, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_EntityRole_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_EntityRole_trk_clientId_epk_csn  ON EntityRole_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_EntityRole_trk_epk_clientId ON EntityRole_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_43")
      database.execSQL("DROP TRIGGER IF EXISTS INS_43")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_43
      |AFTER INSERT ON PersonGroup
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.groupLocalCsn = 0)
      |BEGIN
      |    UPDATE PersonGroup
      |    SET groupMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 43)
      |    WHERE groupUid = NEW.groupUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 43;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_43
      |            AFTER INSERT ON PersonGroup
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.groupMasterCsn = 0)
      |            BEGIN
      |                UPDATE PersonGroup
      |                SET groupMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 43)
      |                WHERE groupUid = NEW.groupUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 43;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 43, NEW.groupUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_43
      |AFTER UPDATE ON PersonGroup
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.groupLocalCsn == OLD.groupLocalCsn OR
      |        NEW.groupLocalCsn == 0))
      |BEGIN
      |    UPDATE PersonGroup
      |    SET groupLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 43) 
      |    WHERE groupUid = NEW.groupUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 43;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_43
      |            AFTER UPDATE ON PersonGroup
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.groupMasterCsn == OLD.groupMasterCsn OR
      |                    NEW.groupMasterCsn == 0))
      |            BEGIN
      |                UPDATE PersonGroup
      |                SET groupMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 43)
      |                WHERE groupUid = NEW.groupUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 43;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 43, NEW.groupUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(43, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(43, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_PersonGroup_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_PersonGroup_trk_clientId_epk_csn  ON PersonGroup_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_PersonGroup_trk_epk_clientId ON PersonGroup_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_44")
      database.execSQL("DROP TRIGGER IF EXISTS INS_44")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_44
      |AFTER INSERT ON PersonGroupMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.groupMemberLocalCsn = 0)
      |BEGIN
      |    UPDATE PersonGroupMember
      |    SET groupMemberMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 44)
      |    WHERE groupMemberUid = NEW.groupMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 44;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_44
      |            AFTER INSERT ON PersonGroupMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.groupMemberMasterCsn = 0)
      |            BEGIN
      |                UPDATE PersonGroupMember
      |                SET groupMemberMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 44)
      |                WHERE groupMemberUid = NEW.groupMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 44;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 44, NEW.groupMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_44
      |AFTER UPDATE ON PersonGroupMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.groupMemberLocalCsn == OLD.groupMemberLocalCsn OR
      |        NEW.groupMemberLocalCsn == 0))
      |BEGIN
      |    UPDATE PersonGroupMember
      |    SET groupMemberLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 44) 
      |    WHERE groupMemberUid = NEW.groupMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 44;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_44
      |            AFTER UPDATE ON PersonGroupMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.groupMemberMasterCsn == OLD.groupMemberMasterCsn OR
      |                    NEW.groupMemberMasterCsn == 0))
      |            BEGIN
      |                UPDATE PersonGroupMember
      |                SET groupMemberMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 44)
      |                WHERE groupMemberUid = NEW.groupMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 44;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 44, NEW.groupMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(44, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(44, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_PersonGroupMember_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_PersonGroupMember_trk_clientId_epk_csn  ON PersonGroupMember_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_PersonGroupMember_trk_epk_clientId ON PersonGroupMember_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_50")
      database.execSQL("DROP TRIGGER IF EXISTS INS_50")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_50
      |AFTER INSERT ON PersonPicture
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.personPictureLocalCsn = 0)
      |BEGIN
      |    UPDATE PersonPicture
      |    SET personPictureMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 50)
      |    WHERE personPictureUid = NEW.personPictureUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 50;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_50
      |            AFTER INSERT ON PersonPicture
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.personPictureMasterCsn = 0)
      |            BEGIN
      |                UPDATE PersonPicture
      |                SET personPictureMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 50)
      |                WHERE personPictureUid = NEW.personPictureUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 50;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 50, NEW.personPictureUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_50
      |AFTER UPDATE ON PersonPicture
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.personPictureLocalCsn == OLD.personPictureLocalCsn OR
      |        NEW.personPictureLocalCsn == 0))
      |BEGIN
      |    UPDATE PersonPicture
      |    SET personPictureLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 50) 
      |    WHERE personPictureUid = NEW.personPictureUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 50;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_50
      |            AFTER UPDATE ON PersonPicture
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.personPictureMasterCsn == OLD.personPictureMasterCsn OR
      |                    NEW.personPictureMasterCsn == 0))
      |            BEGIN
      |                UPDATE PersonPicture
      |                SET personPictureMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 50)
      |                WHERE personPictureUid = NEW.personPictureUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 50;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 50, NEW.personPictureUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(50, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(50, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_PersonPicture_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_PersonPicture_trk_clientId_epk_csn  ON PersonPicture_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_PersonPicture_trk_epk_clientId ON PersonPicture_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_51")
      database.execSQL("DROP TRIGGER IF EXISTS INS_51")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_51
      |AFTER INSERT ON Container
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.cntLocalCsn = 0)
      |BEGIN
      |    UPDATE Container
      |    SET cntMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 51)
      |    WHERE containerUid = NEW.containerUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 51;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_51
      |            AFTER INSERT ON Container
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.cntMasterCsn = 0)
      |            BEGIN
      |                UPDATE Container
      |                SET cntMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 51)
      |                WHERE containerUid = NEW.containerUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 51;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 51, NEW.containerUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_51
      |AFTER UPDATE ON Container
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.cntLocalCsn == OLD.cntLocalCsn OR
      |        NEW.cntLocalCsn == 0))
      |BEGIN
      |    UPDATE Container
      |    SET cntLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 51) 
      |    WHERE containerUid = NEW.containerUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 51;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_51
      |            AFTER UPDATE ON Container
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.cntMasterCsn == OLD.cntMasterCsn OR
      |                    NEW.cntMasterCsn == 0))
      |            BEGIN
      |                UPDATE Container
      |                SET cntMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 51)
      |                WHERE containerUid = NEW.containerUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 51;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 51, NEW.containerUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(51, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(51, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Container_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Container_trk_clientId_epk_csn  ON Container_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Container_trk_epk_clientId ON Container_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_62")
      database.execSQL("DROP TRIGGER IF EXISTS INS_62")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_62
      |AFTER INSERT ON VerbEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.verbLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE VerbEntity
      |    SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 62)
      |    WHERE verbUid = NEW.verbUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 62;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_62
      |            AFTER INSERT ON VerbEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.verbMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE VerbEntity
      |                SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 62)
      |                WHERE verbUid = NEW.verbUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 62;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 62, NEW.verbUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_62
      |AFTER UPDATE ON VerbEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.verbLocalChangeSeqNum == OLD.verbLocalChangeSeqNum OR
      |        NEW.verbLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE VerbEntity
      |    SET verbLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 62) 
      |    WHERE verbUid = NEW.verbUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 62;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_62
      |            AFTER UPDATE ON VerbEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.verbMasterChangeSeqNum == OLD.verbMasterChangeSeqNum OR
      |                    NEW.verbMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE VerbEntity
      |                SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 62)
      |                WHERE verbUid = NEW.verbUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 62;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 62, NEW.verbUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(62, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(62, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_VerbEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_VerbEntity_trk_clientId_epk_csn  ON VerbEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_VerbEntity_trk_epk_clientId ON VerbEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_64")
      database.execSQL("DROP TRIGGER IF EXISTS INS_64")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_64
      |AFTER INSERT ON XObjectEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.xObjectocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE XObjectEntity
      |    SET xObjectMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 64)
      |    WHERE xObjectUid = NEW.xObjectUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 64;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_64
      |            AFTER INSERT ON XObjectEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.xObjectMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE XObjectEntity
      |                SET xObjectMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 64)
      |                WHERE xObjectUid = NEW.xObjectUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 64;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 64, NEW.xObjectUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_64
      |AFTER UPDATE ON XObjectEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.xObjectocalChangeSeqNum == OLD.xObjectocalChangeSeqNum OR
      |        NEW.xObjectocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE XObjectEntity
      |    SET xObjectocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 64) 
      |    WHERE xObjectUid = NEW.xObjectUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 64;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_64
      |            AFTER UPDATE ON XObjectEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.xObjectMasterChangeSeqNum == OLD.xObjectMasterChangeSeqNum OR
      |                    NEW.xObjectMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE XObjectEntity
      |                SET xObjectMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 64)
      |                WHERE xObjectUid = NEW.xObjectUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 64;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 64, NEW.xObjectUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(64, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(64, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_XObjectEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_XObjectEntity_trk_clientId_epk_csn  ON XObjectEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_XObjectEntity_trk_epk_clientId ON XObjectEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_60")
      database.execSQL("DROP TRIGGER IF EXISTS INS_60")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_60
      |AFTER INSERT ON StatementEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.statementLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE StatementEntity
      |    SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 60)
      |    WHERE statementUid = NEW.statementUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 60;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_60
      |            AFTER INSERT ON StatementEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.statementMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE StatementEntity
      |                SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 60)
      |                WHERE statementUid = NEW.statementUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 60;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 60, NEW.statementUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_60
      |AFTER UPDATE ON StatementEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.statementLocalChangeSeqNum == OLD.statementLocalChangeSeqNum OR
      |        NEW.statementLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE StatementEntity
      |    SET statementLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 60) 
      |    WHERE statementUid = NEW.statementUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 60;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_60
      |            AFTER UPDATE ON StatementEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.statementMasterChangeSeqNum == OLD.statementMasterChangeSeqNum OR
      |                    NEW.statementMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE StatementEntity
      |                SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 60)
      |                WHERE statementUid = NEW.statementUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 60;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 60, NEW.statementUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(60, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(60, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_StatementEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_StatementEntity_trk_clientId_epk_csn  ON StatementEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_StatementEntity_trk_epk_clientId ON StatementEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_66")
      database.execSQL("DROP TRIGGER IF EXISTS INS_66")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_66
      |AFTER INSERT ON ContextXObjectStatementJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.verbLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContextXObjectStatementJoin
      |    SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 66)
      |    WHERE contextXObjectStatementJoinUid = NEW.contextXObjectStatementJoinUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 66;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_66
      |            AFTER INSERT ON ContextXObjectStatementJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.verbMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContextXObjectStatementJoin
      |                SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 66)
      |                WHERE contextXObjectStatementJoinUid = NEW.contextXObjectStatementJoinUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 66;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 66, NEW.contextXObjectStatementJoinUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_66
      |AFTER UPDATE ON ContextXObjectStatementJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.verbLocalChangeSeqNum == OLD.verbLocalChangeSeqNum OR
      |        NEW.verbLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContextXObjectStatementJoin
      |    SET verbLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 66) 
      |    WHERE contextXObjectStatementJoinUid = NEW.contextXObjectStatementJoinUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 66;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_66
      |            AFTER UPDATE ON ContextXObjectStatementJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.verbMasterChangeSeqNum == OLD.verbMasterChangeSeqNum OR
      |                    NEW.verbMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContextXObjectStatementJoin
      |                SET verbMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 66)
      |                WHERE contextXObjectStatementJoinUid = NEW.contextXObjectStatementJoinUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 66;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 66, NEW.contextXObjectStatementJoinUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(66, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(66, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContextXObjectStatementJoin_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContextXObjectStatementJoin_trk_clientId_epk_csn  ON ContextXObjectStatementJoin_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContextXObjectStatementJoin_trk_epk_clientId ON ContextXObjectStatementJoin_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_68")
      database.execSQL("DROP TRIGGER IF EXISTS INS_68")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_68
      |AFTER INSERT ON AgentEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.statementLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE AgentEntity
      |    SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 68)
      |    WHERE agentUid = NEW.agentUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 68;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_68
      |            AFTER INSERT ON AgentEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.statementMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE AgentEntity
      |                SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 68)
      |                WHERE agentUid = NEW.agentUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 68;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 68, NEW.agentUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_68
      |AFTER UPDATE ON AgentEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.statementLocalChangeSeqNum == OLD.statementLocalChangeSeqNum OR
      |        NEW.statementLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE AgentEntity
      |    SET statementLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 68) 
      |    WHERE agentUid = NEW.agentUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 68;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_68
      |            AFTER UPDATE ON AgentEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.statementMasterChangeSeqNum == OLD.statementMasterChangeSeqNum OR
      |                    NEW.statementMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE AgentEntity
      |                SET statementMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 68)
      |                WHERE agentUid = NEW.agentUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 68;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 68, NEW.agentUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(68, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(68, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_AgentEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_AgentEntity_trk_clientId_epk_csn  ON AgentEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_AgentEntity_trk_epk_clientId ON AgentEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_70")
      database.execSQL("DROP TRIGGER IF EXISTS INS_70")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_70
      |AFTER INSERT ON StateEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.stateLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE StateEntity
      |    SET stateMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 70)
      |    WHERE stateUid = NEW.stateUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 70;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_70
      |            AFTER INSERT ON StateEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.stateMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE StateEntity
      |                SET stateMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 70)
      |                WHERE stateUid = NEW.stateUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 70;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 70, NEW.stateUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_70
      |AFTER UPDATE ON StateEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.stateLocalChangeSeqNum == OLD.stateLocalChangeSeqNum OR
      |        NEW.stateLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE StateEntity
      |    SET stateLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 70) 
      |    WHERE stateUid = NEW.stateUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 70;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_70
      |            AFTER UPDATE ON StateEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.stateMasterChangeSeqNum == OLD.stateMasterChangeSeqNum OR
      |                    NEW.stateMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE StateEntity
      |                SET stateMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 70)
      |                WHERE stateUid = NEW.stateUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 70;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 70, NEW.stateUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(70, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(70, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_StateEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_StateEntity_trk_clientId_epk_csn  ON StateEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_StateEntity_trk_epk_clientId ON StateEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_72")
      database.execSQL("DROP TRIGGER IF EXISTS INS_72")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_72
      |AFTER INSERT ON StateContentEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.stateContentLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE StateContentEntity
      |    SET stateContentMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 72)
      |    WHERE stateContentUid = NEW.stateContentUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 72;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_72
      |            AFTER INSERT ON StateContentEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.stateContentMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE StateContentEntity
      |                SET stateContentMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 72)
      |                WHERE stateContentUid = NEW.stateContentUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 72;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 72, NEW.stateContentUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_72
      |AFTER UPDATE ON StateContentEntity
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.stateContentLocalChangeSeqNum == OLD.stateContentLocalChangeSeqNum OR
      |        NEW.stateContentLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE StateContentEntity
      |    SET stateContentLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 72) 
      |    WHERE stateContentUid = NEW.stateContentUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 72;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_72
      |            AFTER UPDATE ON StateContentEntity
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.stateContentMasterChangeSeqNum == OLD.stateContentMasterChangeSeqNum OR
      |                    NEW.stateContentMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE StateContentEntity
      |                SET stateContentMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 72)
      |                WHERE stateContentUid = NEW.stateContentUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 72;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 72, NEW.stateContentUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(72, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(72, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_StateContentEntity_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_StateContentEntity_trk_clientId_epk_csn  ON StateContentEntity_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_StateContentEntity_trk_epk_clientId ON StateContentEntity_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_74")
      database.execSQL("DROP TRIGGER IF EXISTS INS_74")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_74
      |AFTER INSERT ON XLangMapEntry
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.statementLangMapLocalCsn = 0)
      |BEGIN
      |    UPDATE XLangMapEntry
      |    SET statementLangMapMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 74)
      |    WHERE statementLangMapUid = NEW.statementLangMapUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 74;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_74
      |            AFTER INSERT ON XLangMapEntry
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.statementLangMapMasterCsn = 0)
      |            BEGIN
      |                UPDATE XLangMapEntry
      |                SET statementLangMapMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 74)
      |                WHERE statementLangMapUid = NEW.statementLangMapUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 74;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 74, NEW.statementLangMapUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_74
      |AFTER UPDATE ON XLangMapEntry
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.statementLangMapLocalCsn == OLD.statementLangMapLocalCsn OR
      |        NEW.statementLangMapLocalCsn == 0))
      |BEGIN
      |    UPDATE XLangMapEntry
      |    SET statementLangMapLocalCsn = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 74) 
      |    WHERE statementLangMapUid = NEW.statementLangMapUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 74;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_74
      |            AFTER UPDATE ON XLangMapEntry
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.statementLangMapMasterCsn == OLD.statementLangMapMasterCsn OR
      |                    NEW.statementLangMapMasterCsn == 0))
      |            BEGIN
      |                UPDATE XLangMapEntry
      |                SET statementLangMapMasterCsn = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 74)
      |                WHERE statementLangMapUid = NEW.statementLangMapUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 74;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 74, NEW.statementLangMapUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(74, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(74, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_XLangMapEntry_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_XLangMapEntry_trk_clientId_epk_csn  ON XLangMapEntry_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_XLangMapEntry_trk_epk_clientId ON XLangMapEntry_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_164")
      database.execSQL("DROP TRIGGER IF EXISTS INS_164")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_164
      |AFTER INSERT ON School
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.schoolLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE School
      |    SET schoolMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 164)
      |    WHERE schoolUid = NEW.schoolUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 164;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_164
      |            AFTER INSERT ON School
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.schoolMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE School
      |                SET schoolMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 164)
      |                WHERE schoolUid = NEW.schoolUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 164;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 164, NEW.schoolUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_164
      |AFTER UPDATE ON School
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.schoolLocalChangeSeqNum == OLD.schoolLocalChangeSeqNum OR
      |        NEW.schoolLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE School
      |    SET schoolLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 164) 
      |    WHERE schoolUid = NEW.schoolUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 164;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_164
      |            AFTER UPDATE ON School
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.schoolMasterChangeSeqNum == OLD.schoolMasterChangeSeqNum OR
      |                    NEW.schoolMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE School
      |                SET schoolMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 164)
      |                WHERE schoolUid = NEW.schoolUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 164;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 164, NEW.schoolUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(164, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(164, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_School_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_School_trk_clientId_epk_csn  ON School_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_School_trk_epk_clientId ON School_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_200")
      database.execSQL("DROP TRIGGER IF EXISTS INS_200")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_200
      |AFTER INSERT ON SchoolMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.schoolMemberLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE SchoolMember
      |    SET schoolMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 200)
      |    WHERE schoolMemberUid = NEW.schoolMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 200;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_200
      |            AFTER INSERT ON SchoolMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.schoolMemberMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE SchoolMember
      |                SET schoolMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 200)
      |                WHERE schoolMemberUid = NEW.schoolMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 200;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 200, NEW.schoolMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_200
      |AFTER UPDATE ON SchoolMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.schoolMemberLocalChangeSeqNum == OLD.schoolMemberLocalChangeSeqNum OR
      |        NEW.schoolMemberLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE SchoolMember
      |    SET schoolMemberLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 200) 
      |    WHERE schoolMemberUid = NEW.schoolMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 200;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_200
      |            AFTER UPDATE ON SchoolMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.schoolMemberMasterChangeSeqNum == OLD.schoolMemberMasterChangeSeqNum OR
      |                    NEW.schoolMemberMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE SchoolMember
      |                SET schoolMemberMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 200)
      |                WHERE schoolMemberUid = NEW.schoolMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 200;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 200, NEW.schoolMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(200, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(200, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_SchoolMember_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_SchoolMember_trk_clientId_epk_csn  ON SchoolMember_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_SchoolMember_trk_epk_clientId ON SchoolMember_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_201")
      database.execSQL("DROP TRIGGER IF EXISTS INS_201")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_201
      |AFTER INSERT ON ClazzWork
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ClazzWork
      |    SET clazzWorkMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 201)
      |    WHERE clazzWorkUid = NEW.clazzWorkUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 201;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_201
      |            AFTER INSERT ON ClazzWork
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ClazzWork
      |                SET clazzWorkMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 201)
      |                WHERE clazzWorkUid = NEW.clazzWorkUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 201;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 201, NEW.clazzWorkUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_201
      |AFTER UPDATE ON ClazzWork
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkLocalChangeSeqNum == OLD.clazzWorkLocalChangeSeqNum OR
      |        NEW.clazzWorkLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ClazzWork
      |    SET clazzWorkLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 201) 
      |    WHERE clazzWorkUid = NEW.clazzWorkUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 201;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_201
      |            AFTER UPDATE ON ClazzWork
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkMasterChangeSeqNum == OLD.clazzWorkMasterChangeSeqNum OR
      |                    NEW.clazzWorkMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ClazzWork
      |                SET clazzWorkMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 201)
      |                WHERE clazzWorkUid = NEW.clazzWorkUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 201;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 201, NEW.clazzWorkUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(201, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(201, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWork_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWork_trk_clientId_epk_csn  ON ClazzWork_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWork_trk_epk_clientId ON ClazzWork_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_204")
      database.execSQL("DROP TRIGGER IF EXISTS INS_204")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_204
      |AFTER INSERT ON ClazzWorkContentJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkContentJoinLCSN = 0)
      |BEGIN
      |    UPDATE ClazzWorkContentJoin
      |    SET clazzWorkContentJoinMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 204)
      |    WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 204;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_204
      |            AFTER INSERT ON ClazzWorkContentJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkContentJoinMCSN = 0)
      |            BEGIN
      |                UPDATE ClazzWorkContentJoin
      |                SET clazzWorkContentJoinMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 204)
      |                WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 204;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 204, NEW.clazzWorkContentJoinUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_204
      |AFTER UPDATE ON ClazzWorkContentJoin
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkContentJoinLCSN == OLD.clazzWorkContentJoinLCSN OR
      |        NEW.clazzWorkContentJoinLCSN == 0))
      |BEGIN
      |    UPDATE ClazzWorkContentJoin
      |    SET clazzWorkContentJoinLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 204) 
      |    WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 204;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_204
      |            AFTER UPDATE ON ClazzWorkContentJoin
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkContentJoinMCSN == OLD.clazzWorkContentJoinMCSN OR
      |                    NEW.clazzWorkContentJoinMCSN == 0))
      |            BEGIN
      |                UPDATE ClazzWorkContentJoin
      |                SET clazzWorkContentJoinMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 204)
      |                WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 204;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 204, NEW.clazzWorkContentJoinUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(204, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(204, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWorkContentJoin_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWorkContentJoin_trk_clientId_epk_csn  ON ClazzWorkContentJoin_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWorkContentJoin_trk_epk_clientId ON ClazzWorkContentJoin_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_208")
      database.execSQL("DROP TRIGGER IF EXISTS INS_208")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_208
      |AFTER INSERT ON Comments
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.commentsLCSN = 0)
      |BEGIN
      |    UPDATE Comments
      |    SET commentsMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 208)
      |    WHERE commentsUid = NEW.commentsUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 208;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_208
      |            AFTER INSERT ON Comments
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.commentsMCSN = 0)
      |            BEGIN
      |                UPDATE Comments
      |                SET commentsMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 208)
      |                WHERE commentsUid = NEW.commentsUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 208;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 208, NEW.commentsUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_208
      |AFTER UPDATE ON Comments
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.commentsLCSN == OLD.commentsLCSN OR
      |        NEW.commentsLCSN == 0))
      |BEGIN
      |    UPDATE Comments
      |    SET commentsLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 208) 
      |    WHERE commentsUid = NEW.commentsUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 208;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_208
      |            AFTER UPDATE ON Comments
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.commentsMCSN == OLD.commentsMCSN OR
      |                    NEW.commentsMCSN == 0))
      |            BEGIN
      |                UPDATE Comments
      |                SET commentsMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 208)
      |                WHERE commentsUid = NEW.commentsUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 208;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 208, NEW.commentsUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(208, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(208, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Comments_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Comments_trk_clientId_epk_csn  ON Comments_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Comments_trk_epk_clientId ON Comments_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_202")
      database.execSQL("DROP TRIGGER IF EXISTS INS_202")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_202
      |AFTER INSERT ON ClazzWorkQuestion
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkQuestionLCSN = 0)
      |BEGIN
      |    UPDATE ClazzWorkQuestion
      |    SET clazzWorkQuestionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 202)
      |    WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 202;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_202
      |            AFTER INSERT ON ClazzWorkQuestion
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkQuestionMCSN = 0)
      |            BEGIN
      |                UPDATE ClazzWorkQuestion
      |                SET clazzWorkQuestionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 202)
      |                WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 202;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 202, NEW.clazzWorkQuestionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_202
      |AFTER UPDATE ON ClazzWorkQuestion
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkQuestionLCSN == OLD.clazzWorkQuestionLCSN OR
      |        NEW.clazzWorkQuestionLCSN == 0))
      |BEGIN
      |    UPDATE ClazzWorkQuestion
      |    SET clazzWorkQuestionLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 202) 
      |    WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 202;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_202
      |            AFTER UPDATE ON ClazzWorkQuestion
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkQuestionMCSN == OLD.clazzWorkQuestionMCSN OR
      |                    NEW.clazzWorkQuestionMCSN == 0))
      |            BEGIN
      |                UPDATE ClazzWorkQuestion
      |                SET clazzWorkQuestionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 202)
      |                WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 202;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 202, NEW.clazzWorkQuestionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(202, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(202, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWorkQuestion_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWorkQuestion_trk_clientId_epk_csn  ON ClazzWorkQuestion_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWorkQuestion_trk_epk_clientId ON ClazzWorkQuestion_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_203")
      database.execSQL("DROP TRIGGER IF EXISTS INS_203")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_203
      |AFTER INSERT ON ClazzWorkQuestionOption
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkQuestionOptionLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ClazzWorkQuestionOption
      |    SET clazzWorkQuestionOptionMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 203)
      |    WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 203;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_203
      |            AFTER INSERT ON ClazzWorkQuestionOption
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkQuestionOptionMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ClazzWorkQuestionOption
      |                SET clazzWorkQuestionOptionMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 203)
      |                WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 203;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 203, NEW.clazzWorkQuestionOptionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_203
      |AFTER UPDATE ON ClazzWorkQuestionOption
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkQuestionOptionLocalChangeSeqNum == OLD.clazzWorkQuestionOptionLocalChangeSeqNum OR
      |        NEW.clazzWorkQuestionOptionLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ClazzWorkQuestionOption
      |    SET clazzWorkQuestionOptionLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 203) 
      |    WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 203;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_203
      |            AFTER UPDATE ON ClazzWorkQuestionOption
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkQuestionOptionMasterChangeSeqNum == OLD.clazzWorkQuestionOptionMasterChangeSeqNum OR
      |                    NEW.clazzWorkQuestionOptionMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ClazzWorkQuestionOption
      |                SET clazzWorkQuestionOptionMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 203)
      |                WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 203;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 203, NEW.clazzWorkQuestionOptionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(203, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(203, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWorkQuestionOption_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWorkQuestionOption_trk_clientId_epk_csn  ON ClazzWorkQuestionOption_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWorkQuestionOption_trk_epk_clientId ON ClazzWorkQuestionOption_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_206")
      database.execSQL("DROP TRIGGER IF EXISTS INS_206")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_206
      |AFTER INSERT ON ClazzWorkSubmission
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkSubmissionLCSN = 0)
      |BEGIN
      |    UPDATE ClazzWorkSubmission
      |    SET clazzWorkSubmissionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 206)
      |    WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 206;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_206
      |            AFTER INSERT ON ClazzWorkSubmission
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkSubmissionMCSN = 0)
      |            BEGIN
      |                UPDATE ClazzWorkSubmission
      |                SET clazzWorkSubmissionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 206)
      |                WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 206;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 206, NEW.clazzWorkSubmissionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_206
      |AFTER UPDATE ON ClazzWorkSubmission
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkSubmissionLCSN == OLD.clazzWorkSubmissionLCSN OR
      |        NEW.clazzWorkSubmissionLCSN == 0))
      |BEGIN
      |    UPDATE ClazzWorkSubmission
      |    SET clazzWorkSubmissionLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 206) 
      |    WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 206;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_206
      |            AFTER UPDATE ON ClazzWorkSubmission
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkSubmissionMCSN == OLD.clazzWorkSubmissionMCSN OR
      |                    NEW.clazzWorkSubmissionMCSN == 0))
      |            BEGIN
      |                UPDATE ClazzWorkSubmission
      |                SET clazzWorkSubmissionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 206)
      |                WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 206;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 206, NEW.clazzWorkSubmissionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(206, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(206, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWorkSubmission_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWorkSubmission_trk_clientId_epk_csn  ON ClazzWorkSubmission_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWorkSubmission_trk_epk_clientId ON ClazzWorkSubmission_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_209")
      database.execSQL("DROP TRIGGER IF EXISTS INS_209")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_209
      |AFTER INSERT ON ClazzWorkQuestionResponse
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.clazzWorkQuestionResponseLCSN = 0)
      |BEGIN
      |    UPDATE ClazzWorkQuestionResponse
      |    SET clazzWorkQuestionResponseMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 209)
      |    WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 209;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_209
      |            AFTER INSERT ON ClazzWorkQuestionResponse
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.clazzWorkQuestionResponseMCSN = 0)
      |            BEGIN
      |                UPDATE ClazzWorkQuestionResponse
      |                SET clazzWorkQuestionResponseMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 209)
      |                WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 209;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 209, NEW.clazzWorkQuestionResponseUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_209
      |AFTER UPDATE ON ClazzWorkQuestionResponse
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.clazzWorkQuestionResponseLCSN == OLD.clazzWorkQuestionResponseLCSN OR
      |        NEW.clazzWorkQuestionResponseLCSN == 0))
      |BEGIN
      |    UPDATE ClazzWorkQuestionResponse
      |    SET clazzWorkQuestionResponseLCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 209) 
      |    WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 209;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_209
      |            AFTER UPDATE ON ClazzWorkQuestionResponse
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.clazzWorkQuestionResponseMCSN == OLD.clazzWorkQuestionResponseMCSN OR
      |                    NEW.clazzWorkQuestionResponseMCSN == 0))
      |            BEGIN
      |                UPDATE ClazzWorkQuestionResponse
      |                SET clazzWorkQuestionResponseMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 209)
      |                WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 209;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 209, NEW.clazzWorkQuestionResponseUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(209, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(209, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ClazzWorkQuestionResponse_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ClazzWorkQuestionResponse_trk_clientId_epk_csn  ON ClazzWorkQuestionResponse_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ClazzWorkQuestionResponse_trk_epk_clientId ON ClazzWorkQuestionResponse_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_210")
      database.execSQL("DROP TRIGGER IF EXISTS INS_210")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_210
      |AFTER INSERT ON ContentEntryProgress
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.contentEntryProgressLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ContentEntryProgress
      |    SET contentEntryProgressMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 210)
      |    WHERE contentEntryProgressUid = NEW.contentEntryProgressUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 210;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_210
      |            AFTER INSERT ON ContentEntryProgress
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.contentEntryProgressMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ContentEntryProgress
      |                SET contentEntryProgressMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 210)
      |                WHERE contentEntryProgressUid = NEW.contentEntryProgressUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 210;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 210, NEW.contentEntryProgressUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_210
      |AFTER UPDATE ON ContentEntryProgress
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.contentEntryProgressLocalChangeSeqNum == OLD.contentEntryProgressLocalChangeSeqNum OR
      |        NEW.contentEntryProgressLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ContentEntryProgress
      |    SET contentEntryProgressLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 210) 
      |    WHERE contentEntryProgressUid = NEW.contentEntryProgressUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 210;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_210
      |            AFTER UPDATE ON ContentEntryProgress
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.contentEntryProgressMasterChangeSeqNum == OLD.contentEntryProgressMasterChangeSeqNum OR
      |                    NEW.contentEntryProgressMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ContentEntryProgress
      |                SET contentEntryProgressMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 210)
      |                WHERE contentEntryProgressUid = NEW.contentEntryProgressUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 210;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 210, NEW.contentEntryProgressUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(210, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(210, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ContentEntryProgress_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ContentEntryProgress_trk_clientId_epk_csn  ON ContentEntryProgress_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ContentEntryProgress_trk_epk_clientId ON ContentEntryProgress_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_101")
      database.execSQL("DROP TRIGGER IF EXISTS INS_101")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_101
      |AFTER INSERT ON Report
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.reportLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE Report
      |    SET reportMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 101)
      |    WHERE reportUid = NEW.reportUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 101;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_101
      |            AFTER INSERT ON Report
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.reportMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE Report
      |                SET reportMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 101)
      |                WHERE reportUid = NEW.reportUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 101;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 101, NEW.reportUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_101
      |AFTER UPDATE ON Report
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.reportLocalChangeSeqNum == OLD.reportLocalChangeSeqNum OR
      |        NEW.reportLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE Report
      |    SET reportLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 101) 
      |    WHERE reportUid = NEW.reportUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 101;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_101
      |            AFTER UPDATE ON Report
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.reportMasterChangeSeqNum == OLD.reportMasterChangeSeqNum OR
      |                    NEW.reportMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE Report
      |                SET reportMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 101)
      |                WHERE reportUid = NEW.reportUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 101;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 101, NEW.reportUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(101, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(101, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_Report_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_Report_trk_clientId_epk_csn  ON Report_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_Report_trk_epk_clientId ON Report_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_102")
      database.execSQL("DROP TRIGGER IF EXISTS INS_102")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_102
      |AFTER INSERT ON ReportFilter
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.reportFilterLocalChangeSeqNum = 0)
      |BEGIN
      |    UPDATE ReportFilter
      |    SET reportFilterMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 102)
      |    WHERE reportFilterUid = NEW.reportFilterUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 102;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_102
      |            AFTER INSERT ON ReportFilter
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.reportFilterMasterChangeSeqNum = 0)
      |            BEGIN
      |                UPDATE ReportFilter
      |                SET reportFilterMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 102)
      |                WHERE reportFilterUid = NEW.reportFilterUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 102;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 102, NEW.reportFilterUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_102
      |AFTER UPDATE ON ReportFilter
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.reportFilterLocalChangeSeqNum == OLD.reportFilterLocalChangeSeqNum OR
      |        NEW.reportFilterLocalChangeSeqNum == 0))
      |BEGIN
      |    UPDATE ReportFilter
      |    SET reportFilterLocalChangeSeqNum = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 102) 
      |    WHERE reportFilterUid = NEW.reportFilterUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 102;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_102
      |            AFTER UPDATE ON ReportFilter
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.reportFilterMasterChangeSeqNum == OLD.reportFilterMasterChangeSeqNum OR
      |                    NEW.reportFilterMasterChangeSeqNum == 0))
      |            BEGIN
      |                UPDATE ReportFilter
      |                SET reportFilterMasterChangeSeqNum = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 102)
      |                WHERE reportFilterUid = NEW.reportFilterUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 102;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 102, NEW.reportFilterUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(102, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(102, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_ReportFilter_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_ReportFilter_trk_clientId_epk_csn  ON ReportFilter_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_ReportFilter_trk_epk_clientId ON ReportFilter_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_301")
      database.execSQL("DROP TRIGGER IF EXISTS INS_301")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_301
      |AFTER INSERT ON LearnerGroup
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.learnerGroupCSN = 0)
      |BEGIN
      |    UPDATE LearnerGroup
      |    SET learnerGroupMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 301)
      |    WHERE learnerGroupUid = NEW.learnerGroupUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 301;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_301
      |            AFTER INSERT ON LearnerGroup
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.learnerGroupMCSN = 0)
      |            BEGIN
      |                UPDATE LearnerGroup
      |                SET learnerGroupMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 301)
      |                WHERE learnerGroupUid = NEW.learnerGroupUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 301;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 301, NEW.learnerGroupUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_301
      |AFTER UPDATE ON LearnerGroup
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.learnerGroupCSN == OLD.learnerGroupCSN OR
      |        NEW.learnerGroupCSN == 0))
      |BEGIN
      |    UPDATE LearnerGroup
      |    SET learnerGroupCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 301) 
      |    WHERE learnerGroupUid = NEW.learnerGroupUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 301;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_301
      |            AFTER UPDATE ON LearnerGroup
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.learnerGroupMCSN == OLD.learnerGroupMCSN OR
      |                    NEW.learnerGroupMCSN == 0))
      |            BEGIN
      |                UPDATE LearnerGroup
      |                SET learnerGroupMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 301)
      |                WHERE learnerGroupUid = NEW.learnerGroupUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 301;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 301, NEW.learnerGroupUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(301, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(301, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_LearnerGroup_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_LearnerGroup_trk_clientId_epk_csn  ON LearnerGroup_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_LearnerGroup_trk_epk_clientId ON LearnerGroup_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_300")
      database.execSQL("DROP TRIGGER IF EXISTS INS_300")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_300
      |AFTER INSERT ON LearnerGroupMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.learnerGroupMemberCSN = 0)
      |BEGIN
      |    UPDATE LearnerGroupMember
      |    SET learnerGroupMemberMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 300)
      |    WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 300;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_300
      |            AFTER INSERT ON LearnerGroupMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.learnerGroupMemberMCSN = 0)
      |            BEGIN
      |                UPDATE LearnerGroupMember
      |                SET learnerGroupMemberMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 300)
      |                WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 300;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 300, NEW.learnerGroupMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_300
      |AFTER UPDATE ON LearnerGroupMember
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.learnerGroupMemberCSN == OLD.learnerGroupMemberCSN OR
      |        NEW.learnerGroupMemberCSN == 0))
      |BEGIN
      |    UPDATE LearnerGroupMember
      |    SET learnerGroupMemberCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 300) 
      |    WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 300;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_300
      |            AFTER UPDATE ON LearnerGroupMember
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.learnerGroupMemberMCSN == OLD.learnerGroupMemberMCSN OR
      |                    NEW.learnerGroupMemberMCSN == 0))
      |            BEGIN
      |                UPDATE LearnerGroupMember
      |                SET learnerGroupMemberMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 300)
      |                WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 300;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 300, NEW.learnerGroupMemberUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(300, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(300, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_LearnerGroupMember_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_LearnerGroupMember_trk_clientId_epk_csn  ON LearnerGroupMember_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_LearnerGroupMember_trk_epk_clientId ON LearnerGroupMember_trk (epk, clientId)")
      database.execSQL("DROP TRIGGER IF EXISTS INS_302")
      database.execSQL("DROP TRIGGER IF EXISTS INS_302")
      database.execSQL("""
      |CREATE TRIGGER INS_LOC_302
      |AFTER INSERT ON GroupLearningSession
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
      |    NEW.groupLearningSessionCSN = 0)
      |BEGIN
      |    UPDATE GroupLearningSession
      |    SET groupLearningSessionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 302)
      |    WHERE groupLearningSessionUid = NEW.groupLearningSessionUid;
      |    
      |    UPDATE SqliteChangeSeqNums
      |    SET sCsnNextPrimary = sCsnNextPrimary + 1
      |    WHERE sCsnTableId = 302;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER INS_PRI_302
      |            AFTER INSERT ON GroupLearningSession
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
      |                NEW.groupLearningSessionMCSN = 0)
      |            BEGIN
      |                UPDATE GroupLearningSession
      |                SET groupLearningSessionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 302)
      |                WHERE groupLearningSessionUid = NEW.groupLearningSessionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 302;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 302, NEW.groupLearningSessionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("""
      |CREATE TRIGGER UPD_LOC_302
      |AFTER UPDATE ON GroupLearningSession
      |FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
      |    AND (NEW.groupLearningSessionCSN == OLD.groupLearningSessionCSN OR
      |        NEW.groupLearningSessionCSN == 0))
      |BEGIN
      |    UPDATE GroupLearningSession
      |    SET groupLearningSessionCSN = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = 302) 
      |    WHERE groupLearningSessionUid = NEW.groupLearningSessionUid;
      |    
      |    UPDATE SqliteChangeSeqNums 
      |    SET sCsnNextLocal = sCsnNextLocal + 1
      |    WHERE sCsnTableId = 302;
      |END
      """.trimMargin())
      database.execSQL("""
      |            CREATE TRIGGER UPD_PRI_302
      |            AFTER UPDATE ON GroupLearningSession
      |            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
      |                AND (NEW.groupLearningSessionMCSN == OLD.groupLearningSessionMCSN OR
      |                    NEW.groupLearningSessionMCSN == 0))
      |            BEGIN
      |                UPDATE GroupLearningSession
      |                SET groupLearningSessionMCSN = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = 302)
      |                WHERE groupLearningSessionUid = NEW.groupLearningSessionUid;
      |                
      |                UPDATE SqliteChangeSeqNums
      |                SET sCsnNextPrimary = sCsnNextPrimary + 1
      |                WHERE sCsnTableId = 302;
      |                
      |                INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
      |SELECT 302, NEW.groupLearningSessionUid, 0, (strftime('%s','now') * 1000) + ((strftime('%f','now') * 1000) % 1000);
      |            END
      """.trimMargin())
      database.execSQL("REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary) VALUES(302, 1, 1)")
      database.execSQL("INSERT INTO TableSyncStatus(tsTableId, tsLastChanged, tsLastSynced) VALUES(302, ${systemTimeInMillis()}, 0)")
      database.execSQL("DROP INDEX IF EXISTS index_GroupLearningSession_trk_clientId_epk_rx_csn")
      database.execSQL("CREATE INDEX index_GroupLearningSession_trk_clientId_epk_csn  ON GroupLearningSession_trk (clientId, epk, csn)")
      database.execSQL("CREATE UNIQUE INDEX index_GroupLearningSession_trk_epk_clientId ON GroupLearningSession_trk (epk, clientId)")
    }
  }
}
