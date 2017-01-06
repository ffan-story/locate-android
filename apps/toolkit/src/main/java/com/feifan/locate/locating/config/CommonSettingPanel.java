package com.feifan.locate.locating.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.widget.popup.Panel;

import java.util.concurrent.Semaphore;

/**
 * 定位控制面板
 * Created by xuchunlei on 16/9/21.
 */

public class CommonSettingPanel extends PanelView implements IPanel {

    @IdRes
    private static final int ID_SCAN_INTERVAL_LABEL = 1;
    @IdRes
    private static final int ID_SCAN_INTERVAL_BAR = 2;
    @IdRes
    private static final int ID_QUERY_INTERVAL_LABEL = 3;
    @IdRes
    private static final int ID_QUERY_INTERVAL_BAR = 4;

    private TextView mScanLabel;
    private TextView mQueryLabel;

    // save
    private LocatingConfig mConfig;
    private OnSharedPreferenceChangeListener mListener;

    public CommonSettingPanel(final Context context) {
        super(context);

        // 初始化
        mConfig = LocatingConfig.getInstance();

        SimpleSeekBarChangeListener listener = new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                switch (seekBar.getId()) {
                    case ID_SCAN_INTERVAL_BAR:
                        mConfig.setScanPeriod(String.valueOf(progress + 1));
                        mScanLabel.setText(context.getString(R.string.locating_panel_scan_period_text, progress + 1));
                        break;
                    case ID_QUERY_INTERVAL_BAR:
                        mConfig.setRequestPeriod(String.valueOf(progress + 1));
                        mQueryLabel.setText(context.getString(R.string.locating_panel_request_period_text, progress + 1));
                        break;
                    default:
                        throw new IllegalStateException("you click an unknown SeekBar with id(" + seekBar.getId() + ")");
                }


            }
        };

        // 数据累计时间
        String scanPeriodValue = mConfig.getScanPeriod();
        int scanPeriodValueI = Integer.valueOf(scanPeriodValue);
        mScanLabel = new TextView(context);
        mScanLabel.setId(ID_SCAN_INTERVAL_LABEL);
        RelativeLayout.LayoutParams scanLParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mScanLabel.setLayoutParams(scanLParams);
        addView(mScanLabel);
        mScanLabel.setText(context.getString(R.string.locating_panel_scan_period_text, scanPeriodValueI));

        SeekBar scanBar = new SeekBar(context);
        scanBar.setId(ID_SCAN_INTERVAL_BAR);
        scanBar.setMax(4);
        RelativeLayout.LayoutParams scanBParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        scanBParams.addRule(RelativeLayout.BELOW, ID_SCAN_INTERVAL_LABEL);
        scanBParams.setMargins(10, 5, 10, 5);
        scanBar.setLayoutParams(scanBParams);
        addView(scanBar);
        scanBar.setProgress(scanPeriodValueI - 1);
        scanBar.setOnSeekBarChangeListener(listener);

        // 数据请求时间
        String queryPeriodValue = mConfig.getRequestPeriod();
        int queryPeriodValueI = Integer.valueOf(queryPeriodValue);
        mQueryLabel = new TextView(context);
        mQueryLabel.setId(ID_QUERY_INTERVAL_LABEL);
        RelativeLayout.LayoutParams queryLParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        queryLParams.addRule(RelativeLayout.BELOW, ID_SCAN_INTERVAL_BAR);
        mQueryLabel.setLayoutParams(queryLParams);
        addView(mQueryLabel);
        mQueryLabel.setText(context.getString(R.string.locating_panel_request_period_text, queryPeriodValueI));

        SeekBar queryBar = new SeekBar(context);
        queryBar.setId(ID_QUERY_INTERVAL_BAR);
        queryBar.setMax(4);
        RelativeLayout.LayoutParams queryBParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        queryBParams.addRule(RelativeLayout.BELOW, ID_QUERY_INTERVAL_LABEL);
        queryBParams.setMargins(10, 5, 10, 5);
        queryBar.setLayoutParams(queryBParams);
        addView(queryBar);
        queryBar.setProgress(queryPeriodValueI - 1);
        queryBar.setOnSeekBarChangeListener(listener);

    }

    public void setConfigChangeListener(OnSharedPreferenceChangeListener listener) {
        if(mListener != null) {
            mConfig.unRegisterChangeListener(mListener);
        }
        mConfig.registerChangeListener(listener);
        mListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mListener != null) {
            mConfig.unRegisterChangeListener(mListener);
        }
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.locating_panel_common_setting_text);
    }

    // save
//    private LocatingConfig mConfig;
//    private static final String PREFERENCE_LOCATING_SETTINGS = "settings";
//    private SharedPreferences mPreferences;
//    private OnSharedPreferenceChangeListener mListener;

    //test
//    private TextView logView;

//    public CommonSettingPanel(Context context) {
//        super(context, null);
//    }
//
//    public CommonSettingPanel(Context context, AttributeSet attrs) {
//        super(context, attrs, 0);
//    }
//
//    public CommonSettingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

//    @Override
//    protected void onInit(Context context, FrameLayout.LayoutParams params) {
//        params.gravity = Gravity.BOTTOM;
//        int spacing = dp2px(context, 5);
//        setPadding(spacing, spacing, spacing, spacing);
//
////        mPreferences = context.getSharedPreferences(PREFERENCE_LOCATING_SETTINGS, Context.MODE_PRIVATE);
//        mConfig = LocatingConfig.getInstance();
//
//        //test
//        logView = (TextView)findViewById(R.id.text_response);
//    }
//
//    public void setConfigChangeListener(OnSharedPreferenceChangeListener listener) {
//        if(mListener != null) {
////            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
//            mConfig.unRegisterChangeListener(mListener);
//        }
////        mPreferences.registerOnSharedPreferenceChangeListener(listener);
//        mConfig.registerChangeListener(listener);
//        mListener = listener;
//    }
//
//    // test
//    public void updateLog(String logText) {
//        if(logView != null) {
//            logView.setText(logText);
//        }else {
//            logView = (TextView)findViewById(R.id.text_response);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if(mListener != null) {
////            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
//            mConfig.unRegisterChangeListener(mListener);
//        }
//    }
//
//    @Override
//    protected void initView(Context context) {
//        // algorithm
//        RadioGroup rgAlgorithm = findView(R.id.radiogroup_algorithm);
//        rgAlgorithm.setOnCheckedChangeListener(this);
//        RadioButton checkedButton = (RadioButton) rgAlgorithm.findViewWithTag(mConfig.getAlgorithm());
//        if(checkedButton != null) {
//            checkedButton.setChecked(true);
//        }
//
//        SimpleSeekBarChangeListener listener = new SimpleSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                super.onProgressChanged(seekBar, progress, fromUser);
//                ViewGroup parent = (ViewGroup)seekBar.getParent();
//                String key = "";
//                switch (parent.getId()) {
//                    case R.id.seek_scan_period:
//                        mConfig.setScanPeriod(String.valueOf(progress + 1));
//                        break;
//                    case R.id.seek_request_period:
//                        mConfig.setRequestPeriod(String.valueOf(progress + 1));
//                        break;
//                    default:
//                        throw new IllegalStateException("you click an unknown SeekBar with id(" + seekBar.getId() + ")");
//                }
//
//
//            }
//        };
//
//        // scan period 数据累计时间
//        ViewGroup scanContainer = findView(R.id.seek_scan_period);
//        SeekBar scanPeriod = (SeekBar) scanContainer.findViewById(R.id.seek);
//        scanPeriod.setOnSeekBarChangeListener(listener);
//        String scanPeriodValue = mConfig.getScanPeriod();
//        scanPeriod.setProgress(Integer.valueOf(scanPeriodValue) - 1);
//
//        // request period 数据请求时间
//        ViewGroup reqContainer = findView(R.id.seek_request_period);
//        SeekBar reqPeriod = (SeekBar) reqContainer.findViewById(R.id.seek);
//        reqPeriod.setOnSeekBarChangeListener(listener);
//        String reqPeriodValue = mConfig.getRequestPeriod();
//        reqPeriod.setProgress(Integer.valueOf(reqPeriodValue) - 1);
//    }
//
//    /**
//     * <p>Called when the checked radio button has changed. When the
//     * selection is cleared, checkedId is -1.</p>
//     *
//     * @param group     the group in which the checked radio button has changed
//     * @param checkedId the unique identifier of the newly checked radio button
//     */
//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        int rbId = group.getCheckedRadioButtonId();
//        RadioButton radioButton = findView(rbId);
//        switch (group.getId()) {
//            case R.id.radiogroup_algorithm:
//                mConfig.setAlgorithm(radioButton.getText().toString());
//                break;
//        }
//    }
//
//    private int dp2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int)(dpValue * scale + 0.5f);
//    }
//
//    @Override
//    public String getTitle() {
//        return getContext().getString(R.string.locating_panel_common_setting_text);
//    }
}
