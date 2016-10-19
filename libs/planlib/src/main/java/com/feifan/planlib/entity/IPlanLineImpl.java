package com.feifan.planlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.planlib.ILayerLine;
import com.feifan.planlib.ILayerPoint;

/**
 * Created by bianying on 2016/10/16.
 */

public class IPlanLineImpl implements ILayerLine, Parcelable {

    private int mId;
    private ILayerPoint mPointOne;
    private ILayerPoint mPointTwo;

    public IPlanLineImpl() {

    }

    protected IPlanLineImpl(Parcel in) {
        mId = in.readInt();
        mPointOne = in.readParcelable(ILayerPoint.class.getClassLoader());
        mPointTwo = in.readParcelable(ILayerPoint.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeParcelable((Parcelable) mPointOne, flags);
        dest.writeParcelable((Parcelable) mPointTwo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IPlanLineImpl> CREATOR = new Creator<IPlanLineImpl>() {
        @Override
        public IPlanLineImpl createFromParcel(Parcel in) {
            return new IPlanLineImpl(in);
        }

        @Override
        public IPlanLineImpl[] newArray(int size) {
            return new IPlanLineImpl[size];
        }
    };

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public ILayerPoint getPointOne() {
        return mPointOne;
    }

    @Override
    public ILayerPoint getPointTwo() {
        return mPointTwo;
    }

    @Override
    public void setPointOne(ILayerPoint point) {
        mPointOne = point;
    }

    @Override
    public void setPointTwo(ILayerPoint point) {
        mPointTwo = point;
    }

    @Override
    public void clearPoint(ILayerPoint point) {
        if(point != null) {
            if(mPointOne != null && point.equals(mPointOne)) {
                setPointOne(null);
            }
            if(mPointTwo != null && point.equals(mPointTwo)) {
                setPointTwo(null);
            }
        }
    }

    @Override
    public String toString() {
        String ret = "";
        if(mPointOne != null) {
            ret = mPointOne.toString() + "-";
        }
        if(mPointTwo != null) {
            ret += mPointTwo.toString();
        }
        return ret;
    }
}
