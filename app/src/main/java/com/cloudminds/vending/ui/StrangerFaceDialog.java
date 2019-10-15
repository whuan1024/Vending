package com.cloudminds.vending.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class StrangerFaceDialog extends DialogFragment {

    public interface IStrangerFaceDialogCallback extends Serializable {
        void onEstablishFace();
    }

    private IStrangerFaceDialogCallback dialogCallback;

    public static StrangerFaceDialog getInstance(IStrangerFaceDialogCallback callback) {
        StrangerFaceDialog dialog = new StrangerFaceDialog();
        if (callback != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Callback", callback);
            dialog.setArguments(bundle);
        }
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            dialogCallback = (IStrangerFaceDialogCallback) getArguments().getSerializable("Callback");
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
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_establish_face, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_open).setOnClickListener(v -> {
            dismiss();
            if (dialogCallback != null) {
                dialogCallback.onEstablishFace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //背景要设为透明，否则白色小叉号显示不出来
    }
}
