package com.feifan.sampling.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.libs.base.mvp.IView;
import com.libs.base.mvp.MvpAnnotation;
import com.libs.utils.PrefUtil;

/**
 * Created by mengmeng on 16/5/24.
 */
public class SettingView extends IView implements View.OnClickListener{

    public SettingView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super(inflater, container, savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.set_setting_fragment_layout;
    }

    private void initView(){
        if(mRootView != null){
            TextView fileView = (TextView) mRootView.findViewById(R.id.file_save);
            TextView beaconView = (TextView) mRootView.findViewById(R.id.beacon_interval);
            TextView scanView = (TextView) mRootView.findViewById(R.id.scan_save);
            mRootView.findViewById(R.id.change_time).setOnClickListener(this);
            mRootView.findViewById(R.id.change_file).setOnClickListener(this);
            mRootView.findViewById(R.id.scan_point).setOnClickListener(this);
            String path = PrefUtil.getString(getContentView().getContext(), Constants.SHAREPREFERENCE.LOG_FILE_PATH,"indoor");
            fileView.setText(path);
            long timeinterval = PrefUtil.getLong(getContentView().getContext(), Constants.SHAREPREFERENCE.RECYCLE_TIME_INTERVAL,1100);
            beaconView.setText(timeinterval+" ms");
            String str = PrefUtil.getString(getContentView().getContext(), Constants.SHAREPREFERENCE.SCAN_MAX_COUNT,Constants.DEFAULT_SCAN_SAMPLES);
            scanView.setText(str);
        }
    }


    @Override
    protected void onRouterBind() {
        super.onRouterBind();
//        mMvpRouter.registerViewRouter(SettingEvent.CHANGE_FILE_PATH_EVENT,"setFilePath");
//        mMvpRouter.registerViewRouter(SettingEvent.CHANGE_BEACON_RECYCLE_EVENT,"setBeaconRecycle");
        mMvpRouter.registers(this);
    }

    @MvpAnnotation(event = SettingViewEvent.CHANGE_FILE_PATH_EVENT)
    public void setFilePath(String filepath){
        if(!TextUtils.isEmpty(filepath)){
            TextView textView = (TextView) mRootView.findViewById(R.id.file_save);
            textView.setText(filepath);
        }
    }

    @MvpAnnotation(event = SettingViewEvent.CHANGE_BEACON_RECYCLE_EVENT)
    public void setBeaconRecycle(String timeinterval){
        if(!TextUtils.isEmpty(timeinterval)){
            TextView textView = (TextView) mRootView.findViewById(R.id.beacon_interval);
            textView.setText(timeinterval+" ms");
        }
    }

    @MvpAnnotation(event = SettingViewEvent.CHANGE_SCAN_POINT_EVENT)
    public void setScanMaxPoint(String str){
        if(!TextUtils.isEmpty(str)){
            TextView textView = (TextView) mRootView.findViewById(R.id.scan_save);
            textView.setText(str);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.change_time:
                Object obj = SettingDialog.TIME_TYPE;
                mMvpRouter.sendEvent(SettingEvent.SHOW_FILE_DIALOG,obj);
                break;
            case R.id.change_file:
                obj = SettingDialog.FILE_TYPE;
                mMvpRouter.sendEvent(SettingEvent.SHOW_FILE_DIALOG,obj);
                break;
            case R.id.scan_point:
                obj = SettingDialog.SCAN_TYPE;
                mMvpRouter.sendEvent(SettingEvent.SHOW_FILE_DIALOG,obj);
                break;
        }
    }

    public interface SettingViewEvent{
        public static final String CHANGE_FILE_PATH_EVENT = "change_file_path_event";
        public static final String CHANGE_BEACON_RECYCLE_EVENT = "change_beacon_recycle_event";
        public static final String CHANGE_SCAN_POINT_EVENT = "change_scan_point_event";
    }
}
