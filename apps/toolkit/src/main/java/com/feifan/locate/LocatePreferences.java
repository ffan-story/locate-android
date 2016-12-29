package com.feifan.locate;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xuchunlei on 2016/10/24.
 */

public final class LocatePreferences {

    private static final LocatePreferences INSTANCE = new LocatePreferences();

    private static final String LOCATE_PREFERENCES = "locate";
    private static final String INITIALIZE_DATA_FLAG = "initial_flag";
    private static final String KEY_LOCATE_SERVER_ADDR = "locate_server_addr";
    private static final String KEY_LOCATE_SERVER_PORT = "locate_server_port";
    private SharedPreferences mPrefs;

    private LocatePreferences() {
        mPrefs = LocateApplication.CONTEXT.getSharedPreferences(LOCATE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static LocatePreferences getInstance() {
        return INSTANCE;
    }

    public void setLocateAddr(String addr) {
        mPrefs.edit().putString(KEY_LOCATE_SERVER_ADDR, addr).apply();
    }

    public void setLocatePort(int port) {
        mPrefs.edit().putInt(KEY_LOCATE_SERVER_PORT, port).apply();
    }

    public String getLocateAddr() {
        return mPrefs.getString(KEY_LOCATE_SERVER_ADDR, "192.168.1.100");
    }

    public int getLocatePort() {
        return mPrefs.getInt(KEY_LOCATE_SERVER_PORT, 80);
    }

    public void setInitialFlag(boolean flag) {
        mPrefs.edit().putBoolean(INITIALIZE_DATA_FLAG, flag).apply();
    }

    public boolean getInitialFlag() {
        return mPrefs.getBoolean(INITIALIZE_DATA_FLAG, false);
    }

    public void clear() {
        mPrefs.edit().clear().apply();
    }
}
