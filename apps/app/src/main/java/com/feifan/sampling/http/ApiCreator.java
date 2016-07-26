package com.feifan.sampling.http;

import com.libs.base.http.RetrofitManager;

import okhttp3.Interceptor;

/**
 * api creator
 * Created by mengmeng on 16/5/16.
 */
public class ApiCreator {

  private static ApiCreator mApiCreator;
  private RetrofitManager mRetrofitManager;

  private ApiCreator(){
    mRetrofitManager = new RetrofitManager(new MyHttpConfig());
  }

  public static ApiCreator getInstance(){
    if(mApiCreator == null){
      synchronized (ApiCreator.class){
        if(mApiCreator == null){
          mApiCreator = new ApiCreator();
        }
      }
    }
    return mApiCreator;
  }

  public <T> T createApi(Class<T> clazz) {
    if (clazz == null) {
      return null;
    }
    return mRetrofitManager.getRetrofit().create(clazz);
  }

  public  <T> T createApi(Class<T> clazz, Interceptor interceptor) {
    if (clazz == null) {
      return null;
    }
    mRetrofitManager.addInterceptor(interceptor);
    return mRetrofitManager.getRetrofit().create(clazz);
  }

  public void addInterceptor(Interceptor interceptor){
    mRetrofitManager.addInterceptor(interceptor);
  }
}
