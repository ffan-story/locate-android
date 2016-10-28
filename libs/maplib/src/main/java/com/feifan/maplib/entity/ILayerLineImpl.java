package com.feifan.maplib.entity;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class ILayerLineImpl implements ILayerLine {

    private int mId;
    private ILayerPoint mPointOne;
    private ILayerPoint mPointTwo;

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
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
    public ILayerPoint getPointOne() {
        return mPointOne;
    }

    @Override
    public ILayerPoint getPointTwo() {
        return mPointTwo;
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
}
