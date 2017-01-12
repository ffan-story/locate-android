package com.feifan.locatelib.cache.request;

import com.feifan.locatelib.cache.model.DownloadInfo;
import com.feifan.locatelib.network.HttpResult;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by xuchunlei on 2016/11/17.
 */

public interface RxPlazaFingerprintService {

    // http://10.1.82.142:8081/download?plazaid=860100010030300001
    //
    @GET("fingerp?")
    Observable<HttpResult<DownloadInfo>> queryFingerprintFile(@QueryMap Map<String, String> params);
}
