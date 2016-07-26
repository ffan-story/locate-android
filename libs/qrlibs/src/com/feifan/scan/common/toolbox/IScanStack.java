package com.feifan.scan.common.toolbox;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.feifan.scan.common.controller.IViewFinder;

import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public abstract class IScanStack<T> {
    protected onScanCallBack mOnScanCallBack;
    protected Rect mRect;
    private IViewFinder mViewFinder;

    /**
     * 初始化
     */
    public abstract void init();

    /**
     * 设定二维码的解析格式
     * @param formats
     */
    public abstract void setFormats(List<T> formats);

    /**
     * 根据相机传过来的数据进行解析
     * @param data
     * @param width
     * @param height
     * @return 解析是否成功
     */
    public abstract boolean performScan(byte[] data, int width, int height);

    /**
     * 进行图片二维码解析
     * @param bitmap
     * @return
     */
    public abstract boolean performBitmapScan(Bitmap bitmap);

    /**
     * 进行二维码解析
     * @param bitmap
     * @return 解析结果
     */
    public abstract String performBitmap(Bitmap bitmap);

    public abstract void onStop();
    /**
     * 设置viewfinder的扫描矩阵
     * @param rect
     */
    public void setPreViewRect(Rect rect){
        if(rect != null) {
            mRect = rect;
        }
    }

    /**
     *
     * @param viewfinder
     */
    public void setViewFinder(IViewFinder viewfinder){
        if(viewfinder != null){
            mViewFinder = viewfinder;
        }
    }

    public IViewFinder getViewFinder(){
        return mViewFinder;
    }

    public void setScanCallBack(onScanCallBack callback){
        if(callback != null){
            mOnScanCallBack = callback;
        }
    }

    public interface onScanCallBack{
        public void onScanSucess(String result,String fromat);
    }
}
