package com.ustadmobile.core.catalog.contenttype

import com.ustadmobile.core.controller.VideoContentPresenterCommon.Companion.VIDEO_MIME_MAP
import com.ustadmobile.core.view.VideoPlayerView

open class VideoTypePlugin : ContentTypePlugin {

    override val viewName: String
        get() = VideoPlayerView.VIEW_NAME

    override val mimeTypes: Array<String>
        get() = VIDEO_MIME_MAP.keys.toTypedArray()

    override val fileExtensions: Array<String>
        get() = VIDEO_MIME_MAP.values.map { it.removePrefix(".") }.toTypedArray()
}