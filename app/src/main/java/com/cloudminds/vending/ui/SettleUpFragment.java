package com.cloudminds.vending.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettleUpFragment extends Fragment {

    private CountDownTimer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading_anim).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.main_title)).setText(R.string.settling_accounts);
        ((TextView) view.findViewById(R.id.sub_title)).setText(R.string.loading);

        startAnim(view.findViewById(R.id.left_bar));
        startAnim(view.findViewById(R.id.middle_bar));
        startAnim(view.findViewById(R.id.right_bar));

        mTimer = new CountDownTimer(1000 * 30, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: nothing
            }

            @Override
            public void onFinish() {
                if (getActivity() != null && isVisible()) {
                    LogUtil.i("[SettleUpFragment] onFinish: return in 30 seconds");
                    getActivity().finish();
                }
            }
        };
        mTimer.start();
    }

    private void startAnim(ImageView imageView) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setOneShot(false);
        animationDrawable.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }
}
