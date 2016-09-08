package com.feifan.scanlib.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * 循环扫描器回调接口
 *
 * Created by bianying on 16/9/4.
 */
public interface CycledLeScanCallback {

    /**
     * 扫描数据回调
     * @param device
     * @param rssi
     * @param scanRecord
     */
    void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    /**
     * 循环结束回调
     */
    void onCycleEnd();
}
