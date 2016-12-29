package com.feifan.locate.sampling.workspot;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class SpotInfo implements Parcelable {

    public int id;
    public float locX;
    public float locY;

    protected SpotInfo(Parcel in) {
        id = in.readInt();
        locX = in.readFloat();
        locY = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(locX);
        dest.writeFloat(locY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SpotInfo> CREATOR = new Creator<SpotInfo>() {
        @Override
        public SpotInfo createFromParcel(Parcel in) {
            return new SpotInfo(in);
        }

        @Override
        public SpotInfo[] newArray(int size) {
            return new SpotInfo[size];
        }
    };
}
