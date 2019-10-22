package com.cloudminds.vending.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.cloudminds.vending.IVendingInterface;
import com.cloudminds.vending.IVendingListener;
import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean mIsBind = false;

    private IVendingInterface mIVendingInterface;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.d("[MainActivity] onServiceConnected");
            mIVendingInterface = IVendingInterface.Stub.asInterface(iBinder);
            try {
                mIVendingInterface.registerCallback(mIVendingListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.d("[MainActivity] onServiceDisconnected");
            try {
                mIVendingInterface.unregisterCallback(mIVendingListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIVendingInterface = null;
            mIsBind = false;
        }
    };

    private IVendingListener mIVendingListener = new IVendingListener.Stub() {
        @Override
        public void onFaceRecognize(String result) throws RemoteException {
            LogUtil.d("[MainActivity] onFaceRecognize: result: " + result);
        }

        @Override
        public void onCommodityRecognize(String result) throws RemoteException {
            LogUtil.d("[MainActivity] onCommodityRecognize: result: " + result);
        }
    };

    private void initService() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.tommy.virtualrobotdemo",
                "com.tommy.virtualrobotdemo.VendingService"));
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();
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
        findViewById(R.id.send_face).setOnClickListener(v -> {
            if (mIsBind) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                try {
                    mIVendingInterface.faceRecognize(bitmap2Byte(bmp));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务未连接", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.send_commodity).setOnClickListener(v -> {
            if (mIsBind) {
                List<String> imageList = new ArrayList<>();
                imageList.add("/sdcard/mideaSDK/imageFile/1.jpg");
                imageList.add("/sdcard/mideaSDK/imageFile/2.jpg");
                imageList.add("/sdcard/mideaSDK/imageFile/3.jpg");
                imageList.add("/sdcard/mideaSDK/imageFile/4.jpg");
                try {
                    mIVendingInterface.commodityRecognize(imageList, "这里传eventId", "预留字段");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务未连接", Toast.LENGTH_SHORT).show();
            }
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

    private byte[] bitmap2Byte(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
