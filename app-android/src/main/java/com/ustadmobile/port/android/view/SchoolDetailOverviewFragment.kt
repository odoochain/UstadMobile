package com.ustadmobile.port.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.toughra.ustadmobile.R
import com.toughra.ustadmobile.databinding.FragmentSchoolOverviewBinding
import com.toughra.ustadmobile.databinding.ItemClazzSimpleBinding
import com.ustadmobile.core.controller.SchoolDetailOverviewPresenter
import com.ustadmobile.core.db.dao.ClazzDao
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.ext.toStringMap
import com.ustadmobile.core.view.EditButtonMode
import com.ustadmobile.core.view.SchoolDetailOverviewView
import com.ustadmobile.door.DoorMutableLiveData
import com.ustadmobile.door.ext.asRepositoryLiveData
import com.ustadmobile.lib.db.entities.Clazz
import com.ustadmobile.lib.db.entities.SchoolWithHolidayCalendar
import com.ustadmobile.port.android.util.ext.currentBackStackEntrySavedStateMap
import com.ustadmobile.port.android.view.ext.setEditFragmentTitle


interface SchoolWithHolidayCalendarDetailFragmentEventHandler {

}

class SchoolDetailOverviewFragment: UstadDetailFragment<SchoolWithHolidayCalendar>(),
        SchoolDetailOverviewView, SchoolWithHolidayCalendarDetailFragmentEventHandler,
        Observer<PagedList<Clazz>>{

    private var mBinding: FragmentSchoolOverviewBinding? = null

    private var mPresenter: SchoolDetailOverviewPresenter? = null

    private var clazzRecyclerAdapter: ClazzRecyclerAdapter? = null

    private var clazzRecyclerView : RecyclerView? = null

    protected var currentLiveData: LiveData<PagedList<Clazz>>? = null

    private val clazzObserver = Observer<List<Clazz>?>{
        t -> clazzRecyclerAdapter?.submitList(t)
    }

    override var schoolClazzes: DataSource.Factory<Int, Clazz>? = null
        get() = field
        set(value) {
            currentLiveData?.removeObserver(this)
            currentLiveData = value?.asRepositoryLiveData(ClazzDao)
            currentLiveData?.observe(this, this)
        }

//    override var schoolClazzes: DoorMutableLiveData<List<Clazz>>? = null
//        get() = field
//        set(value) {
//            field?.removeObserver(clazzObserver)
//            field = value
//            value?.observe(this, clazzObserver)
//        }

    class ClazzRecyclerAdapter()
        : ListAdapter<Clazz,
            ClazzRecyclerAdapter.ClazzViewHolder>(SchoolEditFragment.DIFF_CALLBACK_CLAZZ) {

        class ClazzViewHolder(val binding: ItemClazzSimpleBinding)
            : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClazzViewHolder {
            val viewHolder = ClazzViewHolder(ItemClazzSimpleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            return viewHolder
        }

        override fun onBindViewHolder(holder: ClazzViewHolder, position: Int) {
            holder.binding.clazz = getItem(position)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView: View
        mBinding = FragmentSchoolOverviewBinding.inflate(inflater, container,
                false).also {
            rootView = it.root
        }

        clazzRecyclerView = rootView.findViewById(R.id.fragment_school_detail_overview_detail_clazz_rv)
        clazzRecyclerAdapter = ClazzRecyclerAdapter()
        clazzRecyclerView?.adapter = clazzRecyclerAdapter
        clazzRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

        mPresenter = SchoolDetailOverviewPresenter(requireContext(), arguments.toStringMap(),
                this, this, UstadMobileSystemImpl.instance,
                UmAccountManager.getActiveDatabase(requireContext()),
                UmAccountManager.getRepositoryForActiveAccount(requireContext()),
                UmAccountManager.activeAccountLiveData)
        //mPresenter?.onCreate(savedInstanceState.toNullableStringMap())
        //TODO: Set title

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        mPresenter?.onCreate(navController.currentBackStackEntrySavedStateMap())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mPresenter = null
        entity = null
        clazzRecyclerView = null
        clazzRecyclerAdapter = null
    }

    override fun onResume() {
        super.onResume()
        //TODO: Set title
    }

    override var entity: SchoolWithHolidayCalendar? = null
        get() = field
        set(value) {
            field = value
            mBinding?.schoolWithHolidayCalendar = value
        }

    override var editButtonMode: EditButtonMode = EditButtonMode.GONE
        get() = field
        set(value) {
            mBinding?.editButtonMode = value
            field = value
        }

    override fun onChanged(t: PagedList<Clazz>?) {
        clazzRecyclerAdapter?.submitList(t)
    }

}