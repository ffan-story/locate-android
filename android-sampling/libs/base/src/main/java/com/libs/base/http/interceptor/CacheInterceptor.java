package com.libs.base.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * response响应cache－control字段改写
 * 请求接口上加注解 @Headers("Cache-Control: public, max-age=单位s的时间)
 * Created by afree on 4/3/16.
 */
public class CacheInterceptor implements Interceptor {
  private static final String CACHE_CONTROL = "Cache-Control";

  @Override
  public Response intercept(Chain chain) throws IOException {
    Response origin = chain.proceed(chain.request());
    String cacheControl = chain.request().cacheControl().toString();
    return origin.newBuilder().header(CACHE_CONTROL, cacheControl).build();
  }

}
