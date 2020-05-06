package com.cloudminds.vending.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentInfoFragment extends Fragment {

    static final String TOTAL_AMOUNT = "total_amount";
    static final String TOTAL_NUMBER = "total_number";

    private CountDownTimer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_info, container, false);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String amount = "";
        int number = 0;
        if (getArguments() != null) {
            amount = String.format("¥%.2f", getArguments().getInt(TOTAL_AMOUNT) / 100.0);
            number = getArguments().getInt(TOTAL_NUMBER);
        }
        view.findViewById(R.id.coupon_info).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.payment_bill)).setText(String.format(getString(R.string.actual_payment), 1));
        ((TextView) view.findViewById(R.id.total_cost)).setText("8.30");
        ((TextView) view.findViewById(R.id.total_amount)).setText("10.80");
        view.findViewById(R.id.btn_continue).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        mTimer = new CountDownTimer(1000 * 11, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[PaymentInfoFragment] onFinish: return in 10 seconds");
                    getActivity().finish();
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
