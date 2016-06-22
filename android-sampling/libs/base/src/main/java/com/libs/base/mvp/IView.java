package com.libs.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libs.utils.ExceptionUtils;

/**
 * Created by mengmeng on 16/5/24.
 */
public abstract class IView {
    protected View mRootView;
    protected MvpRouter mMvpRouter;
    public IView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(getLayoutId() == 0){
            ExceptionUtils.throwArgumentExeception("the layout can not be null");
        }
        mRootView = inflater.inflate(getLayoutId(),container,false);
    }

    public void bindRouter(MvpRouter mvpRouter){
        if(mvpRouter != null){
            mMvpRouter = mvpRouter;
            onRouterBind();
        }
    }

    protected void onRouterBind(){

    }

    public View getContentView(){
        return mRootView;
    }

    public abstract int getLayoutId();

    public void onDestory(){
        mMvpRouter = null;
    }
}
