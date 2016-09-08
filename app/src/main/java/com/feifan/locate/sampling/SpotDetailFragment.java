package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.sampling.model.SampleSpotModel;
import com.feifan.locate.setting.SettingSingleFragment;
import com.feifan.locate.utils.DataUtils;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.planlib.layer.MarkPoint;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.locate.sampling.SampleSpotAdapter.OnSampleSpotClickListener;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotDetailFragment extends AbsSensorFragment implements OnSampleSpotClickListener {

    public static final String EXTRA_KEY_MARKPOINT = "markpoint";
    public static final String EXTRA_KEY_SAMPLESPOT_ID = "samplespot_id";

    // RequestCode
    private static final int REQUEST_CODE_PERIOD = 1;
    private static final int REQUEST_CODE_TOTAL = 2;

    // view
    private TextView mInfoV;

    // data
    private static final int LOADER_ID = 3; // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private SampleSpotAdapter mAdapter;
    private SampleSpotModel mCurrentModel;
    private int mCount = 0;
    private float mDegree;    //实时角度
    private MarkPoint mPoint;
    private transient boolean mConfirmed = false; // 是否确定方位，启动扫描

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
    private AtomicInteger mCurrentCount = new AtomicInteger(0);
    private List<SampleBeacon> mCache = new ArrayList<>();
    private static ExportHandler mHandler = new ExportHandler();
    private boolean isRunning = false;

    // sql
    private int mSampleSpotId = -1;

    private static class ExportHandler extends Handler {

        public static final String MSG_DATA_KEY_FILENAME = "filename";
        public static final int MSG_FINISH = 2;

        public ExportHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FINISH:
                    List<SampleBeacon> beacons = (List<SampleBeacon>) msg.obj;
                    String fileName = msg.getData().getString(MSG_DATA_KEY_FILENAME);
                    DataUtils.exportToCSV(Constants.EXPORT_FILE_TITLES, beacons, Constants.EXPORT_PATH_NAME + fileName);
                    beacons.clear();
                    break;
                default:
                    break;
            }
        }
    }

    public SpotDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSampleSpotId = getArguments().getInt(EXTRA_KEY_SAMPLESPOT_ID);
        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                if(mCurrentCount.get() == mCurrentModel.total) { // 采样结束
                    isRunning = false;
                    mScanManager.stop();

                    Message msg = Message.obtain(mHandler, ExportHandler.MSG_FINISH);
                    msg.what = ExportHandler.MSG_FINISH;
                    msg.obj = mCache;
                    msg.getData().putString(ExportHandler.MSG_DATA_KEY_FILENAME,
                            String.format("Loc(%.2f,%.2f,%.2f).csv",
                                    mPoint.getRealX(), mPoint.getRealY(), mCurrentModel.direction));
                    mHandler.sendMessage(msg);

                    SampleSpot.updateScan(getContext(), mCount, mCurrentCount.get(), mCurrentModel.id, SampleSpot.STATUS_FINISH);
                    return;
                }else {
                    mCache.addAll(beacons);
                    mCount += beacons.size();
                    SampleSpot.updateScan(getContext(), mCount, mCurrentCount.get() + 1, mCurrentModel.id, SampleSpot.STATUS_RUNNING);

                    // 保存附加信息
                    final double time = System.currentTimeMillis() / 1000d;
                    for(SampleBeacon beacon : beacons) {
                        beacon.loc_x = mPoint.getRealX();
                        beacon.loc_y = mPoint.getRealY();
                        beacon.loc_d = mCurrentModel.direction;
                        beacon.direction = mDegree;
                        beacon.time = time;
                        beacon.floor = 1;
                    }
                }
                mCurrentCount.getAndIncrement();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(isAdded()) {
            mScanManager.bind(getContext());
        }
        return inflater.inflate(R.layout.fragment_spot_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mPoint = args.getParcelable(EXTRA_KEY_MARKPOINT);
        final Intent data = new Intent();
        data.putExtra(EXTRA_KEY_MARKPOINT, mPoint);

        view.findViewById(R.id.spot_detail_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("Mark " + mPoint.toString() + " is removed!");
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        view.findViewById(R.id.spot_detail_period).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startConfig("采样周期", String.valueOf(1000), REQUEST_CODE_PERIOD);
            }
        });

        view.findViewById(R.id.spot_detail_sample_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning) {
                    Toast.makeText(getContext(), "don't do anything while running scan", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor = SampleSpot.findByStatus(getContext(), SampleSpot.STATUS_READY, mPoint.getId());
                if(cursor.getCount() == 0) { // 不存在就绪状态的样本点
                    // 添加样本点到数据库
                    mSampleSpotId = SampleSpot.add(getContext(), mPoint.getRawX(), mPoint.getRawY(), mDegree, mPoint.getId());
                    mConfirmed = false;
                }else {
                    Toast.makeText(getContext(), "there is already a ready sample point, handle it first", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });

        // 加载样本列表
        RecyclerView recyclerView = findView(R.id.spot_detail_sample_list);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 1)));
        mAdapter = new SampleSpotAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mInfoV = findView(R.id.spot_detail_info);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(isAdded()) {
            mScanManager.stop();
            mScanManager.unBind(getContext());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PERIOD:
                break;
            case REQUEST_CODE_TOTAL:
                if(resultCode == Activity.RESULT_OK) {
                    // 更新配置
                    SampleSpot.updateConfig(getContext(),
                            Integer.valueOf(data.getExtras().getString(SettingSingleFragment.EXTRA_KEY_RESULT)), mSampleSpotId);
                }
                break;
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.spot_detail_title_text;
    }


    @Override
    protected void onOrientationChanged(float radian) {
        // 更新样本列表
        mDegree = NumberUtils.degree(radian);
        //将新角度更新到数据库
        if(!mConfirmed && mInfoV != null) {
            mInfoV.setText(String.format("(%f, %f, %f)", mPoint.getRealX(), mPoint.getRealY(), mDegree));
        }
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    protected Uri getContentUri() {
        return SampleSpot.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }


    @Override
    public void onOperationClick(SampleSpotModel model) {
        if(!isRunning) {
            mCurrentModel = model;
        }
        switch (model.status) {
            case SampleSpot.STATUS_READY:
                // 重置
                mConfirmed = true;
                mCurrentModel.direction = mDegree;
                mCount = 0;
                mCurrentCount.set(0);

                // 更新数据库
                SampleSpot.update(getContext(), mCurrentModel.direction, mSampleSpotId);

                // 开启扫描
                mScanManager.start(getContext().getPackageName(), 1000);
                isRunning = true;
                LogUtils.i("start scan at " + model);
                break;
            case SampleSpot.STATUS_FINISH:
                String fileName = String.format("Loc(%.2f,%.2f,%.2f).csv",
                        mPoint.getRealX(), mPoint.getRealY(), mCurrentModel.direction);
                Toast.makeText(getContext(), "view the result in " + Constants.EXPORT_PATH_NAME + fileName,
                        Toast.LENGTH_LONG).show();
                break;
            case SampleSpot.STATUS_RUNNING:
                Toast.makeText(getContext(), "don't do anything while running scan", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("you are in an illegal state, restart me please");
        }
    }

    @Override
    public void onTimesClick(SampleSpotModel model) {
        switch (model.status) {
            case SampleSpot.STATUS_READY:
                startConfig("采样数量", String.valueOf(model.total), REQUEST_CODE_TOTAL);
                break;
            default:
                Toast.makeText(getContext(), "sampling completed with " + model.times + " times", Toast.LENGTH_SHORT).show();
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
}
