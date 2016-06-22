package com.feifan.sampling.spot.request;

import com.feifan.sampling.zone.model.SpotAddModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface AddSpotInterface {

    String ADD_SPOT_PATH = "/api/v1/spots";
    String SPOT_X = "x";
    String SPOT_Y = "y";
    String SPOT_D = "d";
    String ZONE_ID = "zone_id";

    @FormUrlEncoded
    @POST(ADD_SPOT_PATH)
    Call<BaseJsonBean<SpotAddModel>> addSpot(
            @Field(SPOT_X) String x,
            @Field(SPOT_Y) String y,
            @Field(SPOT_D) String d,
            @Field(ZONE_ID) String zone_id);
}
