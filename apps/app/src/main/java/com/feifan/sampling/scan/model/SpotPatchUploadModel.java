package com.feifan.sampling.scan.model;

import com.feifan.sampling.spot.model.SpotUploadModel;

import java.util.List;

/**
 * Created by mengmeng on 16/5/17.
 */
public class SpotPatchUploadModel {
    private List<SpotUploadModel> ids;
    public List<SpotUploadModel> getIds() {
        return ids;
    }

    public void setIds(List<SpotUploadModel> ids) {
        this.ids = ids;
    }

}
