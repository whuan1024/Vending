package com.cloudminds.vending.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener,
        ImageReader.OnImageAvailableListener {

    public interface IPreviewCallback {
        void onFacePreview(String cameraId, Size previewSize, @NonNull byte[] picBytes);

        void onCameraPermissionDenied();

        void onPictureCaptured(String cameraId, byte[] picBytes);
    }

    private static final int PREVIEW_DETECT = 0;
    private static final int PREVIEW_CAPTURE = 1;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private IPreviewCallback mPreviewCallback;
    private int mPreviewMode;
    private String mCameraId;
    private int mCallbackInterval = 1000;//人脸检测时预览回调间隔，默认1秒
    private long mLastPreviewTime;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    private CameraDevice mCameraDevice;
    private Handler mMainHandler, mBackgroundHandler;
    private CameraManager mCameraManager;
    private CaptureRequest.Builder mRequestBuilder;
    private ImageReader mImageReader;
    private CameraCaptureSession mCaptureSession;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CameraPreview);
        mPreviewMode = ta.getInt(R.styleable.CameraPreview_previewMode, PREVIEW_DETECT);
        mCameraId = "" + ta.getInt(R.styleable.CameraPreview_cameraId, CameraCharacteristics.LENS_FACING_FRONT);
        mCallbackInterval = 1000 / ta.getInt(R.styleable.CameraPreview_previewCallbackFrequency, 1);
        ta.recycle();

        setSurfaceTextureListener(this);
        setKeepScreenOn(true);
    }

    public void setPreviewCallback(IPreviewCallback callback) {
        this.mPreviewCallback = callback;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mMainHandler = new Handler(getContext().getMainLooper());
        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtil.i("[CameraPreview] onSurfaceTextureSizeChanged: width = " + width + ", height = " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCaptureSession != null) {
            mCaptureSession.close();
        }
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //todo nothing
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mPreviewSize == null) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mPreviewSize.getWidth() / mPreviewSize.getHeight()) {
                setMeasuredDimension(width, width * mPreviewSize.getHeight() / mPreviewSize.getWidth());
            } else {
                setMeasuredDimension(height * mPreviewSize.getWidth() / mPreviewSize.getHeight(), height);
            }
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        if (image != null && mPreviewCallback != null) {
            if (mPreviewMode == PREVIEW_CAPTURE) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//由缓冲区存入字节数组
                mPreviewCallback.onPictureCaptured(mCameraId, bytes);
            } else {
                //延缓人脸检测回调的频率，可以避免高频率的回调处理数据而造成卡顿
                if (mLastPreviewTime + mCallbackInterval <= System.currentTimeMillis()) {
                    int byteLength = 0;
                    Image.Plane[] planes = image.getPlanes();
                    //取出各通道的数据
                    byte[][] planeBytes = new byte[planes.length][];
                    for (int i = 0; i < planes.length; i++) {
                        ByteBuffer buffer = planes[i].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        planeBytes[i] = data;
                        byteLength += data.length;
                    }
                    //合并
                    int count = 0;
                    byte[] picBytes = new byte[byteLength];
                    for (byte[] data : planeBytes) {
                        System.arraycopy(data, 0, picBytes, count, data.length);
                        count += data.length;
                    }
                    mPreviewCallback.onFacePreview(mCameraId, mPreviewSize, picBytes);

                    mLastPreviewTime = System.currentTimeMillis();
                }
            }
            image.close();
        }
    }

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (mPreviewCallback != null) {
                mPreviewCallback.onCameraPermissionDenied();
            }
            return;
        }

        try {
            String[] cameraIds = mCameraManager.getCameraIdList();
            if (Arrays.binarySearch(cameraIds, mCameraId) < 0) {
                LogUtil.e("[CameraPreview] openCamera: Camera with ID: " + mCameraId + " is not exist!");
                return;
            }

            HandlerThread childThread = new HandlerThread("Camera_" + mCameraId);
            childThread.start();
            mBackgroundHandler = new Handler(childThread.getLooper());
            configureTransform(width, height);
            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    LogUtil.i("[CameraPreview] onOpened: Camera_" + mCameraId);
                    mCameraDevice = camera;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    LogUtil.i("[CameraPreview] onDisconnected: Camera_" + mCameraId);
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    LogUtil.e("[CameraPreview] onError: Camera_" + mCameraId + ": " + error);
                    mCameraDevice = null;
                }
            }, mMainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mPreviewSize) {
            return;
        }
        int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        setTransform(matrix);
    }

    private void startPreview() {
        if (mCameraDevice == null) {
            return;
        }
        try {
            mPreviewSize = choseBestCameraOutputSize();
            LogUtil.i("[CameraPreview] initPreviewImageReader: mPreviewSize: " + mPreviewSize);
            requestLayout();

            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            int format = ImageFormat.JPEG;
            if (map != null) {
                if (mPreviewMode == PREVIEW_DETECT) {
                    if (map.isOutputSupportedFor(ImageFormat.YUV_420_888)) {
                        format = ImageFormat.YUV_420_888;
                    } else if (map.isOutputSupportedFor(ImageFormat.NV21)) {
                        format = ImageFormat.NV21;
                    }
                }
                Size[] sizes = map.getOutputSizes(format);
                LogUtil.i("[CameraPreview] initPreviewImageReader: sizes: " + Arrays.toString(sizes));
            }
            mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), format, 2);
            mImageReader.setOnImageAvailableListener(this, mMainHandler);

            if (mPreviewMode == PREVIEW_DETECT) {
                mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mRequestBuilder.addTarget(mImageReader.getSurface());
            } else {
                mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            }
            Surface surface = new Surface(this.getSurfaceTexture());
            mRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    mPreviewStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void capturePicture() {
        if (mCameraDevice == null) {
            return;
        }
        try {
            mRequestBuilder.removeTarget(mImageReader.getSurface());
            mRequestBuilder.addTarget(mImageReader.getSurface());
            // 锁定焦点
            mRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mCaptureSession.capture(mRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            LogUtil.i("[CameraPreview] onConfigured: Camera_" + mCameraId + ", mPreviewMode = " + mPreviewMode);
            mCaptureSession = session;
            try {
                // 自动对焦
                mRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                // 获取手机方向
//                int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
//                // 根据设备方向计算设置照片的方向
//                mRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                CaptureRequest request = mRequestBuilder.build();
                session.setRepeatingRequest(request, null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            LogUtil.e("[CameraPreview] onConfigureFailed: Camera_" + mCameraId);
        }
    };

    private Size choseBestCameraOutputSize() throws CameraAccessException {
        int width = getWidth();
        int height = getHeight();
        CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return new Size(width, height);
        }

        Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
        if (sizes == null || sizes.length == 0) {
            return new Size(width, height);
        }

        List<Size> verticalSizes = new ArrayList<>();
        List<Size> horizontalSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() < size.getHeight()) {//竖屏
                verticalSizes.add(size);
            } else {//横屏
                horizontalSizes.add(size);
            }
        }

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        List<Size> choices = null;
        if (width > height && !horizontalSizes.isEmpty()) {//横屏
            choices = horizontalSizes;
        } else if (width < height && !verticalSizes.isEmpty()) {
            choices = verticalSizes;
        }
        if (choices != null) {
            // For still image captures, we use the largest available size.
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Point displaySize = new Point();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getSize(displaySize);
            for (Size option : choices) {
                if (option.getWidth() <= displaySize.x && option.getHeight() <= displaySize.y &&
                        option.getHeight() == option.getWidth() * largest.getHeight() / largest.getWidth()) {
                    if (option.getWidth() >= width && option.getHeight() >= height) {
                        bigEnough.add(option);
                    } else {
                        notBigEnough.add(option);
                    }
                }
            }

            if (bigEnough.size() > 0) {
                return Collections.min(bigEnough, new CompareSizesByArea());
            } else if (notBigEnough.size() > 0) {
                return Collections.max(notBigEnough, new CompareSizesByArea());
            } else {
                return choices.get(0);
            }
        }
        return new Size(width, height);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
