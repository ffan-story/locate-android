package com.libs.ui.fragments;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public abstract class ActionBarDelegate {

//	public void initActionBar(Toolbar toolbar, Activity activity) {
//		this.initActionBar(toolbar, activity, null);
//	}
//
//	public void initActionBar(Toolbar toolbar, Activity activity, int res) {
//		View customView = LayoutInflater.from(activity).inflate(res, null);
//		this.initActionBar(toolbar, activity, customView);
//	}
	public void initActionBar(Toolbar toolbar, Activity activity) {
		if (toolbar == null) {
			throw new IllegalArgumentException("The ToolBar cannot be null");
		}
		if (!(activity instanceof ActionBarActivity)) {
			throw new IllegalArgumentException("The activity must be subclass of ActionBarActivity");
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}
