package com.cloudminds.vending.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cloudminds.vending.R;
import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.controller.DoorController;
import com.cloudminds.vending.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class VendingActivity extends AppCompatActivity implements IFragSwitcher {

    private Fragment mCurrentFragment;

    private FaceDetectFragment mFaceDetectFragment;
    private ScanCodeFragment mScanCodeFragment;
    private OpenServiceFragment mOpenServiceFragment;
    private LockOpenedFragment mLockOpenedFragment;
    private SettleUpFragment mSettleUpFragment;
    private PaymentInfoFragment mPaymentInfoFragment;

    private Bundle mBundle = new Bundle();

    private boolean mAllPermissionsGranted = true;
    private static final int REQUEST_PERMISSIONS_CODE = 1001;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final Handler mUiHandler = new Handler(msg -> {
        if (msg.what == MSG_SWITCH_FRAG) {
            String fragName = (String) msg.obj;
            if (FragDefines.PAYMENT_INFO.equals(fragName)) {
                mBundle.putInt(PaymentInfoFragment.TOTAL_AMOUNT, msg.arg1);
                mBundle.putInt(PaymentInfoFragment.TOTAL_NUMBER, msg.arg2);
            }
            switchFragTo(fragName);
        } else if (msg.what == MSG_FINISH_ACTV) {
            finish();
        } else if (msg.what == MSG_SHOW_DIALOG) {
            UserCheckDialog dialog = new UserCheckDialog();
            mBundle.putString(UserCheckDialog.PHONE_NUMBER, (String) msg.obj);
            mBundle.putInt(UserCheckDialog.QUICK_PAYMENT, msg.arg1);
            mBundle.putInt(UserCheckDialog.UNPAID_ORDER, msg.arg2);
            dialog.setArguments(mBundle);
            if (!dialog.isAdded()) {
                dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
            }
            if (mFaceDetectFragment != null) {
                mFaceDetectFragment.setUserCheckDialog(dialog);
            }
        } else if (msg.what == MSG_SHOW_TOAST) {
            Toast.makeText(this, (String) msg.obj, Toast.LENGTH_LONG).show();
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vending);
        VendingClient.getInstance(this).setHandler(mUiHandler);
        DoorController.getInstance(this).setHandler(mUiHandler);

        if (!hasRequiredPermissions()) {
            mAllPermissionsGranted = false;
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        }
    }

    private boolean hasRequiredPermissions() {
        for (String permission : PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            mAllPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    mAllPermissionsGranted = false;
                    break;
                }
            }
            if (mAllPermissionsGranted) {
                LogUtil.i("[VendingActivity] all permissions granted");
                switchFragTo(getIntent().getStringExtra(TARGET_FRAG));
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAllPermissionsGranted) {
            switchFragTo(getIntent().getStringExtra(TARGET_FRAG));
        }
    }

    @Override
    public void onBackPressed() {
        int fragStackCount = getSupportFragmentManager().getBackStackEntryCount();
        LogUtil.i("[VendingActivity] onBackPressed--fragStackCount: " + fragStackCount);
        if (fragStackCount <= 1) {
            finish();
        } else {
            LogUtil.i("[VendingActivity] onBackPressed--before: " + getSupportFragmentManager().getFragments());
            super.onBackPressed();//弹栈
            LogUtil.i("[VendingActivity] onBackPressed--after: " + getSupportFragmentManager().getFragments());
            mCurrentFragment = getSupportFragmentManager().getFragments().get(0);
        }
    }

    @Override
    public void switchFragTo(@FragDefines String fragName) {
        LogUtil.i("[VendingActivity] switchFragTo: " + fragName);
        if (fragName == null) return;

        Fragment targetFragment = null;
        switch (fragName) {
            case FragDefines.FACE_DETECT:
                if (mFaceDetectFragment == null) {
                    mFaceDetectFragment = new FaceDetectFragment();
                }
                targetFragment = mFaceDetectFragment;
                break;
            case FragDefines.SCAN_CODE:
                if (mScanCodeFragment == null) {
                    mScanCodeFragment = new ScanCodeFragment();
                }
                targetFragment = mScanCodeFragment;
                break;
            case FragDefines.OPEN_SERVICE:
                if (mOpenServiceFragment == null) {
                    mOpenServiceFragment = new OpenServiceFragment();
                }
                targetFragment = mOpenServiceFragment;
                break;
            case FragDefines.LOCK_OPENED:
                if (mLockOpenedFragment == null) {
                    mLockOpenedFragment = new LockOpenedFragment();
                }
                targetFragment = mLockOpenedFragment;
                break;
            case FragDefines.SETTLE_UP:
                if (mSettleUpFragment == null) {
                    mSettleUpFragment = new SettleUpFragment();
                }
                targetFragment = mSettleUpFragment;
                break;
            case FragDefines.PAYMENT_INFO:
                if (mPaymentInfoFragment == null) {
                    mPaymentInfoFragment = new PaymentInfoFragment();
                }
                mPaymentInfoFragment.setArguments(mBundle);
                targetFragment = mPaymentInfoFragment;
                break;
            default:
                break;
        }

        if (targetFragment == null) {
            LogUtil.e("[VendingActivity] targetFragment is not exist!");
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            transaction.add(R.id.vending_container, targetFragment, fragName)
                    .addToBackStack(fragName);//压栈，如果不压栈的话，多个fragment跳转之后，按返回键不会退回到上一个fragment，而是直接退出activity了
        } else {
            transaction.hide(mCurrentFragment).show(targetFragment);
        }
        mCurrentFragment = targetFragment;
        transaction.commitAllowingStateLoss();
        LogUtil.i("[VendingActivity] fragments before switch: " + fragmentManager.getFragments());
    }
}
