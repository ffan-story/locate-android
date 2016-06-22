package com.feifan.sampling.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.provider.SampleData.BeaconUUID;
import com.feifan.sampling.util.ProviderUtil;
import com.feifan.sampling.widget.AbsLoaderFragment;
import com.feifan.sampling.widget.RecyclerCursorAdapter;
import com.feifan.sampling.widget.SimpleAdapter;
import java.util.List;
import com.feifan.sampling.widget.SpaceItemDecoration;
import com.feifan.sampling.zone.ZoneModel;

/**
 * Created by xuchunlei on 16/5/6.
 */
public class BeaconUUIDFragment extends AbsLoaderFragment {

    private SimpleAdapter<UuidModel> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        setupRecyclerView((RecyclerView) rootView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleAdapter<UuidModel>(UuidModel.class);
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER.LOADER_ID_BEACON_UUID;
    }

    @Override
    protected Uri getContentUri() {
        return BeaconUUID.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> infoList = super.getMenuList();
        MenuInfo info = new MenuInfo(R.id.menu_sample_detail_export, Constants.NO_INTEGER, R.string.sample_detail_menu_export_text);
        infoList.add(info);
        return infoList;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ProviderUtil.exportToFile(mAdapter.getCursor());
        return true;
    }
}
