package com.cloudminds.vending.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudminds.vending.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LockOpenedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.lock_opened).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.main_title)).setText(R.string.lock_opened);
        ((TextView) view.findViewById(R.id.sub_title)).setText(R.string.lock_opened_summary);
    }
}
