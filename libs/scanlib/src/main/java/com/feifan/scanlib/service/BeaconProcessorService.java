package com.feifan.scanlib.service;

import android.app.IntentService;
import android.content.Intent;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.BeaconData;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;

/**
 * 处理Beacon数据服务线程
 *
 * Created by bianying on 16/9/4.
 */
public class BeaconProcessorService extends IntentService {

    public BeaconProcessorService() {
        super("BeaconProcessorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.d("got an intent to process");

        BeaconData data = null;
        if (intent != null && intent.getExtras() != null) {
            data = (BeaconData) intent.getExtras().get("data");
        }

        if (data != null) {
            LogUtils.d("got ranging data");
            if (data.getBeacons() == null) {
                LogUtils.w("beacon data is null");
            }

            BeaconNotifier notifier = ScanManager.getInstance().getNotifier();
            Collection<SampleBeacon> beacons = data.getBeacons();
            if (notifier != null) {
                notifier.onBeaconsReceived(beacons);
            }
            else {
                LogUtils.d("but notifier is null, so we're dropping it.");
            }
        }
    }
}
