package com.ustadmobile.core.controller

import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.view.HarView
import org.kodein.di.DI

@ExperimentalStdlibApi
expect class HarContentPresenter(context: Any, arguments: Map<String, String>,
                                 view: HarView,
                                 localHttp: String,
                                 di: DI): HarContentPresenterCommon {


}