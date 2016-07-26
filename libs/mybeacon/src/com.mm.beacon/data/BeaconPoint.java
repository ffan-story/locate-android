package com.mm.beacon.data;

/**
 * Created by mengmeng on 15/9/20.
 */
public class BeaconPoint {
    private  DoublePoint mPoint;
    private double mDistance;

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public  DoublePoint getPoint() {
        return mPoint;
    }

    public void setPoint( DoublePoint mPoint) {
        this.mPoint = mPoint;
    }

}
