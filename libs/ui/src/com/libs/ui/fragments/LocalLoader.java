package com.libs.ui.fragments;

import android.content.Context;
import android.support.v4.content.Loader;

public abstract class LocalLoader<D> extends Loader<D> {

	private D mData;
	private boolean mIsLoading = false;
	public LocalLoader(Context context) { 
		super(context);
	}
	
	@Override
	public void registerListener(int id, OnLoadCompleteListener<D> listener) {
		super.registerListener(id, listener);
	}
	
	@Override
	public void unregisterListener(OnLoadCompleteListener<D> listener) {
		super.unregisterListener(listener);
	}
	
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (!mIsLoading) {
			mIsLoading = true;
			if (mData != null) {
				deliverResult(mData);
			} else {
				forceLoad();
			}
		}
	}
	
	@Override
	public void forceLoad() {
		super.forceLoad();
	}
	
	@Override
	public void deliverResult(D data) {
		super.deliverResult(data);
		mData = data;
		clearData();
	}

	@Override
	protected void onAbandon() {
		super.onAbandon();
		clearData();
	};
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		clearData();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		clearData();
	}
	
	private void clearData(){
		mData = null;
		mIsLoading = false;
	}
	
	
}
