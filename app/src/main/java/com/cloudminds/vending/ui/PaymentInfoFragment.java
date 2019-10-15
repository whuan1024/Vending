package com.cloudminds.vending.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentInfoFragment extends Fragment {

    private Button mButton;
    private CountDownTimer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back).setOnClickListener(v -> getActivity().onBackPressed());
        ((TextView) view.findViewById(R.id.payment_bill)).setText(String.format(getString(R.string.actual_payment), 3));
        mButton = view.findViewById(R.id.btn_ok);
        mButton.setOnClickListener(v -> getActivity().onBackPressed());
        mTimer = new CountDownTimer(1000 * 11, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.i("[PaymentInfoFragment] onTick: millisUntilFinished: " + millisUntilFinished);
                if (isAdded()) {
                    mButton.setText(String.format(getString(R.string.ok_countdown),
                            millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                LogUtil.i("[PaymentInfoFragment] onFinish");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        };
        mTimer.start();
        LogUtil.i("[PaymentInfoFragment] onViewCreated: mTimer start");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
        LogUtil.i("[PaymentInfoFragment] onDestroyView: mTimer cancel");
    }
}
