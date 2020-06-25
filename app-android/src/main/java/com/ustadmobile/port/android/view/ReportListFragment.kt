package com.ustadmobile.port.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.toughra.ustadmobile.databinding.ItemReportListBinding
import com.ustadmobile.core.controller.ReportListPresenter
import com.ustadmobile.core.db.UmAppDatabase
import com.ustadmobile.core.impl.UMAndroidUtil
import com.ustadmobile.core.impl.UmAccountManager
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.MessageIdOption
import com.ustadmobile.core.util.ext.toStringMap
import com.ustadmobile.core.view.ReportListView
import com.ustadmobile.lib.db.entities.Report

import com.ustadmobile.core.view.GetResultMode
import com.ustadmobile.port.android.view.ext.setSelectedIfInList
import com.ustadmobile.port.android.view.util.SelectablePagedListAdapter
import com.ustadmobile.core.controller.UstadListPresenter
import com.ustadmobile.port.android.view.ext.navigateToEditEntity
import com.toughra.ustadmobile.R
import com.ustadmobile.core.util.ReportGraphHelper
import com.ustadmobile.lib.db.entities.ReportWithFilters
import com.ustadmobile.port.android.view.util.NewItemRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import okhttp3.Dispatcher

class ReportListFragment() : UstadListViewFragment<Report, Report>(),
        ReportListView, MessageIdSpinner.OnMessageIdOptionSelectedListener, View.OnClickListener {

    private var mPresenter: ReportListPresenter? = null

    override val listPresenter: UstadListPresenter<*, in Report>?
        get() = mPresenter

    class ReportListViewHolder(val itemBinding: ItemReportListBinding) : RecyclerView.ViewHolder(itemBinding.root)

    class ReportListRecyclerAdapter(var presenter: ReportListPresenter?)
        : SelectablePagedListAdapter<Report, ReportListViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
            val itemBinding = ItemReportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            itemBinding.presenter = presenter
            itemBinding.selectablePagedListAdapter = this
            return ReportListViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
            val item = getItem(position) ?: Report()
            holder.itemBinding.report = item
            holder.itemView.tag = holder.itemBinding.report?.reportUid
            holder.itemView.setSelectedIfInList(item, selectedItems, DIFF_CALLBACK)
            (holder.itemBinding.listReportChart.getTag(R.id.tag_graphlookup_key) as? Job)?.cancel()
            val graphJob = GlobalScope.async(Dispatchers.Main) {
                val chartData = presenter?.getGraphData(item)
                holder.itemBinding.listReportChart.setChartData(chartData)
                holder.itemBinding.chart = chartData
            }
            holder.itemBinding.listReportChart.setTag(R.id.tag_graphlookup_key, graphJob)

        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            presenter = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mPresenter = ReportListPresenter(requireContext(), UMAndroidUtil.bundleToMap(arguments),
                this, this, UstadMobileSystemImpl.instance,
                UmAccountManager.getActiveDatabase(requireContext()),
                UmAccountManager.getRepositoryForActiveAccount(requireContext()),
                UmAccountManager.activeAccountLiveData)

        mDataRecyclerViewAdapter = ReportListRecyclerAdapter(mPresenter)
        val createNewText = requireContext().getString(R.string.create_new,
                requireContext().getString(R.string.report))
        mNewItemRecyclerViewAdapter = NewItemRecyclerViewAdapter(this, createNewText)
        return view
    }

    override fun onResume() {
        super.onResume()
        mActivityWithFab?.activityFloatingActionButton?.text =
                requireContext().getString(R.string.report)
    }

    /**
     * OnClick function that will handle when the user clicks to create a new item
     */
    override fun onClick(view: View?) {
        if (view?.id == R.id.item_createnew_layout)
            navigateToEditEntity(null, R.id.report_edit_dest, Report::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter = null
        dbRepo = null
    }

    override val displayTypeRepo: Any?
        get() = dbRepo?.reportDao

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Report> = object
            : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(oldItem: Report,
                                         newItem: Report): Boolean {
                return oldItem.reportUid == newItem.reportUid
            }

            override fun areContentsTheSame(oldItem: Report,
                                            newItem: Report): Boolean {
                return oldItem == newItem
            }
        }
    }
}