package com.ustadmobile.port.android.screen

import com.agoda.kakao.common.views.KView
import com.kaspersky.kaspresso.screens.KScreen
import com.toughra.ustadmobile.R
import com.ustadmobile.port.android.view.PersonDetailFragment

object PersonDetailScreen : KScreen<PersonDetailScreen>() {
    override val layoutId: Int?
        get() = R.layout.fragment_person_detail
    override val viewClass: Class<*>?
        get() = PersonDetailFragment::class.java

    val changePassView: KView = KView {
        withId(R.id.change_account_password_view)
        withDescendant { withText("Change Password") }
    }

    val createAccView: KView = KView { withId(R.id.create_account_view) }

}