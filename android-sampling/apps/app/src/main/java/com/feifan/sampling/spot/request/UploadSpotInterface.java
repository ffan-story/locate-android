package com.feifan.sampling.spot.request;


import com.feifan.sampling.spot.model.SpotUploadModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface UploadSpotInterface {

    String ADD_SPOT_PATH = "/api/v1/samples/beacons";
    String UUID = "uuid";
    String MAJOR = "major";
    String MINOR = "minor";
    String RSSI = "rssi";
    String TIME = "time";
    String SPOT_ID = "spot_id";
    String DEVICE = "device";

    @FormUrlEncoded
    @POST(ADD_SPOT_PATH)
    Call<BaseJsonBean<SpotUploadModel>> uploadSpot(
            @Field(UUID) String uuid,
            @Field(MAJOR) int major,
            @Field(MINOR) int minor,
            @Field(RSSI) int rssi,
            @Field(TIME) long time,
            @Field(DEVICE) String device,
            @Field(SPOT_ID) int spot_id);
}
