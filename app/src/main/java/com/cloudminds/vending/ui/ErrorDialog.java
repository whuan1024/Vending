package com.cloudminds.vending.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.cloudminds.vending.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ErrorDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(R.string.request_camera_permission)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> activity.finish())
                .create();
    }
}
