package com.cloudminds.vending.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProcessFragment extends Fragment {

    public static ProcessFragment newInstance(String name) {
        ProcessFragment newFragment = new ProcessFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IFragSwitcher.FRAG_NAME, name);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString(IFragSwitcher.FRAG_NAME);
        boolean isLockOpenedFrag = IFragSwitcher.FragDefines.LOCK_OPENED.equals(name);
        LogUtil.i("[ProcessFragment] onViewCreated: frag name: " + name);

        view.findViewById(R.id.lock_opened).setVisibility(isLockOpenedFrag ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.loading_anim).setVisibility(isLockOpenedFrag ? View.GONE : View.VISIBLE);
        ((TextView) view.findViewById(R.id.main_title)).setText(isLockOpenedFrag ?
                R.string.lock_opened : R.string.settling_accounts);
        ((TextView) view.findViewById(R.id.sub_title)).setText(isLockOpenedFrag ?
                R.string.lock_opened_summary : R.string.loading);

        if (!isLockOpenedFrag) {
            startAnim(view.findViewById(R.id.left_bar));
            startAnim(view.findViewById(R.id.middle_bar));
            startAnim(view.findViewById(R.id.right_bar));
        }
    }

    private void startAnim(ImageView imageView) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setOneShot(false);
        animationDrawable.start();
    }
}
