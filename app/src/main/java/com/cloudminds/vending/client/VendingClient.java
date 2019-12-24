package com.cloudminds.vending.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.cloudminds.vending.IVendingInterface;
import com.cloudminds.vending.IVendingListener;
import com.cloudminds.vending.controller.DoorController;
import com.cloudminds.vending.ui.IFragSwitcher;
import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VendingClient {

    private Context mContext;
    private Handler mHandler;
    private static boolean mIsBind = false;
    private IVendingInterface mIVendingInterface;
    private static volatile VendingClient mInstance;
    private static final String TTS = "期待您下次光临";
    private int mRetryCommodityRecognize = 0;

    public static VendingClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VendingClient.class) {
                if (mInstance == null) {
                    LogUtil.d("[VendingClient] init");
                    mInstance = new VendingClient(context);
                }
            }
        } else {
            if (!mIsBind) {
                LogUtil.d("[VendingClient] re-init");
                mInstance = new VendingClient(context);
            }
        }
        return mInstance;
    }

    private VendingClient(Context context) {
        mContext = context;
        bindService(context);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    private void bindService(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.cloudminds.roboticvirtual",
                "com.cloudminds.roboticvirtual.virtrualmanage.VendingService"));
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("[VendingClient] onServiceConnected");
            mIVendingInterface = IVendingInterface.Stub.asInterface(service);
            try {
                mIVendingInterface.registerCallback(mIVendingListener);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to register callback", e);
            }
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("[VendingClient] onServiceDisconnected");
            try {
                mIVendingInterface.unregisterCallback(mIVendingListener);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to unregister callback", e);
            }
            mIVendingInterface = null;
            mIsBind = false;
        }
    };

    private IVendingListener mIVendingListener = new IVendingListener.Stub() {
        @Override
        public void onFaceRecognize(String result) throws RemoteException {
            LogUtil.d("[VendingClient] onFaceRecognize: result: " + result);
        }

        @Override
        public void onCommodityRecognize(String result) throws RemoteException {
            LogUtil.d("[VendingClient] onCommodityRecognize: result: " + result);
        }

        @Override
        public void onReceiveMessage(String msg) throws RemoteException {
            LogUtil.d("[VendingClient] onReceiveMessage: msg: " + msg);
            try {
                JSONObject msgJson = new JSONObject(msg);
                if (msgJson.has("action") && msgJson.has("param")) {
                    JSONObject paramJson = new JSONObject(msgJson.getString("param"));
                    if ("openVending".equals(msgJson.getString("action"))) {
                        if (paramJson.getInt("canOpenFlag") == 1) {
                            DoorController.getInstance(mContext).setEventId(paramJson.getString("eventId"));
                            MideaCabinetSDK.INSTANCE.openLock(true);
                            mHandler.obtainMessage(IFragSwitcher.MSG_SWITCH_FRAG,
                                    IFragSwitcher.FragDefines.LOCK_OPENED).sendToTarget();
                        } else {
                            String phone = paramJson.getString("phone");
                            if (!phone.isEmpty()) {
                                mHandler.obtainMessage(IFragSwitcher.MSG_SHOW_DIALOG,
                                        paramJson.getInt("payOpenFlag"), paramJson.getInt("unPayOrderFlag"),
                                        phone).sendToTarget();
                            }
                        }
                    } else if ("closeVending".equals(msgJson.getString("action"))) {
                        mHandler.obtainMessage(IFragSwitcher.MSG_SWITCH_FRAG,
                                paramJson.getInt("totalAmount"), paramJson.getInt("totalNum"),
                                IFragSwitcher.FragDefines.PAYMENT_INFO).sendToTarget();
                        playTts(TTS);
                        mRetryCommodityRecognize = 0;
                    }
                } else if (msgJson.has("code") && msgJson.has("excption")) {
                    if (msgJson.getInt("code") != 0) {
                        mRetryCommodityRecognize++;
                        String error = "Commodity Recognize Error " + mRetryCommodityRecognize + "\n"
                                + "code:" + msgJson.getInt("code") + "\n"
                                + "errorDetail:" + msgJson.getString("errorDetail") + "\n"
                                + "errorMsg:" + msgJson.getString("errorMsg") + "\n"
                                + "exception:" + msgJson.getString("excption");
                        mHandler.obtainMessage(IFragSwitcher.MSG_SHOW_TOAST, error).sendToTarget();
                        if (mRetryCommodityRecognize < 3) {
                            DoorController.getInstance(mContext).takePhotos();
                        } else {
                            DoorController.getInstance(mContext).reportException();
                            mHandler.obtainMessage(IFragSwitcher.MSG_FINISH_ACTV).sendToTarget();
                        }
                    }
                }
            } catch (JSONException e) {
                LogUtil.e("[VendingClient] Failed to resolve json", e);
            }
        }
    };

    public void faceRecognize(byte[] face) {
        if (mIsBind) {
            try {
                mIVendingInterface.faceRecognize(face);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to face recognize", e);
            }
        } else {
            LogUtil.e("[VendingClient] faceRecognize: Service not connected!");
        }
    }

    public void commodityRecognize(List<String> imageList, String eventId, String reservedField) {
        if (mIsBind) {
            try {
                mIVendingInterface.commodityRecognize(imageList, eventId, reservedField);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to commodity recognize", e);
            }
        } else {
            LogUtil.e("[VendingClient] commodityRecognize: Service not connected!");
        }
    }

    public void playTts(String text) {
        if (mIsBind) {
            try {
                mIVendingInterface.playTts(text);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to play tts", e);
            }
        } else {
            LogUtil.e("[VendingClient] playTts: Service not connected!");
        }
    }

    public void reportStatus(String event, int status) {
        if (mIsBind) {
            try {
                mIVendingInterface.reportStatus(event, status);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to report status", e);
            }
        } else {
            LogUtil.e("[VendingClient] reportStatus: Service not connected!");
        }
    }

    public void reportError(String code, String msg, String extra) {
        if (mIsBind) {
            try {
                mIVendingInterface.reportError(code, msg, extra);
            } catch (RemoteException e) {
                LogUtil.e("[VendingClient] Failed to report error", e);
            }
        } else {
            LogUtil.e("[VendingClient] reportError: Service not connected!");
        }
    }
}
