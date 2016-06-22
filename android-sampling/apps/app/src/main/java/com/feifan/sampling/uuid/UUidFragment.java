package com.feifan.sampling.uuid;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.sample.UuidModel;
import com.feifan.sampling.widget.SimpleAdapter;
import com.feifan.sampling.widget.SpaceItemDecoration;
import com.libs.ui.fragments.CommonCursorFragment;

/**
 * Created by mengmeng on 16/6/6.
 */
public class UUidFragment extends CommonCursorFragment {

    private SimpleAdapter<UuidModel> mAdapter;

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        setupRecyclerView((RecyclerView) rootView);
        initActionBar();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(R.string.uuid_list_title);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleAdapter<UuidModel>(UuidModel.class);
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    protected Bundle getSqlArgument() {
        return null;
    }

    @Override
    protected Uri getSqlUri() {
        return SampleData.BeaconUUID.CONTENT_URI;
    }

    @Override
    protected int getLocalLoadId() {
        return Constants.LOADER.LOADER_ID_BEACON_UUID;
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
}
