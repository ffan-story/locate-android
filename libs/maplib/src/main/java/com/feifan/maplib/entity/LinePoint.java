package com.feifan.maplib.entity;

import com.feifan.baselib.utils.LogUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class LinePoint extends ILayerPointImpl {

    // 邻接边集合
    private List<LinePoint> mEdge = new LinkedList<>();

    public LinePoint(int id) {
        super(id);
    }

    public LinePoint(int id, float drawX, float drawY) {
        super(drawX, drawY);
        setId(id);
    }

    /**
     * 添加邻接边的点集合
     * @param point
     */
    public void add(LinePoint point) {
        mEdge.add(point);
    }

    public List<LinePoint> getEdge() {
        return mEdge;
    }

    @Override
    public boolean isIsolated() {
        return mEdge.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof LinePoint)) {
            return false;
        }

        final LinePoint p = (LinePoint)obj;

        return getId() == p.getId();
    }

    @Override
    public String toString() {
        return getId() + super.toString();
    }
}
