package com.mm.stock.charts;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.libs.base.http.BpCallback;
import com.libs.ui.fragments.CommonFragment;
import com.mm.stock.R;
import com.mm.stock.charts.model.LineBBDChartModel;
import com.mm.stock.charts.request.ChartDataInterface;
import com.mm.stock.http.ApiCreator;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import retrofit2.Call;

/**
 * Created by mengmeng on 16/6/21.
 */
public class PreviewLineChartFragment extends CommonFragment implements View.OnClickListener {
    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;
    private Button mBtn;
    private EditText mEditText;
    /**
     * Deep copy of data.
     */
    private LineChartData previewData;

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.biz_charts_preview_line_chart, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view){
        chart = (LineChartView) view.findViewById(R.id.chart);
        previewChart = (PreviewLineChartView) view.findViewById(R.id.chart_preview);
        mBtn = (Button) view.findViewById(R.id.btn);
        mBtn.setOnClickListener(this);
        mEditText = (EditText) view.findViewById(R.id.stock_edit);
        mEditText.requestFocus();
    }


    private void generateDefaultData(String[] strs) {
        if(strs == null || strs.length == 0){
            return;
        }
        int numValues = strs.length;

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            if(!TextUtils.isEmpty(strs[i])) {
                values.add(new PointValue(i, Float.valueOf(strs[i])));
            }
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN);
        line.setHasPoints(false);// too many values so don't draw points.

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        data = new LineChartData(lines);
        data.setAxisXBottom(new Axis());
        data.setAxisYLeft(new Axis().setHasLines(true));
        chart.setLineChartData(data);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);
        // prepare preview data, is better to use separate deep copy for preview chart.
        // Set color to grey to make preview area more visible.
        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportListener());
        previewX(false);
    }

    private void initData(String code){
        ChartDataInterface request = ApiCreator.getInstance().createApi(ChartDataInterface.class);
        Call<LineBBDChartModel> call = request.getStockData(code);
        call.enqueue(new BpCallback<LineBBDChartModel>() {
            @Override
            public void onResponse(LineBBDChartModel model) {
                if (model != null){
                    Log.e("result",model.getBbdrate().toString());
                    String[] strs = model.getBbdrate();
                    if(strs != null && strs.length >0){
                        generateDefaultData(strs);
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("onFailure",message);
            }
        });
    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            previewChart.setCurrentViewport(tempViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String code = mEditText.getText().toString();
        if(!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code) && code.trim().length() == 6){
            initData(code);
        }
    }


    /**
     * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
     * viewport of upper chart.
     */
    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chart.setCurrentViewport(newViewport);
        }

    }
}
