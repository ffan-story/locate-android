package com.feifan.locatelib.cache;

import com.feifan.locatelib.network.HttpResult;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import rx.internal.util.unsafe.MpmcArrayQueue;

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
     * @param uuid
     * @param major
     * @param minor
     * @param others
     * @return
     */
    @GET("beaconsByUMM?")
    Observable<HttpResult<PlazaBeaconInfo>> getPlazaBaseInfo(@Query("uuid") String uuid,
                                                             @Query("major") int major,
                                                             @Query("minor") int minor,
                                                             @QueryMap Map<String, String> others);

    Observable<HttpResult<PlazaFingerprintInfo>> getPlazaFingerprint();
}
