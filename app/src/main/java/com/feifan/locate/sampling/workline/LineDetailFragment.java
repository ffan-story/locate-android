package com.feifan.locate.sampling.workline;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.provider.LocateData.SampleLine;
import com.feifan.locate.provider.Columns.SampleColumns;
import com.feifan.locate.provider.ProviderHelper;
import com.feifan.locate.sampling.SampleAdapter;
import com.feifan.locate.sampling.SampleDetailFragment;
import com.feifan.locate.sampling.model.SampleLineModel;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;

/**
 * Created by xuchunlei on 16/10/12.
 */

public class LineDetailFragment extends SampleDetailFragment<SampleLineModel> {

    private static final int REQUEST_CODE_NAME = 1;

    // data
    private SampleLineModel mCurrentModel;
    private LineInfo mLine;

    // scan
    private int mTotal = 0; // 当前扫描样本数目
    private long mStartTime = 0l; // 采集开始时间
    private long mEndTime = 0; // 采集结束时间
    private int mGroup = 0; // 样本的分组序号

    // config
    private Intent mCfgIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLine = getArguments().getParcelable(EXTRA_KEY_WORK);
        mCfgIntent = new Intent(getContext(), ToolbarActivity.class);
        Bundle args = new Bundle();
        mCfgIntent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
        ProviderHelper.runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                doAddSample(mLine.pointOneId + "-" + mLine.pointTwoId);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.sample_detail_sample_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCfgIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, LinePickerFragment.class.getName());
                Bundle args = mCfgIntent.getBundleExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS);
                args.putStringArray(LinePickerFragment.EXTRA_KEY_LINES, new String[]{
                        mLine.pointOneId + "-" + mLine.pointTwoId,
                        mLine.pointTwoId + "-" + mLine.pointOneId
                });
                startActivityForResult(mCfgIntent, REQUEST_CODE_NAME);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_NAME:
                if(resultCode == Activity.RESULT_OK) {
                    final String name = data.getStringExtra(LinePickerFragment.EXTRA_KEY_RESULT);
                    ProviderHelper.runOnWorkerThread(new Runnable() {
                        @Override
                        public void run() {
                            doAddSample(name);
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onCustomizeView() {
        super.onCustomizeView();
        TextView removeV = findView(R.id.sample_detail_remove);
        removeV.setText(R.string.line_detail_remove_text);
    }

    @Override
    protected BeaconNotifier onCreateNotifier() {
        return new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                if(isAdded()) {
                    LogUtils.e("receive becon data at " + Thread.currentThread().getId());
                    mCache.addAll(beacons);
                    mTotal += beacons.size();
                    final long consumed = SystemClock.elapsedRealtime() - mStartTime;
                    SampleLine.updateScan(getContext(), mTotal, String.valueOf(consumed / 1000f) + "s" , mCurrentModel.id);
                    final int group = ++mGroup;
                    // 保存附加信息
                    for(SampleBeacon beacon : beacons) {
                        beacon.direction = mAzimuth;
                        beacon.group = group;
                    }
                }

            }
        };
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_SAMPLELINE;
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
                // 设置
                lockOrientation(true);
                mCurrentModel.direction = mAzimuth;
                mGroup = 0;
                mTotal = 0;
                mCache.clear();
                // 更新数据库
                SampleLine.updateStatus(getContext(), SampleColumns.STATUS_RUNNING,
                        mCurrentModel.direction, mCurrentModel.id);
                // 开启扫描
                mScanManager.start(getContext().getPackageName(), Constants.SCAN_DEFAULT_INTERVAL);
                mStartTime = SystemClock.elapsedRealtime();
                LogUtils.i("start scan at " + model);
                break;
            case SampleColumns.STATUS_RUNNING:
                mScanManager.stop();
                mEndTime = SystemClock.elapsedRealtime();
                SampleLine.updateStatus(getContext(), SampleColumns.STATUS_FINISH,
                        mCurrentModel.direction, mCurrentModel.id);
                lockOrientation(false);
                LogUtils.i("stop scan at " + model);

                // 保存
                String fileName = getFileNameBySample(mCurrentModel);
                save(Constants.EXPORT_FILE_TITLES_LINE, fileName);
                LogUtils.e("save becon data at " + Thread.currentThread().getId());
                break;
            case SampleColumns.STATUS_FINISH:
                Toast.makeText(getContext(), "view the result in " + Constants.getExportFilePath() + getFileNameBySample(mCurrentModel),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                throw new IllegalStateException("you are in a invalid status, please restart me");
        }
    }

    private String getFileNameBySample(SampleLineModel model) {
        StringBuilder result = new StringBuilder("(");
        int splitIndex = model.name.indexOf('-');
        int beginId = Integer.valueOf(model.name.substring(0, splitIndex));
        int endId = Integer.valueOf(model.name.substring(splitIndex + 1));

        if(mLine.pointOneId == beginId) {
            result.append(mLine.pointOneX + "," + mLine.pointOneY + ")"); // 起始点信息
            result.append("_" + mStartTime); // 起始时间戳
            result.append("_(" + mLine.pointTwoX + "," + mLine.pointTwoY + ")"); // 结束点信息
            result.append("_" + mEndTime); // 结束时间戳
            result.append("_" + mFloor);
            result.append("_" + mCurrentModel.direction);
        } else {
            result.append(mLine.pointTwoX + "," + mLine.pointTwoY + ")"); // 起始点信息
            result.append("_" + mStartTime); // 起始时间戳
            result.append("_(" + mLine.pointOneX + "," + mLine.pointOneY + ")"); // 结束点信息
            result.append("_" + mEndTime); // 结束时间戳
            result.append("_" + mFloor);
            result.append("_" + mCurrentModel.direction);
        }
        result.append(".csv");
        return result.toString();
    }

    private void doAddSample(String name) {
        Cursor cursor = SampleLine.findByStatus(getContext(), SampleColumns.STATUS_READY, mLine.id);
        if(cursor.getCount() == 0) { // 不存在就绪状态的样本路线
            // 添加样本点到数据库
            SampleLine.add(getContext(), name, mLine.id);
        }else {
            if(cursor.getCount() > 1) {
                throw new IllegalStateException("error!there is more than 1 ready sample spot!");
            }else {
                Toast.makeText(getContext(), "an unhandle sample line existed", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }
}
