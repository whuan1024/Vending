package com.cloudminds.vending.controller;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.net.ApiService;
import com.cloudminds.vending.net.RetrofitUtil;
import com.cloudminds.vending.ui.IFragSwitcher;
import com.cloudminds.vending.utils.DeviceUnityCodeUtil;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.utils.ZipUtil;
import com.cloudminds.vending.vo.BaseResult;
import com.cloudminds.vending.vo.MetaInfo;
import com.cloudminds.vending.vo.NormalInfo;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorController {

    private static final String MIDEA_PATH = Environment.getExternalStorageDirectory().getPath() + "/mideaSDK";

    private Context mContext;
    private Handler mHandler;
    private String mEventId;
    private static volatile DoorController mInstance;

    public static DoorController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DoorController.class) {
                if (mInstance == null) {
                    mInstance = new DoorController(context);
                }
            }
        }
        return mInstance;
    }

    private DoorController(Context context) {
        mContext = context;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public void openDoor() {
        startMonitor();
    }

    public void closeDoor() {
        switchUI();
        stopMonitor();
        takePhotos();
        reportCloseDoor();
    }

    public void openTimeout() {
        finishActivity();
        reportOpenTimeout();
    }

    private void finishActivity() {
        mHandler.obtainMessage(IFragSwitcher.MSG_FINISH_ACTV).sendToTarget();
    }

    private void showToast(String toast) {
        mHandler.obtainMessage(IFragSwitcher.MSG_SHOW_TOAST, toast).sendToTarget();
    }

    private void switchUI() {
        mHandler.obtainMessage(IFragSwitcher.MSG_SWITCH_FRAG,
                IFragSwitcher.FragDefines.SETTLE_UP).sendToTarget();
    }

    public void takePhotos() {
        boolean camera1OK = MideaCabinetSDK.INSTANCE.checkCamera(1);
        boolean camera2OK = MideaCabinetSDK.INSTANCE.checkCamera(2);
        boolean camera3OK = MideaCabinetSDK.INSTANCE.checkCamera(3);
        boolean camera4OK = MideaCabinetSDK.INSTANCE.checkCamera(4);
        LogUtil.i("[DoorController] camera1OK: " + camera1OK + ", camera2OK: " + camera2OK +
                ", camera3OK: " + camera3OK + ", camera4OK: " + camera4OK);
        if (camera1OK && camera2OK && camera3OK && camera4OK) {
            new Thread(() -> {
                MideaCabinetSDK.INSTANCE.startTakePhotos(2);
                List<String> imageList = new ArrayList<>();
                for (int i = 1; i < 5; i++) {
                    imageList.add(MIDEA_PATH + "/imageFile/" + i + ".jpg");
                }
                VendingClient.getInstance(mContext).commodityRecognize(imageList, mEventId, "reserved");
            }).start();
        } else {
            showToast("Commodity Camera Error");
            finishActivity();
            reportException();
        }
    }

    private void startMonitor() {
        new Thread(() -> {
            if (MideaCabinetSDK.INSTANCE.checkCamera(7)) {
                LogUtil.i("[DoorController] startMonitor");
                String filePath = MIDEA_PATH + "/monitorFile/" + mEventId;
                MideaCabinetSDK.INSTANCE.startCapture(filePath, 3 * 60 * 1000);
            } else {
                LogUtil.e("[DoorController] startMonitor: Monitor camera not found!");
            }
        }).start();
    }

    private void stopMonitor() {
        if (MideaCabinetSDK.INSTANCE.checkCamera(7)) {
            LogUtil.i("[DoorController] stopMonitor");
            MideaCabinetSDK.INSTANCE.stopCapture();
            new Thread(() -> {
                try {
                    ZipUtil.zipFile(MIDEA_PATH + "/monitorFile/" + mEventId,
                            MIDEA_PATH + "/monitorFile/" + mEventId + ".zip");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            LogUtil.e("[DoorController] stopMonitor: Monitor camera not found!");
        }
    }

    private void reportCloseDoor() {
        NormalInfo normalInfo = new NormalInfo();
        normalInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(mContext));
        normalInfo.setEventId(mEventId);
        normalInfo.setMonitorFile(mEventId + ".zip");
        LogUtil.i("[DoorController] report close door normal info: " + normalInfo);

        ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
        apiService.closeDoor(normalInfo).enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.code() == 200) {
                    BaseResult result = response.body();
                    LogUtil.i("[DoorController] close door--BaseResult: " + result);
                    if (result.getCode() == 0) {
                        LogUtil.i("[DoorController] close door--success");
                    } else {
                        LogUtil.e("[DoorController] close door--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                    }
                } else {
                    LogUtil.e("[DoorController] close door--response code wrong: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                LogUtil.e("[DoorController] close door--onFailure", t);
            }
        });
    }

    public void reportException() {
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(mContext));
        metaInfo.setEventId(mEventId);
        LogUtil.i("[DoorController] report exception meta info: " + metaInfo);

        ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
        apiService.identifyFail(metaInfo).enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.code() == 200) {
                    BaseResult result = response.body();
                    LogUtil.i("[DoorController] identify fail--BaseResult: " + result);
                    if (result.getCode() == 0) {
                        LogUtil.i("[DoorController] identify fail--success");
                    } else {
                        LogUtil.e("[DoorController] identify fail--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                    }
                } else {
                    LogUtil.e("[DoorController] identify fail--response code wrong: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                LogUtil.e("[DoorController] identify fail--onFailure", t);
            }
        });
    }

    private void reportOpenTimeout() {
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(mContext));
        metaInfo.setEventId(mEventId);
        LogUtil.i("[DoorController] report open door timeout meta info: " + metaInfo);

        ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
        apiService.openTimeout(metaInfo).enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.code() == 200) {
                    BaseResult result = response.body();
                    LogUtil.i("[DoorController] open timeout--BaseResult: " + result);
                    if (result.getCode() == 0) {
                        LogUtil.i("[DoorController] open timeout--success");
                    } else {
                        LogUtil.e("[DoorController] open timeout--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                    }
                } else {
                    LogUtil.e("[DoorController] open timeout--response code wrong: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                LogUtil.e("[DoorController] open timeout--onFailure", t);
            }
        });
    }
}
