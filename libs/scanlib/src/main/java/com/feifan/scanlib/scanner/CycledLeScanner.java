package com.feifan.scanlib.scanner;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.SystemClock;

import com.feifan.baselib.utils.LogUtils;

/**
 * 循环执行的蓝牙扫描器
 *
 * Created by bianying on 16/9/4.
 */

public abstract class CycledLeScanner {

    // 循环
    private int period;
    private long mScanCycleStopTime = 0l;     // 单次循环结束时间
    private boolean mScanningEnabled = false;

    // 蓝牙
    protected BluetoothAdapter adapter = null;

    protected CycledLeScanCallback callback;

    protected final Handler mHandler = new Handler();

    protected CycledLeScanner(BluetoothAdapter adapter, CycledLeScanCallback callback) {
        this.adapter = adapter;
        this.callback = callback;
    }

    /**
     * 创建扫描器
     * @param callback
     * @return
     */
    public static CycledLeScanner createScanner(BluetoothAdapter adapter, CycledLeScanCallback callback) {
        boolean useAndroidLScanner;
        if (android.os.Build.VERSION.SDK_INT < 18) {
            LogUtils.w("Not supported prior to API 18.");
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT < 21) {
            LogUtils.i("This is not Android 5.0.  We are using old scanning APIs");
            useAndroidLScanner = false;
        } else {
            LogUtils.i("This Android 5.0.  We are using new scanning APIs");
            useAndroidLScanner = true;
        }

        if (useAndroidLScanner) {
            return new CycledLeScannerForLollipop(adapter, callback);
        } else {
            return new CycledLeScannerForJellyBeanMr2(adapter, callback);
        }
    }

    public void startAtInterval(int period) {
        this.period = period;

        mScanningEnabled = true;
        scanDevice();
    }

    public void stop() {
        mScanningEnabled = false;
        if (adapter != null) {
            stopScan();
        }
    }

    protected void scheduleScanCycleStop() {
        // Stops scanning after a pre-defined scan period.
        long millisecondsUntilStop = mScanCycleStopTime - SystemClock.elapsedRealtime();
        if (millisecondsUntilStop > 0) {
            LogUtils.d(String.format("Waiting to stop scan cycle for another %s milliseconds", millisecondsUntilStop));

            // 每隔小于1秒的时间检查一次
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scheduleScanCycleStop();
                }
            }, millisecondsUntilStop > 1000 ? 1000 : millisecondsUntilStop);
        } else {
            finishScanCycle();
        }
    }

    private void scanDevice() {
        LogUtils.d("startAtInterval called");
        if (adapter == null) {
            LogUtils.e("No Bluetooth adapter.  beaconService cannot scan.");
        }

        LogUtils.d("starting a new scan cycle");

        try {
            if (adapter != null) {
                if (adapter.isEnabled()) {
                    try {
                        startScan();
                    } catch (Exception e) {
                        LogUtils.e("Internal Android exception scanning for beacons", e);
                    }
                } else {
                    LogUtils.d("Bluetooth is disabled.  Cannot scan for beacons.");
                }
            }
        } catch (Exception e) {
            LogUtils.e("Exception starting Bluetooth scan.  Perhaps Bluetooth is disabled or unavailable", e);
        }

        mScanCycleStopTime = (SystemClock.elapsedRealtime() + period);
        scheduleScanCycleStop();

        LogUtils.d("scan started");
    }

    private void finishScanCycle() {
        LogUtils.d("Done with scan cycle");
        callback.onCycleEnd();

        if (adapter != null) {
            if (adapter.isEnabled()) {
                try {
                    LogUtils.d("stopping bluetooth le scan");
                    stopScan();
                } catch (Exception e) {
                    LogUtils.w("Internal Android exception scanning for beacons", e);
                }
            } else {
                LogUtils.d("Bluetooth is disabled.  Cannot scan for beacons.");
            }
        }

        // 启动下一次扫描
        if(mScanningEnabled) {
            scanDevice();
        }
    }

    protected abstract void stopScan();

    protected abstract void startScan();
}
