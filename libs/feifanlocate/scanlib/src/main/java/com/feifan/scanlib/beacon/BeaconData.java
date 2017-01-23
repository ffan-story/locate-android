package com.feifan.scanlib.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.baselib.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by bianying on 16/9/4.
 */
public class BeaconData implements Parcelable {
    private Collection<SampleBeacon> mBeacons;

    public BeaconData(Collection<SampleBeacon> data) {
        this.mBeacons = data;
    }

    protected BeaconData(Parcel in) {
        LogUtils.d("parsing RangingData");
        Parcelable[] parcelablesBeacons = in.readParcelableArray(this.getClass().getClassLoader());
        mBeacons = new ArrayList<SampleBeacon>(parcelablesBeacons.length);
        for (int i = 0; i < parcelablesBeacons.length; i++) {
            mBeacons.add((SampleBeacon) parcelablesBeacons[i]);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        LogUtils.d("writing RangingData");
        dest.writeParcelableArray(mBeacons.toArray(new Parcelable[0]), flags);
        LogUtils.d("done writing RangingData");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BeaconData> CREATOR = new Creator<BeaconData>() {
        @Override
        public BeaconData createFromParcel(Parcel in) {
            return new BeaconData(in);
        }

        @Override
        public BeaconData[] newArray(int size) {
            return new BeaconData[size];
        }
    };

    public Collection<SampleBeacon> getBeacons() {
        return mBeacons;
    }
}
