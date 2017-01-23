package com.feifan.scanlib.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bianying on 16/9/3.
 */
public class CommandData implements Parcelable {

    // 扫描周期
    private int period;
    private String callPackageName;

    public CommandData(String packageName, int period) {
        this.period = period;
        this.callPackageName = packageName;
    }

    protected CommandData(Parcel in) {
        period = in.readInt();
        callPackageName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(period);
        dest.writeString(callPackageName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommandData> CREATOR = new Creator<CommandData>() {
        @Override
        public CommandData createFromParcel(Parcel in) {
            return new CommandData(in);
        }

        @Override
        public CommandData[] newArray(int size) {
            return new CommandData[size];
        }
    };

    public int getPeriod() {
        return period;
    }

    public String getCallPackageName() {
        return callPackageName;
    }
}
