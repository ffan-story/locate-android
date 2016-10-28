package com.feifan.locate.common;


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

import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleGridCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuildingFragment extends AbsLoaderFragment implements View.OnClickListener {

    // data
    private SimpleGridCursorAdapter<BuildingModel> mAdapter;

    public BuildingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = findView(R.id.locating_zone_grid);
        mAdapter = new SimpleGridCursorAdapter<BuildingModel>(BuildingModel.class){
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
                holder.itemView.setOnClickListener(BuildingFragment.this);
            }
        };
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 2)));
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_BUILDING;
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
        intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, getArguments().getString(ToolbarActivity.EXTRA_KEY_FRAGMENT));

        Bundle args = new Bundle();
        args.putString(LOADER_KEY_SELECTION, "building=?");
        args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(model.id) });
        args.putString(LOADER_KEY_ORDER_BY, LocateData.Zone.FLOOR_NO + " DESC");
        args.putParcelable(Constants.EXTRA_KEY_BUILDING, model);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);

        startActivity(intent);
    }
}
