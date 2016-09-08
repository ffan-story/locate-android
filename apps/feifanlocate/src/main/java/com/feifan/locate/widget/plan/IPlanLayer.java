package com.feifan.locate.widget.plan;

import android.graphics.Canvas;
import android.widget.BaseAdapter;

/**
 * 平面图层接口
 * <pre>
 *     定义图层操作
 * </pre>
 * Created by xuchunlei on 16/8/19.
 */
public interface IPlanLayer {

    /**
     * image坐标系原点
     * @param origin
     */
    void onInitialize(PlanOrigin origin);
    /**
     * 平移事件
     * @param event x表示横轴平移距离，y表示纵轴平移距离，scale表示缩放因子
     */
    void onMoveEvent(LayerEvent event);

    /**
     * 缩放事件
     * @param event x表示缩放中心横坐标，y表示缩放中心纵坐标，scale表示缩放因子
     * @param origin x表示平面图原点横坐标，y表示平面图原点纵坐标
     */
    void onScaleEvent(LayerEvent event, PlanOrigin origin);

    /**
     * 绘制事件
     * @param canvas 画布
     */
    void onDraw(Canvas canvas);

    /**
     * 图层监听
     * @param listener
     */
    void setOnPlanLayerListener(OnPlanLayerListener listener);

    /**
     * 图层事件，封装X轴事件信息、Y轴事件信息和缩放因子
     */
    class LayerEvent {
        static final int ACTION_NONE = 0;
        static final int ACTION_BEGIN = ACTION_NONE + 1;
        static final int ACTION_DOING= ACTION_BEGIN + 1;
        static final int ACTION_END = ACTION_DOING + 1;

        float x;
        float y;
        float scale;
        int action = ACTION_NONE;
    }

    /**
     * 平面图层监听接口
     */
    interface OnPlanLayerListener {

        /**
         * 图层数据变更通知
         */
        void notifyLayerDataChanged();
    }

    /**
     * 可操作的图层
     */
    interface IOperablePlanLayer extends IPlanLayer {
        /**
         * 添加坐标点
         * @param x  显示位置横坐标
         * @param y  显示位置纵坐标
         * @param rx 实际位置横坐标
         * @param ry 实际位置纵坐标
         * @return 平面图坐标点
         */
        IPlanPoint add(float x, float y, float rx, float ry);

        /**
         * 移除坐标点
         * @param point 坐标点
         * @return 成功，返回移除的坐标点，否则返回null
         */
        boolean remove(IPlanPoint point);

        /**
         * 通过触碰坐标寻找坐标点
         *
         * @param x
         * @param y
         * @return
         */
        IPlanPoint findPointByTouch(float x, float y);

        /**
         * 获取活动的坐标点，即当前可以调节坐标的坐标点
         * @return
         */
        IPlanPoint getActivePoint();
    }

    /**
     * 操作监听接口
     * <pre>
     *     实现此接口可监听IOperablePlanLayer图层操作
     * </pre>
     */
    interface OnOperationListener {
        /**
         * 创建点
         * @param point 创建的坐标点
         */
        void onCreatePoint(IPlanPoint point);

        /**
         * 删除点
         * @param point
         */
        void onDeletePoint(IPlanPoint point);

        /**
         * 调整位置结束监听
         * @param point
         */
        void onAdjustEnd(IPlanPoint point);


    }
}
