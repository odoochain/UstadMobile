package com.ustadmobile.lib.contentscrapers.abztract

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.db.dao.ContentEntryDao
import com.ustadmobile.core.db.dao.ScrapeQueueItemDao
import com.ustadmobile.core.db.dao.ScrapeRunDao
import com.ustadmobile.lib.contentscrapers.ContentScraperUtil
import com.ustadmobile.lib.contentscrapers.UMLogUtil
import com.ustadmobile.lib.db.entities.ScrapeQueueItem
import com.ustadmobile.lib.db.entities.ScrapeRun
import com.ustadmobile.sharedse.util.LiveDataWorkQueue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.cli.*
import org.apache.commons.lang.exception.ExceptionUtils
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess


class ScraperRunner(private val containerPath: String, private val indexTotal: Int = 4, private val scraperTotal: Int = 1) {


    private var runDao: ScrapeRunDao
    private var contentEntryDao: ContentEntryDao
    private var queueDao: ScrapeQueueItemDao
    var db: UmAppDatabase = UmAppDatabase.getInstance(Any())


    init {
        runDao = db.scrapeRunDao
        queueDao = db.scrapeQueueItemDao
        contentEntryDao = db.contentEntryDao
    }


    fun start(startingUrl: String, scraperType: String, parentUid: Long) {

        val runId = runDao.insert(ScrapeRun(scraperType,
                ScrapeQueueItemDao.STATUS_PENDING)).toInt()

        val scrapeQueue = ScrapeQueueItem().apply {
            this.runId = runId
            contentType = scraperType
            scrapeUrl = startingUrl
            sqiContentEntryParentUid = parentUid
            status = ScrapeQueueItemDao.STATUS_PENDING
            itemType = ScrapeQueueItem.ITEM_TYPE_INDEX
            timeAdded = System.currentTimeMillis()
        }
        queueDao.insert(scrapeQueue)


        resume(runId)
    }

    fun resume(runId: Int) {

        val indexWorkQueue = LiveDataWorkQueue(queueDao.findNextQueueItems(runId, ScrapeQueueItem.ITEM_TYPE_INDEX),
                { item1, item2 -> item1.sqiUid == item2.sqiUid },
                indexTotal) {

            queueDao.updateSetStatusById(it.sqiUid, ScrapeQueueItemDao.STATUS_RUNNING, 0)

            val startTime = System.currentTimeMillis()
            UMLogUtil.logInfo("Started indexer url ${it.scrapeUrl} at start time: $startTime")

            queueDao.setTimeStarted(it.sqiUid, startTime)
            try {
                val indexerClazz = ScraperTypes.indexerTypeMap[it.contentType]
                val cons = indexerClazz?.clazz?.getConstructor(Long::class.java, Int::class.java, UmAppDatabase::class.java, Int::class.java)
                val obj = cons?.newInstance(it.sqiContentEntryParentUid, it.runId, db, it.sqiUid) as Indexer?
                obj?.indexUrl(it.scrapeUrl!!)

            } catch (e: Exception) {
                UMLogUtil.logError("Exception running indexer ${it.scrapeUrl}")
                UMLogUtil.logError(ExceptionUtils.getStackTrace(e))
            }

            queueDao.setTimeFinished(it.sqiUid, System.currentTimeMillis())
            val duration = System.currentTimeMillis() - startTime
            UMLogUtil.logInfo("Ended indexer for url ${it.scrapeUrl} in duration: $duration")
        }

        GlobalScope.launch {
            UMLogUtil.logDebug("start indexer")
            indexWorkQueue.start()
        }

        val scrapeWorkQueue = LiveDataWorkQueue(queueDao.findNextQueueItems(runId, ScrapeQueueItem.ITEM_TYPE_SCRAPE),
                { item1, item2 -> item1.sqiUid == item2.sqiUid }, scraperTotal) {

            queueDao.updateSetStatusById(it.sqiUid, ScrapeQueueItemDao.STATUS_RUNNING, 0)

            val startTime = System.currentTimeMillis()
            UMLogUtil.logInfo("Started scraper url ${it.scrapeUrl} at start time: $startTime")

            queueDao.setTimeStarted(it.sqiUid, startTime)
            try {

                val scraperClazz = ScraperTypes.scraperTypeMap[it.contentType]
                val cons = scraperClazz?.getConstructor(File::class.java, UmAppDatabase::class.java, Long::class.java, Int::class.java)
                val obj = cons?.newInstance(File(containerPath), db, it.sqiContentEntryParentUid, it.sqiUid)
                obj?.scrapeUrl(it.scrapeUrl!!)
            } catch (e: Exception) {
                UMLogUtil.logError("Exception running scrapeContent ${it.scrapeUrl}")
                UMLogUtil.logError(ExceptionUtils.getStackTrace(e))
            }

            queueDao.setTimeFinished(it.sqiUid, System.currentTimeMillis())
            val duration = System.currentTimeMillis() - startTime
            UMLogUtil.logInfo("Ended scrape for url ${it.scrapeUrl} in duration: $duration")

        }
        GlobalScope.launch {
            UMLogUtil.logDebug("start scraper")
            scrapeWorkQueue.start()
        }

        ContentScraperUtil.waitForQueueToFinish(queueDao, runId)

        UMLogUtil.logDebug("finished queue")

    }


    companion object {

        private const val CONTAINER_ARGS = "container"
        private const val CLAZZ_ARGS = "startScrape"
        private const val LOG_ARGS = "log"
        private const val RUN_ID_ARGS = "resume"
        private const val INDEXER_ARGS = "indexer"
        private const val SCRAPER_ARGS = "scraper"
        private const val START_URL_ARGS = "url"
        private const val PARENT_ENTRY_UID_ARGS = "parentUid"

        const val ERROR_TYPE_UNKNOWN = 10

        @JvmStatic
        fun main(args: Array<String>) {

            val options = Options()

            val containerOption = Option.builder(CONTAINER_ARGS)
                    .argName("file")
                    .hasArg()
                    .required()
                    .desc("container path")
                    .build()
            options.addOption(containerOption)

            val clazz = Option.builder(CLAZZ_ARGS)
                    .argName("scraperType")
                    .hasArg()
                    .desc("choose the class to run the scraper - see scraper type map")
                    .build()
            options.addOption(clazz)

            val startUrlOption = Option.builder(START_URL_ARGS)
                    .argName("url")
                    .hasArg()
                    .desc("starting url for scrape")
                    .build()
            options.addOption(startUrlOption)

            val parentUidOption = Option.builder(PARENT_ENTRY_UID_ARGS)
                    .argName(PARENT_ENTRY_UID_ARGS)
                    .hasArg()
                    .desc("parentUid for indexer to start from")
                    .build()
            options.addOption(parentUidOption)

            val debugOption = Option.builder(LOG_ARGS)
                    .argName("level")
                    .hasArg()
                    .desc("Set the level of the log [trace, info, debug, error, fatal]")
                    .build()
            options.addOption(debugOption)


            val runOption = Option.builder(RUN_ID_ARGS)
                    .argName("run id")
                    .hasArg()
                    .desc("set the run id to resume previous scrape runner")
                    .build()
            options.addOption(runOption)

            val indexOption = Option.builder(INDEXER_ARGS)
                    .argName("index total")
                    .hasArg()
                    .desc("set the total number of indexer should be running together")
                    .build()
            options.addOption(indexOption)

            val scraperOption = Option.builder(SCRAPER_ARGS)
                    .argName("scraper total")
                    .hasArg()
                    .desc("set the total number of scrapers should be running together")
                    .build()
            options.addOption(scraperOption)
            val cmd: CommandLine
            try {

                val parser: CommandLineParser = DefaultParser()
                cmd = parser.parse(options, args)

            } catch (e: ParseException) {
                System.err.println("Parsing failed.  Reason: " + e.message)
                exitProcess(1)
            }

            val indexTotal: Int = cmd?.getOptionValue(INDEXER_ARGS)?.toIntOrNull() ?: 4
            val scraperTotal: Int = cmd?.getOptionValue(SCRAPER_ARGS)?.toIntOrNull() ?: 1

            val logLevel = cmd?.getOptionValue(LOG_ARGS) ?: ""
            UMLogUtil.setLevel(logLevel)


            val runner = ScraperRunner(cmd.getOptionValue(CONTAINER_ARGS), indexTotal, scraperTotal)
            if (cmd.hasOption(RUN_ID_ARGS)) {
                runner.resume(cmd.getOptionValue(RUN_ID_ARGS).toInt())
            } else {

                val parentUid = cmd?.getOptionValue(PARENT_ENTRY_UID_ARGS)?.toLongOrNull() ?: 0
                val scraperType = cmd.getOptionValue(CLAZZ_ARGS)
                val startingUrl = cmd.getOptionValue(START_URL_ARGS)
                        ?: ScraperTypes.indexerTypeMap[scraperType]?.defaultUrl
                        ?: throw IllegalArgumentException("No default url for this scraperType, please provide")

                runner.start(startingUrl, scraperType, parentUid)
            }

        }

    }

}