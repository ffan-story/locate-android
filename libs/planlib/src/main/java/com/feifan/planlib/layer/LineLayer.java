package com.feifan.planlib.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.planlib.ILayer;
import com.feifan.planlib.ILayerPoint;
import com.feifan.planlib.IOperableLayer;
import com.feifan.planlib.OnLayerListener;
import com.feifan.planlib.OnOperationCallback;
import com.feifan.planlib.PlanOrigin;
import com.feifan.planlib.entity.LinePoint;

import java.util.List;
import java.util.Set;

/**
 * Created by bianying on 2016/10/16.
 */

public class LineLayer extends OperablePointLayer<LinePoint> {

    private Paint mLinePaint = new Paint();

    public LineLayer() {
        super();
        mLinePaint.setColor(Color.GREEN);
        mLinePaint.setStrokeWidth(4f);
    }

    @Override
    public String getName() {
        return "line";
    }

    @Override
    protected void drawOthers(Canvas canvas, LinePoint point) {
        super.drawOthers(canvas, point);
        if(point.getEdge() != null && point.getEdge().size() != 0) {
            for(LinePoint p : point.getEdge()) {
                canvas.drawLine(point.getLocX(),
                        point.getLocY() - mIcon.getHeight() / 2,
                        p.getLocX(),
                        p.getLocY() - mIcon.getHeight() / 2, mLinePaint);
            }
        }

    }
}
