package com.feifan.scanlib.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;

import com.feifan.baselib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianying on 16/9/4.
 */
@TargetApi(21)
public class CycledLeScannerForLollipop extends CycledLeScanner {

    private BluetoothLeScanner mScanner;
    private ScanCallback leScanCallback;

    public CycledLeScannerForLollipop(BluetoothAdapter adapter, CycledLeScanCallback callback) {
        super(adapter, callback);
    }

    @Override
    protected void stopScan() {
        try {
            if (getScanner() != null) {
                try {
                    getScanner().stopScan(getNewLeScanCallback());
                }
                catch (NullPointerException npe) {
                    // Necessary because of https://code.google.com/p/android/issues/detail?id=160503
                    LogUtils.e("Cannot stop scan.  Unexpected NPE.", npe);
                }
            }
        }
        catch (IllegalStateException e) {
            LogUtils.w("Cannot stop scan.  Bluetooth may be turned off.");
        }
    }

    @Override
    protected void startScan() {
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        ScanSettings settings;

        LogUtils.d("starting non-filtered scan in SCAN_MODE_LOW_LATENCY");
        settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();

        try {
            if (getScanner() != null) {
                ScanCallback callback = getNewLeScanCallback();
                try {
                    getScanner().startScan(filters, settings, callback);
                }
                catch (NullPointerException npe) {
                    // Necessary because of https://code.google.com/p/android/issues/detail?id=160503
                    LogUtils.w("Cannot start scan.  Unexpected NPE.", npe);
                }
            }
        }
        catch (IllegalStateException e) {
            LogUtils.w("Cannot start scan.  Bluetooth may be turned off.");
        }
    }

    private BluetoothLeScanner getScanner() {
        if (mScanner == null) {
            LogUtils.d("Making new Android L scanner");
            if (adapter != null) {
                mScanner = adapter.getBluetoothLeScanner();
            }
            if (mScanner == null) {
                LogUtils.w("Failed to make new Android L scanner");
            }
        }
        return mScanner;
    }

    private ScanCallback getNewLeScanCallback() {
        if (leScanCallback == null) {
            leScanCallback = new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult scanResult) {
                    callback.onLeScan(scanResult.getDevice(),
                            (byte) scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    LogUtils.d("got batch records");
                    for (ScanResult scanResult : results) {
                        callback.onLeScan(scanResult.getDevice(),
                                (byte) scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                    }
                }

                @Override
                public void onScanFailed(int i) {
                    LogUtils.e("Scan Failed");
                }
            };
        }
        return leScanCallback;
    }
}
