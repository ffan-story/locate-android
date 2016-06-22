package com.mm.beacon.blue;

import android.bluetooth.BluetoothDevice;

/**
 * Created by mengmeng on 15/8/21.
 */
public class ScanData {
    public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }
    public int index;
    @SuppressWarnings("unused")
    public BluetoothDevice device;
    public int rssi;
    public long time;
    public byte[] scanRecord;
}
