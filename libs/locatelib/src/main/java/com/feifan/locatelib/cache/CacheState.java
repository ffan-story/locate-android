package com.feifan.locatelib.cache;

/**
 * 缓存状态类
 * Created by xuchunlei on 2016/11/15.
 */

public class CacheState {
    private static final CacheState INSTANCE = new CacheState();

    private String currentPlazaId = "";
    private int currentFloor = 0;
    private String currentVersion = "0";
    private String currentKey = "";

    private CacheState() {

    }

    public static final CacheState getInstance() {
        return INSTANCE;
    }

    public void setPlazaId(String plazaId) {
        currentPlazaId = plazaId;
    }

    public String getPlazaId() {
        return currentPlazaId;
    }

    public void setFloor(int floor) {
        currentFloor = floor;
    }

    public int getFloor() {
        return currentFloor;
    }

    public void setVersion(String version) {
        currentVersion = version;
    }

    public String getVersion() {
        return currentVersion;
    }

    public void setDownloadKey(String key) {
        currentKey = key;
    }

    public String getDownloadKey() {
        return currentKey;
    }

    public String getStoreName() {
        return currentPlazaId + "v" + currentVersion;
    }

    public boolean isValid() {
        return !currentPlazaId.isEmpty()
                && currentFloor != 0
                && !currentVersion.isEmpty()
                && !currentVersion.isEmpty();
    }

    public void reset() {
        currentPlazaId = "";
        currentFloor = 0;
        currentVersion = "";
        currentKey = "";
    }
}
