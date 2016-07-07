package com.mm.beacon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mengmeng on 15/9/22.
 */
public class FilterBeacon implements Parcelable {

    private String uuid;
    private int major = 0;
    private int minor = 0;

    protected FilterBeacon(Parcel in) {
        uuid = in.readString();
        major = in.readInt();
        minor = in.readInt();
    }

    public static final Creator<FilterBeacon> CREATOR = new Creator<FilterBeacon>() {
        @Override
        public FilterBeacon createFromParcel(Parcel in) {
            return new FilterBeacon(in);
        }

        @Override
        public FilterBeacon[] newArray(int size) {
            return new FilterBeacon[size];
        }
    };

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeInt(major);
        dest.writeInt(minor);
    }
    public String getUuid() {
        return uuid.toString().trim();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

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
}
