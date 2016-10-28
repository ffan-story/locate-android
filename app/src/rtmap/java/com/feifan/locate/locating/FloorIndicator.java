package com.feifan.locate.locating;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.feifan.locate.R;
import com.feifan.locate.widget.ui.TextIndicator;
import com.rtm.frm.map.BaseMapLayer;
import com.rtm.frm.map.LocationLayer;
import com.rtm.frm.map.MapLayer;
import com.rtm.frm.map.MapView;

/**
 * Created by xuchunlei on 2016/10/28.
 */

public final class FloorIndicator extends TextIndicator {

    private MapView mMapView;
    private String mBuildingId;

    public FloorIndicator(Context context) {
        super(context);
    }

    public FloorIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloorIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMapView(MapView mapView, String buildingId) {
        mMapView = mapView;
        mBuildingId = buildingId;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        TextIndicatorModel model = (TextIndicatorModel)v.getTag();
        mMapView.redraw = true;
        mMapView.initMapConfig(mBuildingId, model.text);
        mMapView.initScale();
        mMapView.refreshMap();
    }
}
