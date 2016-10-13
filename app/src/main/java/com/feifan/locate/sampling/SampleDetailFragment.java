package com.feifan.locate.sampling;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.sampling.model.SampleModel;
import com.feifan.locate.sampling.SampleAdapter.OnSampleClickListener;
import com.feifan.locate.utils.DataUtils;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;
import com.feifan.sensorlib.OrientationManager;
import com.feifan.sensorlib.OrientationManager.OrientationListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianying on 2016/10/12.
 */

public abstract class SampleDetailFragment<M extends SampleModel> extends AbsLoaderFragment
        implements OrientationListener, OnSampleClickListener<M> {

    public static final String EXTRA_KEY_SAMPLE_KEY = "sampleKey";
    public static final String EXTRA_KEY_FLOOR = "floor";

    // view
    private TextView mInfoV;

    // data
    protected SampleAdapter<M> mAdapter;
    protected int mFloor;


    // scan
    protected ScanManager mScanManager = ScanManager.getInstance();
    protected List<SampleBeacon> mCache = new ArrayList<>();
    protected float mAzimuth; // 磁方位角度
    protected transient boolean mConfirmed = false; // 是否确定方位，启动扫描
    private OrientationManager mOrientationManager;

    // export
    private static ExportHandler mHandler;
    private static class ExportHandler extends Handler {

        public static final String MSG_DATA_KEY_FILE_NAME = "name";
        public static final String MSG_DATA_KEY_FILE_TITLES = "titles";
        public static final int MSG_FINISH = 2;

        private String building;

        public ExportHandler(String building) {
            this.building = building;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FINISH:
                    List<SampleBeacon> beacons = (List<SampleBeacon>) msg.obj;
                    DataFixer.FixBeacons(building, beacons);
                    String fileName = msg.getData().getString(MSG_DATA_KEY_FILE_NAME);
                    String[] titles = msg.getData().getStringArray(MSG_DATA_KEY_FILE_TITLES);
                    DataUtils.exportToCSV(titles, beacons, fileName);
                    beacons.clear();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFloor = getArguments().getInt(EXTRA_KEY_FLOOR);

        mOrientationManager = OrientationManager.getInstance(getContext());
        String building = getArguments().getString(Constants.EXTRA_KEY_BUILDING);
        mHandler = new ExportHandler(building);
        mScanManager.setNotifier(onCreateNotifier());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(isAdded()) {
            mScanManager.bind(getContext());
        }
        return inflater.inflate(R.layout.fragment_sample_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 加载样本列表
        RecyclerView recyclerView = findView(R.id.sample_detail_sample_list);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 1)));
        mAdapter = getAdapter();
        recyclerView.setAdapter(mAdapter);

        mInfoV = findView(R.id.sample_detail_info);
        mOrientationManager.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrientationManager.unRegister(this);
        if(isAdded()) {
            mScanManager.stop();
            mScanManager.unBind(getContext());
        }
    }

    @Override
    public void onOrientationChanged(float radian) {
        // 更新样本列表
        mAzimuth = NumberUtils.degree(radian);
        //将新角度更新到数据库
        if(!mConfirmed && mInfoV != null) {
            // TODO make a protected method to update me
            mInfoV.setText("N" + mAzimuth);
        }
    }

    /**
     * 创建beacon数据的通知对象
     * <pre>
     *     用来处理扫描得到的beacon数据
     * </pre>
     * @return
     */
    protected abstract BeaconNotifier onCreateNotifier();

    private void showValue(String value) {
        LogUtils.e(value);
    }
}
