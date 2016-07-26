package com.libs.base.http;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * okHttp callback wrapper
 * 
 * @author afree on 4/25/16.
 */
public abstract class BpCallback<T> implements Callback<T> {
  /**
   * 错误状态－Cookie过期
   */
  private static final int STATUS_COOKIE_EXPIRE = 3001;
  /**
   * 错误状态－登陆身份失效
   */
  private static final int STATUS_NOT_LOGIN = 401;

  @Override
  public void onResponse(Call<T> call, Response<T> response) {
    if (response == null) {
      onFailure("response model is null");
      return;
    }
    if (response != null && response.body() != null) {
      onResponse(response.body());
    } else {
      onFailure("response model is null");
    }
  }

  @Override
  public void onFailure(Call<T> call, Throwable t) {
    if (t != null) {
      onFailure(t.getMessage());
    } else {
      onFailure("network error");
    }
  }

  /**
   * @param t model wouldn't be null
   */
  public abstract void onResponse(T t);

  public abstract void onFailure(String message);

}
