package com.feifan.maplib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.rtm.frm.data.Location;
import com.rtm.frm.data.Point;

/**
 * Created by xuchunlei on 2016/10/25.
 */

public class ILayerPointImpl implements ILayerPoint {

    private int mId;
    private Location mLocation;
    private Point mPoint;
    private boolean mMovable;

    public ILayerPointImpl(float drawX, float drawY) {
        mPoint = new Point();
        setDraw(drawX, drawY);
    }

    protected ILayerPointImpl(Parcel in) {
        mId = in.readInt();
        mMovable = in.readByte() != 0;
    }

    public void setLocation(Location l) {
        mLocation = l;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setDraw(float x, float y) {
        mPoint.setX(x);
        mPoint.setY(y);
    }

    @Override
    public Point getDraw() {
        return mPoint;
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public boolean isMovable() {
        return mMovable;
    }

    @Override
    public void setMovable(boolean movable) {
        mMovable = movable;
    }

    @Override
    public String toString() {
//        return "draw(" + mLocation.getX() + "," + mLocation.getY() + ") at "  + mPoint.getX() + "," + mPoint.getY();
        return "(" + mLocation.getX() + "," + mLocation.getY() + ")";
    }
}
