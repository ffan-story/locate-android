package com.feifan.sampling.spot.request;

import com.feifan.sampling.spot.model.SpotList;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mengmeng on 16/6/29.
 */
public interface SpotListRequest {

    String ZONE_LIST_REQUEST_PATH = "/api/v1/spots";
    String PAGEINDEX = "pageIndex";
    String PAGESIZE = "pageSize";
    String ZONE_ID = "zone_id";


//    pageIndex	(required)	query	integer	页码，从1开始
//    pageSize	(required)	query	integer	页面大小
//    zone_id	(required)	query	integer	定位区域id，外键

    @GET(ZONE_LIST_REQUEST_PATH)
    Call<BaseJsonBean<SpotList>> getSpotList(
            @Query(ZONE_ID) int zone_id
            , @Query(PAGEINDEX) int pageIndex
            , @Query(PAGESIZE) int pageSize);
}
