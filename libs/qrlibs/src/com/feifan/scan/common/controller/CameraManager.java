package com.feifan.scan.common.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import java.util.List;

public class CameraManager {
    private static CameraManager mCameraManager;
    private static final int BACKGROUND_CAMERA_ID = -1; // default background camera
    private Camera mCamera;
    private static int mCameraid = BACKGROUND_CAMERA_ID;

    public static CameraManager getCameraManagerInstance() {
        if (mCameraManager == null) {
            synchronized (CameraManager.class) {
                if (mCameraManager == null) {
                    mCameraManager = new CameraManager(-1);
                }
            }
        }
        return mCameraManager;
    }

    public static CameraManager getCameraManagerInstance(int cameraid) {
        if (mCameraManager == null || cameraid != mCameraid) {
            synchronized (CameraManager.class) {
                if (mCameraManager == null || cameraid != mCameraid) {
                    mCameraManager = new CameraManager(mCameraid);
                }
            }
        }
        return mCameraManager;
    }

    private CameraManager() {
        try {
            mCamera = Camera.open();
        }catch (Exception e){

        }
    }

    private CameraManager(int cameraId) {
        try {
            if(mCamera == null || cameraId != mCameraid) {
                mCameraid = cameraId;
                if(mCamera != null){
                    stop();
                }
                if (cameraId == -1) {
                    mCamera = Camera.open(); // attempt to get a Camera instance
                } else {
                    mCamera = Camera.open(cameraId); // attempt to get a Camera instance
                }
            }
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        return getCameraInstance(-1);
    }

    public void setCameraId(int cameraId) {
        if (cameraId != mCameraid || mCamera == null) {
            mCameraid = cameraId;
            if(mCamera != null){
                stop();
            }
            try {
                if(cameraId == -1) {
                    mCamera = Camera.open(); // attempt to get a Camera instance
                } else {
                    mCamera = Camera.open(cameraId); // attempt to get a Camera instance
                }
            }
            catch (Exception e) {
                // Camera is not available (in use or does not exist)
                e.printStackTrace();
            }
        }
    }


    public Camera getCameraInstance(int cameraId) {
        setCameraId(cameraId);
        return mCamera;
    }

    public boolean isFlashSupported(Context context) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

            if (parameters.getFlashMode() == null) {
                return false;
            }

            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
                return false;
            }
        }
        if (context != null) {
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                return false;
            }
        }
        return true;
    }

    public  Camera.Parameters getCameraParameter(){
        if(mCamera != null){
            return mCamera.getParameters();
        }
        return  null;
    }

    public  void setCameraParameter(Camera.Parameters parameter){
        if(mCamera != null && parameter != null){
            mCamera.setParameters(parameter);
        }
    }

    public void stop(){
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onDestory(){
        mCameraManager = null;
    }
}