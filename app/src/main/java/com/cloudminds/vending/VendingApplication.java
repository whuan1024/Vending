package com.cloudminds.vending;

import android.app.Application;

import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;
import com.midea.cabinet.sdk4data.bean.CabinetGridDataBean;
import com.midea.cabinet.sdk4data.bean.ConfigBean;
import com.midea.cabinet.sdk4data.bean.ErrorMsgBean;

import org.jetbrains.annotations.NotNull;

public class VendingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setEnabled(true);
        LogUtil.i("VendingApplication OnCreate");
        MideaCabinetSDK.INSTANCE.init(this, new ConfigBean("1","1","316DTW","vision"));
        MideaCabinetSDK.INSTANCE.setCabinetSDKListener(new MideaCabinetSDK.SDKListener() {
            @Override
            public void onOpenSuccess() {
                LogUtil.i("[MideaCabinetSDK] onOpenSuccess");
            }

            @Override
            public void onOpenTimeOut() {
                LogUtil.i("[MideaCabinetSDK] onOpenTimeOut");
            }

            @Override
            public void onCloseSuccess() {
                LogUtil.i("[MideaCabinetSDK] onCloseSuccess");
            }

            @Override
            public void onCloseTimeOut() {
                LogUtil.i("[MideaCabinetSDK] onCloseTimeOut");
            }

            @Override
            public void onOpenInnerLock(@NotNull CabinetGridDataBean cabinetGridDataBean) {
                LogUtil.i("[MideaCabinetSDK] onOpenInnerLock");
            }

            @Override
            public void onError(@NotNull ErrorMsgBean errorMsgBean) {
                LogUtil.e("[MideaCabinetSDK] onError: errorMsgBean: " + errorMsgBean);
            }
        });
    }
}
