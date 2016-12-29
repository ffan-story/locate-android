package com.feifan.locate.locating;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.window.DebugWindow;
import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.indoorlocation.IndoorLocationError;
import com.feifan.indoorlocation.IndoorLocationListener;
import com.feifan.indoorlocation.model.Beacon;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.common.BuildingModel;
import com.feifan.locate.locating.config.LocatingConfig;
import com.feifan.locate.locating.config.LocatingPanel;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.BaseFragment;
import com.feifan.locate.widget.ui.TextIndicator.TextIndicatorModel;
import com.feifan.locatelib.LocatorFactory;
import com.feifan.locatelib.algorithm.ILocationFinder;
import com.feifan.locatelib.algorithm.LocationFinderFactory;
import com.feifan.locatelib.offline.FeifanLocator;
import com.rtm.frm.data.Location;
import com.rtm.frm.map.LocationLayer;
import com.rtm.frm.map.MapView;

import java.util.ArrayList;
import java.util.List;

import static com.feifan.locate.Constants.PLAZA_MAP;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class RealtimePlanFragment extends AbsLoaderFragment implements IndoorLocationListener, OnSharedPreferenceChangeListener {

    /**
     * 定位模式：区分在线定位和离线定位
     */
    public static final String EXTRA_KEY_MODE = "mode";

    @IdRes
    private static final int ID_MENU_SETTING = 1;
    @IdRes
    private static final int ID_MENU_DEBUG = 2;

    private IIndoorLocator mLocator;

    // view
    private LocatingPanel mPanel;
    private MapView mMapView;
    private FloorIndicator mIndicator;

    // locate
    private Location mLocation = new Location(0, 0);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        int period = Integer.valueOf(LocatingConfig.getInstance().getScanPeriod()) * 1000;
        int mode = getArguments().getInt(EXTRA_KEY_MODE);
        mLocator = LocatorFactory.getLocator(mode);
        mLocator.initialize(context.getApplicationContext());
        mLocator.setBleScanInterval(period);

        // 测试
        if(mLocator instanceof FeifanLocator) {
            ILocationFinder finder = LocationFinderFactory.getInstance().
                    getFinder(LocationFinderFactory.FINDER_FULL_MATCH_MULTI_RESULT);
            ((FeifanLocator)mLocator).setFinder(finder);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = findView(R.id.realtime_map);
        Bitmap normal = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_location);
        LocationLayer layer = new LocationLayer(mMapView, normal, normal);
        mMapView.addMapLayer(layer);

        mPanel = (LocatingPanel) getLayoutInflater(savedInstanceState).inflate(R.layout.layout_locating_panel,
                null, false);
        mPanel.setConfigChangeListener(this);

        mIndicator = findView(R.id.realtime_indicator);
        mIndicator.setMapView(mMapView);

        // 加载地图结束开启定位
        mLocator.startUpdatingLocation(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mLocator.destroy();
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> infos = super.getMenuList();
        infos.add(new MenuInfo(ID_MENU_SETTING, BaseFragment.NO_RES, R.string.common_config_text));
        infos.add(new MenuInfo(ID_MENU_DEBUG, BaseFragment.NO_RES, R.string.common_debug_text));
        return infos;
    }

    // temp
    private boolean enabled = false;

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
            case ID_MENU_DEBUG:
//                DebugWindow.get().setEnabled(enabled = !enabled);
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(LocatingConfig.KEY_SCAN_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            mLocator.setBleScanInterval(period * 1000);
        } else if(key.equals(LocatingConfig.KEY_REQUEST_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            mLocator.setUpdateInterval(period * 1000);
        }
        LogUtils.i("we do locate with every "
                + LocatingConfig.getInstance().getRequestPeriod() + "s and scan beacon every "
                + LocatingConfig.getInstance().getScanPeriod() + "s");
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);

        if(data != null && data.getCount() != 0) {
            List<TextIndicatorModel> listData = new ArrayList<>();
            while(data.moveToNext()) {
                int floorNoIndex = data.getColumnIndexOrThrow(Zone.FLOOR_NO);
                int titleIndex = data.getColumnIndexOrThrow(Zone.TITLE);
                listData.add(new TextIndicatorModel(data.getInt(floorNoIndex), data.getString(titleIndex)));
            }
            mIndicator.setData(listData);
//            mIndicator.setCurrent(mBuilding.minFloor);
        }
//        loader.stopLoading();
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return null;
    }

    @Override
    public void onLocationSucceeded(IIndoorLocator locator, IndoorLocationModel location, List<Beacon> beacons) {
        LogUtils.d(location.locationInfo.plazaId + "," + location.floor + ",(" + location.x + "," + location.y + ")");

        // fixme 地图sdk接口
//        boolean changed = mIndicator.setBuildingId(location.locationInfo.plazaId);
        String bId = PLAZA_MAP.get(location.locationInfo.plazaId);
        boolean changed = mIndicator.setBuildingId(bId);
        if (changed) { // 加载楼层
            Cursor cursor = null;
            try {
//                cursor = LocateData.Building.findBuilding(getContext(), location.locationInfo.plazaId);
                cursor = LocateData.Building.findBuilding(getContext(), bId);
                if (cursor == null || cursor.getCount() == 0) {
                    LogUtils.w(location.toString() + " is invalid, please wait");
//                throw new IllegalStateException(location.locationInfo.plazaName + "(" + location.locationInfo.plazaId + ") is not supported now");
                    return;
                }
                if (cursor.moveToFirst()) {
                    BuildingModel building = new BuildingModel(cursor);
                    loadZones(building);
                }
            }finally {
                IOUtils.closeQuietly(cursor);
            }

        }

        // 切换楼层
        mIndicator.setCurrent(location.floor);

        mLocation.a((float) location.x);
        mLocation.b((float) location.y);
        mLocation.setFloor(location.floor);
        mMapView.setMyCurrentLocation(mLocation, false);
//        mMapView.refreshMap();

//        showLog("time-->" + System.currentTimeMillis() / 1000);
    }

    @Override
    public void onLocationFailed(IIndoorLocator locator, IndoorLocationError error, List<Beacon> beacons) {

    }

    @Override
    public void onFirstLoadFinished() {

    }

    private Bundle mArgs = new Bundle();
    private void loadZones(BuildingModel building) {
        mArgs.clear();
        mArgs.putString(RealtimePlanFragment.LOADER_KEY_SELECTION, "building=?");
        mArgs.putStringArray(RealtimePlanFragment.LOADER_KEY_SELECTION_ARGS,
                new String[]{ String.valueOf(building.id) });
        mArgs.putString(RealtimePlanFragment.LOADER_KEY_ORDER_BY, Zone.FLOOR_NO + " DESC");
        getLoaderManager().restartLoader(getLoaderId(), mArgs, RealtimePlanFragment.this);
    }


    // debug
//    private void showLog(final String content) {
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                subscriber.onNext(content);
//            }
//        }).subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        DebugWindow.get().log(content);
//                    }
//                });
//    }

}
