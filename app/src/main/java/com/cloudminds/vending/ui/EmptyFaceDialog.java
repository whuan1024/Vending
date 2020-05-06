package com.cloudminds.vending.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EmptyFaceDialog extends DialogFragment {

    private TextView mExit;
    private CountDownTimer mTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OutsideCantCancelDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_empty_face, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExit = view.findViewById(R.id.dialog_exit);
        mExit.setOnClickListener(v -> {
            dismiss();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        view.findViewById(R.id.dialog_retry).setOnClickListener(v -> dismiss());

        mTimer = new CountDownTimer(1000 * 6, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    mExit.setText(String.format(getString(R.string.exit_detect),
                            millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[EmptyFaceDialog] onFinish: return in 5 seconds");
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
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dialog_width);
        layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dialog_height);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.verticalMargin = 0.28f;
        window.setAttributes(layoutParams);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }
}
