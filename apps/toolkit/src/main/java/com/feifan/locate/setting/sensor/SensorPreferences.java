package com.feifan.locate.setting.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.LocateApplication;
import com.feifan.locate.R;

/**
 * Created by xuchunlei on 2016/12/7.
 */

public class SensorPreferences {
    private static final SensorPreferences INSTANCE = new SensorPreferences();

    public static final int SENSOR_FLAG_ACCELEROMETER = 0x1;
    public static final int SENSOR_FLAG_LINEAR_ACCELEROMETER = 0x2;
    public static final int SENSOR_FLAG_GRVIMETER = 0x4;
    public static final int SENSOR_FLAG_MAGNETOMETER = 0x8;

    public static final String PREFS_SENSOR_FLAG = "flag";
    public static final String PREFS_SENSOR_FREQUENCY = "frequency";

    private SharedPreferences mPrefs;

    public static SensorPreferences getInstance() {
        return INSTANCE;
    }

    private SensorPreferences() {
        mPrefs = LocateApplication.CONTEXT.getSharedPreferences("sensor", Context.MODE_PRIVATE);
    }

    public void addSensorFlag(int flag) {
        int storeFlag = mPrefs.getInt(PREFS_SENSOR_FLAG, 0);
        storeFlag |= flag;
        mPrefs.edit().putInt(PREFS_SENSOR_FLAG, storeFlag).apply();
        LogUtils.e(Integer.toBinaryString(storeFlag));
    }

    public void removeSensorFlag(int flag) {
        int storeFlag = mPrefs.getInt(PREFS_SENSOR_FLAG, 0);
        storeFlag ^= flag;
        mPrefs.edit().putInt(PREFS_SENSOR_FLAG, storeFlag).apply();
        LogUtils.e(Integer.toBinaryString(storeFlag));
    }

    public int getSensorFlag() {
        return mPrefs.getInt(PREFS_SENSOR_FLAG, 0);
    }

    public void setFrequency(int value) {
        mPrefs.edit().putInt(PREFS_SENSOR_FREQUENCY, value).apply();
    }

    public int getFrequency() {
        return mPrefs.getInt(PREFS_SENSOR_FREQUENCY,
                LocateApplication.CONTEXT.getResources().getInteger(R.integer.default_sensor_scan_frequency));
    }

    public void registerListener(OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unRegisterListener(OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
