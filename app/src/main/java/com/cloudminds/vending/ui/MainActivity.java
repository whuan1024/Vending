package com.cloudminds.vending.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debugUI();
        debugSDK();
    }

    private void debugUI() {
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

    private void debugSDK() {
        findViewById(R.id.open_lock).setOnClickListener(v ->
                MideaCabinetSDK.INSTANCE.openLock(true));
        findViewById(R.id.lock_state).setOnClickListener(v ->
                Toast.makeText(this, "锁状态：" + MideaCabinetSDK.INSTANCE.checkLockState(),
                        Toast.LENGTH_SHORT).show());
        findViewById(R.id.door_state).setOnClickListener(v ->
                Toast.makeText(this, "门状态：" + MideaCabinetSDK.INSTANCE.checkDoorState(),
                        Toast.LENGTH_SHORT).show());
        findViewById(R.id.take_picture).setOnClickListener(v -> {
            new Thread(() -> {
                boolean camera1OK = MideaCabinetSDK.INSTANCE.checkCamera(1);
                boolean camera2OK = MideaCabinetSDK.INSTANCE.checkCamera(2);
                boolean camera3OK = MideaCabinetSDK.INSTANCE.checkCamera(3);
                boolean camera4OK = MideaCabinetSDK.INSTANCE.checkCamera(4);
                LogUtil.i("[MainActivity] camera1OK: " + camera1OK + ", camera2OK: " + camera2OK +
                        ", camera3OK: " + camera3OK + ", camera4OK: " + camera4OK);
                if (camera1OK && camera2OK && camera3OK && camera4OK) {
                    MideaCabinetSDK.INSTANCE.startTakePhotos(2);
                }
            }).start();
        });
    }
}
