package com.feifan.locate.setting.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.recycler.ContentGridAdapter;
import com.feifan.locate.widget.recycler.IContentModel;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.locate.widget.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchunlei on 16/10/8.
 */

public class SensorGridFragment extends BaseFragment {

    private SensorManager mSensorManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = findView(R.id.sensor_grid);
        ContentGridAdapter<SensorModel> adapter = new ContentGridAdapter<>(SensorModel.class);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 2)));

        List<SensorModel> data = new ArrayList<>();
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if(sensors != null && !sensors.isEmpty()) {
            for (Sensor sensor : sensors) {
                data.add(new SensorModel(sensor));
            }
            adapter.setData(data);
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.setting_sensor_detail_title;
    }

    public static class SensorModel implements IContentModel {

        private String name;
        private String content;

        public SensorModel(Sensor sensor) {
            name = sensor.getName();
            StringBuilder builder = new StringBuilder();

            builder.append("厂商:");
            builder.append(sensor.getVendor());
            builder.append("\n");

            builder.append("版本:");
            builder.append(sensor.getVersion());
            builder.append("\n");

            builder.append("最大采集频率(Hz):");
            builder.append(1000000f / sensor.getMinDelay());
            builder.append("\n");

            builder.append("测量范围:");
            builder.append(sensor.getMaximumRange());
            builder.append("\n");

            builder.append("分辨率:");
            builder.append(sensor.getResolution());
            builder.append("\n");

            content = builder.toString();

        }

        @Override
        public String getTitle() {
            return name;
        }

        @Override
        public String getContent() {
            return content;
        }
    }
}
