package com.libs.base.mvp;

import android.content.Context;

import com.libs.base.model.BaseBean;

/**
 * Created by mengmeng on 16/5/24.
 */
public abstract class IPresentor<T extends BaseBean> {
    protected MvpRouter mMvpRouter;
    protected Context mContext;

    public IPresentor(Context context){
        mContext = context;
    }
    public void bindRouter(MvpRouter mvpRouter){
        if(mvpRouter != null){
            mMvpRouter = mvpRouter;
            onRouterBind();
        }
    }

    protected void onRouterBind(){

    }

    public abstract void bindData(T t);

    public void onDestory(){
        mMvpRouter = null;
        mContext = null;
    }

}