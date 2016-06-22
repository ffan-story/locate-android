package com.libs.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.libs.ui.R;


public class BaseDialogFragment extends DialogFragment {

	public Bundle mBundle = null;
	protected Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_NoTitleBar_Fullscreen);
		mBundle = getArguments();
		mContext = getActivity();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = super.onCreateDialog(savedInstanceState);
		d.setCanceledOnTouchOutside(true);
		Window window = d.getWindow();
		window.getAttributes().dimAmount = getDimAmount();
		window.addFlags(getWindowsFlag());
		window.setGravity(getGravity());
		int[] size = getDialogSize();

		if (size != null) {
			window.setLayout(size[0], size[1]);
		}

		if (getbackgroundBg() > 0) {
			window.setBackgroundDrawableResource(getbackgroundBg());
		} else {
			window.setBackgroundDrawableResource(R.drawable.base_transparent_bg);
		}
		
		return d;
	}

	protected int getGravity(){
		return Gravity.CENTER;
	}

	protected int getWindowsFlag(){
		return WindowManager.LayoutParams.FLAG_DIM_BEHIND;
	}

	public float getDimAmount(){
		return 0.70f;
	}

	protected int getbackgroundBg() {
		return R.drawable.base_fragment_bg;
	}

	/**
	 * set size of dialog
	 * 
	 * @return
	 */
	protected int[] getDialogSize() {
		return new int[] { WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT };
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = getLayoutView(inflater, container, savedInstanceState);
		if (view != null) {
			return view;
		} else {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	public void dismissSelf(int id) {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.dismissDialog(id);
		}
	}

	public void showSelf(String tag) {
		if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
			this.show(getActivity().getSupportFragmentManager(), tag);
		}
	}
}
