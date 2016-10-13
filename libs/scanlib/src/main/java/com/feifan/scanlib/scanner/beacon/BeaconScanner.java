package com.feifan.scanlib.scanner.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.beacon.BeaconUtils;

/**
 * 蓝牙扫描器
 *
 * Created by xuchunlei on 16/9/1.
 */
public class BeaconScanner implements LeScanCallback {

    private BluetoothAdapter mAdapter;
    private OnBeaconCallback mCallback;
    private BeaconData mData = new BeaconData();

    public BeaconScanner(BluetoothAdapter adapter) {
        mAdapter = adapter;
//        mCallback = callback;
    }

    public void start() {
        if(mAdapter != null) {
            LogUtils.i("start scan beacons by means of bluetooth");
            mAdapter.startLeScan(this);
        }
    }

    public void stop() {
        if(mAdapter != null) {
            mAdapter.stopLeScan(this);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        mData.reload(bluetoothDevice, i, bytes);
        mCallback.onBeaconScan(mData);
    }

    public interface OnBeaconCallback {
        void onBeaconScan(BeaconData data);
    }

    public static class BeaconData {
        /** 信号强度 */
        public int rssi;
        /***/
        public String uuid;
        /** minor */
        public int minor;
        /** major */
        public int major;
        /** txpower */
        public int txPowser;
        /** 采集时间 */
        public long time;
        /** mac地址 */
        public String mac;
        /** 原始数据 */
        public byte[] rawData;

        void reload(BluetoothDevice device, int rssi, byte[] rawData){
            this.rssi = rssi;
            this.rawData = rawData;
            this.uuid = BeaconUtils.calculateUUID(rawData);
            this.major = BeaconUtils.calculateMajor(rawData);
            this.minor = BeaconUtils.calculateMinor(rawData);
            this.txPowser = BeaconUtils.calculateTxPower(rawData);
            this.mac = device == null ? "" : device.getAddress();
            this.time = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "rssi=" + rssi + ",uuid=" + uuid + ",major=" + major + ",minor=" + minor
                    + ",txpower=" + txPowser + ",mac=" + mac + ",time=" + time;
        }
    }
}
