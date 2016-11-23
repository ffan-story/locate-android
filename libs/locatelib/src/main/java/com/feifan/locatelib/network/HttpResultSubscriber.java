package com.feifan.locatelib.network;

import com.feifan.baselib.utils.LogUtils;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by xuchunlei on 16/9/19.
 */
public abstract class HttpResultSubscriber<T> extends Subscriber<HttpResult<T>> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        try{
            e.printStackTrace();
            //在这里做全局的错误处理
            if (e instanceof HttpException) {
                // ToastUtils.getInstance().showToast(e.getMessage());
            }
            _onError(e);
        } catch (Throwable t) {
            LogUtils.e("An unhandle error was catched:" + t != null ? t.getMessage() : "none");
        }

    }

    @Override
    public void onNext(HttpResult<T> tHttpResult) {
//        if (tHttpResult.errorCode == 0)
//            _onSuccess(tHttpResult.info);
//        else
//            _onError(new Throwable("error=" + tHttpResult.errorCode));
        LogUtils.e("status ---> " + tHttpResult.status);
        if (tHttpResult.status == 0)
            _onSuccess(tHttpResult.data);
        else
            _onError(new Throwable("error=" + tHttpResult.msg));
    }

    protected abstract void _onError(Throwable e);
    protected abstract void _onSuccess(T data);
}
