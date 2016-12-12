package com.feifan.locate.setting.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.setting.SettingSingleFragment;
import com.feifan.locate.widget.settingwork.CheckItemView;
import com.feifan.locate.widget.settingwork.TextItemView;
import com.feifan.locate.widget.settingwork.ToggleItemView;
import com.feifan.locate.widget.ui.BaseFragment;
import com.feifan.sensorlib.SensorController;

/**
 * Created by xuchunlei on 16/10/12.
 */

public class SensorScanFragment extends BaseFragment implements OnCheckedChangeListener,
        View.OnClickListener, OnSharedPreferenceChangeListener {

    // RequestCode
    private static final int REQUEST_CODE_SCAN_FREQUENCY = 1;

    private SensorController mController = SensorController.getInstance();
    private SensorPreferences mPreferences = SensorPreferences.getInstance();

    // view
    private TextItemView mTextFrequency;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mController.bind(context);
        mPreferences.registerListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_sensor_scan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ToggleItemView toggleScan = findView(R.id.sensor_scan_onoff);
        toggleScan.setOnCheckedChangeListener(this);
        final CheckItemView checkAccelerometer = findView(R.id.sensor_scan_accelerometer);
        checkAccelerometer.setOnCheckedChangeListener(this);
        final CheckItemView checkLinearAccelerometer = findView(R.id.sensor_scan_linear_accelerometer);
        checkLinearAccelerometer.setOnCheckedChangeListener(this);
        final CheckItemView checkGravimeter = findView(R.id.sensor_scan_gravimeter);
        checkGravimeter.setOnCheckedChangeListener(this);
        final CheckItemView checkMagnetometer = findView(R.id.sensor_scan_magnetometer);
        checkMagnetometer.setOnCheckedChangeListener(this);
        mTextFrequency = findView(R.id.sensor_scan_frequency);
        mTextFrequency.setOnClickListener(this);
        mTextFrequency.setSubtitle(String.valueOf(mPreferences.getFrequency()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mController.unBind(getContext());
        mPreferences.unRegisterListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCAN_FREQUENCY:
                if(resultCode == Activity.RESULT_OK) {
                    mPreferences.setFrequency(data.getExtras().getInt(SettingSingleFragment.EXTRA_KEY_RESULT));
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = ((View)compoundButton.getParent()).getId();
        if(id == R.id.sensor_scan_onoff) {
            int sensorFlag = mPreferences.getSensorFlag();
            if(b) {
                int frequency = mPreferences.getFrequency();
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_ACCELEROMETER)) {
                    mController.enableSensor(Sensor.TYPE_ACCELEROMETER, frequency);
                }
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_LINEAR_ACCELEROMETER)) {
                    mController.enableSensor(Sensor.TYPE_LINEAR_ACCELERATION, frequency);
                }
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_MAGNETOMETER)) {
                    mController.enableSensor(Sensor.TYPE_MAGNETIC_FIELD, frequency);
                }
            }else {
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_ACCELEROMETER)) {
                    mController.disableSensor(Sensor.TYPE_ACCELEROMETER);
                }
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_LINEAR_ACCELEROMETER)) {
                    mController.disableSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                }
                if(isFlag(sensorFlag, SensorPreferences.SENSOR_FLAG_MAGNETOMETER)) {
                    mController.disableSensor(Sensor.TYPE_MAGNETIC_FIELD);
                }
            }
        } else {
            if(b) {
                mPreferences.addSensorFlag(getFlagById(id));
            } else {
                mPreferences.removeSensorFlag(getFlagById(id));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(SensorPreferences.PREFS_SENSOR_FREQUENCY.equalsIgnoreCase(key)) {
            mTextFrequency.setSubtitle(String.valueOf(mPreferences.getFrequency()));
            mController.setFrequency(mPreferences.getFrequency());
        }
    }

    private int getFlagById(int id) {
        switch (id) {
            case R.id.sensor_scan_accelerometer:
                return 0x1;
            case R.id.sensor_scan_linear_accelerometer:
                return 0x02;
            case R.id.sensor_scan_gravimeter:
                return 0x4;
            case R.id.sensor_scan_magnetometer:
                return 0x8;
            default:
                return 0;
        }
    }

    private boolean isFlag(int flag, int mask) {
        return (flag & mask) != 0;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof TextItemView) {
            TextItemView v = (TextItemView)view;
            String title = v.getTitle();
            String subTitle = v.getSubTitle();
            startConfig(title, subTitle, REQUEST_CODE_SCAN_FREQUENCY);
        }
    }

    /**
     * 启动配置界面
     * @param title
     * @param curValue
     * @param ReqCode
     */
    private void startConfig(String title, String curValue, int ReqCode) {
        Intent intent = new Intent(getContext(), ToolbarActivity.class);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SettingSingleFragment.class.getName());
        Bundle args = new Bundle();
        args.putString(SettingSingleFragment.EXTRA_KEY_TITLE, title);
        args.putString(SettingSingleFragment.EXTRA_KEY_VALUE, curValue);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
        startActivityForResult(intent, ReqCode);
    }
}
