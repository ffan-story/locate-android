package com.feifan.locate.locating.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.feifan.locate.LocateApplication;

/**
 * Created by xuchunlei on 2016/10/28.
 */

public class LocatingConfig {

    // public
    public static final String KEY_ALGORITHM = "algorithm";
    public static final String KEY_SCAN_PERIOD = "scan_period";
    public static final String KEY_REQUEST_PERIOD = "request_period";

    private static final LocatingConfig INSTANCE = new LocatingConfig();

    private static final String PREFERENCE_LOCATING_SETTINGS = "settings";
    private SharedPreferences mPreferences;

    public static LocatingConfig getInstance() {
        return INSTANCE;
    }

    private LocatingConfig(){
        mPreferences = LocateApplication.CONTEXT.getSharedPreferences(PREFERENCE_LOCATING_SETTINGS, Context.MODE_PRIVATE);
    }

    public void registerChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unRegisterChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public String getScanPeriod() {
        return retrieveStringValue(KEY_SCAN_PERIOD, "3");
    }

    public String getRequestPeriod() {
        return retrieveStringValue(KEY_REQUEST_PERIOD, "3");
    }

    public String getAlgorithm() {
        return retrieveStringValue(KEY_ALGORITHM, "centroid,0");
    }

    public void setScanPeriod(String value) {
        saveStringPreference(KEY_SCAN_PERIOD, value);
    }

    public void setRequestPeriod(String value) {
        saveStringPreference(KEY_REQUEST_PERIOD, value);
    }

    public void setAlgorithm(String value) {
        saveStringPreference(KEY_ALGORITHM, value);
    }

    private void saveStringPreference(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    private String retrieveStringValue(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }
}
