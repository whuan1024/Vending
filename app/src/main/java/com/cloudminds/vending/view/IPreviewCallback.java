package com.cloudminds.vending.view;

import android.util.Size;

import androidx.annotation.NonNull;

public interface IPreviewCallback {

    void onFacePreview(int cameraId, Size previewSize, @NonNull byte[] picBytes);

    void onCameraPermissionDenied(int cameraId);
}
