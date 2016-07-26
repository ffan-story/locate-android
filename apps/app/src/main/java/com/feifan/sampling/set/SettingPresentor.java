package com.feifan.sampling.set;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.feifan.sampling.Constants;
import com.libs.base.model.BaseBean;
import com.libs.base.mvp.IPresentor;
import com.libs.base.mvp.MvpAnnotation;
import com.libs.utils.PrefUtil;

import java.lang.ref.WeakReference;

/**
 * Created by mengmeng on 16/5/24.
 */
public class SettingPresentor extends IPresentor implements SettingDialog.SettingBtnCallBack{
    private SettingDialog mDialog;
    private WeakReference<Fragment> mFragment;
    public SettingPresentor(Context context,Fragment fragment) {
        super(context);
        mFragment = new WeakReference<Fragment>(fragment);
    }

    @Override
    public void bindData(BaseBean baseBean) {

    }

    @Override
    protected void onRouterBind() {
        super.onRouterBind();
//        mMvpRouter.registerPresentRouter(SettingEvent.SHOW_FILE_DIALOG,"showDialog");
        mMvpRouter.registers(this);
    }

    @MvpAnnotation(event = SettingPresentorEvent.SHOW_FILE_DIALOG)
    public void showDialog(int type){
        if(mFragment != null && mFragment.get() != null) {
            if (mDialog == null) {
                mDialog = new SettingDialog(this);
            }
            Bundle bundle = new Bundle();
            bundle.putInt(SettingDialog.TYPE,type);
            mDialog.setArguments(bundle);
            mDialog.setTargetFragment(mFragment.get(), 0);
            mDialog.show(mFragment.get().getFragmentManager(), mFragment.get().getClass().getName());
        }
    }

    @Override
    public void onConfirmBtn(String str, int type) {
        if(type == SettingDialog.FILE_TYPE){
            mMvpRouter.sendEvent(SettingEvent.CHANGE_FILE_PATH_EVENT,str);
            PrefUtil.putString(mContext, Constants.SHAREPREFERENCE.LOG_FILE_PATH,str);
//            ((SampleApplication)mContext.getApplicationContext()).refreshLogConfig();
        }else if (type == SettingDialog.TIME_TYPE){
            mMvpRouter.sendEvent(SettingEvent.CHANGE_BEACON_RECYCLE_EVENT,str);
            if(TextUtils.isDigitsOnly(str)) {
                PrefUtil.putInt(mContext, Constants.SHAREPREFERENCE.RECYCLE_TIME_INTERVAL, Integer.valueOf(str));
            }
        }else if(type == SettingDialog.SCAN_TYPE){
            mMvpRouter.sendEvent(SettingEvent.CHANGE_SCAN_POINT_EVENT,str);
            if(TextUtils.isDigitsOnly(str)) {
                PrefUtil.putInt(mContext, Constants.SHAREPREFERENCE.SCAN_MAX_COUNT, Integer.valueOf(str));
            }
        }
    }
    public interface SettingPresentorEvent{
        public static final String SHOW_FILE_DIALOG = "show_file_dialog";
    }
}
