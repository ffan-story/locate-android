package com.feifan.locatelib.network;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 创建网络请求服务的工厂方法
 * Created by xuchunlei on 16/9/14.
 */
public class ServiceFactory {

    private final static ServiceFactory INSTANCE = new ServiceFactory();

    private final static long DEFAULT_TIMEOUT = 10;

    // 默认的缓存目录是当前路径下的locate目录
    private String cacheDir = "locate";

    private ServiceFactory() {

    }

    /**
     * 初始化
     * <p>
     *     可以通过调用该方法初始化，目前支持：
     *     （1）缓存目录
     * </p>
     * @param context
     */
    public void initialize(Context context) {
        if(context != null) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();
        }
    }

    /**
     * 创建服务
     * @param clazz
     * @param <S>
     * @return
     */
    public <S> S createService(Class<S> clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz can not be null");
        }

        String baseUrl = "";
        try {
            Field field = clazz.getField("BASE_URL");
            baseUrl = (String) field.get(clazz);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.getMessage();
            e.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    private OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存
        File httpCacheDirectory = new File(cacheDir, "OkHttpCache");
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));
        return httpClientBuilder.build();
    }

}
