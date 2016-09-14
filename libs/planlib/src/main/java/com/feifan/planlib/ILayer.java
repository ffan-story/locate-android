package com.feifan.planlib;

import android.graphics.Canvas;

/**
 * 平面图层接口
 * <pre>
 *     定义图层操作
 * </pre>
 * Created by xuchunlei on 16/8/19.
 */
public interface ILayer {
    /**
     * image坐标系原点
     * @param origin
     */
    void onInitialize(PlanOrigin origin, float planScale);
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
    void setOnLayerListener(OnLayerListener listener);

    /**
     * 图层事件，封装X轴事件信息、Y轴事件信息和缩放因子
     */
    class LayerEvent {
        public static final int ACTION_NONE = 0;
        public static final int ACTION_BEGIN = ACTION_NONE + 1;
        public static final int ACTION_DOING= ACTION_BEGIN + 1;
        public static final int ACTION_END = ACTION_DOING + 1;

        public float x;
        public float y;
        public float scale;
        public int action = ACTION_NONE;
    }
}
