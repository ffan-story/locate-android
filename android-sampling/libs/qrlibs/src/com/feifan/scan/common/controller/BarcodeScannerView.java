package com.feifan.scan.common.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback  {
    private CameraPreview mPreview;
    protected IViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    protected ScanStack mScanStack;
    private boolean mIsCameraPreviewStop = false;
    protected boolean isCameraReleased = true;
    private CameraManager mCameraManager;

    public BarcodeScannerView(Context context) {
        super(context);
        init();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init(){
        mScanStack = new ScanStack();
        mCameraManager = CameraManager.getCameraManagerInstance();
    }

    public final void setupLayout(Camera camera) {
        removeAllViews();

        mPreview = new CameraPreview(getContext(), camera, this);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.setBackgroundColor(Color.BLACK);
        relativeLayout.addView(mPreview);
        addView(relativeLayout);

        mViewFinderView = createViewFinderView(getContext());
        if (mViewFinderView instanceof View) {
            addView((View) mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by " +
                    "'createViewFinderView()' should be instance of android.view.View");
        }
        onViewInflate();
        if(mScanStack != null){
            mScanStack.setViewFinder(mViewFinderView);
        }
    }

    protected void onViewInflate(){

    }

    public void setTagetPreviewSize(int width){
        if(mPreview != null) {
            mPreview.setTargetWidth(width);
        }
    }
    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderView(context);
    }

    public void startCamera(int cameraId) {
        if(mCameraHandlerThread == null) {
            mCameraHandlerThread = new CameraHandlerThread(this);
        }
        mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(Camera camera) {
        if(camera != null) {
            setupLayout(camera);
            mViewFinderView.setupViewFinder();
            if(mFlashState != null) {
                setFlash(mFlashState);
            }
            setAutoFocus(mAutofocusState);
        }
    }

    public void startCamera() {
        isCameraReleased = false;
        startCamera(-1);
    }

    /**
     * 在fragment或者activity的ondestory的方法中必须回调此方法
     */
    public void stopCamera() {
        isCameraReleased = true;
        if(mCameraManager != null) {
            if(mPreview != null) {
                mPreview.stopCameraPreview();
                mPreview.setCamera(null, null);
            }
            if(mCameraManager != null){
                mCameraManager.stop();
            }
        }
        if(mCameraHandlerThread != null) {
            mCameraHandlerThread.quit();
            mCameraHandlerThread = null;
        }
        if(mScanStack != null){
            mScanStack.onStop();
        }
    }

    public void stopCameraPreview() {
        if(mPreview != null && !mIsCameraPreviewStop) {
            mPreview.stopCameraPreview();
            mIsCameraPreviewStop = true;
        }
    }

    protected void resumeCameraPreview() {
        if(mPreview != null && mIsCameraPreviewStop) {
            mPreview.showCameraPreview();
            mIsCameraPreviewStop = false;
        }
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (mFramingRectInPreview == null) {
            Rect framingRect = mViewFinderView.getFramingRect();
            int viewFinderViewWidth = mViewFinderView.getWidth();
            int viewFinderViewHeight = mViewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);
            rect.left = rect.left * previewWidth / viewFinderViewWidth;
            rect.right = rect.right * previewWidth / viewFinderViewWidth;
            rect.top = rect.top * previewHeight / viewFinderViewHeight;
            rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }

    public boolean isSupportFlash(){
        if(mCameraManager == null){
            init();
        }
        return mCameraManager.isFlashSupported(getContext());
    }

    public void setFlash(boolean flag) {
        mFlashState = flag;
        try {
            if (mCameraManager != null && mCameraManager.isFlashSupported(getContext())) {

                Camera.Parameters parameters = mCameraManager.getCameraParameter();
                if (parameters != null) {
                    if (flag) {
                        if (parameters.getFlashMode() != null && parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                            return;
                        }
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    } else {
                        if (parameters.getFlashMode() != null && parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                            return;
                        }
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    }
                    mCameraManager.setCameraParameter(parameters);
                }
            }
        }catch (Exception e){

        }
    }

    @Deprecated
    public void toggleFlash() {
        if(mCameraManager != null && mCameraManager.isFlashSupported(getContext())) {
            Camera.Parameters parameters = mCameraManager.getCameraParameter();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCameraManager.setCameraParameter(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        mAutofocusState = state;
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }
}
