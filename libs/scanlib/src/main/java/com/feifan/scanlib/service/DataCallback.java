package com.feifan.scanlib.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.beacon.BeaconData;

/**
 * Created by bianying on 16/9/4.
 */
public class DataCallback {

    private transient Intent intent;
    private String packageName;

    public DataCallback(String packageName) {
        LogUtils.e(packageName);
        this.packageName = packageName;
        initializeIntent();
    }

    public void call(Context context, Parcelable data) {
        LogUtils.i("call data processor to handle " + ((BeaconData)data).getBeacons().size() + " scan data");

        if (intent != null) {
            LogUtils.d("attempting callback via intent: %s" + intent.getComponent());
            intent.putExtra("data", data);
            context.startService(intent);
        }
    }

    private void initializeIntent() {
        if (packageName != null) {
            intent = new Intent();
            intent.setComponent(new ComponentName(packageName, "com.feifan.scanlib.service.BeaconProcessorService"));
        }
    }
}
