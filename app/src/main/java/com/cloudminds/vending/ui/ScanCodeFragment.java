package com.cloudminds.vending.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.DeviceUnityCodeUtil;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.utils.QREncodeUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScanCodeFragment extends Fragment {

    private CountDownTimer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        ((TextView) view.findViewById(R.id.title)).setText(R.string.scan_code);
        view.findViewById(R.id.slogan_single).setVisibility(View.VISIBLE);

        int size = getResources().getDimensionPixelOffset(R.dimen.qr_code_size);
        String content = DeviceUnityCodeUtil.getQrCodeContent(getContext());
        ((ImageView) view.findViewById(R.id.img_qr_code)).setImageBitmap(QREncodeUtil.createQRCode(content, size, size, null));

        ((ImageView) view.findViewById(R.id.img_step1)).setImageResource(R.drawable.ic_scan_qrcode);
        ((ImageView) view.findViewById(R.id.img_step2)).setImageResource(R.drawable.ic_shopping);
        ((ImageView) view.findViewById(R.id.img_step3)).setImageResource(R.drawable.ic_payment);
        ((TextView) view.findViewById(R.id.tv_step1)).setText(R.string.open_door);
        ((TextView) view.findViewById(R.id.tv_step2)).setText(R.string.purchase);
        ((TextView) view.findViewById(R.id.tv_step3)).setText(R.string.auto_settle);

        mTimer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[ScanCodeFragment] onFinish: return in 1 minute");
                    getActivity().finish();
                }
            }
        };
        mTimer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }
}
