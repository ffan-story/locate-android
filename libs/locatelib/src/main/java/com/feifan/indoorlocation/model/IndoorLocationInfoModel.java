package com.feifan.indoorlocation.model;

import java.io.Serializable;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public class IndoorLocationInfoModel implements Serializable {
    public String plazaId; // 广场id，必须
    public String plazaName; // 广场名字，必须
    public double gps; // 定位点gps，当前可选

    public IndoorLocationInfoModel() {

    }

    public IndoorLocationInfoModel(String plazaId, String plazaName, double gps) {
        this.plazaId = plazaId;
        this.plazaName = plazaName;
        this.gps = gps;
    }
}

