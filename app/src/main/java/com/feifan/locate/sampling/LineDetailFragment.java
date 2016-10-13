package com.feifan.locate.sampling;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.SampleLine;
import com.feifan.locate.provider.LocateData.SampleColumns;
import com.feifan.locate.sampling.model.SampleLineModel;
import com.feifan.locate.sampling.model.SampleModel;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;

/**
 * Created by xuchunlei on 16/10/12.
 */

public class LineDetailFragment extends SampleDetailFragment<SampleLineModel> {

    // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private static final int LOADER_ID = 6;

    // data
    private SampleLineModel mCurrentModel;

    // scan
    private int mTotal = 0; // 当前扫描样本数目
    private long mStartTime = 0l; // 当前消耗时间
    private int mGroup = 0; // 样本的分组序号

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // FIXME temp for test
//        ContentResolver resolver = getContext().getContentResolver();
//
//        // workline
//        ContentValues values = new ContentValues();
//        values.put(LocateData.WorkLine._ID, 1);
//        values.put(LocateData.WorkLine.SPOT_ONE, 1);
//        values.put(LocateData.WorkLine.SPOT_TWO, 2);
//        values.put(LocateData.WorkLine.ZONE, 1);
//        resolver.insert(LocateData.WorkLine.CONTENT_URI, values);
//
//        // sampleline
//        values.clear();
//        values.put(SampleLine._ID, 1);
//        values.put(SampleLine._NAME, "1-2");
//        values.put(SampleLine.WORKLINE, 1);
//        resolver.insert(SampleLine.CONTENT_URI, values);
    }

    @Override
    protected BeaconNotifier onCreateNotifier() {
        return new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                mCache.addAll(beacons);
                mTotal += beacons.size();
                final long consumed = SystemClock.elapsedRealtime() - mStartTime;
                SampleLine.updateScan(getContext(), mTotal, String.valueOf(consumed / 1000f) + "s" , 1);

                // 保存附加信息
                for(SampleBeacon beacon : beacons) {
//                    beacon.loc_x = mPoint.getRealX();
//                    beacon.loc_y = mPoint.getRealY();
//                    beacon.loc_d = mCurrentModel.direction;
                    beacon.direction = mAzimuth;
                    beacon.floor = mFloor;
                    beacon.group = ++mGroup;
                }
            }
        };
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    protected Uri getContentUri() {
        return SampleLine.CONTENT_URI;
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        if(mAdapter == null) {
            mAdapter = new SampleAdapter(SampleLineModel.class, this);
        }
        return (A)mAdapter;
    }

    @Override
    public void onOperationClick(SampleLineModel model) {

        mCurrentModel = model;

        switch (model.status) {
            case SampleColumns.STATUS_READY:
                // 重置
                mConfirmed = true;
                mCurrentModel.direction = mAzimuth;
                mGroup = 0;
                // 更新数据库
                SampleLine.updateStatus(getContext(), SampleColumns.STATUS_RUNNING, mCurrentModel.direction, 1);

                // 开启扫描
                mScanManager.start(getContext().getPackageName(), 1000);
                mStartTime = SystemClock.elapsedRealtime();
                LogUtils.i("start scan at " + model);
                break;
            case SampleColumns.STATUS_RUNNING:
                mScanManager.stop();
                SampleLine.updateStatus(getContext(), SampleColumns.STATUS_FINISH, mCurrentModel.direction, 1);
                mConfirmed = false;
                LogUtils.i("stop scan at " + model);
                break;
        }
    }
}
