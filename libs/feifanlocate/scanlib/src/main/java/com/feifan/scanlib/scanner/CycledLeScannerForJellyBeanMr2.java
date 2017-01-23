package com.feifan.scanlib.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;

import com.feifan.baselib.utils.LogUtils;

/**
 * Created by bianying on 16/9/4.
 */
@TargetApi(18)
public class CycledLeScannerForJellyBeanMr2 extends CycledLeScanner{

    private LeScanCallback leScanCallback;

    public CycledLeScannerForJellyBeanMr2(BluetoothAdapter adapter, CycledLeScanCallback callback) {
        super(adapter, callback);
    }

    @Override
    protected void stopScan() {
        try {
            if (adapter != null) {
                adapter.stopLeScan(getLeScanCallback());
            }
        } catch (Exception e) {
            LogUtils.e("Internal Android exception scanning for beacons", e);
        }
    }

    @Override
    protected void startScan() {
        adapter.startLeScan(getLeScanCallback());
    }

    private BluetoothAdapter.LeScanCallback getLeScanCallback() {
        if (leScanCallback == null) {
            leScanCallback =
                    new BluetoothAdapter.LeScanCallback() {

                        @Override
                        public void onLeScan(final BluetoothDevice device, final int rssi,
                                             final byte[] scanRecord) {
                            callback.onLeScan(device, (byte) rssi, scanRecord);
                        }
                    };
        }
        return leScanCallback;
    }
}
