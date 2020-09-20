package com.ustadmobile.port.sharedse.contentformats

import com.ustadmobile.port.sharedse.contentformats.xapi.plugin.XapiPackageTypeFilePlugin
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.FileOutputStream

class XapiContentTypePluginTest {

    @JvmField
    @Rule
    var temporaryFolder = TemporaryFolder()

    @Test
    fun givenValidTinCanFormatFile_whenGetContentEntryCalled_thenShouldReadMetaData() {
        val inputStream = this::class.java.getResourceAsStream(
                "/com/ustadmobile/port/sharedse/contentformats/ustad-tincan.zip")
        val tempFile = temporaryFolder.newFile("tincan.zip")
        inputStream.use {inStream ->
            FileOutputStream(tempFile).use {
                inStream.copyTo(it)
                it.flush()
            }
        }

        val xapiPlugin = XapiPackageTypeFilePlugin()
        val contentEntry = xapiPlugin.getContentEntry(tempFile.toURI().toString())

        Assert.assertEquals("Got expected title",
                "Ustad Mobile", contentEntry?.title)
        Assert.assertEquals("Got expected description",
            "Ustad Mobile sample tincan", contentEntry?.description)
    }


}