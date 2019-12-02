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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QRCodeFragment extends Fragment {

    private CountDownTimer mTimer;

    public static QRCodeFragment newInstance(String name) {
        QRCodeFragment newFragment = new QRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IFragSwitcher.FRAG_NAME, name);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString(IFragSwitcher.FRAG_NAME);
        boolean isScanCodeFrag = IFragSwitcher.FragDefines.SCAN_CODE.equals(name);
        LogUtil.i("[QRCodeFragment] onViewCreated: frag name: " + name);

        view.findViewById(R.id.back).setOnClickListener(v -> getActivity().onBackPressed());
        ((TextView) view.findViewById(R.id.title)).setText(isScanCodeFrag ? R.string.scan_code : R.string.open_service);
        view.findViewById(R.id.slogan_single).setVisibility(isScanCodeFrag ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.slogan_first).setVisibility(isScanCodeFrag ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.slogan_second).setVisibility(isScanCodeFrag ? View.GONE : View.VISIBLE);

        int size = getResources().getDimensionPixelOffset(R.dimen.qr_code_size);
        ((ImageView) view.findViewById(R.id.img_qr_code)).setImageBitmap(QREncodeUtil.createQRCode(
                "https://sit137.cloudminds.com?rcuCode=" + DeviceUnityCodeUtil.getDeviceUnityCode(getContext()), size, size, null));

        ((ImageView) view.findViewById(R.id.img_step1)).setImageResource(isScanCodeFrag ?
                R.drawable.ic_scan_qrcode : R.drawable.ic_without_pwd);
        ((ImageView) view.findViewById(R.id.img_step2)).setImageResource(isScanCodeFrag ?
                R.drawable.ic_shopping : R.drawable.ic_scan_face);
        ((ImageView) view.findViewById(R.id.img_step3)).setImageResource(isScanCodeFrag ?
                R.drawable.ic_payment : R.drawable.ic_success);

        ((TextView) view.findViewById(R.id.tv_step1)).setText(isScanCodeFrag ?
                R.string.open_door : R.string.exempt_pwd);
        ((TextView) view.findViewById(R.id.tv_step2)).setText(isScanCodeFrag ?
                R.string.purchase : R.string.face_login);
        ((TextView) view.findViewById(R.id.tv_step3)).setText(isScanCodeFrag ?
                R.string.auto_settle : R.string.open_success);

        mTimer = new CountDownTimer(1000 * 120, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[QRCodeFragment] onFinish: return in 2 minutes");
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
