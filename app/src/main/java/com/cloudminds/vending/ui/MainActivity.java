package com.cloudminds.vending.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mLockState;
    private TextView mDoorState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mLockState = findViewById(R.id.lock_state);
        mDoorState = findViewById(R.id.door_state);
        findViewById(R.id.open_lock).setOnClickListener(v -> {
            MideaCabinetSDK.INSTANCE.openLock();
            mLockState.setText("锁状态：" + MideaCabinetSDK.INSTANCE.checkLockState());
            mDoorState.setText("门状态：" + MideaCabinetSDK.INSTANCE.checkDoorState());
        });
        findViewById(R.id.close_lock).setOnClickListener(v -> {
            MideaCabinetSDK.INSTANCE.closeLock();
            mLockState.setText("锁状态：" + MideaCabinetSDK.INSTANCE.checkLockState());
            mDoorState.setText("门状态：" + MideaCabinetSDK.INSTANCE.checkDoorState());
        });
        findViewById(R.id.detect_face).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.FACE_DETECT);
            startActivity(intent);
        });
        findViewById(R.id.scan_code).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.SCAN_CODE);
            startActivity(intent);
        });
        findViewById(R.id.open_service).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.OPEN_SERVICE);
            startActivity(intent);
        });
        findViewById(R.id.lock_opened).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.LOCK_OPENED);
            startActivity(intent);
        });
        findViewById(R.id.settle_up).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.SETTLE_UP);
            startActivity(intent);
        });
        findViewById(R.id.payment_info).setOnClickListener(v -> {
            Intent intent = new Intent(this, VendingActivity.class);
            intent.putExtra(IFragSwitcher.TARGET_FRAG, IFragSwitcher.FragDefines.PAYMENT_INFO);
            startActivity(intent);
        });
    }
}
