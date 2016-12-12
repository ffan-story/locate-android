package com.feifan.locate.sampling.mac;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.common.BuildingModel;
import com.feifan.locate.provider.LocateData.Mac;
import com.feifan.locate.sampling.model.MacModel;
import com.feifan.locate.utils.SizeUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;
import com.feifan.locate.widget.recycler.SpaceItemDecoration;
import com.feifan.locate.widget.ui.BaseFragment;
import com.feifan.locatelib.utils.PrintUtils;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.Region;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;
import com.hannesdorfmann.debugoverlay.DebugOverlay;

import java.util.Collection;
import java.util.List;

import static com.feifan.locate.Constants.BUILDING_MAP;
import static com.feifan.locate.Constants.PLAZA_MAP;

/**
 * 展示Mac地址详情
 * Created by xuchunlei on 16/9/29.
 */

public class MacDetailFragment extends AbsLoaderFragment {

    @IdRes
    private static final int ID_MENU_SETTING = 1;

    private SimpleCursorAdapter<MacModel> mAdapter;

    // view
    private TextView mTotalV;
    private TextView mUpdateV;
    private int count = 0;
    private int total = 0;
    private MacPanel mPanel;

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
    private boolean mStarted = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mScanManager.bind(context);
        mPanel = new MacPanel(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mac_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BuildingModel building = getArguments().getParcelable(Constants.EXTRA_KEY_BUILDING);
        setTitle(getString(R.string.mac_detail_title_format, building.name));

        RecyclerView recyclerView = findView(R.id.mac_detail_list);
        mAdapter = new SimpleCursorAdapter<>(MacModel.class);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(getContext(), 5)));

        mTotalV = findView(R.id.mac_detail_total);
        mUpdateV = findView(R.id.mac_detail_update);

        // scan inited
        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                for(SampleBeacon beacon : beacons) {
                    Mac.add(getContext(),
                            beacon.uuid,
                            beacon.major,
                            beacon.minor,
                            beacon.mac,
                            building.id,
                            BUILDING_MAP.get(building.code));
                }
            }
        });
        view.findViewById(R.id.mac_detail_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mStarted) {
                    mStarted = mScanManager.start(getContext().getPackageName(), 1000,
                            new Region("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000));
//                            new Region("ecb33b47-781f-4c16-8513-73fcbb7134f2", 21249));
                }else {
                    count = total;
                    mScanManager.stop();
                }
            }
        });
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> infos = super.getMenuList();
        infos.add(new MenuInfo(ID_MENU_SETTING, BaseFragment.NO_RES, R.string.common_config_text));
        return infos;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case ID_MENU_SETTING:
                if(mPanel.isShown()) {
                    mPanel.hide();
                }else {
                    mPanel.show(getActivity().getWindow());
                }
                return true;
        }

        return super.onMenuItemClick(item);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        if(!mStarted) {
            count = data.getCount();
        }
        total = data.getCount();
        mTotalV.setText(String.valueOf(total));
        mUpdateV.setText(String.valueOf(total - count));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mScanManager.unBind(getContext());
    }

    /**
     * 生成Loader的ID
     */
    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_MAC;
    }

    /**
     * 获得ContentProvider的Uri
     */
    @Override
    protected Uri getContentUri() {
        return Mac.CONTENT_URI;
    }

    /**
     * 获得Adapter
     */
    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }
}
