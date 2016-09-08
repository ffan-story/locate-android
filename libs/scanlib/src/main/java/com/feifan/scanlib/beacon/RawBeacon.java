package com.feifan.scanlib.beacon;

import android.bluetooth.BluetoothDevice;

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
        return beacon;
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
