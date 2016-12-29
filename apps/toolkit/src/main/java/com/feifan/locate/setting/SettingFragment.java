package com.feifan.locate.setting;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.window.DebugWindow;
import com.feifan.locate.Constants;
import com.feifan.locate.LocatePreferences;
import com.feifan.locate.MainActivity;
import com.feifan.locate.R;
import com.feifan.locate.baseuilib.ToolbarActivity;
import com.feifan.locate.locating.config.LocatingConfig;
import com.feifan.locate.setting.sensor.SensorGridFragment;
import com.feifan.locate.setting.sensor.SensorScanFragment;
import com.feifan.locate.utils.DataUtils;
import com.feifan.locate.widget.settingwork.TextItemView;
import com.feifan.locate.widget.BaseFragment;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements OnClickListener {

    // RequestCode
    private static final int REQUEST_CODE_LOCATE_ADDR = 1;
    private static final int REQUEST_CODE_LOCATE_PORT = 2;

    private TextItemView mServerAddrV;
    private TextItemView mServerPortV;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mServerAddrV = findView(R.id.setting_server_address);
        mServerAddrV.setSubtitle(LocatePreferences.getInstance().getLocateAddr());
        mServerPortV = findView(R.id.setting_server_port);
        mServerPortV.setSubtitle(String.valueOf(LocatePreferences.getInstance().getLocatePort()));

        view.findViewById(R.id.setting_sensor_detection).setOnClickListener(this);
        view.findViewById(R.id.setting_sensor_sampling).setOnClickListener(this);
        mServerAddrV.setOnClickListener(this);
        mServerPortV.setOnClickListener(this);

        view.findViewById(R.id.setting_reset).setOnClickListener(this);
        view.findViewById(R.id.setting_test).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_LOCATE_ADDR:
                if(resultCode == Activity.RESULT_OK) {
                    String value = data.getExtras().getString(SettingSingleFragment.EXTRA_KEY_RESULT);
                    LocatePreferences.getInstance().
                            setLocateAddr(value);
                    mServerAddrV.setSubtitle(value);
                }
                break;
            case REQUEST_CODE_LOCATE_PORT:
                if(resultCode == Activity.RESULT_OK) {
                    int value = data.getExtras().getInt(SettingSingleFragment.EXTRA_KEY_RESULT);
                    LocatePreferences.getInstance().
                            setLocatePort(value);
                    mServerPortV.setSubtitle(String.valueOf(value));
                }
                break;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ToolbarActivity.class);
        switch (v.getId()) {
            case R.id.setting_sensor_detection:

                intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SensorGridFragment.class.getName());
                startActivity(intent);
                break;
            case R.id.setting_sensor_sampling:
                intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SensorScanFragment.class.getName());
                startActivity(intent);
                break;
            case R.id.setting_server_address:
                String curAddr = LocatePreferences.getInstance().getLocateAddr();
                startConfig(getString(R.string.setting_server_address_title), curAddr, REQUEST_CODE_LOCATE_ADDR);
                break;
            case R.id.setting_server_port:
                int curPort = LocatePreferences.getInstance().getLocatePort();
                startConfig(getString(R.string.setting_server_port_title), String.valueOf(curPort), REQUEST_CODE_LOCATE_PORT);
                break;
            case R.id.setting_reset:
                LocatePreferences.getInstance().clear();
                LocatingConfig.getInstance().clear();
                getContext().deleteDatabase("sample.db");
                DataUtils.deleteFile(getContext().getCacheDir());
                DataUtils.deleteFile(getContext().getExternalCacheDir());
                DataUtils.deleteFile(new File(Constants.EXPORT_ROOT_PATH_NAME));
                restartApp();
                break;
            case R.id.setting_test:
                LogUtils.e("what do you want to test???");
                DebugWindow.get().logE("test");
                break;
            default:
                throw new IllegalStateException("the setting is not implemented");
        }

    }

    /**
     * 启动配置界面
     * @param title
     * @param curValue
     * @param ReqCode
     */
    private void startConfig(String title, String curValue, int ReqCode) {
        Intent intent = new Intent(getContext(), ToolbarActivity.class);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SettingSingleFragment.class.getName());
        Bundle args = new Bundle();
        args.putString(SettingSingleFragment.EXTRA_KEY_TITLE, title);
        args.putString(SettingSingleFragment.EXTRA_KEY_VALUE, curValue);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
        startActivityForResult(intent, ReqCode);
    }

    private void restartApp() {

        Intent mStartActivity = new Intent(getContext(), MainActivity.class);
        int mPendingIntentId = 707;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
        Runtime.getRuntime().exit(0);
    }
}
