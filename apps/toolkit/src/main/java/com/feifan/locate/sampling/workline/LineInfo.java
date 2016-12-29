package com.feifan.locate.sampling.workline;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class LineInfo implements Parcelable {

    public int id;
    public int pointOneId;
    public int pointTwoId;
    public float pointOneX;
    public float pointOneY;
    public float pointTwoX;
    public float pointTwoY;

    public LineInfo() {

    }

    protected LineInfo(Parcel in) {
        id = in.readInt();
        pointOneId = in.readInt();
        pointTwoId = in.readInt();
        pointOneX = in.readFloat();
        pointOneY = in.readFloat();
        pointTwoX = in.readFloat();
        pointTwoY = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(pointOneId);
        dest.writeInt(pointTwoId);
        dest.writeFloat(pointOneX);
        dest.writeFloat(pointOneY);
        dest.writeFloat(pointTwoX);
        dest.writeFloat(pointTwoY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LineInfo> CREATOR = new Creator<LineInfo>() {
        @Override
        public LineInfo createFromParcel(Parcel in) {
            return new LineInfo(in);
        }

        @Override
        public LineInfo[] newArray(int size) {
            return new LineInfo[size];
        }
    };
}
