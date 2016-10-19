package com.feifan.planlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.planlib.ILayerPoint;

/**
 * Created by xuchunlei on 16/9/27.
 */

public class LayerPoint implements ILayerPoint, Parcelable {

    private int id;

    // 绘制坐标
    private float locX;
    private float locY;

    // 平面图坐标
    private float rawX;
    private float rawY;

    // 实际坐标
    private float realX;
    private float realY;

    // 平面图比例尺
    private float scale;

    /** 锁定，锁定时不能被移动 */
    private boolean mMovable = true;

    public LayerPoint() {
        realX = -10;
        realY = -10;
        scale = 1;
    }

    protected LayerPoint(Parcel in) {
        id = in.readInt();
        locX = in.readFloat();
        locY = in.readFloat();
        rawX = in.readFloat();
        rawY = in.readFloat();
        realX = in.readFloat();
        realY = in.readFloat();
        scale = in.readFloat();
        mMovable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(locX);
        dest.writeFloat(locY);
        dest.writeFloat(rawX);
        dest.writeFloat(rawY);
        dest.writeFloat(realX);
        dest.writeFloat(realY);
        dest.writeFloat(scale);
        dest.writeByte((byte) (mMovable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LayerPoint> CREATOR = new Creator<LayerPoint>() {
        @Override
        public LayerPoint createFromParcel(Parcel in) {
            return new LayerPoint(in);
        }

        @Override
        public LayerPoint[] newArray(int size) {
            return new LayerPoint[size];
        }
    };

    /**
     * 设置索引编号
     *
     * @param id
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取索引编号
     *
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * 获取实际位置横坐标
     *
     * @return
     */
    @Override
    public float getRawX() {
        return rawX;
    }

    /**
     * 获取实际位置纵坐标
     *
     * @return
     */
    @Override
    public float getRawY() {
        return rawY;
    }

    /**
     * 获取绘制位置横坐标
     *
     * @return
     */
    @Override
    public float getLocX() {
        return locX;
    }

    /**
     * 获取绘制位置纵坐标
     *
     * @return
     */
    @Override
    public float getLocY() {
        return locY;
    }

    /**
     * 获取真实位置横坐标
     *
     * @return
     */
    @Override
    public float getRealX() {
        if(mMovable) {
            realX = rawX * scale;
        }
        return realX;
    }

    /**
     * 获取真实位置纵坐标
     *
     * @return
     */
    @Override
    public float getRealY() {
        if(mMovable){
            realY = rawY * scale;
        }
        return realY;
    }

    /**
     * 平移绘制坐标
     *
     * @param deltaX
     * @param deltaY
     */
    @Override
    public void offset(float deltaX, float deltaY) {
        locX += deltaX;
        locY += deltaY;
    }

    /**
     * 平移计算坐标
     *
     * @param deltaX
     * @param deltaY
     */
    @Override
    public void offsetRaw(float deltaX, float deltaY) {
        rawX += deltaX;
        rawY += deltaY;
    }

    /**
     * 设置绘制坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void setDraw(float x, float y) {
        locX = x;
        locY = y;
    }

    /**
     * 设置计算坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void setRaw(float x, float y) {
        rawX = x;
        rawY = y;
    }

    /**
     * 设置真实坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void setReal(float x, float y) {
        realX = x;
        realY = y;
    }

    /**
     * 设置比例尺
     * <p>
     * 平面图坐标与实际坐标的比值
     * </p>
     *
     * @param scale
     */
    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public boolean isMovable() {
        return mMovable;
    }

    @Override
    public void setMovable(boolean movable) {
        mMovable = movable;
    }


}
