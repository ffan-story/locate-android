package com.libs.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libs.ui.R;

public class CommonMenuFragment<C> extends CommonFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateCustomView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(isShowMenu()) {
			initActionBar();
		}
	}

	protected void initActionBar() {
		if (mAbar != null) {
			mAbar.setHomeAsUpIndicator(R.drawable.ic_menu);
			mAbar.setDisplayHomeAsUpEnabled(true);
			if (mToolBarDelegate != null) {
				mToolBarDelegate.getBackView().setVisibility(View.GONE);
			}
		}
	}

	protected boolean isShowMenu(){
		return true;
	}
}
