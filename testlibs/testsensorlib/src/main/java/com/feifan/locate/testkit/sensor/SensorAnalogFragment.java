package com.feifan.locate.testkit.sensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.widget.BaseFragment;
import com.onenine.testsensorlib.R;

/**
 * Created by xuchunlei on 2016/12/20.
 */

public class SensorAnalogFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sensor_analog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView map = findView(R.id.map);
        Log.e("SensorAnalogFragment", "map=" + map);
    }

    @Override
    protected int getTitleResource() {
        return R.string.title_sensor_analog;
    }
}
