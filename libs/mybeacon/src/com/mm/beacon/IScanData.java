package com.mm.beacon;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mengmeng on 16/7/1.
 */
public class IScanData implements Parcelable {
    public IScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
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
    public float direction;

    protected IScanData(Parcel in) {
        index = in.readInt();
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readInt();
        time = in.readLong();
        scanRecord = in.createByteArray();
        direction = in.readFloat();
    }

    public static final Creator<IScanData> CREATOR = new Creator<IScanData>() {
        @Override
        public IScanData createFromParcel(Parcel in) {
            return new IScanData(in);
        }

        @Override
        public IScanData[] newArray(int size) {
            return new IScanData[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeParcelable(device, flags);
        dest.writeInt(rssi);
        dest.writeLong(time);
        dest.writeByteArray(scanRecord);
        dest.writeFloat(direction);
    }
}
