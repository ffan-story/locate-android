package com.libs.base.mvp;

/**
 * Created by mengmeng on 16/5/24.
 */
public abstract  class IRouter {
    protected IView mIView;
    protected IPresentor mPresentor;

    public IRouter(IView view, IPresentor presentor) {
        this.mIView = view;
        this.mPresentor = presentor;
    }

    public IView getView() {
        return mIView;
    }

    public IPresentor getPresentor() {
        return mPresentor;
    }



}
