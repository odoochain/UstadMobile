package com.ustadmobile.port.android.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.toughra.ustadmobile.R;
import com.ustadmobile.core.controller.ReportSalesLogDetailPresenter;
import com.ustadmobile.lib.db.entities.ReportSalesLog;
import com.ustadmobile.core.util.UMCalendarUtil;
import com.ustadmobile.core.view.ReportSalesLogDetailView;
import com.ustadmobile.port.android.util.UMAndroidUtil;

import java.util.List;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class ReportSalesLogDetailActivity extends UstadBaseActivity
        implements ReportSalesLogDetailView {

    private Toolbar toolbar;
    private ReportSalesLogDetailPresenter mPresenter;
    private FloatingTextButton fab;
    Menu menu;
    private boolean fabVisibility=true;

    private TextView xLabel, yLabel;
    private LinearLayout chartLL;


    /**
     * Creates the options on the toolbar - specifically the Done tick menu item
     * @param thisMenu  The menu options
     * @return  true. always.
     */
    public boolean onCreateOptionsMenu(Menu thisMenu) {
        menu = thisMenu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report_detail, menu);

        menu.findItem(R.id.menu_report_detail_download).setVisible(true);
        menu.findItem(R.id.menu_report_detail_edit).setVisible(true);

        return true;
    }

    /**
     * This method catches menu buttons/options pressed in the toolbar. Here it is making sure
     * the activity goes back when the back button is pressed.
     *
     * @param item The item selected
     * @return true if accounted for
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (i == R.id.menu_report_detail_download) {
            mPresenter.handleClickDownloadReport();
            return true;
        } else if (i == R.id.menu_report_detail_edit) {
            mPresenter.handleClickEditReport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting layout:
        setContentView(R.layout.activity_report_sales_performance_detail);

        //Toolbar:
        toolbar = findViewById(R.id.activity_report_sales_performance_detail_toolbar);
        toolbar.setTitle(getText(R.string.sales_performance_report));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        xLabel = findViewById(R.id.activity_report_sales_perforance_detail_x_label);
        yLabel = findViewById(R.id.activity_report_sales_perforance_detail_y_label);
        chartLL = findViewById(R.id.activity_report_sales_perforance_detail_report_ll);

        xLabel.setVisibility(View.INVISIBLE);
        yLabel.setVisibility(View.INVISIBLE);

        //Call the Presenter
        mPresenter = new ReportSalesLogDetailPresenter(this,
                UMAndroidUtil.bundleToHashtable(getIntent().getExtras()), this);
        mPresenter.onCreate(UMAndroidUtil.bundleToHashtable(savedInstanceState));

        //FAB and its listener
        fab = findViewById(R.id.activity_report_sales_performance_detail_fab);
        fab.setOnClickListener(v -> mPresenter.handleClickAddToDashboard());
        fab.setVisibility(fabVisibility?View.VISIBLE:View.INVISIBLE);
    }


    @Override
    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void showDownloadButton(boolean show) {
        if(menu!=null){
            menu.getItem(R.id.menu_report_detail_download).setVisible(show);
        }
    }

    @Override
    public void showAddToDashboardButton(boolean show) {
        fabVisibility = show;
        runOnUiThread(() -> {
            if(fab!= null){
                fab.setVisibility(show?View.VISIBLE:View.INVISIBLE);
            }
        });
    }

    @Override
    public void setReportData(List<Object> dataSet) {

        chartLL.removeAllViews();
        LinearLayout logs = createSalesLog(dataSet);
        chartLL.addView(logs);
    }

    @Override
    public void setReportType(int reportType) {
        runOnUiThread(() -> toolbar.setTitle(R.string.sales_performance_report));
    }


    private LinearLayout createSalesLog(List<Object> dataSet){
        LinearLayout topLL = new LinearLayout(this);
        topLL.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams wrapParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        topLL.setLayoutParams(params);

        for(Object data:dataSet){
            ReportSalesLog entry = (ReportSalesLog)data;

            TextView t1 = new TextView(this);
            t1.setText(entry.getLeName() + ", at location " + entry.getLocationName());
            t1.setPadding(0,8,0,0);

            topLL.addView(t1);
            TextView v1 = new TextView(this);
            v1.setTextSize(18);
            v1.setTextColor(Color.parseColor("#F57C00"));
            v1.setText(String.valueOf(entry.getSaleValue()));

            LinearLayout tLL = new LinearLayout(this);
            tLL.setOrientation(LinearLayout.HORIZONTAL);
            tLL.setLayoutParams(wrapParams);
            TextView l1 = new TextView(this);
            l1.setText(entry.getProductNames());

            v1.setPadding(0,0, 32, 0);
            tLL.addView(v1);
            l1.setPadding(32,0,0,0);
            tLL.addView(l1);

            topLL.addView(tLL);

            v1.setPadding(0,0,0,8);
            TextView d1 = new TextView(this);
            d1.setText(UMCalendarUtil.getPrettyDateSuperSimpleFromLong(entry.getSaleDate(), null));
            d1.setPadding(0,0,0,8);
            topLL.addView(d1);
            topLL.addView(getHorizontalLine());
        }

        return topLL;

    }


    private LinearLayout createSalesLog(){
        LinearLayout topLL = new LinearLayout(this);
        topLL.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams wrapParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        topLL.setLayoutParams(params);

        String[] names = new String[]{"Anoosha", "Leena", "Freshta"};
        String[] products = new String[]{"1x pink hat, 1x maxi dress","10x pink hat","100x pink hat"};
        String[] date = new String[]{"01/June/2019","26/May/2019","20/May/2019"};
        String[] values = new String[]{"4,000 Afs","3,500 Afs","40,000 Afs"};
        String[] location = new String[]{"Kote-Senghi Kabul","Khakrez, Kandahat","Froshga, Kabul"};
        for(int i=0; i<3;i++){

            TextView t1 = new TextView(this);
            t1.setText(names[i] + ", at location " + location[i]);
            t1.setPadding(0,8,0,0);

            topLL.addView(t1);
            TextView v1 = new TextView(this);
            v1.setTextSize(18);
            v1.setTextColor(Color.parseColor("#F57C00"));
            v1.setText(values[i]);

            LinearLayout tLL = new LinearLayout(this);
            tLL.setOrientation(LinearLayout.HORIZONTAL);
            tLL.setLayoutParams(wrapParams);
            TextView l1 = new TextView(this);
            l1.setText(products[i]);

            v1.setPadding(0,0, 32, 0);
            tLL.addView(v1);
            l1.setPadding(32,0,0,0);
            tLL.addView(l1);

            topLL.addView(tLL);

            v1.setPadding(0,0,0,8);
            TextView d1 = new TextView(this);
            d1.setText(date[i]);
            d1.setPadding(0,0,0,8);
            topLL.addView(d1);
            topLL.addView(getHorizontalLine());
        }

        return topLL;

    }

    /**
     * Creates a new Horizontal line for a table's row.
     * @return  The horizontal line view.
     */
    public View getHorizontalLine(){
        //Horizontal line
        ViewGroup.LayoutParams hlineParams = new ViewGroup.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, 1);
        View hl = new View(this);
        hl.setBackgroundColor(Color.GRAY);
        hl.setLayoutParams(hlineParams);
        return hl;
    }

}
