package com.feifan.locatelib.offline;

import android.content.Context;

import com.feifan.indoorlocation.IndoorLocationListener;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.LocatorBase;

import java.util.Map;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public class FeifanLocator extends LocatorBase {

    private static final FeifanLocator INSTANCE = new FeifanLocator();

    private FeifanLocator() {

    }

    public static final FeifanLocator getInstance() {
        return INSTANCE;
    }

    @Override
    public void startUpdatingLocation(IndoorLocationListener listener) {

    }

    @Override
    public void stopUpdatingLocation(IndoorLocationListener listener) {

    }

    @Override
    protected void handleScanData(Map<String, Float> data) {

    }

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void destroy() {

    }

    @Override
    protected void updateLocation(IndoorLocationModel model) {

    }

    @Override
    public void setBleScanInterval(long timeInMillis) {

    }

    @Override
    public long getBleScanInterval() {
        return 0;
    }

    @Override
    public void setUpdateInterval(long timeInMillis) {

    }

    @Override
    public long getUpdateInterval() {
        return 0;
    }

    @Override
    public String getToken() {
        return null;
    }
}
