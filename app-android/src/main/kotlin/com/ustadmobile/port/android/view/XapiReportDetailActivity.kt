package com.ustadmobile.port.android.view

import android.os.Bundle
import android.view.MenuItem
import com.toughra.ustadmobile.R
import com.ustadmobile.core.controller.XapiReportDetailPresenter
import com.ustadmobile.core.controller.XapiReportOptions
import com.ustadmobile.core.db.dao.StatementDao
import com.ustadmobile.core.impl.UMAndroidUtil
import com.ustadmobile.core.view.XapiReportDetailView
import java.util.*

class XapiReportDetailActivity : UstadBaseActivity(), XapiReportDetailView {

    private lateinit var chartView: XapiChartView

    private lateinit var verticalTextView: VerticalTextView

    private lateinit var presenter: XapiReportDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xapi_report_preview)

        setUMToolbar(R.id.preview_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        umToolbar.title = getString(R.string.activity_preview_xapi)

        chartView = findViewById(R.id.preview_chart_view)
        verticalTextView = findViewById(R.id.preview_ylabel)

        presenter = XapiReportDetailPresenter(viewContext,
                Objects.requireNonNull(UMAndroidUtil.bundleToMap(intent.extras)),
                this)
        presenter.onCreate(UMAndroidUtil.bundleToMap(savedInstanceState))
    }

    override fun setChartData(chartData: List<StatementDao.ReportData>, options: XapiReportOptions,
                              xAxisLabels: Map<String, String>, subgroupLabels: Map<String, String>) =
            chartView.setChartData(chartData, options, xAxisLabels, subgroupLabels)

    override fun setChartYAxisLabel(yAxisLabel: String) {
        verticalTextView.text = yAxisLabel
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                runOnUiThread { finish() }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}