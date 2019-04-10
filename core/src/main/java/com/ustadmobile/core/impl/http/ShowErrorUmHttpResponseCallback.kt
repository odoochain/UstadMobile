package com.ustadmobile.core.impl.http

import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.view.ViewWithErrorNotifier

import java.io.IOException

/**
 * Utility callback that will use the views method to show an error when an HTTP request fails or
 * the response isSuccessful is false.
 */
abstract class ShowErrorUmHttpResponseCallback(private val view: ViewWithErrorNotifier, errorMessageId: Int) : UmHttpResponseCallback {

    private var errorMessageId = -1

    init {
        this.errorMessageId = errorMessageId
    }

    override fun onComplete(call: UmHttpCall, response: UmHttpResponse) {
        if (!response.isSuccessful) {
            onFailure(call, IOException())
        }
    }

    override fun onFailure(call: UmHttpCall, exception: IOException) {
        view.showErrorNotification(UstadMobileSystemImpl.instance.getString(errorMessageId,
                view.context), null!!, 0)
    }
}
