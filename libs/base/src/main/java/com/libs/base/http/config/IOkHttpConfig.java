package com.libs.base.http.config;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import retrofit2.Converter;

/**
 * OkHttp ｜ retrofit configs
 * extensions will be added in the future for business need
 * Created by afree on 4/4/16.
 */
public interface IOkHttpConfig {

  /**
   * @return interceptor for doing something on the request or response
   */
  List<Interceptor> getInterceptors();

  void addInterceptor(Interceptor interceptor);

  /**
   * @return cache for OkHttp lib
   */
  Cache getCache();

  /**
   * @return protocol + domain,like "http://api.ffan.com"
   */
  String getBaseUrl();

  /**
   * @return response data converter , json,xml, etc.
   */
  List<Converter.Factory> getConverter();

  /**
   * @return ssl for https
   */
  SSLSocketFactory getSSlSocketFactory();

  /**
   * @return verifier for request
   */
  HostnameVerifier getHostnameVerifier();
}
