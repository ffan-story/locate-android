package com.feifan.sampling.http;

import android.util.Log;

import com.libs.base.http.config.OkHttpConfigImpl;
import com.libs.base.http.interceptor.CustomInterceptor;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mengmeng on 16/5/17.
 */
public class MyHttpConfig extends OkHttpConfigImpl {
    public MyHttpConfig(){
        super();
        addInterceptor(new CustomInterceptor() {
            @Override
            public void onPreRequest(Request request) {
                String url = request.url().uri().toString();
                Log.d("url",url);
            }

            @Override
            public void onPostRequest(Response response) {
                if(response != null) {
                    Log.d("code", response.code() + "");
                    Log.d("msg", response.message() + "");
                }
            }
        });
    }

    @Override
    public String getBaseUrl() {
        return ApiFactory.BASE_URl;
    }
}
