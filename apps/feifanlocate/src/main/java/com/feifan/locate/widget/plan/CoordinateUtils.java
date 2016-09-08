package com.feifan.locate.widget.plan;

/**
 * 坐标工具类
 * 用于计算平移、缩放情况下的坐标变化
 * Created by xuchunlei on 16/8/26.
 */
public class CoordinateUtils {
    private CoordinateUtils(){

    }

    /**
     * 计算缩放条件下的坐标
     * @param x 横轴绘制坐标
     * @param y 纵轴绘制坐标
     * @param ox 横轴计算坐标系原点
     * @param oy 纵轴计算坐标系原点
     * @param scale 缩放因子
     * @param resultXY 结果坐标
     * @return
     */
    public static float[] compute(float x, float y, float ox, float oy, float scale, float[] resultXY) {
        if(resultXY != null && resultXY.length == 2) {
            resultXY[0] = (x - ox) / scale;
            resultXY[1] = (y - oy) / scale;
        }
        return resultXY;
    }
}
