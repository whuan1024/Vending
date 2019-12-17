package com.cloudminds.vending.ui;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.DeviceUnityCodeUtil;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.utils.QREncodeUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class UserCheckDialog extends DialogFragment {

    static final String PHONE_NUMBER = "phone_number";
    static final String QUICK_PAYMENT = "quick_payment";
    static final String UNPAID_ORDER = "unpaid_order";

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
        return inflater.inflate(R.layout.dialog_user_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.i("[UserCheckDialog] Arguments: " + getArguments());

        if (getArguments() != null) {
            String phone = getArguments().getString(PHONE_NUMBER);
            String encryptedPhone = "";
            if (phone != null && phone.length() == 11) {
                encryptedPhone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }

            String tip = "";
            if (getArguments().getInt(QUICK_PAYMENT) == 0) {
                tip = String.format(getString(R.string.no_quick_payment), encryptedPhone);
            } else if (getArguments().getInt(UNPAID_ORDER) == 1) {
                tip = String.format(getString(R.string.has_unpaid_order), encryptedPhone);
            }
            ((TextView) view.findViewById(R.id.tip)).setText(tip);
        }

        int size = getResources().getDimensionPixelOffset(R.dimen.dialog_qr_code_size);
        String content = DeviceUnityCodeUtil.getQrCodeContent(getContext());
        ((ImageView) view.findViewById(R.id.img_qr_code)).setImageBitmap(QREncodeUtil.createQRCode(content, size, size, null));
        view.findViewById(R.id.close).setOnClickListener(v -> dismiss());

        mTimer = new CountDownTimer(1000 * 60 * 3, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[UserCheckDialog] onFinish: return in 3 minutes");
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
