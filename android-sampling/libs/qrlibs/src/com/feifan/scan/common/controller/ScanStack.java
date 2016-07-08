package com.feifan.scan.common.controller;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.feifan.scan.common.toolbox.IScanStack;
import com.feifan.scan.common.toolbox.ZXingStack;

import java.util.List;

/**
 * Created by mengmeng on 16/1/26.
 */
public class ScanStack extends IScanStack {
    //default use zxing to scan
    private IScanStack mStack;

    public void setScanStack(IScanStack stack){
        if(stack != null){
            mStack = stack;
            init();
        }
    }
    public ScanStack(){
        mStack = new ZXingStack();
        init();
    }

    public void init(IViewFinder viewFinder){
        mStack.init();
        mStack.setViewFinder(viewFinder);
    }

    @Override
    public void init() {
        mStack.init();
    }

    @Override
    public void setViewFinder(IViewFinder viewfinder) {
        mStack.setViewFinder(viewfinder);
    }

    @Override
    public IViewFinder getViewFinder() {
        return mStack.getViewFinder();
    }

    @Override
    public void setFormats(List formats) {
mStack.setFormats(formats);
    }

    @Override
    public boolean performScan(byte[] data, int width, int height) {
        return mStack.performScan(data,width,height);
    }

    @Override
    public boolean performBitmapScan(Bitmap bitmap) {
        return mStack.performBitmapScan(bitmap);
    }

    @Override
    public String performBitmap(Bitmap bitmap) {
        return mStack.performBitmap(bitmap);
    }

    @Override
    public void onStop() {
        mStack.onStop();
    }

    @Override
    public void setPreViewRect(Rect rect) {
        mStack.setPreViewRect(rect);
    }

    public void setOnSanCallBack(onScanCallBack callback){
        if(callback != null){
            mStack.setScanCallBack(callback);
        }
    }
}
