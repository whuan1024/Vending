package com.cloudminds.vending.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.cloudminds.vending.IVendingInterface;
import com.cloudminds.vending.IVendingListener;
import com.cloudminds.vending.utils.LogUtil;

import java.util.List;

public class VendingClient {

    private boolean mIsBind = false;
    private IVendingInterface mIVendingInterface;
    private static volatile VendingClient mInstance;

    public static VendingClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VendingClient.class) {
                if (mInstance == null) {
                    mInstance = new VendingClient(context);
                }
            }
        }
        return mInstance;
    }

    private VendingClient(Context context) {
        bindService(context);
    }

    private void bindService(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.cloudminds.roboticvirtual",
                "com.cloudminds.roboticvirtual.virtrualmanage.VendingService"));
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.d("[VendingClient] onServiceConnected");
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
            LogUtil.d("[VendingClient] onServiceDisconnected");
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
            LogUtil.d("[VendingClient] onFaceRecognize: result: " + result);
        }

        @Override
        public void onCommodityRecognize(String result) throws RemoteException {
            LogUtil.d("[VendingClient] onCommodityRecognize: result: " + result);
        }

        @Override
        public void onReceiveMessage(String msg) throws RemoteException {
            LogUtil.d("[VendingClient] onReceiveMessage: msg: " + msg);
        }
    };

    public void faceRecognize(byte[] face) {
        if (mIsBind) {
            try {
                mIVendingInterface.faceRecognize(face);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.e("[VendingClient] faceRecognize: Service not connected!");
        }
    }

    public void commodityRecognize(List<String> imageList, String eventId, String extraType) {
        if (mIsBind) {
            try {
                mIVendingInterface.commodityRecognize(imageList, eventId, extraType);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.e("[VendingClient] commodityRecognize: Service not connected!");
        }
    }

    public void reportStatus(String event, int status) {
        if (mIsBind) {
            try {
                mIVendingInterface.reportStatus(event, status);
            } catch (RemoteException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        } else {
            LogUtil.e("[VendingClient] reportError: Service not connected!");
        }
    }
}
