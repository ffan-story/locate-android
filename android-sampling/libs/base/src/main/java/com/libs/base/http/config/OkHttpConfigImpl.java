package com.libs.base.http.config;


import com.libs.base.http.interceptor.CacheInterceptor;
import com.libs.base.http.interceptor.PostRequestInterceptor;
import com.libs.base.http.interceptor.PreRequestInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;

/**
 * Created by mengmeng on 16/5/16.
 */
public class OkHttpConfigImpl implements IOkHttpConfig {
  private List<Interceptor> mInterceptors;
  private Cache mCache;
  private List<Converter.Factory> mConverterFactories;
  private static final String CACHE_PATH = "/api/okhttp";
  private static final int CACHE_SIZE = 5 * 1024 * 1024;

  public OkHttpConfigImpl() {
    initInterceptors();
    initCache();
    initConverters();
  }

  private void initInterceptors() {
    mInterceptors = new ArrayList<>();
    mInterceptors.add(new PreRequestInterceptor());
    mInterceptors.add(new CacheInterceptor());
    mInterceptors.add(new PostRequestInterceptor());
  }

  private void initCache() {
    String parentPath = getCacheParentPath();
    if (parentPath != null) {
      File f = new File(parentPath.concat(CACHE_PATH));
      if (f.exists() || f.mkdirs()) {
        mCache = new Cache(f, CACHE_SIZE);
      }
    }
  }

  private void initConverters() {
    mConverterFactories = new ArrayList<>();
    mConverterFactories.add(GsonConverterFactory.create());
  }

  @Override
  public List<Interceptor> getInterceptors() {
    return mInterceptors;
  }

  @Override
  public void addInterceptor(Interceptor interceptor) {
    mInterceptors.add(interceptor);
  }

  @Override
  public Cache getCache() {
    return mCache;
  }

  @Override
  public String getBaseUrl() {
    //TODO 16/5/16
    return "";
  }

  @Override
  public List<Converter.Factory> getConverter() {
    return mConverterFactories;
  }

  @Override
  public SSLSocketFactory getSSlSocketFactory() {
    return null; // TODO: 4/5/16
  }

  @Override
  public HostnameVerifier getHostnameVerifier() {
    return null; // TODO: 4/5/16
  }


  private String getCacheParentPath() {
   return "";
  }
}
