package com.cloudminds.vending.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import java.io.IOException;

public class CameraPreview extends TextureView {

    private static final int DEFAULT_INTERVAL = 1000; //人脸检测预览回调间隔，默认1秒

    private Camera mCamera;
    private IPreviewCallback mPreviewCallback;
    private int mCameraId;
    private int mCallbackInterval;
    private long mLastPreviewTime;
    private boolean mPreviewRunning = false;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CameraPreview);
        mCameraId = ta.getInt(R.styleable.CameraPreview_cameraId, 0);
        mCallbackInterval = DEFAULT_INTERVAL / ta.getInt(R.styleable.CameraPreview_previewCallbackFrequency, 1);
        ta.recycle();

        setSurfaceTextureListener(mSurfaceTextureListener);
        setKeepScreenOn(true);
    }

    public void setPreviewCallback(IPreviewCallback callback) {
        mPreviewCallback = callback;
    }

    private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.i("[CameraPreview] onSurfaceTextureAvailable: camera count = " + Camera.getNumberOfCameras()
                    + ", mCameraId = " + mCameraId + ", width = " + width + ", height = " + height);
            try {
                mCamera = Camera.open(mCameraId);
                initCamera();
                startPreview(surface);
                int previewWidth = mCamera.getParameters().getPreviewSize().width;
                int previewHeight = mCamera.getParameters().getPreviewSize().height;

                mCamera.setPreviewCallback((data, camera) -> {
                    if (mLastPreviewTime + mCallbackInterval <= System.currentTimeMillis()) {
                        mPreviewCallback.onFacePreview(mCameraId,
                                new Size(previewWidth, previewHeight), data);
                        mLastPreviewTime = System.currentTimeMillis();
                    }
                });
            } catch (Exception e) {
                LogUtil.e("[CameraPreview] Failed to start preview", e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtil.i("[CameraPreview] onSurfaceTextureSizeChanged: width = " + width + ", height = " + height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.i("[CameraPreview] onSurfaceTextureDestroyed");
            releaseCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void initCamera() {
        Camera.Parameters parameters = mCamera.getParameters();

        //设置图片预览的格式
        parameters.setPreviewFormat(ImageFormat.NV21);
        //设置图片预览的尺寸
        parameters.setPreviewSize(1920, 1080);
        //设置照片格式
        parameters.setPictureFormat(ImageFormat.JPEG);

        mCamera.setParameters(parameters);
    }

    private void setCameraDisplayOrientation(Activity activity, Camera camera) {
        int degree;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: // portrait
                degree = 90;
                break;
            case Surface.ROTATION_90: // landscape
                degree = 0;
                break;
            case Surface.ROTATION_180: // portrait-reverse
                degree = 270;
                break;
            case Surface.ROTATION_270: // landscape-reverse
                degree = 180;
                break;
            default:
                degree = 90; // 大部分使用场景都是portrait，默认使用portrait的显示方向
                break;
        }
        camera.setDisplayOrientation(degree);
    }

    private void startPreview(SurfaceTexture surface) {
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }
        setCameraDisplayOrientation((Activity) getContext(), mCamera);
        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mPreviewRunning = true;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
