package com.feifan.locate.sampling;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.common.BuildingModel;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.sampling.workline.LinePlanFragment;
import com.feifan.locate.sampling.workspot.SpotPlanFragment;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.locate.widget.ui.BaseFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneFragment extends AbsLoaderFragment implements View.OnClickListener {

    @IdRes
    private static final int ID_PRE_GROUP = 1;
    @IdRes
    private static final int ID_PRE_MAC = 1;
    @IdRes
    private static final int ID_PRE_UUID = 2;

    // 数据适配器
    private SimpleCursorAdapter<ZoneModel> mAdapter;

    private BuildingModel mBuilding;

    public ZoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBuilding = getArguments().getParcelable(Constants.EXTRA_KEY_BUILDING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zone, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = findView(R.id.sampling_zone_list);
        mAdapter = new SimpleCursorAdapter<ZoneModel>(ZoneModel.class){
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
                holder.itemView.setOnClickListener(ZoneFragment.this);
            }
        };
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 5)));
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> list = super.getMenuList();
        MenuInfo macMenu = new MenuInfo(ID_PRE_MAC, BaseFragment.NO_RES, R.string.sampling_mac_text);
        macMenu.setGroupId(ID_PRE_GROUP);
        MenuInfo uuidMenu = new MenuInfo(ID_PRE_UUID, BaseFragment.NO_RES, R.string.sampling_uuid_text);
        uuidMenu.setGroupId(ID_PRE_GROUP);
        list.add(macMenu);
        list.add(uuidMenu);
        return list;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case ID_PRE_MAC:
                Intent intent = new Intent(getContext(), ToolbarActivity.class);
                intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, MacDetailFragment.class.getName());
                Bundle args = new Bundle();
                args.putParcelable(Constants.EXTRA_KEY_BUILDING, mBuilding);
                intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
                startActivity(intent);
                break;
            case ID_PRE_UUID:
                break;
        }
        return super.onMenuItemClick(item);
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_ZONE;
    }

    @Override
    protected Uri getContentUri() {
        return Zone.CONTENT_URI;
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    @Override
    public void onClick(View v) {
        ZoneModel model = (ZoneModel)v.getTag();
        Constants.setExportParentPathName(model.name);

        Intent intent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);

        // TODO 重构根据选取的采集方式选择界面
        String whereZone = "";
        if(Constants.isLineMode()) {
            intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, LinePlanFragment.class.getName());
            whereZone = "workline.zone=?";

        }else {
            intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SpotPlanFragment.class.getName());
            whereZone = "zone=?";
        }

        Bundle args = new Bundle();
        // query
        args.putString(LOADER_KEY_SELECTION, whereZone);
        args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(model.id) });
        // others
        args.putParcelable(Constants.EXTRA_KEY_ZONE, model);
        args.putString(Constants.EXTRA_KEY_BUILDING, mBuilding.code);

        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
        startActivity(intent);
    }
}
