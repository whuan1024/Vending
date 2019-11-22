package com.cloudminds.vending.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.cloudminds.vending.R;
import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.net.ApiService;
import com.cloudminds.vending.net.RetrofitUtil;
import com.cloudminds.vending.utils.DeviceUnityCodeUtil;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.utils.ZipUtil;
import com.cloudminds.vending.vo.BaseResult;
import com.cloudminds.vending.vo.MetaInfo;
import com.cloudminds.vending.vo.NormalInfo;
import com.google.gson.Gson;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debugUI();
        debugSDK();
        debugInteract();
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
        findViewById(R.id.start_monitor).setOnClickListener(v -> {
            new Thread(() -> {
                if (MideaCabinetSDK.INSTANCE.checkCamera(7)) {
                    LogUtil.i("[MainActivity] startCapture");
                    String filePath = Environment.getExternalStorageDirectory().getPath() +
                            "/mideaSDK/monitorFile/" + System.currentTimeMillis();
                    MideaCabinetSDK.INSTANCE.startCapture(filePath, 3 * 60 * 1000);
                } else {
                    LogUtil.e("[MainActivity] startMonitor: Monitor camera not found!");
                }
            }).start();
        });
        findViewById(R.id.stop_monitor).setOnClickListener(v -> {
            new Thread(() -> {
                if (MideaCabinetSDK.INSTANCE.checkCamera(7)) {
                    LogUtil.i("[MainActivity] stopCapture");
                    MideaCabinetSDK.INSTANCE.stopCapture();
                } else {
                    LogUtil.e("[MainActivity] stopMonitor: Monitor camera not found!");
                }
            }).start();
        });
    }

    private void debugInteract() {
        findViewById(R.id.send_face).setOnClickListener(v -> {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            VendingClient.getInstance(this).faceRecognize(bitmap2Byte(bmp));
        });

        findViewById(R.id.send_commodity).setOnClickListener(v -> {
            List<String> imageList = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                imageList.add(Environment.getExternalStorageDirectory().getPath() +
                        "/mideaSDK/imageFile/" + i + ".jpg");
            }
            VendingClient.getInstance(this).commodityRecognize(imageList, "这里传eventId", "预留字段");
        });

        findViewById(R.id.report_close_door).setOnClickListener(v -> {
            NormalInfo normalInfo = new NormalInfo();
            normalInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(this));
            normalInfo.setEventId("3");
            normalInfo.setMonitorFile("monitor_file.zip");
            LogUtil.i("[MainActivity] report_close_door normalInfo: " + normalInfo);

            ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
            apiService.closeDoor(normalInfo).enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                    if (response.code() == 200) {
                        BaseResult result = response.body();
                        LogUtil.i("[MainActivity] close door--BaseResult: " + result);
                        if (result.getCode() == 0) {
                            LogUtil.i("[MainActivity] close door--success");
                        } else {
                            LogUtil.e("[MainActivity] close door--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                        }
                    } else {
                        LogUtil.e("[MainActivity] close door--response code wrong: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    LogUtil.e("[MainActivity] close door--onFailure", t);
                }
            });
        });

        findViewById(R.id.report_exception).setOnClickListener(v -> {
            MetaInfo metaInfo = new MetaInfo();
            metaInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(this));
            metaInfo.setEventId("3");
            LogUtil.i("[MainActivity] report_exception metaInfo: " + metaInfo);

            ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
            apiService.identifyFail(metaInfo).enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                    if (response.code() == 200) {
                        BaseResult result = response.body();
                        LogUtil.i("[MainActivity] identify fail--BaseResult: " + result);
                        if (result.getCode() == 0) {
                            LogUtil.i("[MainActivity] identify fail--success");
                        } else {
                            LogUtil.e("[MainActivity] identify fail--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                        }
                    } else {
                        LogUtil.e("[MainActivity] identify fail--response code wrong: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    LogUtil.e("[MainActivity] identify fail--onFailure", t);
                }
            });
        });

        findViewById(R.id.upload_monitor).setOnClickListener(v -> {
            MetaInfo metaInfo = new MetaInfo();
            metaInfo.setRcuCode(DeviceUnityCodeUtil.getDeviceUnityCode(this));
            metaInfo.setEventId("3");
            LogUtil.i("[MainActivity] upload_monitor metaInfo: " + metaInfo);

            String path = Environment.getExternalStorageDirectory().getPath() + "/mideaSDK/imageFile/1.jpg";
            File file = new File(path);
            MultipartBody.Part metaPart = MultipartBody.Part.createFormData("meta", new Gson().toJson(metaInfo));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ApiService apiService = RetrofitUtil.getInstance().create(ApiService.class);
            apiService.uploadMonitor(metaPart, filePart).enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                    if (response.code() == 200) {
                        BaseResult result = response.body();
                        LogUtil.i("[MainActivity] upload monitor--BaseResult: " + result);
                        if (result.getCode() == 0) {
                            LogUtil.i("[MainActivity] upload monitor--success");
                        } else {
                            LogUtil.e("[MainActivity] upload monitor--response error, code: " + result.getCode() + ", " + "message: " + result.getMessage());
                        }
                    } else {
                        LogUtil.e("[MainActivity] upload monitor--response code wrong: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    LogUtil.e("[MainActivity] upload monitor--onFailure", t);
                }
            });
        });

        findViewById(R.id.zip).setOnClickListener(v -> {
            try {
                ZipUtil.zipFile(Environment.getExternalStorageDirectory().getPath() + "/mideaSDK/imageFile",
                        Environment.getExternalStorageDirectory().getPath() + "/mideaSDK/imageFile.zip");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private byte[] bitmap2Byte(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
