package com.cloudminds.vending.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudminds.facedetect.FaceDetect;
import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.FileUtil;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.view.CameraPreview;
import com.cloudminds.vending.view.IPreviewCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FaceDetectFragment extends Fragment implements FaceDetect.InitCallBack, Handler.Callback,
        EmptyFaceDialog.IEmptyFaceDialogCallback, StrangerFaceDialog.IStrangerFaceDialogCallback,
        IPreviewCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static final int MSG_FACE_DETECT_FAIL = 0;
    private static final int MSG_STRANGER_FACE_DETECTED = 1;

    private boolean mDetectInitSuccess = false;

    private IFragSwitcher mFragSwitcher;

    private CameraPreview mNormalPreview;
    private CameraPreview mInfraredPreview;

    private EmptyFaceDialog mEmptyFaceDialog;
    private StrangerFaceDialog mStrangerFaceDialog;

    private FaceDetect mFaceDetect;

    private Handler mHandler = new Handler(this);

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
        mNormalPreview = view.findViewById(R.id.camera_preview_normal);
        mNormalPreview.setPreviewCallback(this);
        mInfraredPreview = view.findViewById(R.id.camera_preview_infrared);
        mInfraredPreview.setPreviewCallback(this);

        view.findViewById(R.id.close).setOnClickListener(v -> getActivity().onBackPressed());

        FaceDetect.init(getContext().getApplicationContext(), this);
        mFaceDetect = new FaceDetect();

        //simulate dialog
        view.findViewById(R.id.btn_detect_fail).setOnClickListener(v -> {
            if (mEmptyFaceDialog == null) {
                mEmptyFaceDialog = EmptyFaceDialog.getInstance(this);
            }
            if (!mEmptyFaceDialog.isAdded() && !mEmptyFaceDialog.isRemoving() && !mEmptyFaceDialog.isVisible()) {
                mEmptyFaceDialog.show(getFragmentManager(), mEmptyFaceDialog.getClass().getSimpleName());
            }
        });
        view.findViewById(R.id.btn_new_user).setOnClickListener(v -> {
            if (mStrangerFaceDialog == null) {
                mStrangerFaceDialog = StrangerFaceDialog.getInstance(this);
            }
            if (!mStrangerFaceDialog.isAdded() && !mStrangerFaceDialog.isRemoving() && !mStrangerFaceDialog.isVisible()) {
                mStrangerFaceDialog.show(getFragmentManager(), mStrangerFaceDialog.getClass().getSimpleName());
            }
        });
        //simulate end
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragSwitcher) {
            mFragSwitcher = (IFragSwitcher) context;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_FACE_DETECT_FAIL:
                LogUtil.i("[FaceDetectFragment] showEmptyFaceDialog");
                if (mEmptyFaceDialog == null) {
                    mEmptyFaceDialog = EmptyFaceDialog.getInstance(this);
                }
                if (!mEmptyFaceDialog.isAdded() && !mEmptyFaceDialog.isRemoving() && !mEmptyFaceDialog.isVisible()) {
                    mEmptyFaceDialog.show(getFragmentManager(), mEmptyFaceDialog.getClass().getSimpleName());
                }
                break;
            case MSG_STRANGER_FACE_DETECTED:
                LogUtil.i("[FaceDetectFragment] showStrangerDetectedDialog");
                if (mStrangerFaceDialog == null) {
                    mStrangerFaceDialog = StrangerFaceDialog.getInstance(this);
                }
                if (!mStrangerFaceDialog.isAdded() && !mStrangerFaceDialog.isRemoving() && !mStrangerFaceDialog.isVisible()) {
                    mStrangerFaceDialog.show(getFragmentManager(), mStrangerFaceDialog.getClass().getSimpleName());
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onFaceExit() {
        getActivity().finish();
    }

    @Override
    public void onFaceRetry() {
        //mHandler.sendEmptyMessageDelayed(MSG_STRANGER_FACE_DETECTED, FACE_DETECT_DELAY);
    }

    @Override
    public void onEstablishFace() {
        if (mFragSwitcher != null) {
            mFragSwitcher.switchFragTo(IFragSwitcher.FragDefines.OPEN_SERVICE);
        } else {
            LogUtil.e("[FaceDetectFragment] onEstablishFace: mFragSwitcher is undefined!");
        }
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
        LogUtil.i("[FaceDetectFragment] onFacePreview: CameraId = " + cameraId + ", previewSize = "
                + previewSize + ", picBytes length = " + picBytes.length);

        if (mDetectInitSuccess) {
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
                    LogUtil.d("[FaceDetectFragment] onFacePreview: detect done!");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result.faceJpeg, 0, result.faceJpeg.length);
                    String photoName = FileUtil.getPhotoPath(getContext(), "face");
                    FileUtil.saveBitmap(photoName, bitmap);
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
