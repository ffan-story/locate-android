package com.feifan.scan.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.feifan.scan.common.controller.BarcodeScannerView;
import com.feifan.scan.common.controller.DisplayUtils;
import com.feifan.scan.common.controller.IViewFinder;
import com.feifan.scan.common.toolbox.IScanStack;

/**
 * 扫描的view 如果有自定义的viewfinder可以在setViewFinder里面进行设置
 */
public class ScannerView extends BarcodeScannerView {
    private static final String TAG = "ScannerView";
    //TODO
    private final int TIME_POST = 2000000;
    private IViewFinder mFinderView;
    private int mTargetWidth;
    public ScannerView(Context context) {
        super(context);
        mScanStack.init();
    }

    public ScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mScanStack.init();
        mTargetWidth = (int) (getWidth());
    }

    @Override
    protected void onViewInflate() {
        super.onViewInflate();
    }

    /**
     *
     * @param stack
     */
    public void setScanStack(IScanStack stack){
        if(stack != null){
            mScanStack.setScanStack(stack);
            mScanStack.init();
        }
    }

    public void scanBitMap(Bitmap bitmap){
        if(mScanStack != null){
            mScanStack.performBitmapScan(bitmap);
        }
    }

    public void setScanCallBck(IScanStack.onScanCallBack callback){
        if(mScanStack != null){
            mScanStack.setOnSanCallBack(callback);
        }
    }

    /**
     * 设置 customViewfinder
     * @param viewfinder
     */
    public void setFindView(IViewFinder viewfinder){
        if(viewfinder != null){
            mViewFinderView = viewfinder;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data,final Camera camera) {
        if(mScanStack == null){
            return;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            int width = size.width;
            int height = size.height;

            if(DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                int tmp = width;
                width = height;
                height = tmp;
                data = rotatedData;
            }
            Rect rect = getFramingRectInPreview(width,height);
            mScanStack.setPreViewRect(rect);
            boolean isScanSucess = mScanStack.performScan(data,width,height);
            if(isScanSucess){
                stopCameraPreview();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resumeCameraPreview();
                    }
                },TIME_POST);
            } else if(!isCameraReleased){
                resumeCameraPreview();
                camera.setOneShotPreviewCallback(this);
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
        }catch (OutOfMemoryError e){
            //当出现内存溢出的时候降低camera的预览size。
            mTargetWidth = (int) (mTargetWidth * (0.75));
            setTagetPreviewSize(mTargetWidth);
        }
    }

    /**
     * 自定义扫描view，可以使用此方法
     * @param context {@link Context}
     * @return
     */
    @Override
    protected IViewFinder createViewFinderView(Context context) {
        if(mViewFinderView != null){
            return mViewFinderView;
        }else {
            return super.createViewFinderView(context);
        }
    }
}
