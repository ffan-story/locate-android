package com.feifan.locate.locating;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleGridCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocatingFragment extends AbsLoaderFragment {

    // data
    private static final int LOADER_ID = 1; // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private SimpleGridCursorAdapter<ZoneModel> mAdapter;

    public LocatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locating, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = findView(R.id.locating_zone_grid);
        mAdapter = new SimpleGridCursorAdapter<ZoneModel>(ZoneModel.class){
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
//                holder.itemView.setOnClickListener(SamplingFragment.this);
            }
        };
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 2)));
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    protected Uri getContentUri() {
        return Zone.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }
}
