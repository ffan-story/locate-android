package com.libs.base.http.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 使用此类进行请求前的切片处理
 * Created by mengmeng on 16/5/16.
 */
public class PostRequestInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    performCookie(response);
    return response;
  }

  private void performCookie(Response response){
    if(response != null){

    }
  }
}
