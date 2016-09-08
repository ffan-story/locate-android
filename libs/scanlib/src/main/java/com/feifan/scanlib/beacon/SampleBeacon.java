package com.feifan.scanlib.beacon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bianying on 16/9/4.
 */
public class SampleBeacon extends RawBeacon implements Parcelable{

    public double time;
    public float direction;
    public float accuracy;
    public float loc_x;
    public float loc_y;
    public float loc_d;
    public int floor;

    public SampleBeacon(RawBeacon beacon) {
        update(beacon);
    }

    protected SampleBeacon(Parcel in) {
        uuid = in.readString();
        major = in.readInt();
        minor = in.readInt();
        rssi = in.readInt();
        mac = in.readString();
        txPowser = in.readInt();
//        time = in.readLong();
//        direction = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeInt(major);
        dest.writeInt(minor);
        dest.writeInt(rssi);
        dest.writeString(mac);
        dest.writeInt(txPowser);
//        dest.writeLong(time);
//        dest.writeFloat(direction);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SampleBeacon> CREATOR = new Creator<SampleBeacon>() {
        @Override
        public SampleBeacon createFromParcel(Parcel in) {
            return new SampleBeacon(in);
        }

        @Override
        public SampleBeacon[] newArray(int size) {
            return new SampleBeacon[size];
        }
    };

    public void update(RawBeacon beacon) {
        uuid = beacon.uuid;
        major = beacon.major;
        minor = beacon.minor;
        rssi = beacon.rssi;
        rawData = beacon.rawData;
        mac = beacon.mac;
        txPowser = beacon.txPowser;
    }

    public String toFormattedString() {
        return uuid + "," + major + "," + minor + "," + rssi
                + "," + accuracy + "," + direction + "," + time
                + "," + loc_x + "," + loc_y + "," + loc_d
                + "," + floor;
    }
}
