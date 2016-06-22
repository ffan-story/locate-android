package com.libs.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.libs.ui.R;
import com.libs.ui.views.LoadView;
import com.wanda.logger.toolbox.SystemUtil;


public class BaseProgressDialog extends BaseDialogFragment {

	private String mDesc = "";
	public static final String PROGRESSDIALOG_DESC = "progress_dialog_desc";

	public BaseProgressDialog() {

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = super.onCreateDialog(savedInstanceState);
		d.setCanceledOnTouchOutside(false);
        Window window = d.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.CENTER);
		initParams();
		return d;
	}

	private void initParams(){
		Bundle bundle = getArguments();
		if(bundle != null){
			String desc = bundle.getString(PROGRESSDIALOG_DESC);
			if(!TextUtils.isEmpty(desc)){
				mDesc = desc;
			}
		}
	}

	@Override
	protected int getbackgroundBg() {
		return R.drawable.base_process_bg;
	}
//
    @Override
    protected int[] getDialogSize() {
        int size = SystemUtil.dipToPx(mContext, (int) (mContext.getResources().getDimension(R.dimen.common_process_layout)));
        return new int[] { size,size};
    }

    @Override
	protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.common_process_dialog_layout, container, true);
		setProcessDesc(view,mDesc);
		return view;
	}

    /**
     * set desc
     * @param desc
     */
	public void setProcessDesc(View view,String desc) {
        LoadView loadView = (LoadView)view.findViewById(R.id.loadview);
        loadView.setDesc(desc);
	}
}
