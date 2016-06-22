package com.libs.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libs.ui.R;

public class CommonFragment<C> extends RootFragment {
    private BaseProgressDialog mProcessDialog;
	private final String TAG = "CommonFragment";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProcessDialog = new BaseProgressDialog();
		showDataState();
	}

	@Override
	public void onActivityEventPost(Object object) {
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}

	@Override
	public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	@Override
	protected View onCreateLoadingView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_loading_layout, container,false);
	}

	@Override
	protected View onCreateEmptyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_empty_layout, container,false);
	}
 
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
	public void onStart() {
		super.onStart();
		showDataState();
	}

    protected void showProcessDialog(){
        mProcessDialog.setTargetFragment(CommonFragment.this, 0);
        mProcessDialog.show(getFragmentManager(), CommonFragment.class.getName());
    }

    protected  void disMissDialog(){
        if(mProcessDialog != null && mProcessDialog.isAdded()){
            mProcessDialog.dismiss();
        }
    }

	protected void showLoadState() {
		showProcessDialog();
	}
	
	protected void showEmptyState() {
		if(mEmptyView != null){
			mEmptyView.setVisibility(View.VISIBLE);
		}
	}
	
	protected void showDataState() {
		if(mEmptyView != null){
			mEmptyView.setVisibility(View.GONE);
		}
		if(mLoadingView != null){
			mLoadingView.setVisibility(View.GONE);
		}
		if(mContainerView != null){
			mContainerView.setVisibility(View.VISIBLE);
		}
		disMissDialog();
	}

	@Override
	public void onLoaderReset(Loader arg0) {
		
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
