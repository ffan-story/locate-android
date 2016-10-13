package com.feifan.locate.setting.sensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.widget.ui.BaseFragment;

/**
 * Created by xuchunlei on 16/10/12.
 */

public class SensorScanFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_sensor_scan, container, false);
    }
}
