package com.feifan.sampling.zone;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.SpotListActivity;
import com.feifan.sampling.provider.SampleData.Zone;
import com.feifan.sampling.spot.SpotListFragment;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.widget.SimpleAdapter;
import com.feifan.sampling.widget.SpaceItemDecoration;
import com.libs.ui.activities.BaseActivity;
import com.libs.ui.fragments.CommonFragment;
import com.libs.ui.fragments.FragmentDelegate;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneListFragment extends CommonFragment<Cursor> implements View.OnClickListener {

    private SimpleAdapter<ZoneModel> mAdapter;

    // 启动采集点活动
    private Intent mIntent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIntent = new Intent(context.getApplicationContext(), SpotListActivity.class);
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.biz_zone_list, container, false);
        setupRecyclerView((RecyclerView) rootView.findViewById(R.id.recyclelist));
        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "ZoneEditFragment", ZoneEditFragment.class.getName(), BaseActivity.class, null);
                startActivity(intent);
            }
        });
        initActionBar();
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleAdapter<ZoneModel>(ZoneModel.class) {
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
                holder.itemView.setOnClickListener(ZoneListFragment.this);

            }
        };
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoader();
    }

    private void initActionBar() {
        if (mAbar != null) {
            mAbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            setTitle(R.string.zone_list_title);
            mAbar.setDisplayHomeAsUpEnabled(true);
            if (mToolBarDelegate != null) {
                mToolBarDelegate.getBackView().setVisibility(View.GONE);
            }
        }
    }

    private void initLoader(){
        Bundle args = new Bundle();
        startLoadLocal(args);
    }

    @Override
    protected int getLocalLoadId() {
        return Constants.LOADER.LOADER_ID_ZONE;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == getLocalLoadId()) {    // 子项Loader
            return new CursorLoader(getContext(), Zone.CONTENT_URI, null, null, null, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if(id == getLocalLoadId()) {    // 加载子项数据
            mAdapter.swapCursor((Cursor) data);
        }
    }

    @Override
    public void onClick(View v) {
        ZoneModel model = (ZoneModel) v.getTag();
        String id = model.getRemoteId();
        String name = model.getName();
        LogUtil.d(Constants.DEBUG_TAG, "click zone " + id + " to open it");
        mIntent.putExtra(Constants.EXTRA.KEY_ID, id);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA.KEY_ID,id);
        bundle.putString(Constants.EXTRA.KEY_NAME,name);
        Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "SpotListFragment", SpotListFragment.class.getName(), BaseActivity.class, bundle);
        startActivity(intent);
//        startActivity(mIntent);
    }
}
