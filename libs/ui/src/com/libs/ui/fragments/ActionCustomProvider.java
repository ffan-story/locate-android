package com.libs.ui.fragments;


import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;

public class ActionCustomProvider extends ActionProvider {

	private ActionProviderDelegate mActionProviderDelegate;
	public ActionCustomProvider(Context context,  ActionProviderDelegate delegate) {
		super(context);
		if(delegate == null){
			throw new IllegalArgumentException("the argument of delagate cannot be null");   
		}
		mActionProviderDelegate = delegate;
	}

	@Override
	public View onCreateActionView() {
		return mActionProviderDelegate.getActionView();
	}

	@Override
	public boolean onPerformDefaultAction() {
		// TODO Auto-generated method stub
		return mActionProviderDelegate.isPerformDefaultAction();
	}
	
	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		super.onPrepareSubMenu(subMenu);
		mActionProviderDelegate.prepareSubMenu(subMenu);
	}
	
	@Override
	public boolean hasSubMenu() {
		//告诉系统，这个有子菜单
		return mActionProviderDelegate.hasSubMenu();
	}
}
