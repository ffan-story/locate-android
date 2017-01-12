package com.feifan.locatelib.network;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.feifan.baselib.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

    private Map<String, Retrofit> retrofitStore;

    private ServiceFactory() {
        retrofitStore = new HashMap<>();
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
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
        if(context != null && cacheDir == null) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();
            LogUtils.d("initialize ServiceFactory instance");
        }
    }

    /**
     * 创建服务
     * @param clazz
     * @param <S>
     * @return
     */
    public <S> S createService(Class<S> clazz, String baseUrl) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz can not be null");
        }
        if(TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("baseUrl can not be empty");
        }
//        String baseUrl = "";
//        try {
//            Field field = clazz.getField("BASE_URL");
//            baseUrl = (String) field.get(clazz);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.getMessage();
//            e.printStackTrace();
//        }

        Retrofit retrofit = null;
        if(!TextUtils.isEmpty(baseUrl)) {
            retrofit = retrofitStore.get(baseUrl);
            if(retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
                retrofitStore.put(baseUrl, retrofit);
            }
        }

        if(retrofit == null) {
            throw new NullPointerException("create retrofit instance failed.");
        }
        LogUtils.i("create a retrofit service " + clazz.getSimpleName() + " with " + baseUrl);
        return retrofit.create(clazz);
    }

    private OkHttpClient getOkHttpClient() {
        // 定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // 设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        // 设置缓存
        File httpCacheDirectory = new File(cacheDir, "OkHttpCache");
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));

        // 设置日志
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(logging);

        // 设置headers
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("User-Agent", Build.MODEL);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClientBuilder.build();
    }

}
