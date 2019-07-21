package com.ustadmobile.port.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.Nullable
import com.toughra.ustadmobile.R
import com.ustadmobile.core.db.UmProvider
import com.ustadmobile.core.view.PersonListView
import com.ustadmobile.lib.db.entities.Person

/**
 * Created by mike on 3/8/18.
 */

class PersonListFragment : UstadBaseFragment(), PersonListView {
    override val viewContext: Any
        get() = context!!

    private var rootView: View? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_person_list, container, false)


        return rootView
    }

    override fun setProvider(provider: UmProvider<Person>) {

    }


}