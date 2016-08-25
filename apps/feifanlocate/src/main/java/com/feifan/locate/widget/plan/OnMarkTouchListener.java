package com.feifan.locate.widget.plan;

/**
 * 触碰标记监听接口
 *
 * Created by xuchunlei on 16/8/22.
 */
public interface OnMarkTouchListener {

    /**
     * 创建标记点
     * @param x
     * @param y
     */
    void onCreateMark(MarkLayer.MarkPoint mark, float x, float y);
    /**
     * 点击标记监听
     * @param mark
     * @param x
     * @param y
     */
    void onPress(MarkLayer.MarkPoint mark, float x, float y);

    /**
     * 长按标记监听
     * @param mark
     * @param x
     * @param y
     */
    void onLongPress(MarkLayer.MarkPoint mark, float x, float y);
}
