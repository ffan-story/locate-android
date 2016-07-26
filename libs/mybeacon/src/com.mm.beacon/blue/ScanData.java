package com.mm.beacon.blue;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;

import com.mm.beacon.IScanData;

/**
 * Created by mengmeng on 15/8/21.
 */
public class ScanData extends IScanData {
    public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
        super(device, rssi, scanRecord);
    }

    protected ScanData(Parcel in) {
        super(in);
    }
//    public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        this.device = device;
//        this.rssi = rssi;
//        this.scanRecord = scanRecord;
//    }
//    public int index;
//    @SuppressWarnings("unused")
//    public BluetoothDevice device;
//    public int rssi;
//    public long time;
//    public byte[] scanRecord;
//
//    protected ScanData(Parcel in) {
//        index = in.readInt();
//        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
//        rssi = in.readInt();
//        time = in.readLong();
//        scanRecord = in.createByteArray();
//    }
//
//    public static final Creator<ScanData> CREATOR = new Creator<ScanData>() {
//        @Override
//        public ScanData createFromParcel(Parcel in) {
//            return new ScanData(in);
//        }
//
//        @Override
//        public ScanData[] newArray(int size) {
//            return new ScanData[size];
//        }
//    };
//
//    /**
//     * Describe the kinds of special objects contained in this Parcelable's
//     * marshalled representation.
//     *
//     * @return a bitmask indicating the set of special object types marshalled
//     * by the Parcelable.
//     */
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    /**
//     * Flatten this object in to a Parcel.
//     *
//     * @param dest  The Parcel in which the object should be written.
//     * @param flags Additional flags about how the object should be written.
//     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
//     */
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(index);
//        dest.writeParcelable(device, flags);
//        dest.writeInt(rssi);
//        dest.writeLong(time);
//        dest.writeByteArray(scanRecord);
//    }
}
