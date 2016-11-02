package com.feifan.maplib.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.view.View;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.maplib.entity.ILayerLine;
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

    public boolean remove(ILayerLine<LinePoint> line) {
        LogUtils.i("layer:delete line " + line.toString() + " successfully");

        // 删除第一个点
        LinePoint pointOne = findPoint(line.getPointOne());
        if(pointOne != null) {
            pointOne.getEdge().remove(line.getPointTwo());
            if(pointOne != null) {
                if(pointOne.isIsolated()) {
                    remove(pointOne);
                }
            }
        }

        // 删除第二个点
        LinePoint pointTwo = findPoint(line.getPointTwo());
        if(pointTwo != null) {
            pointTwo.getEdge().remove(line.getPointOne());
            if(pointTwo.isIsolated()) {
                remove(pointTwo);
            }
        }


        return true;
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
