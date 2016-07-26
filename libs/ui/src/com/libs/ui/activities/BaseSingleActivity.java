package com.libs.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.libs.ui.fragments.ActionCustomProvider;
import com.libs.ui.fragments.ActionProviderDelegate;

public class BaseSingleActivity extends BaseActivity {

	private Toolbar mToolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View actionbarView = onActionBarCustomViewCreated();
		if(actionbarView != null){
			getSupportActionBar().setCustomView(actionbarView);
		}
		mToolbar = buildToolbar();
		if(mToolbar != null){
			setSupportActionBar(mToolbar);
		}
	}
	
	protected Toolbar buildToolbar() {
		 return null;
	}
	
	
	protected void  setTitleName(String title) {
		if(mToolbar != null && TextUtils.isEmpty(title)){
			mToolbar.setTitle(title);
		}
	}
	
	protected void setNaviIcon(int res) {
		if(mToolbar != null && res > 0){
			mToolbar.setNavigationIcon(res);
		}
	}
	protected void setNaviIcon(Drawable darwable) {
		if(mToolbar != null && darwable != null){
			mToolbar.setNavigationIcon(darwable);
		}
	}
	
	/**
	 * you can add custom view at this method .like spinner , image
	 * @return
	 */
	protected View onActionBarCustomViewCreated() { 
		return null;
	}
	
	/**
	 * 
	 * @param menu
	 * @param title
	 * @param res 
	 * @param actionnemu  one of option of 
	 *  MenuItemCompat.SHOW_AS_ACTION_ALWAYS;
		MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
		MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
		MenuItemCompat.SHOW_AS_ACTION_NEVER;
		MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;
		
		@param delegate  delegate of provider
	 */
	protected void buildCollaspMenuItem(Menu menu,String title, int res,int actionnemu,ActionProviderDelegate delegate) {
		MenuItem setItem = menu.add(title).setIcon(res);
		MenuItemCompat.setActionProvider(setItem, new ActionCustomProvider(this,delegate));
		MenuItemCompat.setShowAsAction(setItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mToolbar = null;
	}
	
	public interface onEventListener{
		public void onEventPosted(Object obj);
	}
}

