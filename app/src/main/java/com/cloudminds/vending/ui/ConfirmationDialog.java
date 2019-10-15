package com.cloudminds.vending.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.cloudminds.vending.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ConfirmationDialog extends DialogFragment {
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Fragment parent = getParentFragment();
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.request_camera_permission)
                .setPositiveButton(android.R.string.ok, (dialog, which)
                        -> parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION))
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> {
                            Activity activity = parent.getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        })
                .create();
    }
}
