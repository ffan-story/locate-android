package com.feifan.locate.sampling;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.sampling.model.MacModel;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;

/**
 * 展示Mac地址详情
 * Created by xuchunlei on 16/9/29.
 */

public class MacDetailFragment extends AbsLoaderFragment {

    // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
//    private static final int LOADER_ID = 6;

    private SimpleCursorAdapter<MacModel> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mac_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 生成Loader的ID
     */
    @Override
    protected int getLoaderId() {
//        return LOADER_ID;
        return 0;
    }

    /**
     * 获得ContentProvider的Uri
     */
    @Override
    protected Uri getContentUri() {
        return null;
    }

    /**
     * 获得Adapter
     */
    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }
}
