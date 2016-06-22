package com.mm.stock.charts.request;


import com.mm.stock.charts.model.LineBBDChartModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mengmeng on 16/5/17.
 */
public interface ChartDataInterface {

//    002023
    String UPLOAD_PATH = "/stock/getdata";
    String CODE = "code";

    @GET(UPLOAD_PATH)
    Call<LineBBDChartModel> getStockData(
            @Query(CODE) String code);
}
