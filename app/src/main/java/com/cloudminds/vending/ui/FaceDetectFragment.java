package com.cloudminds.vending.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudminds.facedetect.FaceDetect;
import com.cloudminds.vending.R;
import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.view.CameraPreview;
import com.cloudminds.vending.view.IPreviewCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FaceDetectFragment extends Fragment implements FaceDetect.InitCallBack,
        IPreviewCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private int mSample = 0;
    private int mRetry = 0;
    private boolean mDetectInitSuccess = false;

    private EmptyFaceDialog mEmptyFaceDialog;
    private StrangerFaceDialog mStrangerFaceDialog;

    private FaceDetect mFaceDetect;

    private class FaceDetectImage {
        private byte[] coloredFace;
        private Size coloredSize;
        private byte[] infraredFace;
        private Size infraredSize;
    }

    private FaceDetectImage mDetectedImage = new FaceDetectImage();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_detect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((CameraPreview) view.findViewById(R.id.camera_preview_normal)).setPreviewCallback(this);
        ((CameraPreview) view.findViewById(R.id.camera_preview_infrared)).setPreviewCallback(this);
        view.findViewById(R.id.close).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        FaceDetect.init(getContext(), this);
        mFaceDetect = new FaceDetect();
    }

    @Override
    public void onInitSuccess() {
        LogUtil.i("[FaceDetectFragment] FaceDetect onInitSuccess");
        mDetectInitSuccess = true;
        mFaceDetect = new FaceDetect();
    }

    @Override
    public void onInitFailure(int i, String s) {
        LogUtil.e("[FaceDetectFragment] FaceDetect onInitFailure! Error message: " + s);
        mDetectInitSuccess = false;
    }

    @Override
    public void onFacePreview(int cameraId, Size previewSize, @NonNull byte[] picBytes) {
        if (mDetectInitSuccess && isVisible()
                && (mEmptyFaceDialog == null || !mEmptyFaceDialog.isVisible())
                && (mStrangerFaceDialog == null || !mStrangerFaceDialog.isVisible())) {
            LogUtil.i("[FaceDetectFragment] onFacePreview: CameraId = " + cameraId + ", previewSize = "
                    + previewSize + ", picBytes length = " + picBytes.length);
            if (cameraId == 0) {
                if (mDetectedImage.coloredFace != null && mDetectedImage.infraredFace != null) {
                    mDetectedImage = new FaceDetectImage();
                }
                mDetectedImage.infraredFace = picBytes;
                mDetectedImage.infraredSize = previewSize;
            } else if (cameraId == 1) {
                if (mDetectedImage.coloredFace != null && mDetectedImage.infraredFace != null) {
                    mDetectedImage = new FaceDetectImage();
                }
                mDetectedImage.coloredFace = picBytes;
                mDetectedImage.coloredSize = previewSize;
            }

            if (mDetectedImage.coloredFace != null && mDetectedImage.infraredFace != null) {
                FaceDetect.Result result = mFaceDetect.getFaceJPEG(
                        mDetectedImage.coloredFace,
                        mDetectedImage.coloredSize.getWidth(),
                        mDetectedImage.coloredSize.getHeight(),
                        mDetectedImage.infraredFace,
                        mDetectedImage.infraredSize.getWidth(),
                        mDetectedImage.infraredSize.getHeight(), 90);
                if (result != null) {
                    LogUtil.i("[FaceDetectFragment] onFacePreview: detect done " + (mSample + 1) + " time(s)");
                    mRetry = 0;
                    VendingClient.getInstance(getContext()).faceRecognize(result.faceJpeg);
                    if (++mSample == 7) {
                        mSample = 0;
                        if (mStrangerFaceDialog == null) {
                            mStrangerFaceDialog = new StrangerFaceDialog();
                        }
                        if (!mStrangerFaceDialog.isAdded() && getFragmentManager() != null) {
                            mStrangerFaceDialog.show(getFragmentManager(), mStrangerFaceDialog.getClass().getSimpleName());
                        }
                    }
                    /*
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result.faceJpeg, 0, result.faceJpeg.length);
                    String photoName = FileUtil.getPhotoPath(getContext(), "face");
                    FileUtil.saveBitmap(photoName, bitmap);
                    */
                } else {
                    LogUtil.i("[FaceDetectFragment] no face: " + (mRetry + 1));
                    if (++mRetry == 15) {
                        mRetry = 0;
                        if (mEmptyFaceDialog == null) {
                            mEmptyFaceDialog = new EmptyFaceDialog();
                        }
                        if (!mEmptyFaceDialog.isAdded() && getFragmentManager() != null) {
                            mEmptyFaceDialog.show(getFragmentManager(), mEmptyFaceDialog.getClass().getSimpleName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCameraPermissionDenied(int cameraId) {
        if (cameraId == 0) {
            //此处加入摄像头Id判断，是为了对于双摄的情况确保弹框只弹一次
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new ErrorDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
