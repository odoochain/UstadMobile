package com.ustadmobile.core.db

import androidx.room.Database
import com.ustadmobile.core.db.dao.*
import com.ustadmobile.door.*
import com.ustadmobile.door.annotation.MinSyncVersion
import com.ustadmobile.door.entities.*
import com.ustadmobile.door.ext.DoorTag
import com.ustadmobile.door.ext.dbType
import com.ustadmobile.door.util.systemTimeInMillis
import com.ustadmobile.lib.db.entities.*
import kotlin.js.JsName
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

@Database(entities = [NetworkNode::class, DownloadJobItemHistory::class,
    ClazzLog::class, ClazzLogAttendanceRecord::class,
    Schedule::class, DateRange::class, HolidayCalendar::class, Holiday::class,
    ScheduledCheck::class,
    AuditLog::class, CustomField::class, CustomFieldValue::class, CustomFieldValueOption::class,
    Person::class, DownloadJob::class, DownloadJobItem::class, DownloadJobItemParentChildJoin::class,
    Clazz::class, ClazzMember::class, PersonCustomFieldValue::class,
    ContentEntry::class, ContentEntryContentCategoryJoin::class, ContentEntryParentChildJoin::class,
    ContentEntryRelatedEntryJoin::class, ContentCategorySchema::class, ContentCategory::class,
    Language::class, LanguageVariant::class, AccessToken::class, PersonAuth::class, Role::class,
    EntityRole::class, PersonGroup::class, PersonGroupMember::class,
    PersonPicture::class,
    ScrapeQueueItem::class, ScrapeRun::class, ContentEntryStatus::class, ConnectivityStatus::class,
    Container::class, ContainerEntry::class, ContainerEntryFile::class,
    VerbEntity::class, XObjectEntity::class, StatementEntity::class,
    ContextXObjectStatementJoin::class, AgentEntity::class,
    StateEntity::class, StateContentEntity::class, XLangMapEntry::class,
    SyncNode::class, LocallyAvailableContainer::class, ContainerETag::class,
    SyncResult::class, School::class,
    SchoolMember::class, ClazzWork::class, ClazzWorkContentJoin::class, Comments::class,
    ClazzWorkQuestion::class, ClazzWorkQuestionOption::class, ClazzWorkSubmission::class,
    ClazzWorkQuestionResponse::class, ContentEntryProgress::class,
    Report::class, ReportFilter::class,
    DeviceSession::class, WorkSpace::class, ContainerImportJob::class,
    LearnerGroup::class, LearnerGroupMember::class,
    GroupLearningSession::class,

    //Door Helper entities
    SqliteChangeSeqNums::class,
    UpdateNotification::class,
    TableSyncStatus::class,
    ChangeLog::class

    //TODO: DO NOT REMOVE THIS COMMENT!
    //#DOORDB_TRACKER_ENTITIES

], version = 52)
@MinSyncVersion(28)
abstract class UmAppDatabase : DoorDatabase(), SyncableDoorDatabase {

    /*
        Changes from 38-39:
        1. Added personGroupUid to Person
        2. Added personGroupFlag to PersonGroup
        3. Removed groupPersonUid from PersonGroup

        Changes from 36:
        1. Added school uid to Clazz
        2. Added school Phone number to School
        3. Added schoolGender to School
        4. Added schoolHolidayCalendar to School
        5. Added SchoolMember and SchoolMemberDao
        6. Added ClazzWork, ClazzWorkContentJoin, Comments,ClazzWorkQuestion,ClazzWorkQuestionOption
        7. Added ContainerUploadJob
        Changes in 34:
        Added School and Assignment based entities
        Updated Clazz : added clazzFeatures and removed individual feature bits
     */


    var attachmentsDir: String? = null

    override val master: Boolean
        get() = false


    /**
     * Preload a few entities where we have fixed UIDs for fixed items (e.g. Xapi Verbs)
     */
    fun preload() {
        verbDao.initPreloadedVerbs()
    }

    @JsName("networkNodeDao")
    abstract val networkNodeDao: NetworkNodeDao

    @JsName("downloadJobDao")
    abstract val downloadJobDao: DownloadJobDao

    @JsName("downloadJobItemDao")
    abstract val downloadJobItemDao: DownloadJobItemDao

    @JsName("downloadJobItemParentChildJoinDao")
    abstract val downloadJobItemParentChildJoinDao: DownloadJobItemParentChildJoinDao

    @JsName("downloadJobItemHistoryDao")
    abstract val downloadJobItemHistoryDao: DownloadJobItemHistoryDao

    @JsName("personDao")
    abstract val personDao: PersonDao

    @JsName("clazzDao")
    abstract val clazzDao: ClazzDao

    @JsName("clazzMemberDao")
    abstract val clazzMemberDao: ClazzMemberDao

    @JsName("contentEntryDao")
    abstract val contentEntryDao: ContentEntryDao

    @JsName("contentEntryContentCategoryJoinDao")
    abstract val contentEntryContentCategoryJoinDao: ContentEntryContentCategoryJoinDao

    @JsName("contentEntryParentChildJoinDao")
    abstract val contentEntryParentChildJoinDao: ContentEntryParentChildJoinDao

    @JsName("contentEntryRelatedEntryJoinDao")
    abstract val contentEntryRelatedEntryJoinDao: ContentEntryRelatedEntryJoinDao

    // abstract val syncStatusDao: SyncStatusDao

    @JsName("contentCategorySchemaDao")
    abstract val contentCategorySchemaDao: ContentCategorySchemaDao

    @JsName("contentCategoryDao")
    abstract val contentCategoryDao: ContentCategoryDao

    @JsName("languageDao")
    abstract val languageDao: LanguageDao

    @JsName("languageVariantDao")
    abstract val languageVariantDao: LanguageVariantDao

    @JsName("scrapeQueueItemDao")
    abstract val scrapeQueueItemDao: ScrapeQueueItemDao

    @JsName("personAuthDao")
    abstract val personAuthDao: PersonAuthDao

    @JsName("accessTokenDao")
    abstract val accessTokenDao: AccessTokenDao

    @JsName("roleDao")
    abstract val roleDao: RoleDao

    @JsName("personGroupDao")
    abstract val personGroupDao: PersonGroupDao

    @JsName("personGroupMemberDao")
    abstract val personGroupMemberDao: PersonGroupMemberDao

    @JsName("entityRoleDao")
    abstract val entityRoleDao: EntityRoleDao

    @JsName("personPictureDao")
    abstract val personPictureDao: PersonPictureDao

    @JsName("scrapeRunDao")
    abstract val scrapeRunDao: ScrapeRunDao

    @JsName("contentEntryStatusDao")
    abstract val contentEntryStatusDao: ContentEntryStatusDao

    @JsName("connectivityStatusDao")
    abstract val connectivityStatusDao: ConnectivityStatusDao

    @JsName("containerDao")
    abstract val containerDao: ContainerDao

    @JsName("containerEntryDao")
    abstract val containerEntryDao: ContainerEntryDao

    @JsName("containerEntryFileDao")
    abstract val containerEntryFileDao: ContainerEntryFileDao

    @JsName("containerETagDao")
    abstract val containerETagDao: ContainerETagDao

    @JsName("verbDao")
    abstract val verbDao: VerbDao

    @JsName("xObjectDao")
    abstract val xObjectDao: XObjectDao

    @JsName("reportDao")
    abstract val reportDao: ReportDao

    @JsName("reportFilterDao")
    abstract val reportFilterDao: ReportFilterDao

    @JsName("containerImportJobDao")
    abstract val containerImportJobDao: ContainerImportJobDao

    @JsName("statementDao")
    abstract val statementDao: StatementDao

    @JsName("contextXObjectStatementJoinDao")
    abstract val contextXObjectStatementJoinDao: ContextXObjectStatementJoinDao

    @JsName("stateDao")
    abstract val stateDao: StateDao

    @JsName("stateContentDao")
    abstract val stateContentDao: StateContentDao

    @JsName("agentDao")
    abstract val agentDao: AgentDao

    @JsName("learnerGroupDao")
    abstract val learnerGroupDao: LearnerGroupDao

    @JsName("learnerGroupMemberDao")
    abstract val learnerGroupMemberDao: LearnerGroupMemberDao

    @JsName("groupLearningSessionDao")
    abstract val groupLearningSessionDao: GroupLearningSessionDao

    @JsName("contentEntryProgressDao")
    abstract val contentEntryProgressDao: ContentEntryProgressDao

    abstract val syncresultDao: SyncResultDao


    abstract val clazzLogAttendanceRecordDao: ClazzLogAttendanceRecordDao
    abstract val clazzLogDao: ClazzLogDao
    abstract val customFieldDao: CustomFieldDao
    abstract val customFieldValueDao: CustomFieldValueDao
    abstract val customFieldValueOptionDao: CustomFieldValueOptionDao

    abstract val scheduleDao: ScheduleDao

    abstract val scheduledCheckDao: ScheduledCheckDao

    abstract val holidayCalendarDao: HolidayCalendarDao
    abstract val holidayDao: HolidayDao
    abstract val schoolDao: SchoolDao

    @JsName("xLangMapEntryDao")
    abstract val xLangMapEntryDao: XLangMapEntryDao

    abstract val locallyAvailableContainerDao: LocallyAvailableContainerDao

    @JsName("schoolMemberDao")
    abstract val schoolMemberDao: SchoolMemberDao

    @JsName("clazzWorkDao")
    abstract val clazzWorkDao: ClazzWorkDao

    @JsName("clazzWorkSubmissionDao")
    abstract val clazzWorkSubmissionDao: ClazzWorkSubmissionDao

    @JsName("clazzWorkContentJoinDao")
    abstract val clazzWorkContentJoinDao: ClazzWorkContentJoinDao

    @JsName("clazzWorkQuestionDao")
    abstract val clazzWorkQuestionDao: ClazzWorkQuestionDao

    @JsName("clazzWorkQuestionOptionDao")
    abstract val clazzWorkQuestionOptionDao: ClazzWorkQuestionOptionDao

    @JsName("commentsDao")
    abstract val commentsDao: CommentsDao

    @JsName("clazzWorkQuestionResponseDao")
    abstract val clazzWorkQuestionResponseDao: ClazzWorkQuestionResponseDao

    @JsName("syncNodeDao")
    abstract val syncNodeDao: SyncNodeDao

    @JsName("deviceSessionDao")
    abstract val deviceSessionDao: DeviceSessionDao

    @JsName("workSpaceDao")
    abstract val workSpaceDao: WorkSpaceDao

    //TODO: DO NOT REMOVE THIS COMMENT!
    //#DOORDB_SYNCDAO


    companion object {

        const val TAG_DB = DoorTag.TAG_DB

        const val TAG_REPO = DoorTag.TAG_REPO


        @Volatile
        private var instance: UmAppDatabase? = null

        private val namedInstances = mutableMapOf<String, UmAppDatabase>()

        /**
         * For use by other projects using this app as a library. By calling setInstance before
         * any other usage (e.g. in the Android Application class) a child class of this database (eg.
         * with additional entities) can be used.
         *
         * @param instance
         */
        @Synchronized
        @JsName("setInstance")
        fun setInstance(instance: UmAppDatabase) {
            UmAppDatabase.instance = instance
        }

        /**
         * For use by other projects using this app as a library. By calling setInstance before
         * any other usage (e.g. in the Android Application class) a child class of this database (eg.
         * with additional entities) can be used.
         *
         * @param instance
         * @param dbName
         */
        @Synchronized
        @JsName("setInstanceWithName")
        fun setInstance(instance: UmAppDatabase, dbName: String) {
            namedInstances[dbName] = instance
        }

        @JsName("getInstance")
        fun getInstance(context: Any) = lazy { getInstance(context, "UmAppDatabase") }.value

        @JsName("getInstanceWithDbName")
        @Synchronized
        fun getInstance(context: Any, dbName: String): UmAppDatabase {
            var db = namedInstances[dbName]

            if (db == null) {
                var builder = DatabaseBuilder.databaseBuilder(
                        context, UmAppDatabase::class, dbName)
                builder = addMigrations(builder)
                //db = addCallbacks(builder).build()
                db = builder.build()
                namedInstances[dbName] = db
            }

            return db
        }


        val MIGRATION_32_33 = object : DoorMigration(32, 33) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("""ALTER TABLE ScrapeQueueItem ADD COLUMN errorCode INTEGER NOT NULL DEFAULT 0""".trimMargin())
                println("finished migration from 32 to 33")
            }
        }

        val MIGRATION_33_34 = object : DoorMigration(33, 34) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("""ALTER TABLE ScrapeQueueItem ADD COLUMN priority INTEGER NOT NULL DEFAULT 1""".trimMargin())
                println("finished migration from 33 to 34")
            }
        }


        val MIGRATION_34_35 = object : DoorMigration(34, 35) {
            override fun migrate(database: DoorSqlDatabase) {

                database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzDesc TEXT
                        """.trimMargin())

                database.execSQL("""ALTER TABLE Location 
                        ADD COLUMN timeZone TEXT""".trimMargin())

                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN personAddress TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN personNotes TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN fatherName TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN motherName TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN motherNum TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN fatherNumber TEXT""".trimMargin())

                database.execSQL("""ALTER TABLE PersonAuth 
                        ADD COLUMN personAuthStatus INTEGER DEFAULT 0 NOT NULL""".trimMargin())
                database.execSQL("""ALTER TABLE PersonAuth 
                        ADD COLUMN lastChangedBy INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                database.execSQL("""DROP TABLE PersonCustomField""".trimMargin())

                database.execSQL("""ALTER TABLE StatementEntity 
                        ADD COLUMN extensionProgress INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzHolidayUMCalendarUid  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzScheuleUMCalendarUid  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzStartTime  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzEndTime  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN clazzFeatures  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE ClazzMember 
                        ADD COLUMN clazzMemberAttendancePercentage REAL DEFAULT 0 NOT NULL 
                    """.trimMargin())

                    database.execSQL("""ALTER TABLE ClazzMember 
                        ADD COLUMN clazzMemberActive  INTEGER DEFAULT 0 NOT NULL
                    """.trimMargin())

                    database.execSQL("""ALTER TABLE Clazz
                        ADD COLUMN isClazzActive  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE Person 
                        ADD COLUMN dateOfBirth INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("ALTER TABLE EntityRole ADD COLUMN erActive INTEGER DEFAULT 0 NOT NULL")

                    database.execSQL("""ALTER TABLE Role 
                        ADD COLUMN roleActive INTEGER DEFAULT 1 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE PersonGroup 
                        ADD COLUMN groupActive INTEGER DEFAULT 1 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE PersonGroupMember 
                        ADD COLUMN groupMemberActive INTEGER DEFAULT 1 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE PersonAuth 
                        ADD COLUMN personAuthLocalChangeSeqNum INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE PersonAuth 
                        ADD COLUMN personAuthMasterChangeSeqNum INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE StatementEntity 
                        ADD COLUMN statementContentEntryUid INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS FeedEntry (`feedEntryUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `feedEntryPersonUid` INTEGER NOT NULL, `title` TEXT, `description` TEXT, `link` TEXT, `feedEntryClazzName` TEXT, `deadline` INTEGER NOT NULL, `feedEntryHash` INTEGER NOT NULL, `feedEntryDone` INTEGER NOT NULL, `feedEntryClazzLogUid` INTEGER NOT NULL, `dateCreated` INTEGER NOT NULL, `feedEntryCheckType` INTEGER NOT NULL, `feedEntryLocalChangeSeqNum` INTEGER NOT NULL, `feedEntryMasterChangeSeqNum` INTEGER NOT NULL, `feedEntryLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestion (`selQuestionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionText` TEXT, `selQuestionSelQuestionSetUid` INTEGER NOT NULL, `questionIndex` INTEGER NOT NULL, `assignToAllClasses` INTEGER NOT NULL, `multiNominations` INTEGER NOT NULL, `questionType` INTEGER NOT NULL, `questionActive` INTEGER NOT NULL, `selQuestionMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionResponse (`selQuestionResponseUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `selQuestionResponseSelQuestionSetResponseUid` INTEGER NOT NULL, `selQuestionResponseSelQuestionUid` INTEGER NOT NULL, `selQuestionResponseMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionResponseLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionResponseLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionResponseNomination (`selqrnUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `selqrnClazzMemberUid` INTEGER NOT NULL, `selqrnSelQuestionResponseUId` INTEGER NOT NULL, `nominationActive` INTEGER NOT NULL, `selqrnMCSN` INTEGER NOT NULL, `selqrnMCSNLCSN` INTEGER NOT NULL, `selqrnMCSNLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSet (`selQuestionSetUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `selQuestionSetMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionSetLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionSetLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSetRecognition (`selQuestionSetRecognitionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `selqsrSelQuestionSetResponseUid` INTEGER NOT NULL, `selQuestionSetRecognitionClazzMemberUid` INTEGER NOT NULL, `isSelQuestionSetRecognitionRecognized` INTEGER NOT NULL, `selQuestionSetRecognitionMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionSetRecognitionLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionSetRecognitionLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSetResponse (`selQuestionSetResposeUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `selQuestionSetResponseSelQuestionSetUid` INTEGER NOT NULL, `selQuestionSetResponseClazzMemberUid` INTEGER NOT NULL, `selQuestionSetResponseStartTime` INTEGER NOT NULL, `selQuestionSetResponseFinishTime` INTEGER NOT NULL, `selQuestionSetResponseRecognitionPercentage` REAL NOT NULL, `selQuestionSetResponseMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionSetResponseLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionSetResponseLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzActivity (`clazzActivityUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzActivityClazzActivityChangeUid` INTEGER NOT NULL, `isClazzActivityGoodFeedback` INTEGER NOT NULL, `clazzActivityNotes` TEXT, `clazzActivityLogDate` INTEGER NOT NULL, `clazzActivityClazzUid` INTEGER NOT NULL, `clazzActivityDone` INTEGER NOT NULL, `clazzActivityQuantity` INTEGER NOT NULL, `clazzActivityMasterChangeSeqNum` INTEGER NOT NULL, `clazzActivityLocalChangeSeqNum` INTEGER NOT NULL, `clazzActivityLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzActivityChange (`clazzActivityChangeUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzActivityChangeTitle` TEXT, `clazzActivityDesc` TEXT, `clazzActivityUnitOfMeasure` INTEGER NOT NULL, `isClazzActivityChangeActive` INTEGER NOT NULL, `clazzActivityChangeLastChangedBy` INTEGER NOT NULL, `clazzActivityChangeMasterChangeSeqNum` INTEGER NOT NULL, `clazzActivityChangeLocalChangeSeqNum` INTEGER NOT NULL, `clazzActivityLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS UMCalendar (`umCalendarUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `umCalendarName` TEXT, `umCalendarCategory` INTEGER NOT NULL, `umCalendarActive` INTEGER NOT NULL, `isUmCalendarFlag` INTEGER NOT NULL, `umCalendarMasterChangeSeqNum` INTEGER NOT NULL, `umCalendarLocalChangeSeqNum` INTEGER NOT NULL, `umCalendarLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzAssignment (`clazzAssignmentUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzAssignmentTitle` TEXT, `clazzAssignmentClazzUid` INTEGER NOT NULL, `clazzAssignmentInactive` INTEGER NOT NULL, `clazzAssignmentStartDate` INTEGER NOT NULL, `clazzAssignmentDueDate` INTEGER NOT NULL, `clazzAssignmentCreationDate` INTEGER NOT NULL, `clazzAssignmentUpdateDate` INTEGER NOT NULL, `clazzAssignmentInstructions` TEXT, `clazzAssignmentGrading` INTEGER NOT NULL, `clazzAssignmentRequireAttachment` INTEGER NOT NULL, `clazzAssignmentMCSN` INTEGER NOT NULL, `clazzAssignmentLCSN` INTEGER NOT NULL, `clazzAssignmentLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzAssignmentContentJoin(`clazzAssignmentContentJoinUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzAssignmentContentJoinContentUid` INTEGER NOT NULL, `clazzAssignmentContentJoinClazzAssignmentUid` INTEGER NOT NULL, `clazzAssignmentContentJoinInactive` INTEGER NOT NULL, `clazzAssignmentContentJoinDateAdded` INTEGER NOT NULL, `clazzAssignmentContentJoinMCSN` INTEGER NOT NULL, `clazzAssignmentContentJoinLCSN` INTEGER NOT NULL, `clazzAssignmentContentJoinLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionOption (`selQuestionOptionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `optionText` TEXT, `selQuestionOptionQuestionUid` INTEGER NOT NULL, `selQuestionOptionMasterChangeSeqNum` INTEGER NOT NULL, `selQuestionOptionLocalChangeSeqNum` INTEGER NOT NULL, `selQuestionOptionLastChangedBy` INTEGER NOT NULL, `optionActive` INTEGER NOT NULL)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS FeedEntry_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_FeedEntry_trk_clientId_epk_rx_csn` ON FeedEntry_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestion_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestion_trk_clientId_epk_rx_csn` ON SelQuestion_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionResponse_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionResponse_trk_clientId_epk_rx_csn` ON SelQuestionResponse_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionResponseNomination_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionResponseNomination_trk_clientId_epk_rx_csn` ON SelQuestionResponseNomination_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSet_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionSet_trk_clientId_epk_rx_csn` ON SelQuestionSet_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSetRecognition_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionSetRecognition_trk_clientId_epk_rx_csn` ON SelQuestionSetRecognition_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionSetResponse_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionSetResponse_trk_clientId_epk_rx_csn` ON SelQuestionSetResponse_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzActivity_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzActivity_trk_clientId_epk_rx_csn` ON ClazzActivity_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzActivityChange_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzActivityChange_trk_clientId_epk_rx_csn` ON ClazzActivityChange_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS UMCalendar_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_UMCalendar_trk_clientId_epk_rx_csn` ON UMCalendar_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzAssignment_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzAssignment_trk_clientId_epk_rx_csn` ON ClazzAssignment_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS ClazzAssignmentContentJoin_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzAssignmentContentJoin_trk_clientId_epk_rx_csn` ON ClazzAssignmentContentJoin_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS SelQuestionOption_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SelQuestionOption_trk_clientId_epk_rx_csn` ON SelQuestionOption_trk (`clientId`, `epk`, `rx`, `csn`)")


                    //clazzMember rename columns, delete old and add new
                    database.execSQL("ALTER TABLE ClazzMember RENAME to ClazzMember_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzMember` (`clazzMemberUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzMemberPersonUid` INTEGER NOT NULL DEFAULT 0, `clazzMemberClazzUid` INTEGER NOT NULL DEFAULT 0, `clazzMemberDateJoined` INTEGER NOT NULL, `clazzMemberDateLeft` INTEGER NOT NULL, `clazzMemberRole` INTEGER NOT NULL, `clazzMemberAttendancePercentage` REAL NOT NULL, `clazzMemberActive` INTEGER NOT NULL, `clazzMemberLocalChangeSeqNum` INTEGER NOT NULL, `clazzMemberMasterChangeSeqNum` INTEGER NOT NULL, `clazzMemberLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO ClazzMember (clazzMemberUid, clazzMemberPersonUid, clazzMemberClazzUid, clazzMemberDateJoined, clazzMemberDateLeft, clazzMemberRole, clazzMemberAttendancePercentage, clazzMemberActive, clazzMemberLocalChangeSeqNum, clazzMemberMasterChangeSeqNum, clazzMemberLastChangedBy) SELECT clazzMemberUid, clazzMemberPersonUid, clazzMemberClazzUid, dateJoined, dateLeft, role, clazzMemberAttendancePercentage, clazzMemberActive, clazzMemberLocalChangeSeqNum, clazzMemberMasterChangeSeqNum, clazzMemberLastChangedBy FROM ClazzMember_OLD")
                    database.execSQL("DROP TABLE ClazzMember_OLD")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_ClazzMember_clazzMemberPersonUid ON ClazzMember (clazzMemberPersonUid)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_ClazzMember_clazzMemberClazzUid ON ClazzMember (clazzMemberClazzUid)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzMember_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzMember_trk_clientId_epk_rx_csn` ON `ClazzMember_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_65
          |AFTER UPDATE ON ClazzMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzMemberMasterChangeSeqNum = 0 
          |OR OLD.clazzMemberMasterChangeSeqNum = NEW.clazzMemberMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.clazzMemberLocalChangeSeqNum = 0  
          |OR OLD.clazzMemberLocalChangeSeqNum = NEW.clazzMemberLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ClazzMember SET clazzMemberLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzMemberLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(clazzMemberLocalChangeSeqNum), OLD.clazzMemberLocalChangeSeqNum) + 1 FROM ClazzMember) END),
          |clazzMemberMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzMemberMasterChangeSeqNum), OLD.clazzMemberMasterChangeSeqNum) + 1 FROM ClazzMember)
          |ELSE NEW.clazzMemberMasterChangeSeqNum END)
          |WHERE clazzMemberUid = NEW.clazzMemberUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_65
          |AFTER INSERT ON ClazzMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzMemberMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.clazzMemberLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzMember SET clazzMemberLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzMemberLocalChangeSeqNum 
          |ELSE (SELECT MAX(clazzMemberLocalChangeSeqNum) + 1 FROM ClazzMember) END),
          |clazzMemberMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzMemberMasterChangeSeqNum) + 1 FROM ClazzMember)
          |ELSE NEW.clazzMemberMasterChangeSeqNum END)
          |WHERE clazzMemberUid = NEW.clazzMemberUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzLog` (`clazzLogUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzLogClazzUid` INTEGER NOT NULL, `logDate` INTEGER NOT NULL, `timeRecorded` INTEGER NOT NULL, `clazzLogDone` INTEGER NOT NULL, `clazzLogCancelled` INTEGER NOT NULL, `clazzLogNumPresent` INTEGER NOT NULL, `clazzLogNumAbsent` INTEGER NOT NULL, `clazzLogNumPartial` INTEGER NOT NULL, `clazzLogScheduleUid` INTEGER NOT NULL, `clazzLogMSQN` INTEGER NOT NULL, `clazzLogLCSN` INTEGER NOT NULL, `clazzLogLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzLog_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzLog_trk_clientId_epk_rx_csn` ON `ClazzLog_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_14
          |AFTER UPDATE ON ClazzLog FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzLogMSQN = 0 
          |OR OLD.clazzLogMSQN = NEW.clazzLogMSQN
          |)
          |ELSE
          |(NEW.clazzLogLCSN = 0  
          |OR OLD.clazzLogLCSN = NEW.clazzLogLCSN
          |) END)
          |BEGIN 
          |UPDATE ClazzLog SET clazzLogLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzLogLCSN 
          |ELSE (SELECT MAX(MAX(clazzLogLCSN), OLD.clazzLogLCSN) + 1 FROM ClazzLog) END),
          |clazzLogMSQN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzLogMSQN), OLD.clazzLogMSQN) + 1 FROM ClazzLog)
          |ELSE NEW.clazzLogMSQN END)
          |WHERE clazzLogUid = NEW.clazzLogUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_14
          |AFTER INSERT ON ClazzLog FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzLogMSQN = 0 
          |
          |)
          |ELSE
          |(NEW.clazzLogLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzLog SET clazzLogLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzLogLCSN 
          |ELSE (SELECT MAX(clazzLogLCSN) + 1 FROM ClazzLog) END),
          |clazzLogMSQN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzLogMSQN) + 1 FROM ClazzLog)
          |ELSE NEW.clazzLogMSQN END)
          |WHERE clazzLogUid = NEW.clazzLogUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzLogAttendanceRecord` (`clazzLogAttendanceRecordUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzLogAttendanceRecordClazzLogUid` INTEGER NOT NULL, `clazzLogAttendanceRecordClazzMemberUid` INTEGER NOT NULL, `attendanceStatus` INTEGER NOT NULL, `clazzLogAttendanceRecordMasterChangeSeqNum` INTEGER NOT NULL, `clazzLogAttendanceRecordLocalChangeSeqNum` INTEGER NOT NULL, `clazzLogAttendanceRecordLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzLogAttendanceRecord_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzLogAttendanceRecord_trk_clientId_epk_rx_csn` ON `ClazzLogAttendanceRecord_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_15
          |AFTER UPDATE ON ClazzLogAttendanceRecord FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzLogAttendanceRecordMasterChangeSeqNum = 0 
          |OR OLD.clazzLogAttendanceRecordMasterChangeSeqNum = NEW.clazzLogAttendanceRecordMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.clazzLogAttendanceRecordLocalChangeSeqNum = 0  
          |OR OLD.clazzLogAttendanceRecordLocalChangeSeqNum = NEW.clazzLogAttendanceRecordLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ClazzLogAttendanceRecord SET clazzLogAttendanceRecordLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzLogAttendanceRecordLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(clazzLogAttendanceRecordLocalChangeSeqNum), OLD.clazzLogAttendanceRecordLocalChangeSeqNum) + 1 FROM ClazzLogAttendanceRecord) END),
          |clazzLogAttendanceRecordMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzLogAttendanceRecordMasterChangeSeqNum), OLD.clazzLogAttendanceRecordMasterChangeSeqNum) + 1 FROM ClazzLogAttendanceRecord)
          |ELSE NEW.clazzLogAttendanceRecordMasterChangeSeqNum END)
          |WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_15
          |AFTER INSERT ON ClazzLogAttendanceRecord FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzLogAttendanceRecordMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.clazzLogAttendanceRecordLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzLogAttendanceRecord SET clazzLogAttendanceRecordLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzLogAttendanceRecordLocalChangeSeqNum 
          |ELSE (SELECT MAX(clazzLogAttendanceRecordLocalChangeSeqNum) + 1 FROM ClazzLogAttendanceRecord) END),
          |clazzLogAttendanceRecordMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzLogAttendanceRecordMasterChangeSeqNum) + 1 FROM ClazzLogAttendanceRecord)
          |ELSE NEW.clazzLogAttendanceRecordMasterChangeSeqNum END)
          |WHERE clazzLogAttendanceRecordUid = NEW.clazzLogAttendanceRecordUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `School` (`schoolUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `schoolName` TEXT, `schoolDesc` TEXT, `schoolAddress` TEXT, `schoolActive` INTEGER NOT NULL, `schoolFeatures` INTEGER NOT NULL, `schoolLocationLong` REAL NOT NULL, `schoolLocationLatt` REAL NOT NULL, `schoolMasterChangeSeqNum` INTEGER NOT NULL, `schoolLocalChangeSeqNum` INTEGER NOT NULL, `schoolLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `School_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_School_trk_clientId_epk_rx_csn` ON `School_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_164
          |AFTER UPDATE ON School FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.schoolMasterChangeSeqNum = 0 
          |OR OLD.schoolMasterChangeSeqNum = NEW.schoolMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.schoolLocalChangeSeqNum = 0  
          |OR OLD.schoolLocalChangeSeqNum = NEW.schoolLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE School SET schoolLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.schoolLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(schoolLocalChangeSeqNum), OLD.schoolLocalChangeSeqNum) + 1 FROM School) END),
          |schoolMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(schoolMasterChangeSeqNum), OLD.schoolMasterChangeSeqNum) + 1 FROM School)
          |ELSE NEW.schoolMasterChangeSeqNum END)
          |WHERE schoolUid = NEW.schoolUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_164
          |AFTER INSERT ON School FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.schoolMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.schoolLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE School SET schoolLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.schoolLocalChangeSeqNum 
          |ELSE (SELECT MAX(schoolLocalChangeSeqNum) + 1 FROM School) END),
          |schoolMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(schoolMasterChangeSeqNum) + 1 FROM School)
          |ELSE NEW.schoolMasterChangeSeqNum END)
          |WHERE schoolUid = NEW.schoolUid
          |; END
          """.trimMargin())


                    database.execSQL("ALTER TABLE PersonCustomFieldValue RENAME to PersonCustomFieldValue_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonCustomFieldValue` (`personCustomFieldValueUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `personCustomFieldValuePersonCustomFieldUid` INTEGER NOT NULL, `personCustomFieldValuePersonUid` INTEGER NOT NULL, `fieldValue` TEXT, `personCustomFieldValueMasterChangeSeqNum` INTEGER NOT NULL, `personCustomFieldValueLocalChangeSeqNum` INTEGER NOT NULL, `personCustomFieldValueLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO PersonCustomFieldValue (personCustomFieldValueUid, personCustomFieldValuePersonCustomFieldUid, personCustomFieldValuePersonUid, fieldValue, personCustomFieldValueMasterChangeSeqNum, personCustomFieldValueLocalChangeSeqNum, personCustomFieldValueLastChangedBy) SELECT personCustomFieldValueUid, personCustomFieldValuePersonCustomFieldUid, personCustomFieldValuePersonUid, fieldValue, 0, 0, 0 FROM PersonCustomFieldValue_OLD")
                    database.execSQL("DROP TABLE PersonCustomFieldValue_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonCustomFieldValue_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_PersonCustomFieldValue_trk_clientId_epk_rx_csn` ON `PersonCustomFieldValue_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_178
          |AFTER UPDATE ON PersonCustomFieldValue FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.personCustomFieldValueMasterChangeSeqNum = 0 
          |OR OLD.personCustomFieldValueMasterChangeSeqNum = NEW.personCustomFieldValueMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.personCustomFieldValueLocalChangeSeqNum = 0  
          |OR OLD.personCustomFieldValueLocalChangeSeqNum = NEW.personCustomFieldValueLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE PersonCustomFieldValue SET personCustomFieldValueLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.personCustomFieldValueLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(personCustomFieldValueLocalChangeSeqNum), OLD.personCustomFieldValueLocalChangeSeqNum) + 1 FROM PersonCustomFieldValue) END),
          |personCustomFieldValueMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(personCustomFieldValueMasterChangeSeqNum), OLD.personCustomFieldValueMasterChangeSeqNum) + 1 FROM PersonCustomFieldValue)
          |ELSE NEW.personCustomFieldValueMasterChangeSeqNum END)
          |WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_178
          |AFTER INSERT ON PersonCustomFieldValue FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.personCustomFieldValueMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.personCustomFieldValueLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE PersonCustomFieldValue SET personCustomFieldValueLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.personCustomFieldValueLocalChangeSeqNum 
          |ELSE (SELECT MAX(personCustomFieldValueLocalChangeSeqNum) + 1 FROM PersonCustomFieldValue) END),
          |personCustomFieldValueMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(personCustomFieldValueMasterChangeSeqNum) + 1 FROM PersonCustomFieldValue)
          |ELSE NEW.personCustomFieldValueMasterChangeSeqNum END)
          |WHERE personCustomFieldValueUid = NEW.personCustomFieldValueUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `AuditLog` (`auditLogUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `auditLogMasterChangeSeqNum` INTEGER NOT NULL, `auditLogLocalChangeSeqNum` INTEGER NOT NULL, `auditLogLastChangedBy` INTEGER NOT NULL, `auditLogActorPersonUid` INTEGER NOT NULL, `auditLogTableUid` INTEGER NOT NULL, `auditLogEntityUid` INTEGER NOT NULL, `auditLogDate` INTEGER NOT NULL, `notes` TEXT)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `AuditLog_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_AuditLog_trk_clientId_epk_rx_csn` ON `AuditLog_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_53
          |AFTER UPDATE ON AuditLog FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.auditLogMasterChangeSeqNum = 0 
          |OR OLD.auditLogMasterChangeSeqNum = NEW.auditLogMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.auditLogLocalChangeSeqNum = 0  
          |OR OLD.auditLogLocalChangeSeqNum = NEW.auditLogLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE AuditLog SET auditLogLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.auditLogLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(auditLogLocalChangeSeqNum), OLD.auditLogLocalChangeSeqNum) + 1 FROM AuditLog) END),
          |auditLogMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(auditLogMasterChangeSeqNum), OLD.auditLogMasterChangeSeqNum) + 1 FROM AuditLog)
          |ELSE NEW.auditLogMasterChangeSeqNum END)
          |WHERE auditLogUid = NEW.auditLogUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_53
          |AFTER INSERT ON AuditLog FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.auditLogMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.auditLogLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE AuditLog SET auditLogLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.auditLogLocalChangeSeqNum 
          |ELSE (SELECT MAX(auditLogLocalChangeSeqNum) + 1 FROM AuditLog) END),
          |auditLogMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(auditLogMasterChangeSeqNum) + 1 FROM AuditLog)
          |ELSE NEW.auditLogMasterChangeSeqNum END)
          |WHERE auditLogUid = NEW.auditLogUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomField` (`customFieldUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customFieldName` TEXT, `customFieldNameAlt` TEXT, `customFieldLabelMessageID` INTEGER NOT NULL, `customFieldIcon` TEXT, `customFieldType` INTEGER NOT NULL,  `customFieldActive` INTEGER NOT NULL, `customFieldDefaultValue` TEXT, `customFieldMCSN` INTEGER NOT NULL, `customFieldLCSN` INTEGER NOT NULL, `customFieldLCB` INTEGER NOT NULL, customFieldEntityType INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomField_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_CustomField_trk_clientId_epk_rx_csn` ON `CustomField_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_56
          |AFTER UPDATE ON CustomField FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldMCSN = 0 
          |OR OLD.customFieldMCSN = NEW.customFieldMCSN
          |)
          |ELSE
          |(NEW.customFieldLCSN = 0  
          |OR OLD.customFieldLCSN = NEW.customFieldLCSN
          |) END)
          |BEGIN 
          |UPDATE CustomField SET customFieldLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldLCSN 
          |ELSE (SELECT MAX(MAX(customFieldLCSN), OLD.customFieldLCSN) + 1 FROM CustomField) END),
          |customFieldMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(customFieldMCSN), OLD.customFieldMCSN) + 1 FROM CustomField)
          |ELSE NEW.customFieldMCSN END)
          |WHERE customFieldUid = NEW.customFieldUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_56
          |AFTER INSERT ON CustomField FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.customFieldLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE CustomField SET customFieldLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldLCSN 
          |ELSE (SELECT MAX(customFieldLCSN) + 1 FROM CustomField) END),
          |customFieldMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(customFieldMCSN) + 1 FROM CustomField)
          |ELSE NEW.customFieldMCSN END)
          |WHERE customFieldUid = NEW.customFieldUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomFieldValue` (`customFieldValueUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customFieldValueFieldUid` INTEGER NOT NULL, `customFieldValueEntityUid` INTEGER NOT NULL, `customFieldValueValue` TEXT, `customFieldValueMCSN` INTEGER NOT NULL, `customFieldValueLCSN` INTEGER NOT NULL, `customFieldValueLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomFieldValue_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_CustomFieldValue_trk_clientId_epk_rx_csn` ON `CustomFieldValue_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_57
          |AFTER UPDATE ON CustomFieldValue FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldValueMCSN = 0 
          |OR OLD.customFieldValueMCSN = NEW.customFieldValueMCSN
          |)
          |ELSE
          |(NEW.customFieldValueLCSN = 0  
          |OR OLD.customFieldValueLCSN = NEW.customFieldValueLCSN
          |) END)
          |BEGIN 
          |UPDATE CustomFieldValue SET customFieldValueLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldValueLCSN 
          |ELSE (SELECT MAX(MAX(customFieldValueLCSN), OLD.customFieldValueLCSN) + 1 FROM CustomFieldValue) END),
          |customFieldValueMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(customFieldValueMCSN), OLD.customFieldValueMCSN) + 1 FROM CustomFieldValue)
          |ELSE NEW.customFieldValueMCSN END)
          |WHERE customFieldValueUid = NEW.customFieldValueUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_57
          |AFTER INSERT ON CustomFieldValue FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldValueMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.customFieldValueLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE CustomFieldValue SET customFieldValueLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldValueLCSN 
          |ELSE (SELECT MAX(customFieldValueLCSN) + 1 FROM CustomFieldValue) END),
          |customFieldValueMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(customFieldValueMCSN) + 1 FROM CustomFieldValue)
          |ELSE NEW.customFieldValueMCSN END)
          |WHERE customFieldValueUid = NEW.customFieldValueUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomFieldValueOption` (`customFieldValueOptionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customFieldValueOptionName` TEXT, `customFieldValueOptionFieldUid` INTEGER NOT NULL, `customFieldValueOptionIcon` TEXT, `customFieldValueOptionMessageId` INTEGER NOT NULL, `customFieldValueOptionActive` INTEGER NOT NULL, `customFieldValueOptionMCSN` INTEGER NOT NULL, `customFieldValueOptionLCSN` INTEGER NOT NULL, `customFieldValueOptionLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `CustomFieldValueOption_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_CustomFieldValueOption_trk_clientId_epk_rx_csn` ON `CustomFieldValueOption_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_55
          |AFTER UPDATE ON CustomFieldValueOption FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldValueOptionMCSN = 0 
          |OR OLD.customFieldValueOptionMCSN = NEW.customFieldValueOptionMCSN
          |)
          |ELSE
          |(NEW.customFieldValueOptionLCSN = 0  
          |OR OLD.customFieldValueOptionLCSN = NEW.customFieldValueOptionLCSN
          |) END)
          |BEGIN 
          |UPDATE CustomFieldValueOption SET customFieldValueOptionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldValueOptionLCSN 
          |ELSE (SELECT MAX(MAX(customFieldValueOptionLCSN), OLD.customFieldValueOptionLCSN) + 1 FROM CustomFieldValueOption) END),
          |customFieldValueOptionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(customFieldValueOptionMCSN), OLD.customFieldValueOptionMCSN) + 1 FROM CustomFieldValueOption)
          |ELSE NEW.customFieldValueOptionMCSN END)
          |WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_55
          |AFTER INSERT ON CustomFieldValueOption FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.customFieldValueOptionMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.customFieldValueOptionLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE CustomFieldValueOption SET customFieldValueOptionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.customFieldValueOptionLCSN 
          |ELSE (SELECT MAX(customFieldValueOptionLCSN) + 1 FROM CustomFieldValueOption) END),
          |customFieldValueOptionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(customFieldValueOptionMCSN) + 1 FROM CustomFieldValueOption)
          |ELSE NEW.customFieldValueOptionMCSN END)
          |WHERE customFieldValueOptionUid = NEW.customFieldValueOptionUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `DateRange` (`dateRangeUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateRangeLocalChangeSeqNum` INTEGER NOT NULL, `dateRangeMasterChangeSeqNum` INTEGER NOT NULL, `dateRangLastChangedBy` INTEGER NOT NULL, `dateRangeFromDate` INTEGER NOT NULL, `dateRangeToDate` INTEGER NOT NULL, `dateRangeUMCalendarUid` INTEGER NOT NULL, `dateRangeName` TEXT, `dateRangeDesc` TEXT, `dateRangeActive` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `DateRange_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_DateRange_trk_clientId_epk_rx_csn` ON `DateRange_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_17
          |AFTER UPDATE ON DateRange FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.dateRangeMasterChangeSeqNum = 0 
          |OR OLD.dateRangeMasterChangeSeqNum = NEW.dateRangeMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.dateRangeLocalChangeSeqNum = 0  
          |OR OLD.dateRangeLocalChangeSeqNum = NEW.dateRangeLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE DateRange SET dateRangeLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.dateRangeLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(dateRangeLocalChangeSeqNum), OLD.dateRangeLocalChangeSeqNum) + 1 FROM DateRange) END),
          |dateRangeMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(dateRangeMasterChangeSeqNum), OLD.dateRangeMasterChangeSeqNum) + 1 FROM DateRange)
          |ELSE NEW.dateRangeMasterChangeSeqNum END)
          |WHERE dateRangeUid = NEW.dateRangeUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_17
          |AFTER INSERT ON DateRange FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.dateRangeMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.dateRangeLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE DateRange SET dateRangeLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.dateRangeLocalChangeSeqNum 
          |ELSE (SELECT MAX(dateRangeLocalChangeSeqNum) + 1 FROM DateRange) END),
          |dateRangeMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(dateRangeMasterChangeSeqNum) + 1 FROM DateRange)
          |ELSE NEW.dateRangeMasterChangeSeqNum END)
          |WHERE dateRangeUid = NEW.dateRangeUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonField` (`personCustomFieldUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fieldName` TEXT, `labelMessageId` INTEGER NOT NULL, `fieldIcon` TEXT, `personFieldMasterChangeSeqNum` INTEGER NOT NULL, `personFieldLocalChangeSeqNum` INTEGER NOT NULL, `personFieldLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonField_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_PersonField_trk_clientId_epk_rx_csn` ON `PersonField_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonDetailPresenterField` (`personDetailPresenterFieldMasterChangeSeqNum` INTEGER NOT NULL, `personDetailPresenterFieldLocalChangeSeqNum` INTEGER NOT NULL, `personDetailPresenterFieldLastChangedBy` INTEGER NOT NULL, `personDetailPresenterFieldUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fieldUid` INTEGER NOT NULL, `fieldType` INTEGER NOT NULL, `fieldIndex` INTEGER NOT NULL, `labelMessageId` INTEGER NOT NULL, `fieldIcon` TEXT, `headerMessageId` INTEGER NOT NULL, `viewModeVisible` INTEGER NOT NULL, `editModeVisible` INTEGER NOT NULL, `isReadyOnly` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonDetailPresenterField_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_PersonDetailPresenterField_trk_clientId_epk_rx_csn` ON `PersonDetailPresenterField_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `Schedule` (`scheduleUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sceduleStartTime` INTEGER NOT NULL, `scheduleEndTime` INTEGER NOT NULL, `scheduleDay` INTEGER NOT NULL, `scheduleMonth` INTEGER NOT NULL, `scheduleFrequency` INTEGER NOT NULL, `umCalendarUid` INTEGER NOT NULL, `scheduleClazzUid` INTEGER NOT NULL, `scheduleMasterChangeSeqNum` INTEGER NOT NULL, `scheduleLocalChangeSeqNum` INTEGER NOT NULL, `scheduleLastChangedBy` INTEGER NOT NULL, `scheduleActive` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `Schedule_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_Schedule_trk_clientId_epk_rx_csn` ON `Schedule_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_21
          |AFTER UPDATE ON Schedule FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.scheduleMasterChangeSeqNum = 0 
          |OR OLD.scheduleMasterChangeSeqNum = NEW.scheduleMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.scheduleLocalChangeSeqNum = 0  
          |OR OLD.scheduleLocalChangeSeqNum = NEW.scheduleLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE Schedule SET scheduleLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.scheduleLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(scheduleLocalChangeSeqNum), OLD.scheduleLocalChangeSeqNum) + 1 FROM Schedule) END),
          |scheduleMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(scheduleMasterChangeSeqNum), OLD.scheduleMasterChangeSeqNum) + 1 FROM Schedule)
          |ELSE NEW.scheduleMasterChangeSeqNum END)
          |WHERE scheduleUid = NEW.scheduleUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_21
          |AFTER INSERT ON Schedule FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.scheduleMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.scheduleLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE Schedule SET scheduleLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.scheduleLocalChangeSeqNum 
          |ELSE (SELECT MAX(scheduleLocalChangeSeqNum) + 1 FROM Schedule) END),
          |scheduleMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(scheduleMasterChangeSeqNum) + 1 FROM Schedule)
          |ELSE NEW.scheduleMasterChangeSeqNum END)
          |WHERE scheduleUid = NEW.scheduleUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ScheduledCheck` (`scheduledCheckUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `checkTime` INTEGER NOT NULL, `checkType` INTEGER NOT NULL, `checkUuid` TEXT, `checkParameters` TEXT, `scClazzLogUid` INTEGER NOT NULL, `scheduledCheckMasterCsn` INTEGER NOT NULL, `scheduledCheckLocalCsn` INTEGER NOT NULL, `scheduledCheckLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ScheduledCheck_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ScheduledCheck_trk_clientId_epk_rx_csn` ON `ScheduledCheck_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_173
          |AFTER UPDATE ON ScheduledCheck FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.scheduledCheckMasterCsn = 0 
          |OR OLD.scheduledCheckMasterCsn = NEW.scheduledCheckMasterCsn
          |)
          |ELSE
          |(NEW.scheduledCheckLocalCsn = 0  
          |OR OLD.scheduledCheckLocalCsn = NEW.scheduledCheckLocalCsn
          |) END)
          |BEGIN 
          |UPDATE ScheduledCheck SET scheduledCheckLocalCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.scheduledCheckLocalCsn 
          |ELSE (SELECT MAX(MAX(scheduledCheckLocalCsn), OLD.scheduledCheckLocalCsn) + 1 FROM ScheduledCheck) END),
          |scheduledCheckMasterCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(scheduledCheckMasterCsn), OLD.scheduledCheckMasterCsn) + 1 FROM ScheduledCheck)
          |ELSE NEW.scheduledCheckMasterCsn END)
          |WHERE scheduledCheckUid = NEW.scheduledCheckUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_173
          |AFTER INSERT ON ScheduledCheck FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.scheduledCheckMasterCsn = 0 
          |
          |)
          |ELSE
          |(NEW.scheduledCheckLocalCsn = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ScheduledCheck SET scheduledCheckLocalCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.scheduledCheckLocalCsn 
          |ELSE (SELECT MAX(scheduledCheckLocalCsn) + 1 FROM ScheduledCheck) END),
          |scheduledCheckMasterCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(scheduledCheckMasterCsn) + 1 FROM ScheduledCheck)
          |ELSE NEW.scheduledCheckMasterCsn END)
          |WHERE scheduledCheckUid = NEW.scheduledCheckUid
          |; END
          """.trimMargin())



                }

                println("finished migration from 34 to 35")
            }
        }

        val MIGRATION_35_36 = object : DoorMigration(35, 36) {
            override fun migrate(database: DoorSqlDatabase) {

                database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzTimeZone TEXT
                        """.trimMargin())

                database.execSQL("""ALTER TABLE Person
                        ADD COLUMN personOrgId TEXT""".trimMargin())

                database.execSQL("DROP TABLE IF EXISTS FeedEntry")
                database.execSQL("DROP TABLE IF EXISTS SelQuestion")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionOption")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionResponse")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionResponseNomination")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSet")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSetRecognition")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSetResponse")
                database.execSQL("DROP TABLE IF EXISTS ClazzActivity")
                database.execSQL("DROP TABLE IF EXISTS ClazzActivityChange")
                database.execSQL("DROP TABLE IF EXISTS UMCalendar")
                database.execSQL("DROP TABLE IF EXISTS ClazzAssignmentContentJoin")
                database.execSQL("DROP TABLE IF EXISTS ClazzAssignment")
                database.execSQL("DROP TABLE IF EXISTS Location")
                database.execSQL("DROP TABLE IF EXISTS Location_trk")
                database.execSQL("DROP TABLE IF EXISTS LocationAncestorJoin")
                database.execSQL("DROP TABLE IF EXISTS PersonLocationJoin")
                database.execSQL("DROP TABLE IF EXISTS PersonLocationJoin_trk")
                database.execSQL("DROP TABLE IF EXISTS FeedEntry_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestion_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionOption_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionResponse_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionResponseNomination_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSet_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSetRecognition_trk")
                database.execSQL("DROP TABLE IF EXISTS SelQuestionSetResponse_trk")
                database.execSQL("DROP TABLE IF EXISTS ClazzActivity_trk")
                database.execSQL("DROP TABLE IF EXISTS ClazzActivityChange_trk")
                database.execSQL("DROP TABLE IF EXISTS UMCalendar_trk")
                database.execSQL("DROP TABLE IF EXISTS ClazzAssignmentContentJoin_trk")
                database.execSQL("DROP TABLE IF EXISTS ClazzAssignment_trk")


                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzStudentsPersonGroupUid INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzTeachersPersonGroupUid INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzSchoolUid  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE School ADD COLUMN schoolHolidayCalendarUid INTEGER DEFAULT 0 NOT NULL""".trimMargin())
                    database.execSQL("""ALTER TABLE School ADD COLUMN schoolStudentsPersonGroupUid INTEGER DEFAULT 0 NOT NULL""".trimMargin())
                    database.execSQL("""ALTER TABLE School ADD COLUMN schoolTeachersPersonGroupUid INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE PersonPicture 
                        ADD COLUMN personPictureActive INTEGER DEFAULT 1 NOT NULL""".trimMargin())

                    database.execSQL("""ALTER TABLE CustomFieldValue 
                        ADD COLUMN customFieldValueCustomFieldValueOptionUid  INTEGER DEFAULT 0 NOT NULL
                        """.trimMargin())

                    database.execSQL("ALTER TABLE StatementEntity RENAME to StatementEntity_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `StatementEntity` (`statementUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `statementId` TEXT, `statementPersonUid` INTEGER NOT NULL, `statementVerbUid` INTEGER NOT NULL, `xObjectUid` INTEGER NOT NULL, `subStatementActorUid` INTEGER NOT NULL, `substatementVerbUid` INTEGER NOT NULL, `subStatementObjectUid` INTEGER NOT NULL, `agentUid` INTEGER NOT NULL, `instructorUid` INTEGER NOT NULL, `authorityUid` INTEGER NOT NULL, `teamUid` INTEGER NOT NULL, `resultCompletion` INTEGER NOT NULL, `resultSuccess` INTEGER NOT NULL, `resultScoreScaled` INTEGER NOT NULL, `resultScoreRaw` INTEGER NOT NULL, `resultScoreMin` INTEGER NOT NULL, `resultScoreMax` INTEGER NOT NULL, `resultDuration` INTEGER NOT NULL, `resultResponse` TEXT, `timestamp` INTEGER NOT NULL, `stored` INTEGER NOT NULL, `contextRegistration` TEXT, `contextPlatform` TEXT, `contextStatementId` TEXT, `fullStatement` TEXT, `statementMasterChangeSeqNum` INTEGER NOT NULL, `statementLocalChangeSeqNum` INTEGER NOT NULL, `statementLastChangedBy` INTEGER NOT NULL, `extensionProgress` INTEGER NOT NULL, `statementContentEntryUid` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO StatementEntity (statementUid, statementId, statementPersonUid, statementVerbUid, xObjectUid, subStatementActorUid, substatementVerbUid, subStatementObjectUid, agentUid, instructorUid, authorityUid, teamUid, resultCompletion, resultSuccess, resultScoreScaled, resultScoreRaw, resultScoreMin, resultScoreMax, resultDuration, resultResponse, timestamp, stored, contextRegistration, contextPlatform, contextStatementId, fullStatement, statementMasterChangeSeqNum, statementLocalChangeSeqNum, statementLastChangedBy, extensionProgress, statementContentEntryUid) SELECT statementUid, statementId, personuid, verbUid, xObjectUid, subStatementActorUid, substatementVerbUid, subStatementObjectUid, agentUid, instructorUid, authorityUid, teamUid, resultCompletion, resultSuccess, resultScoreScaled, resultScoreRaw, resultScoreMin, resultScoreMax, resultDuration, resultResponse, timestamp, stored, contextRegistration, contextPlatform, contextStatementId, fullStatement, statementMasterChangeSeqNum, statementLocalChangeSeqNum, statementLastChangedBy, extensionProgress, statementContentEntryUid FROM StatementEntity_OLD")
                    database.execSQL("DROP TABLE StatementEntity_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `StatementEntity_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_StatementEntity_trk_clientId_epk_rx_csn` ON `StatementEntity_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWork` (`clazzWorkUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkCreatorPersonUid` INTEGER NOT NULL, `clazzWorkClazzUid` INTEGER NOT NULL, `clazzWorkTitle` TEXT, `clazzWorkCreatedDate` INTEGER NOT NULL, `clazzWorkStartDateTime` INTEGER NOT NULL, `clazzWorkStartTime` INTEGER NOT NULL, `clazzWorkDueTime` INTEGER NOT NULL, `clazzWorkDueDateTime` INTEGER NOT NULL, `clazzWorkSubmissionType` INTEGER NOT NULL, `clazzWorkCommentsEnabled` INTEGER NOT NULL, `clazzWorkMaximumScore` INTEGER NOT NULL, `clazzWorkInstructions` TEXT, `clazzWorkActive` INTEGER NOT NULL, `clazzWorkLocalChangeSeqNum` INTEGER NOT NULL, `clazzWorkMasterChangeSeqNum` INTEGER NOT NULL, `clazzWorkLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWork_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWork_trk_clientId_epk_rx_csn` ON `ClazzWork_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_201
          |AFTER UPDATE ON ClazzWork FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkMasterChangeSeqNum = 0 
          |OR OLD.clazzWorkMasterChangeSeqNum = NEW.clazzWorkMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.clazzWorkLocalChangeSeqNum = 0  
          |OR OLD.clazzWorkLocalChangeSeqNum = NEW.clazzWorkLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ClazzWork SET clazzWorkLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(clazzWorkLocalChangeSeqNum), OLD.clazzWorkLocalChangeSeqNum) + 1 FROM ClazzWork) END),
          |clazzWorkMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkMasterChangeSeqNum), OLD.clazzWorkMasterChangeSeqNum) + 1 FROM ClazzWork)
          |ELSE NEW.clazzWorkMasterChangeSeqNum END)
          |WHERE clazzWorkUid = NEW.clazzWorkUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_201
          |AFTER INSERT ON ClazzWork FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWork SET clazzWorkLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkLocalChangeSeqNum 
          |ELSE (SELECT MAX(clazzWorkLocalChangeSeqNum) + 1 FROM ClazzWork) END),
          |clazzWorkMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkMasterChangeSeqNum) + 1 FROM ClazzWork)
          |ELSE NEW.clazzWorkMasterChangeSeqNum END)
          |WHERE clazzWorkUid = NEW.clazzWorkUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestion` (`clazzWorkQuestionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkQuestionText` TEXT, `clazzWorkQuestionClazzWorkUid` INTEGER NOT NULL, `clazzWorkQuestionIndex` INTEGER NOT NULL, `clazzWorkQuestionType` INTEGER NOT NULL, `clazzWorkQuestionActive` INTEGER NOT NULL, `clazzWorkQuestionMCSN` INTEGER NOT NULL, `clazzWorkQuestionLCSN` INTEGER NOT NULL, `clazzWorkQuestionLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestion_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWorkQuestion_trk_clientId_epk_rx_csn` ON `ClazzWorkQuestion_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_202
          |AFTER UPDATE ON ClazzWorkQuestion FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionMCSN = 0 
          |OR OLD.clazzWorkQuestionMCSN = NEW.clazzWorkQuestionMCSN
          |)
          |ELSE
          |(NEW.clazzWorkQuestionLCSN = 0  
          |OR OLD.clazzWorkQuestionLCSN = NEW.clazzWorkQuestionLCSN
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestion SET clazzWorkQuestionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionLCSN 
          |ELSE (SELECT MAX(MAX(clazzWorkQuestionLCSN), OLD.clazzWorkQuestionLCSN) + 1 FROM ClazzWorkQuestion) END),
          |clazzWorkQuestionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkQuestionMCSN), OLD.clazzWorkQuestionMCSN) + 1 FROM ClazzWorkQuestion)
          |ELSE NEW.clazzWorkQuestionMCSN END)
          |WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_202
          |AFTER INSERT ON ClazzWorkQuestion FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkQuestionLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestion SET clazzWorkQuestionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionLCSN 
          |ELSE (SELECT MAX(clazzWorkQuestionLCSN) + 1 FROM ClazzWorkQuestion) END),
          |clazzWorkQuestionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkQuestionMCSN) + 1 FROM ClazzWorkQuestion)
          |ELSE NEW.clazzWorkQuestionMCSN END)
          |WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestionOption` (`clazzWorkQuestionOptionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkQuestionOptionText` TEXT, `clazzWorkQuestionOptionQuestionUid` INTEGER NOT NULL, `clazzWorkQuestionOptionMasterChangeSeqNum` INTEGER NOT NULL, `clazzWorkQuestionOptionLocalChangeSeqNum` INTEGER NOT NULL, `clazzWorkQuestionOptionLastChangedBy` INTEGER NOT NULL, `clazzWorkQuestionOptionActive` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestionOption_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWorkQuestionOption_trk_clientId_epk_rx_csn` ON `ClazzWorkQuestionOption_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_203
          |AFTER UPDATE ON ClazzWorkQuestionOption FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionOptionMasterChangeSeqNum = 0 
          |OR OLD.clazzWorkQuestionOptionMasterChangeSeqNum = NEW.clazzWorkQuestionOptionMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.clazzWorkQuestionOptionLocalChangeSeqNum = 0  
          |OR OLD.clazzWorkQuestionOptionLocalChangeSeqNum = NEW.clazzWorkQuestionOptionLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestionOption SET clazzWorkQuestionOptionLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionOptionLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(clazzWorkQuestionOptionLocalChangeSeqNum), OLD.clazzWorkQuestionOptionLocalChangeSeqNum) + 1 FROM ClazzWorkQuestionOption) END),
          |clazzWorkQuestionOptionMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkQuestionOptionMasterChangeSeqNum), OLD.clazzWorkQuestionOptionMasterChangeSeqNum) + 1 FROM ClazzWorkQuestionOption)
          |ELSE NEW.clazzWorkQuestionOptionMasterChangeSeqNum END)
          |WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_203
          |AFTER INSERT ON ClazzWorkQuestionOption FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionOptionMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkQuestionOptionLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestionOption SET clazzWorkQuestionOptionLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionOptionLocalChangeSeqNum 
          |ELSE (SELECT MAX(clazzWorkQuestionOptionLocalChangeSeqNum) + 1 FROM ClazzWorkQuestionOption) END),
          |clazzWorkQuestionOptionMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkQuestionOptionMasterChangeSeqNum) + 1 FROM ClazzWorkQuestionOption)
          |ELSE NEW.clazzWorkQuestionOptionMasterChangeSeqNum END)
          |WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestionResponse` (`clazzWorkQuestionResponseUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkQuestionResponseClazzWorkUid` INTEGER NOT NULL, `clazzWorkQuestionResponseQuestionUid` INTEGER NOT NULL, `clazzWorkQuestionResponseText` TEXT, `clazzWorkQuestionResponseOptionSelected` INTEGER NOT NULL, `clazzWorkQuestionResponsePersonUid` INTEGER NOT NULL, `clazzWorkQuestionResponseClazzMemberUid` INTEGER NOT NULL, `clazzWorkQuestionResponseInactive` INTEGER NOT NULL, `clazzWorkQuestionResponseDateResponded` INTEGER NOT NULL, `clazzWorkQuestionResponseMCSN` INTEGER NOT NULL, `clazzWorkQuestionResponseLCSN` INTEGER NOT NULL, `clazzWorkQuestionResponseLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkQuestionResponse_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWorkQuestionResponse_trk_clientId_epk_rx_csn` ON `ClazzWorkQuestionResponse_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_209
          |AFTER UPDATE ON ClazzWorkQuestionResponse FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionResponseMCSN = 0 
          |OR OLD.clazzWorkQuestionResponseMCSN = NEW.clazzWorkQuestionResponseMCSN
          |)
          |ELSE
          |(NEW.clazzWorkQuestionResponseLCSN = 0  
          |OR OLD.clazzWorkQuestionResponseLCSN = NEW.clazzWorkQuestionResponseLCSN
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestionResponse SET clazzWorkQuestionResponseLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionResponseLCSN 
          |ELSE (SELECT MAX(MAX(clazzWorkQuestionResponseLCSN), OLD.clazzWorkQuestionResponseLCSN) + 1 FROM ClazzWorkQuestionResponse) END),
          |clazzWorkQuestionResponseMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkQuestionResponseMCSN), OLD.clazzWorkQuestionResponseMCSN) + 1 FROM ClazzWorkQuestionResponse)
          |ELSE NEW.clazzWorkQuestionResponseMCSN END)
          |WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_209
          |AFTER INSERT ON ClazzWorkQuestionResponse FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkQuestionResponseMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkQuestionResponseLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkQuestionResponse SET clazzWorkQuestionResponseLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionResponseLCSN 
          |ELSE (SELECT MAX(clazzWorkQuestionResponseLCSN) + 1 FROM ClazzWorkQuestionResponse) END),
          |clazzWorkQuestionResponseMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkQuestionResponseMCSN) + 1 FROM ClazzWorkQuestionResponse)
          |ELSE NEW.clazzWorkQuestionResponseMCSN END)
          |WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkSubmission` (`clazzWorkSubmissionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkSubmissionClazzWorkUid` INTEGER NOT NULL, `clazzWorkSubmissionClazzMemberUid` INTEGER NOT NULL, `clazzWorkSubmissionMarkerClazzMemberUid` INTEGER NOT NULL, `clazzWorkSubmissionMarkerPersonUid` INTEGER NOT NULL, `clazzWorkSubmissionPersonUid` INTEGER NOT NULL, `clazzWorkSubmissionInactive` INTEGER NOT NULL, `clazzWorkSubmissionDateTimeStarted` INTEGER NOT NULL, `clazzWorkSubmissionDateTimeUpdated` INTEGER NOT NULL, `clazzWorkSubmissionDateTimeFinished` INTEGER NOT NULL, `clazzWorkSubmissionDateTimeMarked` INTEGER NOT NULL, `clazzWorkSubmissionText` TEXT, `clazzWorkSubmissionScore` INTEGER NOT NULL, `clazzWorkSubmissionMCSN` INTEGER NOT NULL, `clazzWorkSubmissionLCSN` INTEGER NOT NULL, `clazzWorkSubmissionLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkSubmission_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWorkSubmission_trk_clientId_epk_rx_csn` ON `ClazzWorkSubmission_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_206
          |AFTER UPDATE ON ClazzWorkSubmission FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkSubmissionMCSN = 0 
          |OR OLD.clazzWorkSubmissionMCSN = NEW.clazzWorkSubmissionMCSN
          |)
          |ELSE
          |(NEW.clazzWorkSubmissionLCSN = 0  
          |OR OLD.clazzWorkSubmissionLCSN = NEW.clazzWorkSubmissionLCSN
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkSubmission SET clazzWorkSubmissionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkSubmissionLCSN 
          |ELSE (SELECT MAX(MAX(clazzWorkSubmissionLCSN), OLD.clazzWorkSubmissionLCSN) + 1 FROM ClazzWorkSubmission) END),
          |clazzWorkSubmissionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkSubmissionMCSN), OLD.clazzWorkSubmissionMCSN) + 1 FROM ClazzWorkSubmission)
          |ELSE NEW.clazzWorkSubmissionMCSN END)
          |WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_206
          |AFTER INSERT ON ClazzWorkSubmission FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkSubmissionMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkSubmissionLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkSubmission SET clazzWorkSubmissionLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkSubmissionLCSN 
          |ELSE (SELECT MAX(clazzWorkSubmissionLCSN) + 1 FROM ClazzWorkSubmission) END),
          |clazzWorkSubmissionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkSubmissionMCSN) + 1 FROM ClazzWorkSubmission)
          |ELSE NEW.clazzWorkSubmissionMCSN END)
          |WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `Comments` (`commentsUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `commentsText` TEXT, `commentsEntityType` INTEGER NOT NULL, `commentsEntityUid` INTEGER NOT NULL, `commentsPublic` INTEGER NOT NULL, `commentsStatus` INTEGER NOT NULL, `commentsPersonUid` INTEGER NOT NULL, `commentsToPersonUid` INTEGER NOT NULL, `commentsFlagged` INTEGER NOT NULL, `commentsInActive` INTEGER NOT NULL, `commentsDateTimeAdded` INTEGER NOT NULL, `commentsDateTimeUpdated` INTEGER NOT NULL, `commentsMCSN` INTEGER NOT NULL, `commentsLCSN` INTEGER NOT NULL, `commentsLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `Comments_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_Comments_trk_clientId_epk_rx_csn` ON `Comments_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_208
          |AFTER UPDATE ON Comments FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.commentsMCSN = 0 
          |OR OLD.commentsMCSN = NEW.commentsMCSN
          |)
          |ELSE
          |(NEW.commentsLCSN = 0  
          |OR OLD.commentsLCSN = NEW.commentsLCSN
          |) END)
          |BEGIN 
          |UPDATE Comments SET commentsLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.commentsLCSN 
          |ELSE (SELECT MAX(MAX(commentsLCSN), OLD.commentsLCSN) + 1 FROM Comments) END),
          |commentsMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(commentsMCSN), OLD.commentsMCSN) + 1 FROM Comments)
          |ELSE NEW.commentsMCSN END)
          |WHERE commentsUid = NEW.commentsUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_208
          |AFTER INSERT ON Comments FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.commentsMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.commentsLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE Comments SET commentsLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.commentsLCSN 
          |ELSE (SELECT MAX(commentsLCSN) + 1 FROM Comments) END),
          |commentsMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(commentsMCSN) + 1 FROM Comments)
          |ELSE NEW.commentsMCSN END)
          |WHERE commentsUid = NEW.commentsUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ContainerUploadJob` (`cujUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cujContainerUid` INTEGER NOT NULL, `sessionId` TEXT, `jobStatus` INTEGER NOT NULL, `bytesSoFar` INTEGER NOT NULL, `contentLength` INTEGER NOT NULL, `containerEntryFileUids` TEXT)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `SchoolMember` (`schoolMemberUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `schoolMemberPersonUid` INTEGER NOT NULL, `schoolMemberSchoolUid` INTEGER NOT NULL, `schoolMemberJoinDate` INTEGER NOT NULL, `schoolMemberLeftDate` INTEGER NOT NULL, `schoolMemberRole` INTEGER NOT NULL, `schoolMemberActive` INTEGER NOT NULL, `schoolMemberLocalChangeSeqNum` INTEGER NOT NULL, `schoolMemberMasterChangeSeqNum` INTEGER NOT NULL, `schoolMemberLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SchoolMember_schoolMemberPersonUid` ON `SchoolMember` (`schoolMemberPersonUid`)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SchoolMember_schoolMemberSchoolUid` ON `SchoolMember` (`schoolMemberSchoolUid`)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `SchoolMember_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_SchoolMember_trk_clientId_epk_rx_csn` ON `SchoolMember_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_200
          |AFTER UPDATE ON SchoolMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.schoolMemberMasterChangeSeqNum = 0 
          |OR OLD.schoolMemberMasterChangeSeqNum = NEW.schoolMemberMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.schoolMemberLocalChangeSeqNum = 0  
          |OR OLD.schoolMemberLocalChangeSeqNum = NEW.schoolMemberLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE SchoolMember SET schoolMemberLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.schoolMemberLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(schoolMemberLocalChangeSeqNum), OLD.schoolMemberLocalChangeSeqNum) + 1 FROM SchoolMember) END),
          |schoolMemberMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(schoolMemberMasterChangeSeqNum), OLD.schoolMemberMasterChangeSeqNum) + 1 FROM SchoolMember)
          |ELSE NEW.schoolMemberMasterChangeSeqNum END)
          |WHERE schoolMemberUid = NEW.schoolMemberUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_200
          |AFTER INSERT ON SchoolMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.schoolMemberMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.schoolMemberLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE SchoolMember SET schoolMemberLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.schoolMemberLocalChangeSeqNum 
          |ELSE (SELECT MAX(schoolMemberLocalChangeSeqNum) + 1 FROM SchoolMember) END),
          |schoolMemberMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(schoolMemberMasterChangeSeqNum) + 1 FROM SchoolMember)
          |ELSE NEW.schoolMemberMasterChangeSeqNum END)
          |WHERE schoolMemberUid = NEW.schoolMemberUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `ContentEntryProgress` (`contentEntryProgressUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `contentEntryProgressActive` INTEGER NOT NULL, `contentEntryProgressContentEntryUid` INTEGER NOT NULL, `contentEntryProgressPersonUid` INTEGER NOT NULL, `contentEntryProgressProgress` INTEGER NOT NULL, `contentEntryProgressStatusFlag` INTEGER NOT NULL, `contentEntryProgressLocalChangeSeqNum` INTEGER NOT NULL, `contentEntryProgressMasterChangeSeqNum` INTEGER NOT NULL, `contentEntryProgressLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ContentEntryProgress_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ContentEntryProgress_trk_clientId_epk_rx_csn` ON `ContentEntryProgress_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_210
          |AFTER UPDATE ON ContentEntryProgress FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.contentEntryProgressMasterChangeSeqNum = 0 
          |OR OLD.contentEntryProgressMasterChangeSeqNum = NEW.contentEntryProgressMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.contentEntryProgressLocalChangeSeqNum = 0  
          |OR OLD.contentEntryProgressLocalChangeSeqNum = NEW.contentEntryProgressLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ContentEntryProgress SET contentEntryProgressLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.contentEntryProgressLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(contentEntryProgressLocalChangeSeqNum), OLD.contentEntryProgressLocalChangeSeqNum) + 1 FROM ContentEntryProgress) END),
          |contentEntryProgressMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(contentEntryProgressMasterChangeSeqNum), OLD.contentEntryProgressMasterChangeSeqNum) + 1 FROM ContentEntryProgress)
          |ELSE NEW.contentEntryProgressMasterChangeSeqNum END)
          |WHERE contentEntryProgressUid = NEW.contentEntryProgressUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_210
          |AFTER INSERT ON ContentEntryProgress FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.contentEntryProgressMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.contentEntryProgressLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ContentEntryProgress SET contentEntryProgressLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.contentEntryProgressLocalChangeSeqNum 
          |ELSE (SELECT MAX(contentEntryProgressLocalChangeSeqNum) + 1 FROM ContentEntryProgress) END),
          |contentEntryProgressMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(contentEntryProgressMasterChangeSeqNum) + 1 FROM ContentEntryProgress)
          |ELSE NEW.contentEntryProgressMasterChangeSeqNum END)
          |WHERE contentEntryProgressUid = NEW.contentEntryProgressUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `DeviceSession` (`deviceSessionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dsDeviceId` INTEGER NOT NULL, `dsPersonUid` INTEGER NOT NULL, `expires` INTEGER NOT NULL)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `TimeZoneEntity` (`id` TEXT NOT NULL, `rawOffset` INTEGER NOT NULL, PRIMARY KEY(`id`))")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `HolidayCalendar` (`umCalendarUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `umCalendarName` TEXT, `umCalendarCategory` INTEGER NOT NULL, `umCalendarActive` INTEGER NOT NULL, `umCalendarMasterChangeSeqNum` INTEGER NOT NULL, `umCalendarLocalChangeSeqNum` INTEGER NOT NULL, `umCalendarLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `HolidayCalendar_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_HolidayCalendar_trk_clientId_epk_rx_csn` ON `HolidayCalendar_trk` (`clientId`, `epk`, `rx`, `csn`)")


                    database.execSQL("""
          |CREATE TRIGGER UPD_28
          |AFTER UPDATE ON HolidayCalendar FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.umCalendarMasterChangeSeqNum = 0 
          |OR OLD.umCalendarMasterChangeSeqNum = NEW.umCalendarMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.umCalendarLocalChangeSeqNum = 0  
          |OR OLD.umCalendarLocalChangeSeqNum = NEW.umCalendarLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE HolidayCalendar SET umCalendarLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.umCalendarLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(umCalendarLocalChangeSeqNum), OLD.umCalendarLocalChangeSeqNum) + 1 FROM HolidayCalendar) END),
          |umCalendarMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(umCalendarMasterChangeSeqNum), OLD.umCalendarMasterChangeSeqNum) + 1 FROM HolidayCalendar)
          |ELSE NEW.umCalendarMasterChangeSeqNum END)
          |WHERE umCalendarUid = NEW.umCalendarUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_28
          |AFTER INSERT ON HolidayCalendar FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.umCalendarMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.umCalendarLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE HolidayCalendar SET umCalendarLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.umCalendarLocalChangeSeqNum 
          |ELSE (SELECT MAX(umCalendarLocalChangeSeqNum) + 1 FROM HolidayCalendar) END),
          |umCalendarMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(umCalendarMasterChangeSeqNum) + 1 FROM HolidayCalendar)
          |ELSE NEW.umCalendarMasterChangeSeqNum END)
          |WHERE umCalendarUid = NEW.umCalendarUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `Holiday` (`holUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `holMasterCsn` INTEGER NOT NULL, `holLocalCsn` INTEGER NOT NULL, `holLastModBy` INTEGER NOT NULL, `holActive` INTEGER NOT NULL, `holHolidayCalendarUid` INTEGER NOT NULL, `holStartTime` INTEGER NOT NULL, `holEndTime` INTEGER NOT NULL, `holName` TEXT)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `Holiday_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_Holiday_trk_clientId_epk_rx_csn` ON `Holiday_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_99
          |AFTER UPDATE ON Holiday FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.holMasterCsn = 0 
          |OR OLD.holMasterCsn = NEW.holMasterCsn
          |)
          |ELSE
          |(NEW.holLocalCsn = 0  
          |OR OLD.holLocalCsn = NEW.holLocalCsn
          |) END)
          |BEGIN 
          |UPDATE Holiday SET holLocalCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.holLocalCsn 
          |ELSE (SELECT MAX(MAX(holLocalCsn), OLD.holLocalCsn) + 1 FROM Holiday) END),
          |holMasterCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(holMasterCsn), OLD.holMasterCsn) + 1 FROM Holiday)
          |ELSE NEW.holMasterCsn END)
          |WHERE holUid = NEW.holUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_99
          |AFTER INSERT ON Holiday FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.holMasterCsn = 0 
          |
          |)
          |ELSE
          |(NEW.holLocalCsn = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE Holiday SET holLocalCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.holLocalCsn 
          |ELSE (SELECT MAX(holLocalCsn) + 1 FROM Holiday) END),
          |holMasterCsn = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(holMasterCsn) + 1 FROM Holiday)
          |ELSE NEW.holMasterCsn END)
          |WHERE holUid = NEW.holUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `WorkSpace` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `guestLogin` INTEGER NOT NULL, `registrationAllowed` INTEGER NOT NULL)")

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkContentJoin` (`clazzWorkContentJoinUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clazzWorkContentJoinContentUid` INTEGER NOT NULL, `clazzWorkContentJoinClazzWorkUid` INTEGER NOT NULL, `clazzWorkContentJoinInactive` INTEGER NOT NULL, `clazzWorkContentJoinDateAdded` INTEGER NOT NULL, `clazzWorkContentJoinMCSN` INTEGER NOT NULL, `clazzWorkContentJoinLCSN` INTEGER NOT NULL, `clazzWorkContentJoinLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ClazzWorkContentJoin_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ClazzWorkContentJoin_trk_clientId_epk_rx_csn` ON `ClazzWorkContentJoin_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_204
          |AFTER UPDATE ON ClazzWorkContentJoin FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkContentJoinMCSN = 0 
          |OR OLD.clazzWorkContentJoinMCSN = NEW.clazzWorkContentJoinMCSN
          |)
          |ELSE
          |(NEW.clazzWorkContentJoinLCSN = 0  
          |OR OLD.clazzWorkContentJoinLCSN = NEW.clazzWorkContentJoinLCSN
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkContentJoin SET clazzWorkContentJoinLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkContentJoinLCSN 
          |ELSE (SELECT MAX(MAX(clazzWorkContentJoinLCSN), OLD.clazzWorkContentJoinLCSN) + 1 FROM ClazzWorkContentJoin) END),
          |clazzWorkContentJoinMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(clazzWorkContentJoinMCSN), OLD.clazzWorkContentJoinMCSN) + 1 FROM ClazzWorkContentJoin)
          |ELSE NEW.clazzWorkContentJoinMCSN END)
          |WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_204
          |AFTER INSERT ON ClazzWorkContentJoin FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.clazzWorkContentJoinMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.clazzWorkContentJoinLCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ClazzWorkContentJoin SET clazzWorkContentJoinLCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkContentJoinLCSN 
          |ELSE (SELECT MAX(clazzWorkContentJoinLCSN) + 1 FROM ClazzWorkContentJoin) END),
          |clazzWorkContentJoinMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(clazzWorkContentJoinMCSN) + 1 FROM ClazzWorkContentJoin)
          |ELSE NEW.clazzWorkContentJoinMCSN END)
          |WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS `Report` (`reportUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `reportOwnerUid` INTEGER NOT NULL, `chartType` INTEGER NOT NULL, `xAxis` INTEGER NOT NULL, `yAxis` INTEGER NOT NULL, `subGroup` INTEGER NOT NULL, `fromDate` INTEGER NOT NULL, `toDate` INTEGER NOT NULL, `reportTitle` TEXT, `reportInactive` INTEGER NOT NULL, `reportMasterChangeSeqNum` INTEGER NOT NULL, `reportLocalChangeSeqNum` INTEGER NOT NULL, `reportLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `Report_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_Report_trk_clientId_epk_rx_csn` ON `Report_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_101
          |AFTER UPDATE ON Report FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.reportMasterChangeSeqNum = 0 
          |OR OLD.reportMasterChangeSeqNum = NEW.reportMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.reportLocalChangeSeqNum = 0  
          |OR OLD.reportLocalChangeSeqNum = NEW.reportLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE Report SET reportLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(reportLocalChangeSeqNum), OLD.reportLocalChangeSeqNum) + 1 FROM Report) END),
          |reportMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(reportMasterChangeSeqNum), OLD.reportMasterChangeSeqNum) + 1 FROM Report)
          |ELSE NEW.reportMasterChangeSeqNum END)
          |WHERE reportUid = NEW.reportUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_101
          |AFTER INSERT ON Report FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.reportMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.reportLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE Report SET reportLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportLocalChangeSeqNum 
          |ELSE (SELECT MAX(reportLocalChangeSeqNum) + 1 FROM Report) END),
          |reportMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(reportMasterChangeSeqNum) + 1 FROM Report)
          |ELSE NEW.reportMasterChangeSeqNum END)
          |WHERE reportUid = NEW.reportUid
          |; END
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS `ReportFilter` (`reportFilterUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `reportFilterReportUid` INTEGER NOT NULL, `entityUid` INTEGER NOT NULL, `entityType` INTEGER NOT NULL, `filterInactive` INTEGER NOT NULL, `reportFilterMasterChangeSeqNum` INTEGER NOT NULL, `reportFilterLocalChangeSeqNum` INTEGER NOT NULL, `reportFilterLastChangedBy` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `ReportFilter_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_ReportFilter_trk_clientId_epk_rx_csn` ON `ReportFilter_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_102
          |AFTER UPDATE ON ReportFilter FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.reportFilterMasterChangeSeqNum = 0 
          |OR OLD.reportFilterMasterChangeSeqNum = NEW.reportFilterMasterChangeSeqNum
          |)
          |ELSE
          |(NEW.reportFilterLocalChangeSeqNum = 0  
          |OR OLD.reportFilterLocalChangeSeqNum = NEW.reportFilterLocalChangeSeqNum
          |) END)
          |BEGIN 
          |UPDATE ReportFilter SET reportFilterLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportFilterLocalChangeSeqNum 
          |ELSE (SELECT MAX(MAX(reportFilterLocalChangeSeqNum), OLD.reportFilterLocalChangeSeqNum) + 1 FROM ReportFilter) END),
          |reportFilterMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(reportFilterMasterChangeSeqNum), OLD.reportFilterMasterChangeSeqNum) + 1 FROM ReportFilter)
          |ELSE NEW.reportFilterMasterChangeSeqNum END)
          |WHERE reportFilterUid = NEW.reportFilterUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_102
          |AFTER INSERT ON ReportFilter FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.reportFilterMasterChangeSeqNum = 0 
          |
          |)
          |ELSE
          |(NEW.reportFilterLocalChangeSeqNum = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE ReportFilter SET reportFilterLocalChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportFilterLocalChangeSeqNum 
          |ELSE (SELECT MAX(reportFilterLocalChangeSeqNum) + 1 FROM ReportFilter) END),
          |reportFilterMasterChangeSeqNum = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(reportFilterMasterChangeSeqNum) + 1 FROM ReportFilter)
          |ELSE NEW.reportFilterMasterChangeSeqNum END)
          |WHERE reportFilterUid = NEW.reportFilterUid
          |; END
          """.trimMargin())


                } else
                    if (database.dbType() == DoorDbType.POSTGRES) {

                        // from 35 - 36
                        database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzStudentsPersonGroupUid BIGINT DEFAULT 0 NOT NULL
                        """.trimMargin())
                        database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzTeachersPersonGroupUid  BIGINT DEFAULT 0 NOT NULL
                        """.trimMargin())
                        database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzSchoolUid  BIGINT DEFAULT 0 NOT NULL
                        """.trimMargin())

                        database.execSQL("""ALTER TABLE School ADD COLUMN schoolHolidayCalendarUid BIGINT DEFAULT 0 NOT NULL""".trimMargin())
                        database.execSQL("""ALTER TABLE School ADD COLUMN schoolStudentsPersonGroupUid BIGINT DEFAULT 0 NOT NULL""".trimMargin())
                        database.execSQL("""ALTER TABLE School ADD COLUMN schoolTeachersPersonGroupUid BIGINT DEFAULT 0 NOT NULL""".trimMargin())

                        database.execSQL("""ALTER TABLE PersonPicture 
                        ADD COLUMN personPictureActive BOOL DEFAULT true NOT NULL""".trimMargin())

                        database.execSQL("""ALTER TABLE StatementEntity RENAME COLUMN personuid to statementPersonUid
                        """.trimMargin())
                        database.execSQL("""ALTER TABLE StatementEntity RENAME COLUMN verbUid to statementVerbUid
                        """.trimMargin())

                        database.execSQL("CREATE TABLE IF NOT EXISTS SchoolMember (  schoolMemberPersonUid  BIGINT , schoolMemberSchoolUid  BIGINT , schoolMemberJoinDate  BIGINT , schoolMemberLeftDate  BIGINT , schoolMemberRole  INTEGER , schoolMemberActive  BOOL , schoolMemberLocalChangeSeqNum  BIGINT , schoolMemberMasterChangeSeqNum  BIGINT , schoolMemberLastChangedBy  INTEGER , schoolMemberUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS SchoolMember_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS SchoolMember_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_200_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE SchoolMember SET schoolMemberLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.schoolMemberLocalChangeSeqNum 
                          | ELSE NEXTVAL('SchoolMember_lcsn_seq') END),
                          | schoolMemberMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('SchoolMember_mcsn_seq') 
                          | ELSE NEW.schoolMemberMasterChangeSeqNum END)
                          | WHERE schoolMemberUid = NEW.schoolMemberUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_200_trig 
                          |AFTER UPDATE OR INSERT ON SchoolMember 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_200_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS SchoolMember_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_SchoolMember_trk_clientId_epk_rx_csn 
                          |ON SchoolMember_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table SchoolMember for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWork (  clazzWorkCreatorPersonUid  BIGINT , clazzWorkClazzUid  BIGINT , clazzWorkTitle  TEXT , clazzWorkCreatedDate  BIGINT , clazzWorkStartDateTime  BIGINT , clazzWorkStartTime  BIGINT , clazzWorkDueTime  BIGINT , clazzWorkDueDateTime  BIGINT , clazzWorkSubmissionType  INTEGER , clazzWorkCommentsEnabled  BOOL , clazzWorkMaximumScore  INTEGER , clazzWorkInstructions  TEXT , clazzWorkActive  BOOL , clazzWorkLocalChangeSeqNum  BIGINT , clazzWorkMasterChangeSeqNum  BIGINT , clazzWorkLastChangedBy  INTEGER , clazzWorkUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWork_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWork_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_201_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWork SET clazzWorkLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkLocalChangeSeqNum 
                          | ELSE NEXTVAL('ClazzWork_lcsn_seq') END),
                          | clazzWorkMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWork_mcsn_seq') 
                          | ELSE NEW.clazzWorkMasterChangeSeqNum END)
                          | WHERE clazzWorkUid = NEW.clazzWorkUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_201_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWork 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_201_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWork_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWork_trk_clientId_epk_rx_csn 
                          |ON ClazzWork_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWork for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestion (  clazzWorkQuestionText  TEXT , clazzWorkQuestionClazzWorkUid  BIGINT , clazzWorkQuestionIndex  INTEGER , clazzWorkQuestionType  INTEGER , clazzWorkQuestionActive  BOOL , clazzWorkQuestionMCSN  BIGINT , clazzWorkQuestionLCSN  BIGINT , clazzWorkQuestionLCB  INTEGER , clazzWorkQuestionUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestion_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestion_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_202_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWorkQuestion SET clazzWorkQuestionLCSN =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionLCSN 
                          | ELSE NEXTVAL('ClazzWorkQuestion_lcsn_seq') END),
                          | clazzWorkQuestionMCSN = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWorkQuestion_mcsn_seq') 
                          | ELSE NEW.clazzWorkQuestionMCSN END)
                          | WHERE clazzWorkQuestionUid = NEW.clazzWorkQuestionUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_202_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWorkQuestion 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_202_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestion_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWorkQuestion_trk_clientId_epk_rx_csn 
                          |ON ClazzWorkQuestion_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWorkQuestion for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestionOption (  clazzWorkQuestionOptionText  TEXT , clazzWorkQuestionOptionQuestionUid  BIGINT , clazzWorkQuestionOptionMasterChangeSeqNum  BIGINT , clazzWorkQuestionOptionLocalChangeSeqNum  BIGINT , clazzWorkQuestionOptionLastChangedBy  INTEGER , clazzWorkQuestionOptionActive  BOOL , clazzWorkQuestionOptionUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestionOption_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestionOption_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_203_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWorkQuestionOption SET clazzWorkQuestionOptionLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionOptionLocalChangeSeqNum 
                          | ELSE NEXTVAL('ClazzWorkQuestionOption_lcsn_seq') END),
                          | clazzWorkQuestionOptionMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWorkQuestionOption_mcsn_seq') 
                          | ELSE NEW.clazzWorkQuestionOptionMasterChangeSeqNum END)
                          | WHERE clazzWorkQuestionOptionUid = NEW.clazzWorkQuestionOptionUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_203_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWorkQuestionOption 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_203_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestionOption_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWorkQuestionOption_trk_clientId_epk_rx_csn 
                          |ON ClazzWorkQuestionOption_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWorkQuestionOption for PostgreSQL


                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestionResponse (  clazzWorkQuestionResponseClazzWorkUid  BIGINT , clazzWorkQuestionResponseQuestionUid  BIGINT , clazzWorkQuestionResponseText  TEXT , clazzWorkQuestionResponseOptionSelected  BIGINT , clazzWorkQuestionResponsePersonUid  BIGINT , clazzWorkQuestionResponseClazzMemberUid  BIGINT , clazzWorkQuestionResponseInactive  BOOL , clazzWorkQuestionResponseDateResponded  BIGINT , clazzWorkQuestionResponseMCSN  BIGINT , clazzWorkQuestionResponseLCSN  BIGINT , clazzWorkQuestionResponseLCB  INTEGER , clazzWorkQuestionResponseUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestionResponse_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkQuestionResponse_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_209_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWorkQuestionResponse SET clazzWorkQuestionResponseLCSN =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkQuestionResponseLCSN 
                          | ELSE NEXTVAL('ClazzWorkQuestionResponse_lcsn_seq') END),
                          | clazzWorkQuestionResponseMCSN = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWorkQuestionResponse_mcsn_seq') 
                          | ELSE NEW.clazzWorkQuestionResponseMCSN END)
                          | WHERE clazzWorkQuestionResponseUid = NEW.clazzWorkQuestionResponseUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_209_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWorkQuestionResponse 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_209_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkQuestionResponse_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWorkQuestionResponse_trk_clientId_epk_rx_csn 
                          |ON ClazzWorkQuestionResponse_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWorkQuestionResponse for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkSubmission (  clazzWorkSubmissionClazzWorkUid  BIGINT , clazzWorkSubmissionClazzMemberUid  BIGINT , clazzWorkSubmissionMarkerClazzMemberUid  BIGINT , clazzWorkSubmissionMarkerPersonUid  BIGINT , clazzWorkSubmissionPersonUid  BIGINT , clazzWorkSubmissionInactive  BOOL , clazzWorkSubmissionDateTimeStarted  BIGINT , clazzWorkSubmissionDateTimeUpdated  BIGINT , clazzWorkSubmissionDateTimeFinished  BIGINT , clazzWorkSubmissionDateTimeMarked  BIGINT , clazzWorkSubmissionText  TEXT , clazzWorkSubmissionScore  INTEGER , clazzWorkSubmissionMCSN  BIGINT , clazzWorkSubmissionLCSN  BIGINT , clazzWorkSubmissionLCB  INTEGER , clazzWorkSubmissionUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkSubmission_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkSubmission_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_206_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWorkSubmission SET clazzWorkSubmissionLCSN =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkSubmissionLCSN 
                          | ELSE NEXTVAL('ClazzWorkSubmission_lcsn_seq') END),
                          | clazzWorkSubmissionMCSN = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWorkSubmission_mcsn_seq') 
                          | ELSE NEW.clazzWorkSubmissionMCSN END)
                          | WHERE clazzWorkSubmissionUid = NEW.clazzWorkSubmissionUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_206_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWorkSubmission 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_206_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkSubmission_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWorkSubmission_trk_clientId_epk_rx_csn 
                          |ON ClazzWorkSubmission_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWorkSubmission for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS Comments (  commentsText  TEXT , commentsEntityType  INTEGER , commentsEntityUid  BIGINT , commentsPublic  BOOL , commentsStatus  INTEGER , commentsPersonUid  BIGINT , commentsToPersonUid  BIGINT , commentsFlagged  BOOL , commentsInActive  BOOL , commentsDateTimeAdded  BIGINT , commentsDateTimeUpdated  BIGINT , commentsMCSN  BIGINT , commentsLCSN  BIGINT , commentsLCB  INTEGER , commentsUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Comments_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Comments_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_208_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE Comments SET commentsLCSN =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.commentsLCSN 
                          | ELSE NEXTVAL('Comments_lcsn_seq') END),
                          | commentsMCSN = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('Comments_mcsn_seq') 
                          | ELSE NEW.commentsMCSN END)
                          | WHERE commentsUid = NEW.commentsUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_208_trig 
                          |AFTER UPDATE OR INSERT ON Comments 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_208_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS Comments_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_Comments_trk_clientId_epk_rx_csn 
                          |ON Comments_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table Comments for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ContainerUploadJob (  cujContainerUid  BIGINT , sessionId  TEXT , jobStatus  INTEGER , bytesSoFar  BIGINT , contentLength  BIGINT , containerEntryFileUids  TEXT , cujUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        //End: Create table ContainerUploadJob for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ContentEntryProgress (  contentEntryProgressActive  BOOL , contentEntryProgressContentEntryUid  BIGINT , contentEntryProgressPersonUid  BIGINT , contentEntryProgressProgress  INTEGER , contentEntryProgressStatusFlag  INTEGER , contentEntryProgressLocalChangeSeqNum  BIGINT , contentEntryProgressMasterChangeSeqNum  BIGINT , contentEntryProgressLastChangedBy  INTEGER , contentEntryProgressUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ContentEntryProgress_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ContentEntryProgress_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_210_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ContentEntryProgress SET contentEntryProgressLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.contentEntryProgressLocalChangeSeqNum 
                          | ELSE NEXTVAL('ContentEntryProgress_lcsn_seq') END),
                          | contentEntryProgressMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ContentEntryProgress_mcsn_seq') 
                          | ELSE NEW.contentEntryProgressMasterChangeSeqNum END)
                          | WHERE contentEntryProgressUid = NEW.contentEntryProgressUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_210_trig 
                          |AFTER UPDATE OR INSERT ON ContentEntryProgress 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_210_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ContentEntryProgress_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ContentEntryProgress_trk_clientId_epk_rx_csn 
                          |ON ContentEntryProgress_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ContentEntryProgress for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS DeviceSession (  dsDeviceId  INTEGER , dsPersonUid  BIGINT , expires  BIGINT , deviceSessionUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        //End: Create table DeviceSession for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS TimeZoneEntity (  id  TEXT  PRIMARY KEY  NOT NULL , rawOffset  INTEGER )")
                        //End: Create table TimeZoneEntity for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS Holiday (  holMasterCsn  BIGINT , holLocalCsn  BIGINT , holLastModBy  INTEGER , holActive  BOOL , holHolidayCalendarUid  BIGINT , holStartTime  BIGINT , holEndTime  BIGINT , holName  TEXT , holUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Holiday_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Holiday_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_99_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE Holiday SET holLocalCsn =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.holLocalCsn 
                          | ELSE NEXTVAL('Holiday_lcsn_seq') END),
                          | holMasterCsn = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('Holiday_mcsn_seq') 
                          | ELSE NEW.holMasterCsn END)
                          | WHERE holUid = NEW.holUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_99_trig 
                          |AFTER UPDATE OR INSERT ON Holiday 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_99_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS Holiday_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_Holiday_trk_clientId_epk_rx_csn 
                          |ON Holiday_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table Holiday for PostgreSQL


                        database.execSQL("CREATE TABLE IF NOT EXISTS HolidayCalendar (  umCalendarName  TEXT , umCalendarCategory  INTEGER , umCalendarActive  BOOL , umCalendarMasterChangeSeqNum  BIGINT , umCalendarLocalChangeSeqNum  BIGINT , umCalendarLastChangedBy  INTEGER , umCalendarUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS HolidayCalendar_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS HolidayCalendar_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_28_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE HolidayCalendar SET umCalendarLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.umCalendarLocalChangeSeqNum 
                          | ELSE NEXTVAL('HolidayCalendar_lcsn_seq') END),
                          | umCalendarMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('HolidayCalendar_mcsn_seq') 
                          | ELSE NEW.umCalendarMasterChangeSeqNum END)
                          | WHERE umCalendarUid = NEW.umCalendarUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_28_trig 
                          |AFTER UPDATE OR INSERT ON HolidayCalendar 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_28_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS HolidayCalendar_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_HolidayCalendar_trk_clientId_epk_rx_csn 
                          |ON HolidayCalendar_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table HolidayCalendar for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS WorkSpace (  name  TEXT , guestLogin  BOOL , registrationAllowed  BOOL , uid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        //End: Create table WorkSpace for PostgreSQL


                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkContentJoin (  clazzWorkContentJoinContentUid  BIGINT , clazzWorkContentJoinClazzWorkUid  BIGINT , clazzWorkContentJoinInactive  BOOL , clazzWorkContentJoinDateAdded  BIGINT , clazzWorkContentJoinMCSN  BIGINT , clazzWorkContentJoinLCSN  BIGINT , clazzWorkContentJoinLCB  INTEGER , clazzWorkContentJoinUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkContentJoin_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ClazzWorkContentJoin_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_204_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ClazzWorkContentJoin SET clazzWorkContentJoinLCSN =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.clazzWorkContentJoinLCSN 
                          | ELSE NEXTVAL('ClazzWorkContentJoin_lcsn_seq') END),
                          | clazzWorkContentJoinMCSN = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ClazzWorkContentJoin_mcsn_seq') 
                          | ELSE NEW.clazzWorkContentJoinMCSN END)
                          | WHERE clazzWorkContentJoinUid = NEW.clazzWorkContentJoinUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_204_trig 
                          |AFTER UPDATE OR INSERT ON ClazzWorkContentJoin 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_204_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ClazzWorkContentJoin_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ClazzWorkContentJoin_trk_clientId_epk_rx_csn 
                          |ON ClazzWorkContentJoin_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ClazzWorkContentJoin for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS Report (  reportOwnerUid  BIGINT , chartType  INTEGER , xAxis  INTEGER , yAxis  INTEGER , subGroup  INTEGER , fromDate  BIGINT , toDate  BIGINT , reportTitle  TEXT , reportInactive  BOOL , reportMasterChangeSeqNum  BIGINT , reportLocalChangeSeqNum  BIGINT , reportLastChangedBy  INTEGER , reportUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Report_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS Report_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_101_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE Report SET reportLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportLocalChangeSeqNum 
                          | ELSE NEXTVAL('Report_lcsn_seq') END),
                          | reportMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('Report_mcsn_seq') 
                          | ELSE NEW.reportMasterChangeSeqNum END)
                          | WHERE reportUid = NEW.reportUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_101_trig 
                          |AFTER UPDATE OR INSERT ON Report 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_101_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS Report_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_Report_trk_clientId_epk_rx_csn 
                          |ON Report_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table Report for PostgreSQL

                        database.execSQL("CREATE TABLE IF NOT EXISTS ReportFilter (  reportFilterReportUid  BIGINT , entityUid  BIGINT , entityType  INTEGER , filterInactive  BOOL , reportFilterMasterChangeSeqNum  BIGINT , reportFilterLocalChangeSeqNum  BIGINT , reportFilterLastChangedBy  INTEGER , reportFilterUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ReportFilter_mcsn_seq")
                        database.execSQL("CREATE SEQUENCE IF NOT EXISTS ReportFilter_lcsn_seq")
                        database.execSQL("""
                          |CREATE OR REPLACE FUNCTION 
                          | inccsn_102_fn() RETURNS trigger AS ${'$'}${'$'}
                          | BEGIN  
                          | UPDATE ReportFilter SET reportFilterLocalChangeSeqNum =
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.reportFilterLocalChangeSeqNum 
                          | ELSE NEXTVAL('ReportFilter_lcsn_seq') END),
                          | reportFilterMasterChangeSeqNum = 
                          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                          | THEN NEXTVAL('ReportFilter_mcsn_seq') 
                          | ELSE NEW.reportFilterMasterChangeSeqNum END)
                          | WHERE reportFilterUid = NEW.reportFilterUid;
                          | RETURN null;
                          | END ${'$'}${'$'}
                          | LANGUAGE plpgsql
                          """.trimMargin())
                        database.execSQL("""
                          |CREATE TRIGGER inccsn_102_trig 
                          |AFTER UPDATE OR INSERT ON ReportFilter 
                          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
                          |EXECUTE PROCEDURE inccsn_102_fn()
                          """.trimMargin())
                        database.execSQL("CREATE TABLE IF NOT EXISTS ReportFilter_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                        database.execSQL("""
                          |CREATE 
                          | INDEX index_ReportFilter_trk_clientId_epk_rx_csn 
                          |ON ReportFilter_trk (clientId, epk, rx, csn)
                          """.trimMargin())
                        //End: Create table ReportFilter for PostgreSQL
                    }


                database.execSQL("""ALTER TABLE School 
                        ADD COLUMN schoolTimeZone TEXT
                        """.trimMargin())
                database.execSQL("""ALTER TABLE School 
                        ADD COLUMN schoolEmailAddress TEXT
                        """.trimMargin())
                database.execSQL("""ALTER TABLE School 
                        ADD COLUMN schoolPhoneNumber TEXT
                        """.trimMargin())
                database.execSQL("""ALTER TABLE School ADD COLUMN schoolGender INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                database.execSQL("""ALTER TABLE ClazzLog ADD COLUMN cancellationNote TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE ClazzLog ADD COLUMN clazzLogStatusFlag INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                database.execSQL("""ALTER TABLE CustomField
                        ADD COLUMN actionOnClick TEXT""".trimMargin())
                database.execSQL("""ALTER TABLE CustomField ADD COLUMN customFieldIconId INTEGER DEFAULT 0 NOT NULL""".trimMargin())
                database.execSQL("""ALTER TABLE CustomField ADD COLUMN customFieldInputType INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                println("finished migration from 35 to 36")

            }


        }


        /**
         * Add fields required for class and school codes for students to join a class or school
         */
        val MIGRATION_36_37 = object : DoorMigration(36, 37) {
            override fun migrate(database: DoorSqlDatabase) {

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("ALTER TABLE PersonAuth RENAME to PersonAuth_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `PersonAuth` (`personAuthUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `passwordHash` TEXT, `personAuthStatus` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO PersonAuth (personAuthUid, passwordHash, personAuthStatus) SELECT personAuthUid, passwordHash, 0 FROM PersonAuth_OLD")
                    database.execSQL("DROP TABLE PersonAuth_OLD")

                    database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzPendingStudentsPersonGroupUid INTEGER DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("ALTER TABLE School ADD COLUMN " +
                            "schoolPendingStudentsPersonGroupUid INTEGER DEFAULT 0 NOT NULL")

                } else if (database.dbType() == DoorDbType.POSTGRES) {

                    database.execSQL("""ALTER TABLE PersonAuth DROP COLUMN personAuthLocalChangeSeqNum
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE PersonAuth DROP COLUMN personAuthMasterChangeSeqNum
                        """.trimMargin())
                    database.execSQL("""ALTER TABLE PersonAuth DROP COLUMN lastChangedBy
                        """.trimMargin())

                    database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzPendingStudentsPersonGroupUid BIGINT DEFAULT 0 NOT NULL""".trimMargin())

                    database.execSQL("ALTER TABLE School ADD COLUMN " +
                            "schoolPendingStudentsPersonGroupUid BIGINT DEFAULT 0 NOT NULL")
                }


                database.execSQL("""ALTER TABLE Clazz 
                        ADD COLUMN clazzCode TEXT""".trimMargin())


                database.execSQL("ALTER TABLE School ADD COLUMN schoolCode TEXT")
                println("finished migration from 36 to 37")
            }
        }

        val MIGRATION_37_38 = object : DoorMigration(37, 38) {
            override fun migrate(database: DoorSqlDatabase) {

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("ALTER TABLE StatementEntity RENAME to StatementEntity_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS StatementEntity (`statementUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `statementId` TEXT, `statementPersonUid` INTEGER NOT NULL, `statementVerbUid` INTEGER NOT NULL, `xObjectUid` INTEGER NOT NULL, `subStatementActorUid` INTEGER NOT NULL, `substatementVerbUid` INTEGER NOT NULL, `subStatementObjectUid` INTEGER NOT NULL, `agentUid` INTEGER NOT NULL, `instructorUid` INTEGER NOT NULL, `authorityUid` INTEGER NOT NULL, `teamUid` INTEGER NOT NULL, `resultCompletion` INTEGER NOT NULL, `resultSuccess` INTEGER NOT NULL, `resultScoreScaled` REAL NOT NULL, `resultScoreRaw` INTEGER NOT NULL, `resultScoreMin` INTEGER NOT NULL, `resultScoreMax` INTEGER NOT NULL, `resultDuration` INTEGER NOT NULL, `resultResponse` TEXT, `timestamp` INTEGER NOT NULL, `stored` INTEGER NOT NULL, `contextRegistration` TEXT, `contextPlatform` TEXT, `contextStatementId` TEXT, `fullStatement` TEXT, `statementMasterChangeSeqNum` INTEGER NOT NULL, `statementLocalChangeSeqNum` INTEGER NOT NULL, `statementLastChangedBy` INTEGER NOT NULL, `extensionProgress` INTEGER NOT NULL, `statementContentEntryUid` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO StatementEntity (statementUid, statementId, statementPersonUid, statementVerbUid, xObjectUid, subStatementActorUid, substatementVerbUid, subStatementObjectUid, agentUid, instructorUid, authorityUid, teamUid, resultCompletion, resultSuccess, resultScoreScaled, resultScoreRaw, resultScoreMin, resultScoreMax, resultDuration, resultResponse, timestamp, stored, contextRegistration, contextPlatform, contextStatementId, fullStatement, statementMasterChangeSeqNum, statementLocalChangeSeqNum, statementLastChangedBy, extensionProgress, statementContentEntryUid) SELECT statementUid, statementId, statementPersonUid, statementVerbUid, xObjectUid, subStatementActorUid, substatementVerbUid, subStatementObjectUid, agentUid, instructorUid, authorityUid, teamUid, resultCompletion, resultSuccess, resultScoreScaled, resultScoreRaw, resultScoreMin, resultScoreMax, resultDuration, resultResponse, timestamp, stored, contextRegistration, contextPlatform, contextStatementId, fullStatement, statementMasterChangeSeqNum, statementLocalChangeSeqNum, statementLastChangedBy, extensionProgress, statementContentEntryUid FROM StatementEntity_OLD")
                    database.execSQL("DROP TABLE StatementEntity_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS `StatementEntity_trk` (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_StatementEntity_trk_clientId_epk_rx_csn` ON `StatementEntity_trk` (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
                      |CREATE TRIGGER UPD_60
                      |AFTER UPDATE ON StatementEntity FOR EACH ROW WHEN
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(NEW.statementMasterChangeSeqNum = 0 
                      |OR OLD.statementMasterChangeSeqNum = NEW.statementMasterChangeSeqNum
                      |)
                      |ELSE
                      |(NEW.statementLocalChangeSeqNum = 0  
                      |OR OLD.statementLocalChangeSeqNum = NEW.statementLocalChangeSeqNum
                      |) END)
                      |BEGIN 
                      |UPDATE StatementEntity SET statementLocalChangeSeqNum = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.statementLocalChangeSeqNum 
                      |ELSE (SELECT MAX(MAX(statementLocalChangeSeqNum), OLD.statementLocalChangeSeqNum) + 1 FROM StatementEntity) END),
                      |statementMasterChangeSeqNum = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(SELECT MAX(MAX(statementMasterChangeSeqNum), OLD.statementMasterChangeSeqNum) + 1 FROM StatementEntity)
                      |ELSE NEW.statementMasterChangeSeqNum END)
                      |WHERE statementUid = NEW.statementUid
                      |; END
                      """.trimMargin())
                    database.execSQL("""
                      |CREATE TRIGGER INS_60
                      |AFTER INSERT ON StatementEntity FOR EACH ROW WHEN
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(NEW.statementMasterChangeSeqNum = 0 
                      |
                      |)
                      |ELSE
                      |(NEW.statementLocalChangeSeqNum = 0  
                      |
                      |) END)
                      |BEGIN 
                      |UPDATE StatementEntity SET statementLocalChangeSeqNum = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.statementLocalChangeSeqNum 
                      |ELSE (SELECT MAX(statementLocalChangeSeqNum) + 1 FROM StatementEntity) END),
                      |statementMasterChangeSeqNum = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(SELECT MAX(statementMasterChangeSeqNum) + 1 FROM StatementEntity)
                      |ELSE NEW.statementMasterChangeSeqNum END)
                      |WHERE statementUid = NEW.statementUid
                      |; END
                      """.trimMargin())
                } else if (database.dbType() == DoorDbType.POSTGRES) {

                    database.execSQL("ALTER TABLE StatementEntity ALTER COLUMN resultScoreScaled TYPE FLOAT")
                }

            }
        }

        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        val MIGRATION_38_39 = object: DoorMigration(38, 39) {
            override fun migrate(db: DoorSqlDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS SqliteSyncablePrimaryKey (sspTableId INTEGER NOT NULL PRIMARY KEY, sspNextPrimaryKey INTEGER NOT NULL)")

                if (db.dbType() != DoorDbType.SQLITE)
                    return
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (14, (SELECT COALESCE((SELECT MAX(clazzLogUid) FROM ClazzLog WHERE clazzLogUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (15, (SELECT COALESCE((SELECT MAX(clazzLogAttendanceRecordUid) FROM ClazzLogAttendanceRecord WHERE clazzLogAttendanceRecordUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (20, (SELECT COALESCE((SELECT MAX(personCustomFieldUid) FROM PersonField WHERE personCustomFieldUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (19, (SELECT COALESCE((SELECT MAX(personDetailPresenterFieldUid) FROM PersonDetailPresenterField WHERE personDetailPresenterFieldUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (21, (SELECT COALESCE((SELECT MAX(scheduleUid) FROM Schedule WHERE scheduleUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (17, (SELECT COALESCE((SELECT MAX(dateRangeUid) FROM DateRange WHERE dateRangeUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (28, (SELECT COALESCE((SELECT MAX(umCalendarUid) FROM HolidayCalendar WHERE umCalendarUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (99, (SELECT COALESCE((SELECT MAX(holUid) FROM Holiday WHERE holUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (173, (SELECT COALESCE((SELECT MAX(scheduledCheckUid) FROM ScheduledCheck WHERE scheduledCheckUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (53, (SELECT COALESCE((SELECT MAX(auditLogUid) FROM AuditLog WHERE auditLogUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (56, (SELECT COALESCE((SELECT MAX(customFieldUid) FROM CustomField WHERE customFieldUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (57, (SELECT COALESCE((SELECT MAX(customFieldValueUid) FROM CustomFieldValue WHERE customFieldValueUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (55, (SELECT COALESCE((SELECT MAX(customFieldValueOptionUid) FROM CustomFieldValueOption WHERE customFieldValueOptionUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (9, (SELECT COALESCE((SELECT MAX(personUid) FROM Person WHERE personUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (6, (SELECT COALESCE((SELECT MAX(clazzUid) FROM Clazz WHERE clazzUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (65, (SELECT COALESCE((SELECT MAX(clazzMemberUid) FROM ClazzMember WHERE clazzMemberUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (178, (SELECT COALESCE((SELECT MAX(personCustomFieldValueUid) FROM PersonCustomFieldValue WHERE personCustomFieldValueUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (42, (SELECT COALESCE((SELECT MAX(contentEntryUid) FROM ContentEntry WHERE contentEntryUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (3, (SELECT COALESCE((SELECT MAX(ceccjUid) FROM ContentEntryContentCategoryJoin WHERE ceccjUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (7, (SELECT COALESCE((SELECT MAX(cepcjUid) FROM ContentEntryParentChildJoin WHERE cepcjUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (8, (SELECT COALESCE((SELECT MAX(cerejUid) FROM ContentEntryRelatedEntryJoin WHERE cerejUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (2, (SELECT COALESCE((SELECT MAX(contentCategorySchemaUid) FROM ContentCategorySchema WHERE contentCategorySchemaUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (1, (SELECT COALESCE((SELECT MAX(contentCategoryUid) FROM ContentCategory WHERE contentCategoryUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (13, (SELECT COALESCE((SELECT MAX(langUid) FROM Language WHERE langUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (10, (SELECT COALESCE((SELECT MAX(langVariantUid) FROM LanguageVariant WHERE langVariantUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (45, (SELECT COALESCE((SELECT MAX(roleUid) FROM Role WHERE roleUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (47, (SELECT COALESCE((SELECT MAX(erUid) FROM EntityRole WHERE erUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (43, (SELECT COALESCE((SELECT MAX(groupUid) FROM PersonGroup WHERE groupUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (44, (SELECT COALESCE((SELECT MAX(groupMemberUid) FROM PersonGroupMember WHERE groupMemberUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (50, (SELECT COALESCE((SELECT MAX(personPictureUid) FROM PersonPicture WHERE personPictureUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (51, (SELECT COALESCE((SELECT MAX(containerUid) FROM Container WHERE containerUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (62, (SELECT COALESCE((SELECT MAX(verbUid) FROM VerbEntity WHERE verbUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (64, (SELECT COALESCE((SELECT MAX(xObjectUid) FROM XObjectEntity WHERE xObjectUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (60, (SELECT COALESCE((SELECT MAX(statementUid) FROM StatementEntity WHERE statementUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (66, (SELECT COALESCE((SELECT MAX(contextXObjectStatementJoinUid) FROM ContextXObjectStatementJoin WHERE contextXObjectStatementJoinUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (68, (SELECT COALESCE((SELECT MAX(agentUid) FROM AgentEntity WHERE agentUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (70, (SELECT COALESCE((SELECT MAX(stateUid) FROM StateEntity WHERE stateUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (72, (SELECT COALESCE((SELECT MAX(stateContentUid) FROM StateContentEntity WHERE stateContentUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (74, (SELECT COALESCE((SELECT MAX(statementLangMapUid) FROM XLangMapEntry WHERE statementLangMapUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (164, (SELECT COALESCE((SELECT MAX(schoolUid) FROM School WHERE schoolUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (200, (SELECT COALESCE((SELECT MAX(schoolMemberUid) FROM SchoolMember WHERE schoolMemberUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (201, (SELECT COALESCE((SELECT MAX(clazzWorkUid) FROM ClazzWork WHERE clazzWorkUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (204, (SELECT COALESCE((SELECT MAX(clazzWorkContentJoinUid) FROM ClazzWorkContentJoin WHERE clazzWorkContentJoinUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (208, (SELECT COALESCE((SELECT MAX(commentsUid) FROM Comments WHERE commentsUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (202, (SELECT COALESCE((SELECT MAX(clazzWorkQuestionUid) FROM ClazzWorkQuestion WHERE clazzWorkQuestionUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (203, (SELECT COALESCE((SELECT MAX(clazzWorkQuestionOptionUid) FROM ClazzWorkQuestionOption WHERE clazzWorkQuestionOptionUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (206, (SELECT COALESCE((SELECT MAX(clazzWorkSubmissionUid) FROM ClazzWorkSubmission WHERE clazzWorkSubmissionUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (209, (SELECT COALESCE((SELECT MAX(clazzWorkQuestionResponseUid) FROM ClazzWorkQuestionResponse WHERE clazzWorkQuestionResponseUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (210, (SELECT COALESCE((SELECT MAX(contentEntryProgressUid) FROM ContentEntryProgress WHERE contentEntryProgressUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (101, (SELECT COALESCE((SELECT MAX(reportUid) FROM Report WHERE reportUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
                db.execSQL("REPLACE INTO SqliteSyncablePrimaryKey (sspTableId, sspNextPrimaryKey) VALUES (102, (SELECT COALESCE((SELECT MAX(reportFilterUid) FROM ReportFilter WHERE reportFilterUid & (SELECT nodeClientId << 32 FROM SyncNode) = (SELECT nodeClientId << 32 FROM SyncNode)), (SELECT nodeClientId << 32 FROM SyncNode)+1)))")
            }
        }

        /**
         * Add fields required for class and school codes for students to join a class or school
         *  Changes from 39-40:
        1. Added personGroupUid to Person
        2. Added personGroupFlag to PersonGroup
        3. Removed groupPersonUid from PersonGroup
         */
        val MIGRATION_39_40 = object : DoorMigration(39, 40) {
            override fun migrate(database: DoorSqlDatabase) {

                if (database.dbType() == DoorDbType.SQLITE) {
                    //Person
                    database.execSQL("ALTER TABLE Person ADD COLUMN personGroupUid BIGINT DEFAULT 0 NOT NULL")

                    //PersonGroup
                    database.execSQL("ALTER TABLE PersonGroup RENAME to PersonGroup_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS PersonGroup (  " +
                            "groupMasterCsn  BIGINT NOT NULL , groupLocalCsn  BIGINT NOT NULL, " +
                            "groupLastChangedBy  INTEGER NOT NULL, groupName  TEXT , " +
                            "groupActive  INTEGER NOT NULL , personGroupFlag  INTEGER NOT NULL, " +
                            "groupUid  INTEGER NOT NULL PRIMARY KEY  AUTOINCREMENT  NOT NULL )")
                    database.execSQL("INSERT INTO PersonGroup (" +
                            "groupUid, groupMasterCsn, groupLocalCsn, groupLastChangedBy, " +
                            "groupName, groupActive) SELECT groupUid, groupMasterCsn, " +
                            "groupLocalCsn, groupLastChangedBy, groupName, groupActive " +
                            "FROM PersonGroup_OLD")
                    database.execSQL("DROP TABLE PersonGroup_OLD")

                    //Triggers for PersonGroup
                    database.execSQL("""
                      |CREATE TRIGGER UPD_43
                      |AFTER UPDATE ON PersonGroup FOR EACH ROW WHEN
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(NEW.groupMasterCsn = 0 
                      |OR OLD.groupMasterCsn = NEW.groupMasterCsn
                      |)
                      |ELSE
                      |(NEW.groupLocalCsn = 0  
                      |OR OLD.groupLocalCsn = NEW.groupLocalCsn
                      |) END)
                      |BEGIN 
                      |UPDATE PersonGroup SET groupLocalCsn = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.groupLocalCsn 
                      |ELSE (SELECT MAX(MAX(groupLocalCsn), OLD.groupLocalCsn) + 1 FROM PersonGroup) END),
                      |groupMasterCsn = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(SELECT MAX(MAX(groupMasterCsn), OLD.groupMasterCsn) + 1 FROM PersonGroup)
                      |ELSE NEW.groupMasterCsn END)
                      |WHERE groupUid = NEW.groupUid
                      |; END
                      """.trimMargin())
                    database.execSQL("""
                      |CREATE TRIGGER INS_43
                      |AFTER INSERT ON PersonGroup FOR EACH ROW WHEN
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(NEW.groupMasterCsn = 0 
                      |
                      |)
                      |ELSE
                      |(NEW.groupLocalCsn = 0  
                      |
                      |) END)
                      |BEGIN 
                      |UPDATE PersonGroup SET groupLocalCsn = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.groupLocalCsn 
                      |ELSE (SELECT MAX(groupLocalCsn) + 1 FROM PersonGroup) END),
                      |groupMasterCsn = 
                      |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
                      |(SELECT MAX(groupMasterCsn) + 1 FROM PersonGroup)
                      |ELSE NEW.groupMasterCsn END)
                      |WHERE groupUid = NEW.groupUid
                      |; END
                    """.trimMargin())


                } else if (database.dbType() == DoorDbType.POSTGRES) {
                    //Person
                    database.execSQL("ALTER TABLE Person ADD COLUMN personGroupUid BIGINT DEFAULT 0 NOT NULL")

                    //PersonGroup
                    database.execSQL("ALTER TABLE PersonGroup ADD COLUMN personGroupFlag INTEGER DEFAULT 0 NOT NULL")
                    database.execSQL("ALTER TABLE PersonGroup DROP COLUMN IF EXISTS groupPersonUid")
                }
            }
        }


        val MIGRATION_40_41 = object : DoorMigration(40, 41) {
            override fun migrate(database: DoorSqlDatabase) {

                database.execSQL("DROP TABLE PersonDetailPresenterField")
                database.execSQL("DROP TABLE PersonDetailPresenterField_trk")
                database.execSQL("DROP TABLE PersonField")
                database.execSQL("DROP TABLE PersonField_trk")

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("ALTER TABLE StatementEntity ADD COLUMN statementLearnerGroupUid INTEGER DEFAULT 0 NOT NULL")

                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroup (`learnerGroupUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `learnerGroupName` TEXT, `learnerGroupDescription` TEXT, `learnerGroupActive` INTEGER NOT NULL, `learnerGroupMCSN` INTEGER NOT NULL, `learnerGroupCSN` INTEGER NOT NULL, `learnerGroupLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroup_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_LearnerGroup_trk_clientId_epk_rx_csn` ON LearnerGroup_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_301
          |AFTER UPDATE ON LearnerGroup FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.learnerGroupMCSN = 0 
          |OR OLD.learnerGroupMCSN = NEW.learnerGroupMCSN
          |)
          |ELSE
          |(NEW.learnerGroupCSN = 0  
          |OR OLD.learnerGroupCSN = NEW.learnerGroupCSN
          |) END)
          |BEGIN 
          |UPDATE LearnerGroup SET learnerGroupCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupCSN 
          |ELSE (SELECT MAX(MAX(learnerGroupCSN), OLD.learnerGroupCSN) + 1 FROM LearnerGroup) END),
          |learnerGroupMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(learnerGroupMCSN), OLD.learnerGroupMCSN) + 1 FROM LearnerGroup)
          |ELSE NEW.learnerGroupMCSN END)
          |WHERE learnerGroupUid = NEW.learnerGroupUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_301
          |AFTER INSERT ON LearnerGroup FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.learnerGroupMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.learnerGroupCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE LearnerGroup SET learnerGroupCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupCSN 
          |ELSE (SELECT MAX(learnerGroupCSN) + 1 FROM LearnerGroup) END),
          |learnerGroupMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(learnerGroupMCSN) + 1 FROM LearnerGroup)
          |ELSE NEW.learnerGroupMCSN END)
          |WHERE learnerGroupUid = NEW.learnerGroupUid
          |; END
          """.trimMargin())


                    database.execSQL( "CREATE TABLE IF NOT EXISTS LearnerGroupMember (`learnerGroupMemberUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `learnerGroupMemberPersonUid` INTEGER NOT NULL, `learnerGroupMemberLgUid` INTEGER NOT NULL, `learnerGroupMemberRole` INTEGER NOT NULL, `learnerGroupMemberActive` INTEGER NOT NULL, `learnerGroupMemberMCSN` INTEGER NOT NULL, `learnerGroupMemberCSN` INTEGER NOT NULL, `learnerGroupMemberLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroupMember_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_LearnerGroupMember_trk_clientId_epk_rx_csn` ON LearnerGroupMember_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_300
          |AFTER UPDATE ON LearnerGroupMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.learnerGroupMemberMCSN = 0 
          |OR OLD.learnerGroupMemberMCSN = NEW.learnerGroupMemberMCSN
          |)
          |ELSE
          |(NEW.learnerGroupMemberCSN = 0  
          |OR OLD.learnerGroupMemberCSN = NEW.learnerGroupMemberCSN
          |) END)
          |BEGIN 
          |UPDATE LearnerGroupMember SET learnerGroupMemberCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupMemberCSN 
          |ELSE (SELECT MAX(MAX(learnerGroupMemberCSN), OLD.learnerGroupMemberCSN) + 1 FROM LearnerGroupMember) END),
          |learnerGroupMemberMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(learnerGroupMemberMCSN), OLD.learnerGroupMemberMCSN) + 1 FROM LearnerGroupMember)
          |ELSE NEW.learnerGroupMemberMCSN END)
          |WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_300
          |AFTER INSERT ON LearnerGroupMember FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.learnerGroupMemberMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.learnerGroupMemberCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE LearnerGroupMember SET learnerGroupMemberCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupMemberCSN 
          |ELSE (SELECT MAX(learnerGroupMemberCSN) + 1 FROM LearnerGroupMember) END),
          |learnerGroupMemberMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(learnerGroupMemberMCSN) + 1 FROM LearnerGroupMember)
          |ELSE NEW.learnerGroupMemberMCSN END)
          |WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid
          |; END
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS GroupLearningSession (`groupLearningSessionUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `groupLearningSessionContentUid` INTEGER NOT NULL, `groupLearningSessionLearnerGroupUid` INTEGER NOT NULL, `groupLearningSessionInactive` INTEGER NOT NULL, `groupLearningSessionMCSN` INTEGER NOT NULL, `groupLearningSessionCSN` INTEGER NOT NULL, `groupLearningSessionLCB` INTEGER NOT NULL)")
                    database.execSQL("CREATE TABLE IF NOT EXISTS GroupLearningSession_trk (`pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `epk` INTEGER NOT NULL, `clientId` INTEGER NOT NULL, `csn` INTEGER NOT NULL, `rx` INTEGER NOT NULL, `reqId` INTEGER NOT NULL, `ts` INTEGER NOT NULL)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS `index_GroupLearningSession_trk_clientId_epk_rx_csn` ON GroupLearningSession_trk (`clientId`, `epk`, `rx`, `csn`)")

                    database.execSQL("""
          |CREATE TRIGGER UPD_302
          |AFTER UPDATE ON GroupLearningSession FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.groupLearningSessionMCSN = 0 
          |OR OLD.groupLearningSessionMCSN = NEW.groupLearningSessionMCSN
          |)
          |ELSE
          |(NEW.groupLearningSessionCSN = 0  
          |OR OLD.groupLearningSessionCSN = NEW.groupLearningSessionCSN
          |) END)
          |BEGIN 
          |UPDATE GroupLearningSession SET groupLearningSessionCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.groupLearningSessionCSN 
          |ELSE (SELECT MAX(MAX(groupLearningSessionCSN), OLD.groupLearningSessionCSN) + 1 FROM GroupLearningSession) END),
          |groupLearningSessionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(MAX(groupLearningSessionMCSN), OLD.groupLearningSessionMCSN) + 1 FROM GroupLearningSession)
          |ELSE NEW.groupLearningSessionMCSN END)
          |WHERE groupLearningSessionUid = NEW.groupLearningSessionUid
          |; END
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER INS_302
          |AFTER INSERT ON GroupLearningSession FOR EACH ROW WHEN
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(NEW.groupLearningSessionMCSN = 0 
          |
          |)
          |ELSE
          |(NEW.groupLearningSessionCSN = 0  
          |
          |) END)
          |BEGIN 
          |UPDATE GroupLearningSession SET groupLearningSessionCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.groupLearningSessionCSN 
          |ELSE (SELECT MAX(groupLearningSessionCSN) + 1 FROM GroupLearningSession) END),
          |groupLearningSessionMCSN = 
          |(SELECT CASE WHEN (SELECT master FROM SyncNode) THEN 
          |(SELECT MAX(groupLearningSessionMCSN) + 1 FROM GroupLearningSession)
          |ELSE NEW.groupLearningSessionMCSN END)
          |WHERE groupLearningSessionUid = NEW.groupLearningSessionUid
          |; END
          """.trimMargin())


                } else if (database.dbType() == DoorDbType.POSTGRES) {

                    database.execSQL("ALTER TABLE StatementEntity ADD COLUMN statementLearnerGroupUid BIGINT DEFAULT 0 NOT NULL")

                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroup (  learnerGroupName  TEXT , learnerGroupDescription  TEXT , learnerGroupActive  BOOL , learnerGroupMCSN  BIGINT , learnerGroupCSN  BIGINT , learnerGroupLCB  INTEGER , learnerGroupUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS LearnerGroup_mcsn_seq")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS LearnerGroup_lcsn_seq")
                    database.execSQL("""
          |CREATE OR REPLACE FUNCTION 
          | inccsn_301_fn() RETURNS trigger AS ${'$'}${'$'}
          | BEGIN  
          | UPDATE LearnerGroup SET learnerGroupCSN =
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupCSN 
          | ELSE NEXTVAL('LearnerGroup_lcsn_seq') END),
          | learnerGroupMCSN = 
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
          | THEN NEXTVAL('LearnerGroup_mcsn_seq') 
          | ELSE NEW.learnerGroupMCSN END)
          | WHERE learnerGroupUid = NEW.learnerGroupUid;
          | RETURN null;
          | END ${'$'}${'$'}
          | LANGUAGE plpgsql
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER inccsn_301_trig 
          |AFTER UPDATE OR INSERT ON LearnerGroup 
          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
          |EXECUTE PROCEDURE inccsn_301_fn()
          """.trimMargin())
                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroup_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("""
          |CREATE 
          | INDEX index_LearnerGroup_trk_clientId_epk_rx_csn 
          |ON LearnerGroup_trk (clientId, epk, rx, csn)
          """.trimMargin())


                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroupMember (  learnerGroupMemberPersonUid  BIGINT , learnerGroupMemberLgUid  BIGINT , learnerGroupMemberRole  INTEGER , learnerGroupMemberActive  BOOL , learnerGroupMemberMCSN  BIGINT , learnerGroupMemberCSN  BIGINT , learnerGroupMemberLCB  INTEGER , learnerGroupMemberUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS LearnerGroupMember_mcsn_seq")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS LearnerGroupMember_lcsn_seq")
                    database.execSQL("""
          |CREATE OR REPLACE FUNCTION 
          | inccsn_300_fn() RETURNS trigger AS ${'$'}${'$'}
          | BEGIN  
          | UPDATE LearnerGroupMember SET learnerGroupMemberCSN =
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.learnerGroupMemberCSN 
          | ELSE NEXTVAL('LearnerGroupMember_lcsn_seq') END),
          | learnerGroupMemberMCSN = 
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
          | THEN NEXTVAL('LearnerGroupMember_mcsn_seq') 
          | ELSE NEW.learnerGroupMemberMCSN END)
          | WHERE learnerGroupMemberUid = NEW.learnerGroupMemberUid;
          | RETURN null;
          | END ${'$'}${'$'}
          | LANGUAGE plpgsql
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER inccsn_300_trig 
          |AFTER UPDATE OR INSERT ON LearnerGroupMember 
          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
          |EXECUTE PROCEDURE inccsn_300_fn()
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS LearnerGroupMember_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("""
          |CREATE 
          | INDEX index_LearnerGroupMember_trk_clientId_epk_rx_csn 
          |ON LearnerGroupMember_trk (clientId, epk, rx, csn)
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS GroupLearningSession (  groupLearningSessionContentUid  BIGINT , groupLearningSessionLearnerGroupUid  BIGINT , groupLearningSessionInactive  BOOL , groupLearningSessionMCSN  BIGINT , groupLearningSessionCSN  BIGINT , groupLearningSessionLCB  INTEGER , groupLearningSessionUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS GroupLearningSession_mcsn_seq")
                    database.execSQL("CREATE SEQUENCE IF NOT EXISTS GroupLearningSession_lcsn_seq")
                    database.execSQL("""
          |CREATE OR REPLACE FUNCTION 
          | inccsn_302_fn() RETURNS trigger AS ${'$'}${'$'}
          | BEGIN  
          | UPDATE GroupLearningSession SET groupLearningSessionCSN =
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.groupLearningSessionCSN 
          | ELSE NEXTVAL('GroupLearningSession_lcsn_seq') END),
          | groupLearningSessionMCSN = 
          | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
          | THEN NEXTVAL('GroupLearningSession_mcsn_seq') 
          | ELSE NEW.groupLearningSessionMCSN END)
          | WHERE groupLearningSessionUid = NEW.groupLearningSessionUid;
          | RETURN null;
          | END ${'$'}${'$'}
          | LANGUAGE plpgsql
          """.trimMargin())
                    database.execSQL("""
          |CREATE TRIGGER inccsn_302_trig 
          |AFTER UPDATE OR INSERT ON GroupLearningSession 
          |FOR EACH ROW WHEN (pg_trigger_depth() = 0) 
          |EXECUTE PROCEDURE inccsn_302_fn()
          """.trimMargin())

                    database.execSQL("CREATE TABLE IF NOT EXISTS GroupLearningSession_trk (  epk  BIGINT , clientId  INTEGER , csn  INTEGER , rx  BOOL , reqId  INTEGER , ts  BIGINT , pk  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                    database.execSQL("""
          |CREATE 
          | INDEX index_GroupLearningSession_trk_clientId_epk_rx_csn 
          |ON GroupLearningSession_trk (clientId, epk, rx, csn)
          """.trimMargin())


                }


            }
        }

        val MIGRATION_41_42 = object : DoorMigration(41, 42) {
            override fun migrate(database: DoorSqlDatabase) {

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("ALTER TABLE ScrapeQueueItem ADD COLUMN sqiContentEntryUid INTEGER DEFAULT 0 NOT NULL")
                    database.execSQL("ALTER TABLE ScrapeQueueItem ADD COLUMN overrideEntry INTEGER DEFAULT 0 NOT NULL")

                } else if (database.dbType() == DoorDbType.POSTGRES) {

                    database.execSQL("ALTER TABLE ScrapeQueueItem ADD COLUMN sqiContentEntryUid BIGINT DEFAULT 0 NOT NULL")
                    database.execSQL("ALTER TABLE ScrapeQueueItem ADD COLUMN overrideEntry BOOL DEFAULT false NOT NULL")

                }
            }
        }

        val MIGRATION_42_43 = UmAppDatabase_SyncPushMigration()

        val MIGRATION_43_44 = object : DoorMigration(43, 44) {
            override fun migrate(database: DoorSqlDatabase) {
                try {
                    //Sometimes the permission on this goes horribly wrong for no apparent reason
                    database.execSQL("ALTER TABLE SqliteSyncablePrimaryKey RENAME to SqliteSyncablePk")
                }catch(e: Exception) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS SqliteSyncablePk (  sspTableId  INTEGER  PRIMARY KEY  NOT NULL , sspNextPrimaryKey  INTEGER  NOT NULL )")
                }

            }
        }

        val MIGRATION_44_45 = object : DoorMigration(44, 45) {
            override fun migrate(database: DoorSqlDatabase) {

                database.execSQL("DROP TABLE ContainerUploadJob")

                if (database.dbType() == DoorDbType.SQLITE) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS ContainerImportJob (`cijUid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cijContainerUid` INTEGER NOT NULL, `cijFilePath` TEXT, `cijContainerBaseDir` TEXT, `cijContentEntryUid` INTEGER NOT NULL, `cijMimeType` TEXT, `cijSessionId` TEXT, `cijJobStatus` INTEGER NOT NULL, `cijBytesSoFar` INTEGER NOT NULL, `cijImportCompleted` INTEGER NOT NULL, `cijContentLength` INTEGER NOT NULL, `cijContainerEntryFileUids` TEXT, `cijConversionParams` TEXT)")
                }else if (database.dbType() == DoorDbType.POSTGRES) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS ContainerImportJob (  cijContainerUid  BIGINT , cijFilePath  TEXT , cijContainerBaseDir  TEXT , cijContentEntryUid  BIGINT , cijMimeType  TEXT , cijSessionId  TEXT , cijJobStatus  INTEGER , cijBytesSoFar  BIGINT , cijImportCompleted  BOOL , cijContentLength  BIGINT , cijContainerEntryFileUids  TEXT , cijConversionParams  TEXT , cijUid  BIGSERIAL  PRIMARY KEY  NOT NULL )")
                }
            }
        }

        val MIGRATION_45_46 = object : DoorMigration(45, 46) {
            override fun migrate(database: DoorSqlDatabase) {

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("""
                        Update ClazzWorkQuestionResponse
                        SET clazzWorkQuestionResponseLCB = (SELECT nodeClientId from SyncNode)
                        WHERE
                        clazzWorkQuestionResponseLCB = 0
                    """.trimIndent())

                }
            }
        }

        /**
         * Add indexes to improve performance of queries that check permissions
         */
        val MIGRATION_46_47 = object : DoorMigration(46, 47) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("CREATE INDEX index_ClazzMember_clazzMemberPersonUid_clazzMemberClazzUid ON ClazzMember (clazzMemberPersonUid, clazzMemberClazzUid)")
                database.execSQL("CREATE INDEX index_ClazzMember_clazzMemberClazzUid_clazzMemberPersonUid ON ClazzMember (clazzMemberClazzUid, clazzMemberPersonUid)")
                database.execSQL("CREATE INDEX index_EntityRole_erGroupUid_erRoleUid_erTableId ON EntityRole (erGroupUid, erRoleUid, erTableId)")
                database.execSQL("CREATE INDEX index_Role_rolePermissions ON Role(rolePermissions)")

                //Add a PersonGroup for Admin
                if(database.dbType() == DoorDbType.POSTGRES) {
                    database.execSQL("""
                        INSERT INTO PersonGroup(groupName, groupActive, personGroupFlag, groupMasterCsn, groupLocalCsn, groupLastChangedBy) 
                        SELECT 'PGA' || person.personUid AS groupName, 
                        true as groupActive,
                        1 as personGroupFlag,
                        0 as groupMasterCsn,
                        0 as groupLocalCsn,
                        (SELECT nodeClientId FROM SyncNode) as groupLastChangedBy
                        FROM person
                        where admin = true
                        AND personGroupUid = 0""")
                    database.execSQL("""
                        UPDATE Person SET
                        personGroupUid = (SELECT groupUid FROM PersonGroup WHERE groupName = ('PGA' || Person.personUid) LIMIT 1),
                        personLastChangedBy = (SELECT nodeClientId FROM SyncNode) 
                        WHERE
                        admin = true AND personGroupUid = 0
                    """)
                    database.execSQL("""
                        INSERT INTO PersonGroupMember(groupMemberPersonUid, groupMemberGroupUid, groupMemberMasterCsn, groupMemberLocalCsn, groupMemberLastChangedBy)
                        SELECT Person.personUid AS groupMemberPersonUid,
                        Person.personGroupUid AS groupMemberGroupUid,
                        0 AS groupMemberMasterCsn,
                        0 AS groupMemberLocalCsn,
                        (SELECT nodeClientId FROM SyncNode) AS groupMemberLastChangedBy
                        FROM Person
                        WHERE admin = true
                        AND (SELECT COUNT(*) FROM PersonGroupMember WHERE PersonGroupmember.groupMemberGroupUid = Person.personGroupUid) = 0
                    """)
                }

            }
        }

        val MIGRATION_47_48 = object : DoorMigration(47, 48) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("CREATE INDEX " +
                        "index_ClazzMember_clazzMemberClazzUid_clazzMemberRole " +
                        "ON ClazzMember (clazzMemberClazzUid, clazzMemberRole)")
                database.execSQL("CREATE INDEX " +
                        "index_SchoolMember_schoolMemberSchoolUid_schoolMemberActive_schoolMemberRole " +
                        "ON SchoolMember (schoolMemberSchoolUid, schoolMemberActive, schoolMemberRole)")
            }
        }

        val MIGRATION_48_49 = object : DoorMigration(48, 49) {
            override fun migrate(database: DoorSqlDatabase) {

                database.execSQL("""ALTER TABLE ScrapeRun ADD COLUMN conversionParams TEXT""".trimMargin())

                database.execSQL("""
          |CREATE 
          | INDEX index_ScrapeQueueItem_status_itemType 
          |ON ScrapeQueueItem (status, itemType)
          """.trimMargin())

                if (database.dbType() == DoorDbType.SQLITE) {

                    database.execSQL("ALTER TABLE ScrapeRun RENAME to ScrapeRun_OLD")
                    database.execSQL("CREATE TABLE IF NOT EXISTS ScrapeRun (  scrapeType  TEXT , scrapeRunStatus  INTEGER  NOT NULL , conversionParams  TEXT , scrapeRunUid  INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL )")
                    database.execSQL("INSERT INTO ScrapeRun (scrapeRunUid, scrapeType, scrapeRunStatus, conversionParams) SELECT scrapeRunUid, scrapeType, status, conversionParams FROM ScrapeRun_OLD")
                    database.execSQL("DROP TABLE ScrapeRun_OLD")

                }else if (database.dbType() == DoorDbType.POSTGRES) {
                    database.execSQL("""ALTER TABLE ScrapeRun RENAME COLUMN status to scrapeRunStatus
                        """.trimMargin())
                }

            }
        }

        val MIGRATION_49_50 = object : DoorMigration(49, 50) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("DROP TABLE TimeZoneEntity")
            }
        }

        val MIGRATION_50_51 = object: DoorMigration(50, 51) {
            override fun migrate(database: DoorSqlDatabase) {
                database.execSQL("DROP TABLE IF EXISTS SqliteSyncablePk")
            }
        }

        //One off server only change to update clazz end time default to Long.MAX_VALUE
        val MIGRATION_51_52 = object: DoorMigration(51, 52) {
            override fun migrate(database: DoorSqlDatabase) {
                if(database.dbType() == DoorDbType.POSTGRES) {
                    database.execSQL("UPDATE Clazz SET clazzEndTime = ${systemTimeInMillis()}," +
                            "clazzLastChangedBy = (SELECT nodeClientId FROM SyncNode LIMIT 1) " +
                            "WHERE clazzEndTime = 0")
                }
            }
        }


        private fun addMigrations(builder: DatabaseBuilder<UmAppDatabase>): DatabaseBuilder<UmAppDatabase> {

            builder.addMigrations(MIGRATION_32_33, MIGRATION_33_34, MIGRATION_33_34, MIGRATION_34_35,
                    MIGRATION_35_36, MIGRATION_36_37, MIGRATION_37_38, MIGRATION_38_39,
                    MIGRATION_39_40, MIGRATION_40_41, MIGRATION_41_42, MIGRATION_42_43,
                    MIGRATION_43_44, MIGRATION_44_45, MIGRATION_45_46, MIGRATION_46_47,
                    MIGRATION_47_48, MIGRATION_48_49, MIGRATION_49_50, MIGRATION_50_51,
                    MIGRATION_51_52)



            return builder
        }
    }


}
