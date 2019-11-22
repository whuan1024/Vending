package com.cloudminds.vending;

import android.app.Application;

import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.controller.DoorController;
import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;
import com.midea.cabinet.sdk4data.bean.CabinetGridDataBean;
import com.midea.cabinet.sdk4data.bean.ConfigBean;
import com.midea.cabinet.sdk4data.bean.ErrorMsgBean;

import org.jetbrains.annotations.NotNull;

public class VendingApplication extends Application {

    private VendingClient mClient;
    private DoorController mController;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setEnabled(true);
        LogUtil.i("[VendingApplication] OnCreate");
        mClient = VendingClient.getInstance(this);
        mController = DoorController.getInstance(this);
        MideaCabinetSDK.INSTANCE.init(this, new ConfigBean("1","1","316DTW","vision"));
        MideaCabinetSDK.INSTANCE.setCabinetSDKListener(new MideaCabinetSDK.SDKListener() {
            @Override
            public void onOpenSuccess() {
                //开门成功
                LogUtil.i("[MideaCabinetSDK] onOpenSuccess");
                mController.openDoor();
                mClient.reportStatus("door_state", 1);
            }

            @Override
            public void onOpenTimeOut() {
                //开门超时
                LogUtil.i("[MideaCabinetSDK] onOpenTimeOut");
                mController.openTimeout();
            }

            @Override
            public void onCloseSuccess() {
                //关门成功
                LogUtil.i("[MideaCabinetSDK] onCloseSuccess");
                mController.closeDoor();
                mClient.reportStatus("door_state", 0);
            }

            @Override
            public void onCloseTimeOut() {
                //关门超时
                LogUtil.i("[MideaCabinetSDK] onCloseTimeOut");
            }

            @Override
            public void onOpenInnerLock(@NotNull CabinetGridDataBean cabinetGridDataBean) {
                //开小门成功
                LogUtil.i("[MideaCabinetSDK] onOpenInnerLock");
            }

            @Override
            public void onError(@NotNull ErrorMsgBean errorMsgBean) {
                //异常监听
                LogUtil.e("[MideaCabinetSDK] onError: errorMsgBean: " + errorMsgBean);
                mClient.reportError(errorMsgBean.getCode(), errorMsgBean.getMsg(), errorMsgBean.getExtra());
            }
        });
    }
}
