package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.plan.MarkLayer;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.scanlib.IScanService;
import com.feifan.scanlib.ScanService;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotDetailFragment extends AbsSensorFragment {

    public static final String EXTRA_KEY_MARKPOINT = "markpoint";

    // data
    private static final int LOADER_ID = 3; //// 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private SampleSpotAdapter mAdapter;

    // scan
    IScanService mScanService;
    private ServiceConnection mConnection;

    public SpotDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spot_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        final MarkLayer.MarkPoint mark = args.getParcelable(EXTRA_KEY_MARKPOINT);
        final Intent data = new Intent();
        data.putExtra(EXTRA_KEY_MARKPOINT, mark);

        view.findViewById(R.id.spot_detail_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("Mark " + mark.toString() + " is removed!");
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        // 加载样本列表
        RecyclerView recyclerView = findView(R.id.spot_detail_sample_list);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 1)));
        mAdapter = new SampleSpotAdapter();
        recyclerView.setAdapter(mAdapter);

        // 连接扫描服务
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mScanService = IScanService.Stub.asInterface(service);
                try {
                    mScanService.startScan(mark.getRawX(), mark.getRawY(), 3.1415926f);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mScanService = null;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isAdded()) {
            // 5.0 以上版本必须显示调用
            Intent intent = new Intent(IScanService.class.getName());
            intent.setClass(getContext(), ScanService.class);
            getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isAdded()) {
            getContext().unbindService(mConnection);
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.spot_detail_title_text;
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // 更新样本列表
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


}
