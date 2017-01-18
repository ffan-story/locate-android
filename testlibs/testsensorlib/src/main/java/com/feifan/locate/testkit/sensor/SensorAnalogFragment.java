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

public class SensorAnalogFragment extends BaseFragment implements View.OnClickListener {

    private MapView mMapView;
    private boolean isStarted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sensor_analog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = findView(R.id.map);
        view.findViewById(R.id.action).setOnClickListener(this);

    }

    @Override
    protected int getTitleResource() {
        return R.string.sensor_analog_title;
    }

    @Override
    public void onClick(View v) {
        if(isStarted) {
            mMapView.stopDraw();
            isStarted = false;
        }else {
            mMapView.startDraw();
            isStarted = true;
        }
    }
}
