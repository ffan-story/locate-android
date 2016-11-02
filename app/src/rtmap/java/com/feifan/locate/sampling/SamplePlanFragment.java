package com.feifan.locate.sampling;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.maplib.layer.OnOperationCallback;
import com.rtm.frm.map.MapView;
import com.rtm.frm.map.TapPOILayer;
import com.rtm.frm.map.XunluMap;

import java.util.Map;

/**
 * Created by xuchunlei on 2016/10/26.
 */

public abstract class SamplePlanFragment extends AbsLoaderFragment {

    public static final String EXTRA_KEY_ZONE = "zone";
    protected static final int REQUEST_CODE_DETAIL = 0;

    private ZoneModel zone;

    // plan
    private MapView mMapView;
    private int transY;

    // action
    private BubbleMenu menu;

    // intent
    protected Intent detailIntent;
    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zone = getArguments().getParcelable(EXTRA_KEY_ZONE);
        LogUtils.i("enter into " + zone.buildingIdCode + "-" + zone.title);

        detailIntent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);
        args = new Bundle();
        args.putInt(SampleDetailFragment.EXTRA_KEY_FLOOR, zone.floorNo);
        args.putString(Constants.EXTRA_KEY_BUILDING, zone.buildingIdCode);
        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = findView(R.id.sample_plan_map);
        onCreateLayer(mMapView);

        mMapView.initMapConfig(zone.buildingIdCode, zone.title);
        mMapView.initScale();

        menu = new BubbleMenu(getContext());
        onCreatePlanMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(menu.isShown()) {
            menu.dismiss();
        }
    }

    protected void onCreatePlanMenu(final BubbleMenu planMenu) {

    }

    /**
     * 获取详情界面类名
     * @return
     */
    protected void setDetailFragment(String detailFragment) {
        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, detailFragment);
    }

    protected ZoneModel getZone() {
        return zone;
    }

    protected BubbleMenu getMenu() {
        return menu;
    }

    /**
     * 显示操作菜单
     * @param x
     * @param y
     */
    protected void showMenu(float x, float y) {
        if(transY == 0) {
            // todo 使用mark的高度更为合理
            transY = ((AppCompatActivity)getActivity()).getSupportActionBar().getHeight() - 48;
        }
        menu.show(mMapView, (int)x, (int)y + transY);
    }

    protected void showDetail(Map<String, Object> params) {
        if(params != null && !params.isEmpty()) {
            for(Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if(value instanceof Parcelable) {
                    args.putParcelable(entry.getKey(), (Parcelable)value);
                }else if(value instanceof String) {
                    args.putString(entry.getKey(), value.toString());
                } else if(value instanceof String[]) {
                    args.putStringArray(entry.getKey(), (String[])value);
                }

            }
        }
        startActivityForResult(detailIntent, REQUEST_CODE_DETAIL);
    }

    protected abstract void onCreateLayer(MapView mapView);
}
