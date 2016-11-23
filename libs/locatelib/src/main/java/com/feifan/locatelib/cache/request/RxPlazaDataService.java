package com.feifan.locatelib.cache.request;

import com.feifan.locatelib.cache.model.PlazaBeaconInfo;
import com.feifan.locatelib.network.HttpResult;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 广场的beacon数据网络请求服务接口
 *
 * Created by xuchunlei on 2016/11/9.
 */

public interface RxPlazaDataService {

    /**
     * 获取广场基础信息
     * <p>
     *     包括广场ID，广场名称和广场点位数据
     * </p>
     *
     * @param params
     * @return
     */
    @GET("ihos/beacon/v1/beaconsByUMM?")
    Observable<HttpResult<PlazaBeaconInfo>> getPlazaBaseInfo(@QueryMap Map<String, String> params);

    /**
     * 下载指纹文件
     * @param name
     * @return
     */
    @GET("tfs/v1/files/{name}")
    Observable<Response<ResponseBody>> downloadFPFile(@Path("name") String name);
}
