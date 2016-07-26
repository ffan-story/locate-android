package com.libs.ui.fragments;

import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public abstract  class ActionProviderDelegate { 

	public void prepareSubMenu(SubMenu subMenu){
		if(subMenu.size() >0){
			subMenu.clear();
		}
	}

	/**
	 * if show in collasp should return null.because if getActionView not null ,the method of prepareSubmenu cannot be called
	 * @return
	 */
	public View getActionView(){
		return null;
	}
	
	public boolean hasSubMenu(){
		return true;
	}
	
	public boolean isPerformDefaultAction(){
		return true;
	}
	
	protected void performSubMenuClick(MenuItem menuitem) {
		 
	}
}
