package com.feifan.sampling.charts.request;


import com.feifan.sampling.scan.model.SpotPatchUploadModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface ChartDataInterface {

    String UPLOAD_PATH = "/api/v1/samples/beacons/patch";
    String CODE = "code";

    @FormUrlEncoded
    @GET(UPLOAD_PATH)
    Call<BaseJsonBean<SpotPatchUploadModel>> getStockData(
            @Query(CODE) String code);
}
