package com.feifan.locate.locating.config;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locatelib.utils.DebugUtils;

/**
 * Created by xuchunlei on 2017/1/3.
 */

public class AlgorithmSettingPanel extends PanelView implements IPanel, OnSeekBarChangeListener {

    @IdRes
    private static final int ID_THRESHOLD_LABEL = 1;
    @IdRes
    private static final int ID_THRESHOLD_BAR = 2;
    @IdRes
    private static final int ID_WEIGHT_LABEL = 3;
    @IdRes
    private static final int ID_WEIGHT_BAR = 4;
    @IdRes
    private static final int ID_SCALE_LABEL = 5;
    @IdRes
    private static final int ID_SCALE_BAR = 6;

    private TextView mWeightLabel;
    private TextView mScaleLabel;

    public AlgorithmSettingPanel(Context context) {
        super(context);

        // 阈值
        TextView thresholdLabel = new TextView(context);
        thresholdLabel.setId(ID_THRESHOLD_LABEL);
        thresholdLabel.setText(R.string.locating_panel_algorithm_valid_threshold_text);
        LayoutParams thresholdLParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        thresholdLabel.setLayoutParams(thresholdLParams);
        addView(thresholdLabel);

        RangeBar thresholdBar = (RangeBar) inflate(context, R.layout.panel_part_algorithm_threshold, null);
        thresholdBar.setId(ID_THRESHOLD_BAR);
        RelativeLayout.LayoutParams thresholdBParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, SizeUtils.dp2px(context, 72));
        thresholdBParams.setMargins(10, 0, 10, 0);
        thresholdBParams.addRule(RelativeLayout.BELOW, ID_THRESHOLD_LABEL);
        thresholdBParams.addRule(RelativeLayout.ALIGN_BASELINE, ID_THRESHOLD_LABEL);
        thresholdBar.setLayoutParams(thresholdBParams);
        addView(thresholdBar);
        thresholdBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                DebugUtils.Algorithm.MINR_THRESHOLD_MIN = Double.valueOf(leftPinValue);
                DebugUtils.Algorithm.MINR_THRESHOLD_MAX = Double.valueOf(rightPinValue);
            }
        });

        // 权重
        mWeightLabel = new TextView(context);
        mWeightLabel.setId(ID_WEIGHT_LABEL);
        mWeightLabel.setText(context.getString(R.string.locating_panel_algorithm_weight_text, 0.6, 0.4));
        RelativeLayout.LayoutParams weightLParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        weightLParams.addRule(RelativeLayout.BELOW, ID_THRESHOLD_BAR);
        mWeightLabel.setLayoutParams(weightLParams);
        addView(mWeightLabel);

        SeekBar weightBar = new SeekBar(context);
        weightBar.setId(ID_WEIGHT_BAR);
        RelativeLayout.LayoutParams weightBParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        weightBParams.addRule(RelativeLayout.BELOW, ID_WEIGHT_LABEL);
        weightBParams.setMargins(10, 5, 10, 5);
        weightBar.setLayoutParams(weightBParams);
        addView(weightBar);
        weightBar.setOnSeekBarChangeListener(this);
        weightBar.setProgress(60);

        // 缩放因子
        mScaleLabel = new TextView(context);
        mScaleLabel.setId(ID_SCALE_LABEL);
        mScaleLabel.setText(context.getString(R.string.locating_panel_algorithm_scale_factor_text, 0.1));
        RelativeLayout.LayoutParams scaleLParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        scaleLParams.addRule(RelativeLayout.BELOW, ID_WEIGHT_BAR);
        mScaleLabel.setLayoutParams(scaleLParams);
        addView(mScaleLabel);

        SeekBar scaleBar = new SeekBar(context);
        scaleBar.setId(ID_SCALE_BAR);
        RelativeLayout.LayoutParams scaleBParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        scaleBParams.addRule(RelativeLayout.BELOW, ID_SCALE_LABEL);
        scaleBParams.setMargins(10, 5, 10, 5);
        scaleBar.setLayoutParams(scaleBParams);
        addView(scaleBar);
        scaleBar.setOnSeekBarChangeListener(this);
        scaleBar.setProgress(10);


        // https://github.com/oli107/material-range-bar
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.locating_panel_algorithm_setting_text);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case ID_WEIGHT_BAR:
                DebugUtils.Algorithm.MINR_WEIGHT_HISTORY = progress / 100f;
                DebugUtils.Algorithm.MINR_WEIGHT_CURRENT = 1 - DebugUtils.Algorithm.MINR_WEIGHT_HISTORY;
                mWeightLabel.setText(getContext().getString(R.string.locating_panel_algorithm_weight_text,
                        DebugUtils.Algorithm.MINR_WEIGHT_HISTORY, DebugUtils.Algorithm.MINR_WEIGHT_CURRENT));
                break;
            case ID_SCALE_BAR:
                DebugUtils.Algorithm.MINR_SCALE_FACTOR = progress / 100f;
                mScaleLabel.setText(getContext().getString(R.string.locating_panel_algorithm_scale_factor_text,
                        DebugUtils.Algorithm.MINR_SCALE_FACTOR));
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
