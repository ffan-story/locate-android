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

public class LocatingPanel extends Panel implements OnCheckedChangeListener {

    // save
    private LocatingConfig mConfig;
//    private static final String PREFERENCE_LOCATING_SETTINGS = "settings";
//    private SharedPreferences mPreferences;
    private OnSharedPreferenceChangeListener mListener;

    //test
    private TextView logView;

    public LocatingPanel(Context context) {
        super(context, null);
    }

    public LocatingPanel(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public LocatingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit(Context context, FrameLayout.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;
        int spacing = dp2px(context, 5);
        setPadding(spacing, spacing, spacing, spacing);

//        mPreferences = context.getSharedPreferences(PREFERENCE_LOCATING_SETTINGS, Context.MODE_PRIVATE);
        mConfig = LocatingConfig.getInstance();

        //test
        logView = (TextView)findViewById(R.id.text_response);
    }

    public void setConfigChangeListener(OnSharedPreferenceChangeListener listener) {
        if(mListener != null) {
//            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
            mConfig.unRegisterChangeListener(mListener);
        }
//        mPreferences.registerOnSharedPreferenceChangeListener(listener);
        mConfig.registerChangeListener(listener);
        mListener = listener;
    }

    // test
    public void updateLog(String logText) {
        if(logView != null) {
            logView.setText(logText);
        }else {
            logView = (TextView)findViewById(R.id.text_response);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mListener != null) {
//            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
            mConfig.unRegisterChangeListener(mListener);
        }
    }

    @Override
    protected void initView(Context context) {
        // algorithm
        RadioGroup rgAlgorithm = findView(R.id.radiogroup_algorithm);
        rgAlgorithm.setOnCheckedChangeListener(this);
        RadioButton checkedButton = (RadioButton) rgAlgorithm.findViewWithTag(mConfig.getAlgorithm());
        if(checkedButton != null) {
            checkedButton.setChecked(true);
        }

        SimpleSeekBarChangeListener listener = new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                ViewGroup parent = (ViewGroup)seekBar.getParent();
                String key = "";
                switch (parent.getId()) {
                    case R.id.seek_scan_period:
                        mConfig.setScanPeriod(String.valueOf(progress + 1));
                        break;
                    case R.id.seek_request_period:
                        mConfig.setRequestPeriod(String.valueOf(progress + 1));
                        break;
                    default:
                        throw new IllegalStateException("you click an unknown SeekBar with id(" + seekBar.getId() + ")");
                }


            }
        };

        // scan period 数据累计时间
        ViewGroup scanContainer = findView(R.id.seek_scan_period);
        SeekBar scanPeriod = (SeekBar) scanContainer.findViewById(R.id.seek);
        scanPeriod.setOnSeekBarChangeListener(listener);
        String scanPeriodValue = mConfig.getScanPeriod();
        scanPeriod.setProgress(Integer.valueOf(scanPeriodValue) - 1);

        // request period 数据请求时间
        ViewGroup reqContainer = findView(R.id.seek_request_period);
        SeekBar reqPeriod = (SeekBar) reqContainer.findViewById(R.id.seek);
        reqPeriod.setOnSeekBarChangeListener(listener);
        String reqPeriodValue = mConfig.getRequestPeriod();
        reqPeriod.setProgress(Integer.valueOf(reqPeriodValue) - 1);
    }

    /**
     * <p>Called when the checked radio button has changed. When the
     * selection is cleared, checkedId is -1.</p>
     *
     * @param group     the group in which the checked radio button has changed
     * @param checkedId the unique identifier of the newly checked radio button
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int rbId = group.getCheckedRadioButtonId();
        RadioButton radioButton = findView(rbId);
        switch (group.getId()) {
            case R.id.radiogroup_algorithm:
                mConfig.setAlgorithm(radioButton.getText().toString());
                break;
        }
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
