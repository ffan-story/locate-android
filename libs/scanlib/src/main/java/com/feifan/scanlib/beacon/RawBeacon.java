package com.feifan.scanlib.beacon;

import android.bluetooth.BluetoothDevice;

import com.feifan.baselib.utils.LogUtils;

/**
 * Created by bianying on 16/9/4.
 */
public class RawBeacon {
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
    /** mac地址 */
    public String mac;
    /** 原始数据 */
    public byte[] rawData;

    public static RawBeacon fromScanData(BluetoothDevice device, int rssi, byte[] rawData){
        RawBeacon beacon = new RawBeacon();
        beacon.rssi = rssi;
        beacon.rawData = rawData;
        beacon.uuid = IBeaconUtils.calculateUUID(rawData);
        beacon.major = IBeaconUtils.calculateMajor(rawData);
        beacon.minor = IBeaconUtils.calculateMinor(rawData);
        beacon.txPowser = IBeaconUtils.calculateTxPower(rawData);
        beacon.mac = device == null ? "" : device.getAddress();

//        dump(rawData, beacon);
        return beacon;
    }

    private static void dump(byte[] rawData, RawBeacon beacon) {
        if (((int)rawData[5] & 0xff) == 0x4c &&
                ((int)rawData[6] & 0xff) == 0x00 &&
                ((int)rawData[7] & 0xff) == 0x02 &&
                ((int)rawData[8] & 0xff) == 0x15) {
            LogUtils.w("I am a ibeacon");
        }else {
            LogUtils.e("I am not a ibeacon:" + beacon.toString());
        }
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof RawBeacon)) {
            return false;
        }
        RawBeacon thatBeacon = (RawBeacon) that;

        return mac.equals(thatBeacon.mac)
                && uuid.equals(thatBeacon.uuid)
                && minor == thatBeacon.minor
                && major == thatBeacon.major;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + major;
        result = 31 * result + minor;
        result = 31 * result + uuid.hashCode();
        result = 31 * result + mac.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "rssi=" + rssi + ",uuid=" + uuid + ",major=" + major + ",minor=" + minor
                + ",txpower=" + txPowser + ",mac=" + mac;
    }
}
