package com.ustadmobile.lib.contentscrapers.googledrive

import com.github.aakira.napier.Napier
import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import com.ustadmobile.core.account.Endpoint
import com.ustadmobile.core.networkmanager.defaultHttpClient
import com.ustadmobile.core.util.DiTag
import com.ustadmobile.core.util.ext.alternative
import com.ustadmobile.lib.contentscrapers.ContentScraperUtil
import com.ustadmobile.lib.contentscrapers.ScraperConstants.SCRAPER_TAG
import com.ustadmobile.lib.contentscrapers.UMLogUtil
import com.ustadmobile.lib.contentscrapers.abztract.Scraper
import com.ustadmobile.lib.db.entities.ContentEntry
import com.ustadmobile.port.sharedse.contentformats.extractContentEntryMetadataFromFile
import com.ustadmobile.port.sharedse.contentformats.importContainerFromFile
import com.ustadmobile.port.sharedse.contentformats.mimeTypeSupported
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files

@ExperimentalStdlibApi
class GoogleDriveScraper(contentEntryUid: Long, sqiUid: Int, parentContentEntryUid: Long, endpoint: Endpoint, di: DI) : Scraper(contentEntryUid, sqiUid, parentContentEntryUid, endpoint, di) {

    private var tempDir: File? = null

    val googleDriveFormat: DateFormat = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    val googleApiKey: String by di.instance(tag = DiTag.TAG_GOOGLE_API)

    private val logPrefix = "[GoogleDriveScraper SQI ID #$sqiUid] "


    override fun scrapeUrl(sourceUrl: String) {

        var apiCall = sourceUrl
        var fileId = sourceUrl.substringAfter("https://www.googleapis.com/drive/v3/files/")
        if (sourceUrl.startsWith("https://drive.google.com/file/d/")) {
            val fileIdLookUp = sourceUrl.substringAfter("https://drive.google.com/file/d/")
            val char = fileIdLookUp.firstOrNull { it == '/' || it == '?' }
            fileId = if (char == null) fileIdLookUp else fileIdLookUp.substringBefore(char)
            apiCall = "https://www.googleapis.com/drive/v3/files/$fileId"
        }

        tempDir = Files.createTempDirectory(fileId).toFile()
        runBlocking {

            defaultHttpClient().get<HttpStatement>(apiCall) {
                parameter("key", googleApiKey)
                parameter("fields", "id,modifiedTime,name,mimeType,description,thumbnailLink")
            }.execute() { fileResponse ->

                val file = fileResponse.receive<GoogleFile>()

                mimeTypeSupported.find { fileMimeType -> fileMimeType == file.mimeType }
                        ?: return@execute

                val recentContainer = containerDao.getMostRecentContainerForContentEntry(contentEntryUid)
                val googleModifiedTime = file.modifiedTime
                val parsedModifiedTime: Long = if (googleModifiedTime != null) googleDriveFormat.parse(googleModifiedTime).local.unixMillisLong else 1
                val isUpdated = parsedModifiedTime > recentContainer?.cntLastModified ?: 0
                if (!isUpdated) {
                    Napier.i("$logPrefix with sourceUrl $sourceUrl already has this entry updated, close here", tag = SCRAPER_TAG)
                    showContentEntry()
                    setScrapeDone(true, 0)
                    close()
                    return@execute
                }

                defaultHttpClient().get<HttpStatement>(apiCall) {
                    parameter("alt", "media")
                    parameter("key", googleApiKey)
                }.execute() {

                    val contentFile = File(tempDir, file.name ?: file.id!!)
                    val stream = it.receive<InputStream>()
                    FileOutputStream(contentFile).use { fileOut ->
                        stream.copyTo(fileOut)
                        fileOut.flush()
                    }
                    stream.close()

                    val metadata = extractContentEntryMetadataFromFile(contentFile, db)

                    if (metadata == null) {
                        Napier.i("$logPrefix with sourceUrl $sourceUrl had no metadata found, not supported", tag = SCRAPER_TAG)
                        hideContentEntry()
                        setScrapeDone(false, ERROR_TYPE_MIME_TYPE_NOT_SUPPORTED)
                        close()
                        return@execute
                    }

                    val metadataContentEntry = metadata.contentEntry
                    var dbEntry = contentEntry
                    var fileEntry: ContentEntry

                    if (dbEntry != null && scrapeQueueItem?.overrideEntry == true) {
                        fileEntry = dbEntry
                    } else {
                        fileEntry = ContentScraperUtil.createOrUpdateContentEntry(
                                metadataContentEntry.entryId ?: contentEntry?.entryId ?: file.id,
                                metadataContentEntry.title ?: contentEntry?.title ?: file.name,
                                "https://www.googleapis.com/drive/v3/files/${file.id}",
                                metadataContentEntry.publisher ?: contentEntry?.publisher ?: "",
                                metadataContentEntry.licenseType.alternative(contentEntry?.licenseType
                                        ?: ContentEntry.LICENSE_TYPE_OTHER),
                                metadataContentEntry.primaryLanguageUid.alternative(contentEntry?.primaryLanguageUid
                                        ?: 0),
                                metadataContentEntry.languageVariantUid.alternative(contentEntry?.languageVariantUid
                                        ?: 0),
                                metadataContentEntry.description.alternative(contentEntry?.description
                                        ?: file.description ?: "")
                                , true, metadataContentEntry.author.alternative(contentEntry?.author
                                ?: ""),
                                metadataContentEntry.thumbnailUrl.alternative(contentEntry?.thumbnailUrl
                                        ?: file.thumbnailLink ?: ""),
                                "", "",
                                metadataContentEntry.contentTypeFlag, contentEntryDao)
                        Napier.d("$logPrefix new entry created/updated with entryUid ${fileEntry.contentEntryUid} with title ${file.name}", tag = SCRAPER_TAG)
                        ContentScraperUtil.insertOrUpdateChildWithMultipleParentsJoin(contentEntryParentChildJoinDao, parentContentEntry, fileEntry, 0)
                    }



                    importContainerFromFile(fileEntry.contentEntryUid,
                            metadata.mimeType, containerFolder.absolutePath,
                            contentFile, db, db, metadata.importMode, Any())

                    Napier.d("$logPrefix finished Scraping", tag = SCRAPER_TAG)
                    showContentEntry()
                    setScrapeDone(true, 0)
                    close()

                }

            }

        }
    }

    override fun close() {
        val deleted = tempDir?.deleteRecursively() ?: false
        UMLogUtil.logError("did it delete: $deleted for ${tempDir?.name} ")
    }


}