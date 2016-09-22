package com.feifan.locate.locating.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;

import java.util.concurrent.Semaphore;

/**
 * 定位控制面板
 * Created by xuchunlei on 16/9/21.
 */

public class LocatingPanel extends LinearLayout implements OnCheckedChangeListener {

    // public
    public static final String KEY_ALGORITHM = "algorithm";
    public static final String KEY_SCAN_PERIOD = "scan_period";
    public static final String KEY_REQUEST_PERIOD = "request_period";

    // save
    private static final String PREFERENCE_LOCATING_SETTINGS = "settings";
    private SharedPreferences mPreferences;
    private OnSharedPreferenceChangeListener mListener;

    private FrameLayout.LayoutParams mParams;

    public LocatingPanel(Context context) {
        this(context, null);
    }

    public LocatingPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocatingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.argb(0x55, 0x55, 0x55, 0x55));
        setOrientation(VERTICAL);
        mParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.gravity = Gravity.BOTTOM;
        int spacing = dp2px(context, 5);
        setPadding(spacing, spacing, spacing, spacing);

        mPreferences = context.getSharedPreferences(PREFERENCE_LOCATING_SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    public void setConfigChangeListener(OnSharedPreferenceChangeListener listener) {
        if(mListener != null) {
            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
        }
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
        mListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mListener != null) {
            mPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
        }
    }

    private void initView() {
        // algorithm
        RadioGroup rgAlgorithm = findView(R.id.radiogroup_algorithm);
        rgAlgorithm.setOnCheckedChangeListener(this);

        SimpleSeekBarChangeListener listener = new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                ViewGroup parent = (ViewGroup)seekBar.getParent();
                String key = "";
                switch (parent.getId()) {
                    case R.id.seek_scan_period:
                        key = KEY_SCAN_PERIOD;
                        break;
                    case R.id.seek_request_period:
                        key = KEY_REQUEST_PERIOD;
                        break;
                    default:
                        throw new IllegalStateException("you click an unknown SeekBar with id(" + seekBar.getId() + ")");
                }

                LocatingPanel.this.saveStringPreference(key, String.valueOf(progress + 1));
            }
        };

        // scan period 数据累计时间
        ViewGroup scanContainer = findView(R.id.seek_scan_period);
        SeekBar scanPeriod = (SeekBar) scanContainer.findViewById(R.id.seek);
        scanPeriod.setOnSeekBarChangeListener(listener);
        String scanPeriodValue = retrieveStringValue(KEY_SCAN_PERIOD, "3");
        scanPeriod.setProgress(Integer.valueOf(scanPeriodValue) - 1);

        // request period 数据请求时间
        ViewGroup reqContainer = findView(R.id.seek_request_period);
        SeekBar reqPeriod = (SeekBar) reqContainer.findViewById(R.id.seek);
        reqPeriod.setOnSeekBarChangeListener(listener);
        String reqPeriodValue = retrieveStringValue(KEY_REQUEST_PERIOD, "3");
        reqPeriod.setProgress(Integer.valueOf(reqPeriodValue) - 1);
    }

    public void show(Window container) {
        container.addContentView(this, mParams);
    }

    public void hide() {
        if(isAttachedToWindow()) {
            ((ViewGroup)getParent()).removeView(this);
        }
    }

    public boolean isShown() {
        return isAttachedToWindow();
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
                saveStringPreference(KEY_ALGORITHM, radioButton.getText().toString());
                break;
        }
    }

    private void saveStringPreference(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    private String retrieveStringValue(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    /**
     * 查找到指定ID的视图
     * @param id
     * @param <T>
     * @return
     */
    private  <T> T findView(@IdRes int id) {
        return (T)findViewById(id);
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
