package com.cloudminds.VirtualRobot;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.unity3d.player.*;

public class UnityService extends Service {
    private String TAG = "UnityService";

    public static UnityCallBack callBack;

    private final String UnityController = "Controller";

    private IBinder mClientBinder = null;

    public final String APP_PKG_NAME = "com.cloudminds.roboticvirtual";

    public final String APP_SERVICE_NAME = "com.cloudminds.roboticvirtual.virtrualmanage.VirtualRobotService";


    public UnityService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "------------------------onBind: --------------------------");
        return mBinder.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    private final CallUnity mBinder = new CallUnity.Stub() {

        //查询可用角色列表
        public  void RequestAvatarList()  throws RemoteException
        {
            UnityPlayer.UnitySendMessage(UnityController,"RequestAvatarList","");
        }
        //切换当前角色
        public  void ChangeAvatarBegin(String name)  throws RemoteException
        {
            UnityPlayer.UnitySendMessage(UnityController,"ChangeAvatarBegin",name);
        }
        public void ChangeAvatarByURL(String urlIn,int timeoutIn)  throws RemoteException{
            String jsonStr=Tools.getJsonFromAvatarInfo(urlIn,timeoutIn);
            UnityPlayer.UnitySendMessage("Controller", "ChangeAvatarByURLBegin", jsonStr);
        }

        @Override
        public void registerCallback(UnityCallBack callBack) throws RemoteException {
            UnityService.callBack = callBack;
            Log.d("registerCallback", "onServiceConnected");
        }

        @Override
        public void unregisterCallback(UnityCallBack callback) throws RemoteException {
            if (UnityService.callBack == callback) {
                UnityService.callBack = null;
                Log.d("unRegisterCallback", "onServiceDisconnected");
            }
        }


        @Override
        public void setVolume(int volume) throws RemoteException {
            Log.d("--sbin--", "--------------volume is 100-----------------------");
            UnityPlayer.UnitySendMessage(UnityController, "SetVolume", volume + "");

        }

        @Override
        public void SetMood(int mood) throws RemoteException {
            Log.d("--sbin--", "--------------set  mood  ----------- "+mood);
            UnityPlayer.UnitySendMessage(UnityController, "SetMood", mood + "");

        }
        @Override
        public void SetFPS(int fpsIn) throws RemoteException {
            Log.d("--sbin--", "--------------set  FPS  ----------- "+fpsIn);
            String fpsStr = "20";
            fpsStr = Integer.toString(fpsIn);
            UnityPlayer.UnitySendMessage("Controller","setFPS",fpsStr);
        }

        @Override
        public void SetBodySwinMode(int modeIn) throws RemoteException {
            Log.d("--sbin--", "--------------set  Body Swing Mode  ----------- "+modeIn);
            String indensityStr = "0";
            if (modeIn==0)indensityStr="0.0";
            if (modeIn==1)indensityStr="0.33";
            if (modeIn==2)indensityStr="0.67";
            if (modeIn==3)indensityStr="0.99";

            UnityPlayer.UnitySendMessage("Controller","SetBodyRotateIndensity",indensityStr);
        }

        @Override
        public void SetAvatarRotate(float angle) throws RemoteException {
            Log.d("--sbin--", "--------------SetAvatarRotate  ----------- "+angle);
            UnityPlayer.UnitySendMessage(UnityController, "SetAvatarRotate", angle + "");

        }
        @Override
        public void setClientBinder(IBinder clientBinder, boolean withClientAlive) throws RemoteException {
            Log.d(TAG, "setClientBinder: " + clientBinder);
            mClientBinder = clientBinder;
            try {
                if (clientBinder != null) {
                    clientBinder.linkToDeath(mDeathRecipient, 0);
                    Log.d(TAG, "setClientBinder: " + "linkToDeath");
                }
            } catch (RemoteException e) {
                Log.d(TAG, "start Unity RemoteException" + Log.getStackTraceString(e));
            }
        }


        @Override
        public boolean playAudio(String filename, String pic) throws RemoteException {
            //musicUri
            //pictureUri
            String json = "{\"musicUri\":\"" + filename + "\", \"pictureUri\":\"" + pic + "\"}";
            UnityPlayer.UnitySendMessage(UnityController, "PlayMusic", json);
            return true;
        }

        @Override
        public boolean playVedio(String filename) throws RemoteException {

            String json = "{\"videoUri\":\"" + filename + "\"}";
            UnityPlayer.UnitySendMessage(UnityController, "PlayVideo", json);
            return true;
        }

        @Override
        public boolean playTts(String filename, String mood, String text, String language) throws RemoteException {
           // Log.d(TAG,">>> "+text);

            text= text.replace('"','|');
           // Log.d(TAG,">>>"+text);
            String json = "{\"uri\":\"" + filename + "\", \"mood\":\"" + mood + "\", \"txt\":\"" + text + "\"}";
          // Log.d(TAG,">>>>"+json);
            UnityPlayer.UnitySendMessage(UnityController, "PlayVoice", json);
            return true;
        }

        /*
        * urlListIn: tts url list
        * textListIn：tts text list
        * moodIn： charactor mood
        * timeoutIn: max time cost for every tts audio loading,  unit:second
        * isShowTxtIn: is show tts txt in unity UI or not
        */
        @Override
        public boolean playTtsList(String[] urlListIn,  String[] textListIn, String moodIn, int timeoutIn,boolean isShowTxtIn) throws RemoteException {
            // Log.d(TAG,">>> "+text);

            //--text= text.replace('"','|');
            // Log.d(TAG,">>>"+text);
            String json = Tools.getJsonFromTTSInfo(urlListIn,moodIn,textListIn,timeoutIn,isShowTxtIn);
            // Log.d(TAG,">>>>"+json);
            UnityPlayer.UnitySendMessage(UnityController, "PlayVoiceList", json);
            return true;
        }
        @Override
        public boolean stopPlay(int type) throws RemoteException {

            switch (type) {
                case 0:
                    UnityPlayer.UnitySendMessage(UnityController, "StopVoice", "");
                    break;
                case 1:
                    UnityPlayer.UnitySendMessage(UnityController, "StopMusic", "");
                    break;
                case 2:
                    UnityPlayer.UnitySendMessage(UnityController, "StopVideo", "");
                    break;
                default:
                    UnityPlayer.UnitySendMessage(UnityController, "StopVoice", "");
                    UnityPlayer.UnitySendMessage(UnityController, "StopMusic", "");
                    UnityPlayer.UnitySendMessage(UnityController, "StopVideo", "");
                    break;
            }
            return true;
        }

        @Override
        public void sendMessage(String str) throws RemoteException {
            if ("reboot".equals(str)) {
                Log.d(TAG, "sendMessage: ");
                RebootUtils.handlerAppReboot(2000);
            }
        }


        public  void ShowIdleText(String json)  throws RemoteException
        {
            UnityPlayer.UnitySendMessage(UnityController, "ShowIdleText", json);
        }

        public  void ShowWakeupText(String json)  throws RemoteException
        {
            UnityPlayer.UnitySendMessage(UnityController, "ShowWakeupText", json);
        }

        public  void StopGundong()  throws RemoteException
        {
            UnityPlayer.UnitySendMessage(UnityController, "StopGundong", "");
        }


        private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                Log.d(TAG, "Robot is dead ! ");
                mClientBinder.unlinkToDeath(mDeathRecipient, 0);
                Intent intent = new Intent(APP_SERVICE_NAME);
                intent.setPackage(APP_PKG_NAME);
                UnityApplication.context.getApplicationContext().startService(intent);
                Log.d(TAG, "startService: ");

            }
        };
    };

}
