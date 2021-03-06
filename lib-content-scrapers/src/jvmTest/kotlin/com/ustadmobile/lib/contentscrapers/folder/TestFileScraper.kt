package com.ustadmobile.lib.contentscrapers.folder

import com.nhaarman.mockitokotlin2.spy
import com.ustadmobile.core.account.Endpoint
import com.ustadmobile.core.account.EndpointScope
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.util.DiTag
import com.ustadmobile.door.ext.bindNewSqliteDataSourceIfNotExisting
import com.ustadmobile.lib.util.sanitizeDbNameFromUrl
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kodein.di.*
import java.io.File
import java.nio.file.Files
import javax.naming.InitialContext

@ExperimentalStdlibApi
class TestFileScraper {

    private lateinit var scooterFile: File
    private lateinit var englishFolder: File
    lateinit var db: UmAppDatabase


    private lateinit var di: DI
    private lateinit var endpointScope: EndpointScope
    private val endpoint = Endpoint(TestFolderIndexer.TEST_ENDPOINT)

    @Rule
    @JvmField
    val tmpFileRule = TemporaryFolder()

    val tmpDir = Files.createTempDirectory("folder").toFile()
    val containerDir = Files.createTempDirectory("container").toFile()


    @Before
    fun setup(){
        endpointScope = EndpointScope()

        di = DI {
            bind<UmAppDatabase>(tag = UmAppDatabase.TAG_DB) with scoped(endpointScope).singleton {
                val dbName = sanitizeDbNameFromUrl(context.url)
                InitialContext().bindNewSqliteDataSourceIfNotExisting(dbName)
                spy(UmAppDatabase.getInstance(Any(), dbName).also {
                    it.clearAllTables()
                })
            }
            bind<File>(tag = DiTag.TAG_CONTAINER_DIR) with scoped(EndpointScope.Default).singleton {
                containerDir
            }
            bind<String>(tag = DiTag.TAG_GOOGLE_API) with singleton {
                "abc"
            }
        }

        db = di.on(endpoint).direct.instance(tag = UmAppDatabase.TAG_DB)

        englishFolder = File(tmpDir, "english")
        englishFolder.mkdirs()

        scooterFile = File(tmpDir, "scooter-en.epub")
        FileUtils.copyToFile(javaClass.getResourceAsStream("/com/ustadmobile/lib/contentscrapers/folder/314-my-very-own-scooter-EN.epub"),
                scooterFile)

    }


    @Test
    fun givenAFile_whenScraped_thenCreateContainer(){

        val scraper = FileScraper(0, 0,0, endpoint, di)
        scraper.scrapeUrl(scooterFile.path)

        val filEntry = db.contentEntryDao.findBySourceUrl(scooterFile.path)
        Assert.assertEquals("Scooter content exists", "My Own Scooter", filEntry!!.title)

        runBlocking {
            val fileContainer = db.containerDao.findRecentContainerToBeMonitoredWithEntriesUid(listOf(filEntry.contentEntryUid))
            val container = fileContainer[0]
            Assert.assertEquals("container is epub", "application/epub+zip", container.mimeType)
        }

    }



}