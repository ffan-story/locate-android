package com.feifan.planlib;

/**
 * 操作监听接口
 * <pre>
 *     实现此接口可监听IOperablePlanLayer图层操作
 * </pre>
 * Created by xuchunlei on 16/9/7.
 */
public interface OnOperationListener {

    /**
     * 创建点
     * @param info 待创建的坐标点信息
     */
    ILayerPoint onCreatePoint(PointInfo info);

    /**
     * 删除点
     * @param point
     * @return 删除成功返回true，否则返回false
     */
    boolean onDeletePoint(ILayerPoint point);

    /**
     * 调整位置结束监听
     */
    void onAdjustEnd();

    class PointInfo{
        /**
         * 在平面图坐标系上的横坐标
         */
        public float x;
        /**
         * 在平面图坐标系上的纵坐标
         */
        public float y;

        public PointInfo(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
