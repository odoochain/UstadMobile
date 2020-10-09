package com.ustadmobile.port.android.screen

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.text.KTextView
import com.kaspersky.kaspresso.screens.KScreen
import com.toughra.ustadmobile.R
import com.ustadmobile.port.android.view.PersonListFragment
import org.hamcrest.Matcher

object PersonListScreen : KScreen<PersonListScreen>() {


    override val layoutId: Int?
        get() = R.layout.fragment_list
    override val viewClass: Class<*>?
        get() = PersonListFragment::class.java

    val recycler: KRecyclerView = KRecyclerView({
        withId(R.id.fragment_list_recyclerview)
    }, itemTypeBuilder = {
        itemType(::Person)
    })


    class Person(parent: Matcher<View>) : KRecyclerItem<Person>(parent) {
        val personName: KTextView = KTextView(parent) { withId(R.id.item_person_text) }
    }

}