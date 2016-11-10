package com.feifan.locatelib.cache;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 广场Beacon数据信息
 *
 * Created by xuchunlei on 2016/11/9.
 */

public class PlazaBeaconInfo {

    public List<BeaconInfo> beacons;
    public PlazaInfo plaza;

    public static class BeaconInfo {
        public String uuid;
        public int major;
        public int minor;
        public int floor;

        @Override
        public String toString() {
            return "uuid=" + uuid + ",major=" + major + ",minor=" + minor + ",floor=" + floor;
        }
    }

    public static class PlazaInfo implements Parcelable {
        public String plazaId;
        public String plazaName;

        protected PlazaInfo(Parcel in) {
            plazaId = in.readString();
            plazaName = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(plazaId);
            dest.writeString(plazaName);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PlazaInfo> CREATOR = new Creator<PlazaInfo>() {
            @Override
            public PlazaInfo createFromParcel(Parcel in) {
                return new PlazaInfo(in);
            }

            @Override
            public PlazaInfo[] newArray(int size) {
                return new PlazaInfo[size];
            }
        };

        @Override
        public String toString() {
            return "plazaId=" + plazaId + ",plazaName=" + plazaName;
        }
    }
}
