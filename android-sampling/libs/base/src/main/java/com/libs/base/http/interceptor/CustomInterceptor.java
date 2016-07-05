package com.libs.base.http.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mengmeng on 16/5/17.
 * 用于request的切片处理
 */
public abstract class CustomInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = request.url().url().getHost();
        String path = request.url().url().getPath();
        Log.d("path",host+path);
        onPreRequest(request);
        Response response = chain.proceed(request);
        onPostRequest(response);
        return response;
    }

    public abstract void onPreRequest(Request request);
    public abstract void onPostRequest(Response response);

}
