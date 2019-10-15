package com.cloudminds.vending.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cloudminds.vending.R;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EmptyFaceDialog extends DialogFragment {

    public interface IEmptyFaceDialogCallback extends Serializable {
        void onFaceExit();

        void onFaceRetry();
    }

    private IEmptyFaceDialogCallback dialogCallback;

    public static EmptyFaceDialog getInstance(IEmptyFaceDialogCallback dialogCallback) {
        EmptyFaceDialog dialog = new EmptyFaceDialog();
        if (dialogCallback != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Callback", dialogCallback);
            dialog.setArguments(bundle);
        }
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            dialogCallback = (IEmptyFaceDialogCallback) getArguments().getSerializable("Callback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OutsideCantCancelDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_empty_face, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.dialog_empty_exit).setOnClickListener(v -> {
            dismiss();
            if (dialogCallback != null) {
                dialogCallback.onFaceExit();
            }
        });
        view.findViewById(R.id.dialog_empty_try).setOnClickListener(v -> {
            dismiss();
            if (dialogCallback != null) {
                dialogCallback.onFaceRetry();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dialog_width);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }
}
