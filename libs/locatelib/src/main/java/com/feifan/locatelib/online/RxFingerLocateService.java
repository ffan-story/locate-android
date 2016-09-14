package com.feifan.locatelib.online;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Beacon指纹定位网络请求服务接口
 *
 * Created by xuchunlei on 16/9/14.
 */
public interface RxFingerLocateService {
    @POST("fix")
    Observable<LocateResultBean> getLocation(@Body LocateQueryData data);
}
