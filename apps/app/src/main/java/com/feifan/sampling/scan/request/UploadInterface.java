package com.feifan.sampling.scan.request;


import com.feifan.sampling.scan.model.SpotPatchUploadModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface UploadInterface {

    String UPLOAD_PATH = "/api/v1/samples/beacons/patch";
    String DATA = "data";

    @FormUrlEncoded
    @POST(UPLOAD_PATH)
    Call<BaseJsonBean<SpotPatchUploadModel>> uploadSpot(
            @Field(DATA) String data);
}
