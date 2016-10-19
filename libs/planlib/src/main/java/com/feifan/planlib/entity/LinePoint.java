package com.feifan.planlib.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * 图中边的点
 *
 * Created by xuchunlei on 16/10/18.
 */

public class LinePoint extends LayerPoint {

    // 邻接边集合
    private List<LinePoint> mEdge = new LinkedList<>();

    /**
     * 构造方法
     * <p>
     *     用于在平面图外部创建坐标点
     * </p>
     * @param id
     * @param rx
     * @param ry
     */
    public LinePoint(int id, float rx, float ry){
        setId(id);
        setRaw(rx, ry);
    }

    public LinePoint(float dx, float dy, float rx, float ry, float planScale) {
        setDraw(dx, dy);
        setRaw(rx, ry);
        setReal(rx * planScale, ry * planScale);
        setScale(planScale);
        setMovable(false);
    }

    public void add(LinePoint point) {
        mEdge.add(point);
    }

    public List<LinePoint> getEdge() {
        return mEdge;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LinePoint)) {
            return false;
        }
        LinePoint point = (LinePoint)obj;
        return getId() == point.getId();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getId();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d(%.2f, %.2f)", getId(), getRealX(), getRealY());
    }
}
