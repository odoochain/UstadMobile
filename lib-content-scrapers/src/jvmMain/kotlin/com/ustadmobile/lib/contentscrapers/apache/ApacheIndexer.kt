package com.ustadmobile.lib.contentscrapers.apache

import ScraperTypes
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.impl.UMLog
import com.ustadmobile.lib.contentscrapers.ContentScraperUtil
import com.ustadmobile.lib.contentscrapers.ScraperConstants
import com.ustadmobile.lib.contentscrapers.UMLogUtil
import com.ustadmobile.lib.contentscrapers.abztract.Indexer
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.lib.db.entities.ScrapeQueueItem
import com.ustadmobile.port.sharedse.contentformats.mimeTypeSupported
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

@ExperimentalStdlibApi
class ApacheIndexer(parentContentEntryUid: Long, runUid: Int, db: UmAppDatabase, sqiUid: Int, contentEntryUid: Long) : Indexer(parentContentEntryUid, runUid, db, sqiUid, contentEntryUid) {

    override fun indexUrl(sourceUrl: String) {

        val url = URL(sourceUrl)

        val huc: HttpURLConnection = url.openConnection() as HttpURLConnection

        UMLogUtil.logInfo("connection success ${huc.responseCode}")

        val data = String(huc.inputStream.readBytes())
        huc.disconnect()

        val document = Jsoup.parse(data)

        val folderTitle = document.title().substringAfterLast("/")

        UMLogUtil.logInfo("folder Title ${huc.responseCode}")

        val folderEntry = ContentScraperUtil.createOrUpdateContentEntry(folderTitle, folderTitle,
                sourceUrl, folderTitle, ContentEntry.LICENSE_TYPE_PUBLIC_DOMAIN, englishLang.langUid, null,
                ScraperConstants.EMPTY_STRING, false, ScraperConstants.EMPTY_STRING, ScraperConstants.EMPTY_STRING, ScraperConstants.EMPTY_STRING,
                ScraperConstants.EMPTY_STRING, ContentEntry.TYPE_COLLECTION, contentEntryDao)

        ContentScraperUtil.insertOrUpdateParentChildJoin(contentEntryParentChildJoinDao, parentcontentEntry, folderEntry, 0)

        document.select("tr:has([alt])").forEach {

            val alt = it.select("td [alt]").attr("alt")
            var conn: HttpURLConnection? = null
            try {
                val element = it.select("td a")
                val href = element.attr("href")
                val title = element.text()
                val hrefUrl = URL(url, href)

                UMLogUtil.logInfo("href $href with title $title")

                if (alt == "[DIR]") {

                    UMLogUtil.logInfo("it was a directory")

                    val childEntry = ContentScraperUtil.insertTempContentEntry(contentEntryDao, hrefUrl.toString(), folderEntry.primaryLanguageUid, title)
                    createQueueItem(hrefUrl.toString(), childEntry, ScraperTypes.APACHE_INDEXER, ScrapeQueueItem.ITEM_TYPE_INDEX, folderEntry.contentEntryUid)

                } else if (alt == "[   ]") {

                    UMLogUtil.logInfo("it was a file")

                    conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "HEAD"
                    val mimeType = conn.contentType

                    val supported = mimeTypeSupported.find { fileMimeType -> fileMimeType == mimeType }

                    if (supported != null) {
                        val childEntry = ContentScraperUtil.insertTempContentEntry(contentEntryDao, hrefUrl.toString(), folderEntry.primaryLanguageUid, title)
                        createQueueItem(hrefUrl.toString(), childEntry, ScraperTypes.APACHE_SCRAPER, ScrapeQueueItem.ITEM_TYPE_SCRAPE, folderEntry.contentEntryUid)
                    }else{
                        println("file: $title not supported with mimeType: $mimeType")
                        UMLogUtil.logInfo("file: $title not supported with mimeType: $mimeType")
                    }
                }else{
                    println("unknown apache: $title not supported with alt: $alt")
                    UMLogUtil.logInfo("unknown apache: $title not supported with alt: $alt")
                }
            }catch (e : Exception){
                UMLogUtil.logError("Error during directory search on $sourceUrl")
                UMLogUtil.logError(ExceptionUtils.getStackTrace(e))
            }finally {
                conn?.disconnect()
            }
        }

        setIndexerDone(true, 0)
        close()
    }

    override fun close() {


    }
}