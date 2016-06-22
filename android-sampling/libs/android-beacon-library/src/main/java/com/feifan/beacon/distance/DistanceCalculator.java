package com.feifan.beacon.distance;

/**
 * Interface for a class that can estimate the distance between a mobile
 * device and a com.my.com.my.com.mm.beacon based on the measured RSSI and a reference txPower
 * calibration value.
 *
 * Created by dyoung on 8/28/14.
 */
public interface DistanceCalculator {
    public double calculateDistance(int txPower, double rssi);
}
