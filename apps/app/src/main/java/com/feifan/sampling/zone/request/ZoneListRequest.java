package com.feifan.sampling.zone.request;

import com.feifan.sampling.test.model.LocModel;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mengmeng on 16/5/23.
 */
public interface ZoneListRequest {

    String HELPER_CENTER_PATH = "/api/v1/zones";
    String PAGEINDEX = "pageIndex";
    String PAGESIZE = "pageSize";

    @GET(HELPER_CENTER_PATH)
    Call<BaseJsonBean<LocModel>> getZoneList(
            @Query(PAGEINDEX) int pageIndex
            , @Query(PAGESIZE) int pageSize);
}