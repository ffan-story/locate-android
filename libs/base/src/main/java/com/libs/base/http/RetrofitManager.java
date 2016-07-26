package com.libs.base.http;

import android.text.TextUtils;

import com.libs.base.http.config.IOkHttpConfig;
import com.libs.base.http.config.OkHttpConfigImpl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * wrapper,a single instance, for OkHttp,retrofit
 * Created by mengmeng on 16/5/16.
 */
public class RetrofitManager {

  private IOkHttpConfig mConfig;
  private OkHttpClient mOkHttpClient;
  private Retrofit mRetrofit;


  public RetrofitManager(IOkHttpConfig config){
    if(config == null){
      mConfig = new OkHttpConfigImpl();
    }
    mConfig = config;
    init();
  }
  private void init() {
    initOkHttpClient();
    initRetrofit();
  }


  private void initOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (mConfig.getCache() != null) {
      builder.cache(mConfig.getCache());
    }
    if (mConfig.getInterceptors() != null && mConfig.getInterceptors().size() > 0) {
      for (Interceptor i : mConfig.getInterceptors()) {
        builder.addInterceptor(i);
      }
    }

    mOkHttpClient = builder.build();
  }

  public void setHttpCnfig(IOkHttpConfig config){
    if(config != null){
      mConfig = config;
      init();
    }
  }

  /**
   * 添加个性化Interceptor，用于进行requeset的前后的处理
   * @param interceptor
     */
  public void addInterceptor(Interceptor interceptor){
    if(interceptor != null){
      mConfig.addInterceptor(interceptor);
      init();
    }
  }

  public void setBaseUrl(String url){
    if(!TextUtils.isEmpty(url)){
      Retrofit.Builder builder = new Retrofit.Builder();
      builder.callFactory(mOkHttpClient)
              .baseUrl(url);
      if (mConfig.getConverter() != null && mConfig.getConverter().size() > 0) {
        for (Converter.Factory c : mConfig.getConverter()) {
          builder.addConverterFactory(c);
        }
      }
      mRetrofit = builder.build();
    }
  }

  /**
   * retrofit默认连接配置，连接超时15秒,读取超时20秒,没有写入超时
   */
  private void initRetrofit() {
    Retrofit.Builder builder = new Retrofit.Builder();
    builder.callFactory(mOkHttpClient)
            .baseUrl(mConfig.getBaseUrl());
    if (mConfig.getConverter() != null && mConfig.getConverter().size() > 0) {
      for (Converter.Factory c : mConfig.getConverter()) {
        builder.addConverterFactory(c);
      }
    }
    mRetrofit = builder.build();
  }

  public OkHttpClient getOkHttpClient() {
    return mOkHttpClient;
  }

  public Retrofit getRetrofit() {
    return mRetrofit;
  }

  public boolean clearCache() {
    try {
      mConfig.getCache().delete();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
