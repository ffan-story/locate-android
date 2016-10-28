package com.feifan.maplib.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.view.View;

import com.feifan.maplib.entity.ILayerPoint;
import com.feifan.maplib.entity.LinePoint;
import com.rtm.frm.data.Location;
import com.rtm.frm.map.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchunlei on 2016/10/24.
 */

public class LineLayer extends OperablePointLayer<LinePoint> implements View.OnClickListener {

    @IdRes
    private static final int ID_CONFIRM = 1;
    @IdRes
    private static final int ID_CANCEL = 2;

//    private MapView mMapView;
//    private List<Location> mPoints = new ArrayList<>();
    private Paint mLinePaint = new Paint();

    public LineLayer(MapView mapView) {
        super(mapView);
        mLinePaint.setColor(Color.GREEN);
        mLinePaint.setStrokeWidth(4f);
    }


    @Override
    public void destroyLayer() {

    }

    @Override
    public void clearLayer() {

    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case ID_CONFIRM:
                break;
            case ID_CANCEL:
                break;
        }
    }

    @Override
    protected void drawOthers(Canvas canvas, LinePoint point) {
        super.drawOthers(canvas, point);
        if(point.getEdge() != null && point.getEdge().size() != 0) {
            for(LinePoint p : point.getEdge()) {
                canvas.drawLine(point.getDraw().getX(),
                        point.getDraw().getY(),
                        p.getDraw().getX(),
                        p.getDraw().getY(), mLinePaint);
            }
        }
    }
}
