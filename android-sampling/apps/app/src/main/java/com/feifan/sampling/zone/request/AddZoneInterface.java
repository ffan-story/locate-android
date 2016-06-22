package com.feifan.sampling.zone.request;

import com.feifan.sampling.zone.model.SpotAddModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface AddZoneInterface {

    String HELPER_CENTER_PATH = "/api/v1/zones";
    String ZONE_NAME = "zoneName";

    @FormUrlEncoded
    @POST(HELPER_CENTER_PATH)
    Call<BaseJsonBean<SpotAddModel>> addZone(
            @Field(ZONE_NAME) String zoneName);
}
