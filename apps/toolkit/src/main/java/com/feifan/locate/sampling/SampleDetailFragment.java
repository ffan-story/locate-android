package com.feifan.locate.sampling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.feifan.sensorlib.processor.OrientationListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianying on 2016/10/12.
 */

public abstract class SampleDetailFragment<M extends SampleModel> extends AbsLoaderFragment
        implements OrientationListener, OnSampleClickListener<M> {

    public static final String EXTRA_KEY_WORK = "work";
    public static final String EXTRA_KEY_FLOOR = "floor";

    // view
    private TextView mInfoV;

    // data
    protected Parcelable mWork; // 采样工作对象, 目前为采集点或采集路线
    protected SampleAdapter<M> mAdapter;
    protected int mFloor;
    private String mBuilding;

    // scan
    protected ScanManager mScanManager = ScanManager.getInstance();
    protected List<SampleBeacon> mCache = new ArrayList<>();
    protected float mAzimuth; // 磁方位角度
    private transient boolean mConfirmed = false; // 是否确定方位，启动扫描
    private OrientationManager mOrientationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFloor = getArguments().getInt(EXTRA_KEY_FLOOR);

        mOrientationManager = OrientationManager.getInstance(getContext());
        mBuilding = getArguments().getString(Constants.EXTRA_KEY_BUILDING);
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

        // 初始化可通用的视图
        Bundle args = getArguments();
        mWork = args.getParcelable(EXTRA_KEY_WORK);
        final Intent data = new Intent();
        data.putExtra(EXTRA_KEY_WORK, mWork);

        view.findViewById(R.id.sample_detail_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isRunning) {
//                    Toast.makeText(getContext(), "remove is forbidden while scanning", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        // 初始化需要定制的视图
        onCustomizeView();
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
    public void onOrientationChanged(float[] radian) {
        // 更新样本列表
        mAzimuth = NumberUtils.degree(radian[0]);
        //将新角度更新到数据库
        if(!mConfirmed && mInfoV != null) {
            // TODO make a protected method to update me
            mInfoV.setText("N" + mAzimuth);
        }
    }

    /**
     * 保存数据到文件
     * @param titles
     * @param fileName
     */
    protected void save(String[] titles, String fileName) {
//        DataFixer.FixBeacons(mBuilding, mCache);
        DataUtils.exportToCSV(titles, mCache, mBuilding + fileName);
    }

    /**
     * 初始化视图
     */
    protected void onCustomizeView() {

    }

    /**
     * 创建beacon数据的通知对象
     * <pre>
     *     用来处理扫描得到的beacon数据
     * </pre>
     * @return
     */
    protected abstract BeaconNotifier onCreateNotifier();

    /**
     * 锁定方向
     *
     * @param lock true锁定,false解锁
     */
    protected void lockOrientation(boolean lock) {
        mConfirmed = lock;
    }
}
