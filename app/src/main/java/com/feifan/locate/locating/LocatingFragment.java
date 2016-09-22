package com.feifan.locate.locating;


import android.content.Intent;
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
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleGridCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocatingFragment extends AbsLoaderFragment implements View.OnClickListener {

    // data
    private static final int LOADER_ID = 4; // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private SimpleGridCursorAdapter<BuildingModel> mAdapter;

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
        mAdapter = new SimpleGridCursorAdapter<BuildingModel>(BuildingModel.class){
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
                holder.itemView.setOnClickListener(LocatingFragment.this);
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
        return Building.CONTENT_URI;
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    @Override
    public void onClick(View v) {
        BuildingModel model = (BuildingModel) v.getTag();

        Intent intent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, RealtimePlanFragment.class.getName());

        Bundle args = new Bundle();
        args.putParcelable(RealtimePlanFragment.EXTRA_KEY_BUILDING, model);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);

        startActivity(intent);
    }
}
