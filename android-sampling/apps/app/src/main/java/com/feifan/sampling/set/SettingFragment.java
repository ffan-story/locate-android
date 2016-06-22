package com.feifan.sampling.set;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libs.base.mvp.MvpRouter;
import com.libs.ui.fragments.CommonFragment;

/**
 * Created by mengmeng on 16/5/24.
 */
public class SettingFragment extends CommonFragment{
    private SettingView mSetingView;
    private SettingPresentor mSettingPresentor;
    private MvpRouter mMvpRouter;
    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSetingView = new SettingView(inflater, container, savedInstanceState);
        mSettingPresentor = new SettingPresentor(getActivity(),this);
        return mSetingView.getContentView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        mMvpRouter = new MvpRouter(mSetingView,mSettingPresentor);
        mSetingView.bindRouter(mMvpRouter);
        mSettingPresentor.bindRouter(mMvpRouter);
    }
}
