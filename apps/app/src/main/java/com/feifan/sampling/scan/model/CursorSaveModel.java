package com.feifan.sampling.scan.model;

import android.net.Uri;

import com.mm.beacon.IBeacon;

import java.util.List;

/**
 * Created by mengmeng on 16/6/17.
 */
public class CursorSaveModel {
    private String name;
    private String spotId;
    private String direction;
    private Uri uri;
    private List<IBeacon> list;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public List<IBeacon> getList() {
        return list;
    }

    public void setList(List<IBeacon> list) {
        this.list = list;
    }
}
