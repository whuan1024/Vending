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

public class OpenServiceFragment extends Fragment {

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
        ((TextView) view.findViewById(R.id.title)).setText(R.string.open_service);
        view.findViewById(R.id.slogan_first).setVisibility(View.VISIBLE);
        view.findViewById(R.id.slogan_second).setVisibility(View.VISIBLE);

        int size = getResources().getDimensionPixelOffset(R.dimen.qr_code_size);
        String content = DeviceUnityCodeUtil.getQrCodeContent(getContext());
        ((ImageView) view.findViewById(R.id.img_qr_code)).setImageBitmap(QREncodeUtil.createQRCode(content, size, size, null));

        ((ImageView) view.findViewById(R.id.img_step1)).setImageResource(R.drawable.ic_without_pwd);
        ((ImageView) view.findViewById(R.id.img_step2)).setImageResource(R.drawable.ic_scan_face);
        ((ImageView) view.findViewById(R.id.img_step3)).setImageResource(R.drawable.ic_success);
        ((TextView) view.findViewById(R.id.tv_step1)).setText(R.string.exempt_pwd);
        ((TextView) view.findViewById(R.id.tv_step2)).setText(R.string.face_login);
        ((TextView) view.findViewById(R.id.tv_step3)).setText(R.string.open_success);

        mTimer = new CountDownTimer(1000 * 60 * 3, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[OpenServiceFragment] onFinish: return in 3 minutes");
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
