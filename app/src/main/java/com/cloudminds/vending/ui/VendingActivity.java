package com.cloudminds.vending.ui;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class VendingActivity extends AppCompatActivity implements IFragSwitcher {

    private String mFlagName;
    private Fragment mCurrentFragment = null;

    private FaceDetectFragment faceDetectFragment;
    private QRCodeFragment qrCodeFragment;
    private SettleFragment settleFragment;
    private PaymentInfoFragment paymentInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vending);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFlagName = getIntent().getStringExtra(FRAG_NAME);
        switchFragTo(getIntent().getStringExtra(TARGET_FRAG));
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

        Fragment targetFragment = null;
        switch (fragName) {
            case FragDefines.FACE_DETECT:
                if (faceDetectFragment == null) {
                    faceDetectFragment = new FaceDetectFragment();
                }
                targetFragment = faceDetectFragment;
                break;
            case FragDefines.PAYMENT_INFO:
                if (paymentInfoFragment == null) {
                    paymentInfoFragment = new PaymentInfoFragment();
                }
                targetFragment = paymentInfoFragment;
                break;
            case FragDefines.QR_CODE:
                if (qrCodeFragment == null) {
                    qrCodeFragment = QRCodeFragment.newInstance(mFlagName);
                }
                targetFragment = qrCodeFragment;
                break;
            case FragDefines.SETTLE:
                if (settleFragment == null) {
                    settleFragment = SettleFragment.newInstance(mFlagName);
                }
                targetFragment = settleFragment;
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
        if (!targetFragment.isAdded() && !targetFragment.isDetached()) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            transaction.replace(R.id.vending_container, targetFragment, fragName)
                    .addToBackStack(fragName);//压栈
        } else {
            transaction.hide(mCurrentFragment).show(targetFragment);
        }
        mCurrentFragment = targetFragment;
        transaction.commit();
        LogUtil.i("[VendingActivity] fragments before switch: " + fragmentManager.getFragments());
    }
}
