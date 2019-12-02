package com.cloudminds.vending.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StrangerFaceDialog extends DialogFragment {

    private IFragSwitcher mFragSwitcher;
    private CountDownTimer mTimer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFragSwitcher) {
            mFragSwitcher = (IFragSwitcher) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OutsideCantCancelDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_establish_face, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_open).setOnClickListener(v -> {
            dismiss();
            if (mFragSwitcher != null) {
                mFragSwitcher.switchFragTo(IFragSwitcher.FragDefines.OPEN_SERVICE);
            }
        });

        mTimer = new CountDownTimer(1000 * 30, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[StrangerFaceDialog] onFinish: return in 30 seconds");
                    getActivity().finish();
                }
            }
        };
        mTimer.start();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }
}
