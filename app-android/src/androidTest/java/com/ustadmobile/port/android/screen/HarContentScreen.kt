package com.ustadmobile.port.android.screen

import com.agoda.kakao.web.KWebView
import com.kaspersky.kaspresso.screens.KScreen
import com.toughra.ustadmobile.R
import com.ustadmobile.port.android.view.HarContentFragment

object HarContentScreen : KScreen<HarContentScreen>() {
    override val layoutId: Int?
        get() = R.layout.fragment_har_content

    @ExperimentalStdlibApi
    override val viewClass: Class<*>?
        get() = HarContentFragment::class.java

    val harWebView = KWebView { withId(R.id.har_webview)}


}