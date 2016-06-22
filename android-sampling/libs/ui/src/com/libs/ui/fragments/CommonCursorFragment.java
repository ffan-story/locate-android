package com.libs.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public abstract class CommonCursorFragment extends CommonFragment {
	/** 创建Loader时的参数－selection */
	protected static final String LOADER_KEY_SELECTION = "selection";
	/** 创建Loader时的参数－selectionArgs */
	protected static final String LOADER_KEY_SELECTION_ARGS = "selectionArgs";
	/** 创建Loader时的参数－selectionArgs */
	protected static final String LOADER_KEY_PROJECTION_ARGS = "projectionArgs";
	/** 创建Loader时的参数－selectionArgs */
	protected static final String LOADER_KEY_ORDER_ARGS = "orderbyArgs";

	protected abstract Bundle getSqlArgument();

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initLoader();
	}

	protected void initLoader(){
		Bundle args = getSqlArgument();
		startLoadLocal(args);
	}

	protected abstract Uri getSqlUri();

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
		CursorLoader loader = null;
		if(id == getLocalLoadId()) {    // 子项Loader
			String selection = args != null ? args.getString(LOADER_KEY_SELECTION) : null;
			String orderby = args != null ? args.getString(LOADER_KEY_ORDER_ARGS) : null;
			String[] selectionArgs = args != null ? args.getStringArray(LOADER_KEY_SELECTION_ARGS) : null;
			String[] projectionArgs = args != null ? args.getStringArray(LOADER_KEY_PROJECTION_ARGS) : null;
			return new CursorLoader(getContext(), getSqlUri(), projectionArgs, selection, selectionArgs, orderby);
		}
		return loader;
	}
}
