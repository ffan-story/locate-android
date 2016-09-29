package com.feifan.locatelib.online;

import android.os.Build;

import com.feifan.locatelib.network.HttpResult;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Beacon指纹定位网络请求服务接口
 *
 * Created by xuchunlei on 16/9/14.
 */
public interface RxFingerLocateService {
    /**
     * 请求地址
     */
    String BASE_URL = "http://123.56.144.204/";
//    String BASE_URL = "http://10.1.82.242:8081";

    @POST("fix")
    Observable<HttpResult<LocateInfo>> getLocation(@Body LocateQueryData data);
}
