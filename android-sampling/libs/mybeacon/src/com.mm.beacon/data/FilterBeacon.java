package com.mm.beacon.data;

/**
 * Created by mengmeng on 15/9/22.
 */
public class FilterBeacon {

    private String uuid;
    private int major = 0;
    private int minor = 0;

    public String getUuid() {
        return uuid.toString().trim();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }
}
