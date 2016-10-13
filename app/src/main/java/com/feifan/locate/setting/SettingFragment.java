package com.feifan.locate.setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.setting.sensor.SensorGridFragment;
import com.feifan.locate.setting.sensor.SensorScanFragment;
import com.feifan.locate.widget.ui.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements OnClickListener {


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

        view.findViewById(R.id.setting_sensor_detection).setOnClickListener(this);
        view.findViewById(R.id.setting_sensor_sampling).setOnClickListener(this);
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
        }

    }
}
