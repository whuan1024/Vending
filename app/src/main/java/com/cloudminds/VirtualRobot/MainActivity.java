package com.cloudminds.VirtualRobot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.cloudminds.vending.R;
import com.cloudminds.vending.ui.CommodityDetailFragment;
import com.cloudminds.vending.ui.CommodityListFragment;
import com.cloudminds.vending.ui.FaceDetectFragment;
import com.cloudminds.vending.ui.IFragSwitcher;
import com.cloudminds.vending.ui.LockOpenedFragment;
import com.cloudminds.vending.ui.OpenServiceFragment;
import com.cloudminds.vending.ui.PaymentInfoFragment;
import com.cloudminds.vending.ui.SettleUpFragment;
import com.cloudminds.vending.utils.LogUtil;
import com.unity3d.player.UnityPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity implements IFragSwitcher
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    final String TAG="---UnityCallback---" ;
    private AudioManager audioManager;

    private FrameLayout mVendingContainer;


    Mp3Player mp3Player;

    public void PlayMp3(String url)
    {
        mp3Player.Play(url);
    }
    public  void StopMp3()
    {
        mp3Player.Stop();
    }
    public  void SetMp3Volume(float vol)
    {
        mp3Player.SetVolume(vol);
    }

    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("cbs","isGranted == "+isGranted);
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }

    }
    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //创建Mp3播放器
        mp3Player=new Mp3Player(this);

      //  CleanCachingTools. cleanApplicationData(this);
        mUnityPlayer = new UnityPlayer(this);

        //setContentView(mUnityPlayer);
        setContentView(R.layout.activity_main);
        ((FrameLayout) findViewById(R.id.avatar)).addView(mUnityPlayer);
        mVendingContainer = findViewById(R.id.vending_container);
        debugUI();

        mUnityPlayer.requestFocus();
        SetMusicVolume();
        checkPermission();

        //  使用这一句，在启动时设置默认形象
        UnityPlayer.UnitySendMessage("Controller","SetDefaultAvatar","SalesGirl");
        //  使用这一句，在启动时设置屏幕调试, 参数  "0" : 不启用， "1": 启用
        UnityPlayer.UnitySendMessage("Controller","EnadleScreenButtons","0");

        //  使用这一句，设置屏幕朝向， 参数 "ORIENTATION_LANDSCAPE" : 横屏，  "ORIENTATION_PORTRAIT": 竖屏。 其他：  没有作用了
        UnityPlayer.UnitySendMessage("Controller","SetScreenOrientation","ORIENTATION_PORTRAIT");

        //  使用这一句，设置背景， 参数 "salesgirlbackground" : 高斌提供的背景，  "starspace": 地球星空背景。 其他：  纯灰色背景
        UnityPlayer.UnitySendMessage("Controller","SetBackground","empty");

    }

    private void debugUI() {
        findViewById(R.id.commodity_list).setOnClickListener(v -> {
            switchFragTo(FragDefines.COMMODITY_LIST);
        });
        findViewById(R.id.commodity_detail).setOnClickListener(v -> {
            switchFragTo(FragDefines.COMMODITY_DETAIL);
        });
        findViewById(R.id.detect_face).setOnClickListener(v -> {
            switchFragTo(FragDefines.FACE_DETECT);
        });
        findViewById(R.id.open_service).setOnClickListener(v -> {
            switchFragTo(FragDefines.OPEN_SERVICE);
        });
        findViewById(R.id.lock_opened).setOnClickListener(v -> {
            switchFragTo(FragDefines.LOCK_OPENED);
        });
        findViewById(R.id.settle_up).setOnClickListener(v -> {
            switchFragTo(FragDefines.SETTLE_UP);
        });
        findViewById(R.id.payment_info).setOnClickListener(v -> {
            switchFragTo(FragDefines.PAYMENT_INFO);
        });
    }

    private Fragment mCurrentFragment;

    private CommodityListFragment mCommodityListFragment;
    private CommodityDetailFragment mCommodityDetailFragment;
    private FaceDetectFragment mFaceDetectFragment;
    private OpenServiceFragment mOpenServiceFragment;
    private LockOpenedFragment mLockOpenedFragment;
    private SettleUpFragment mSettleUpFragment;
    private PaymentInfoFragment mPaymentInfoFragment;

    @Override
    public void onBackPressed() {
        int fragStackCount = getSupportFragmentManager().getBackStackEntryCount();
        LogUtil.i("[MainActivity] onBackPressed--fragStackCount: " + fragStackCount);
        if (fragStackCount <= 1) {
            //finish();
        } else {
            LogUtil.i("[MainActivity] onBackPressed--before: " + getSupportFragmentManager().getFragments());
            super.onBackPressed();//弹栈
            LogUtil.i("[MainActivity] onBackPressed--after: " + getSupportFragmentManager().getFragments());
            mCurrentFragment = getSupportFragmentManager().getFragments().get(0);
        }
    }

    @Override
    public void switchFragTo(@FragDefines String fragName) {
        LogUtil.i("[MainActivity] switchFragTo: " + fragName);
        if (fragName == null) return;

        Fragment targetFragment = null;
        switch (fragName) {
            case FragDefines.COMMODITY_LIST:
                if (mCommodityListFragment == null) {
                    mCommodityListFragment = new CommodityListFragment();
                }
                targetFragment = mCommodityListFragment;
                break;
            case FragDefines.COMMODITY_DETAIL:
                if (mCommodityDetailFragment == null) {
                    mCommodityDetailFragment = new CommodityDetailFragment();
                }
                targetFragment = mCommodityDetailFragment;
                break;
            case FragDefines.FACE_DETECT:
                if (mFaceDetectFragment == null) {
                    mFaceDetectFragment = new FaceDetectFragment();
                }
                targetFragment = mFaceDetectFragment;
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
                //mPaymentInfoFragment.setArguments(mBundle);
                targetFragment = mPaymentInfoFragment;
                break;
            default:
                break;
        }

        if (targetFragment == null) {
            LogUtil.e("[MainActivity] targetFragment is not exist!");
            return;
        }

        mVendingContainer.setVisibility(View.VISIBLE);
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
        LogUtil.i("[MainActivity] fragments before switch: " + fragmentManager.getFragments());
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();

        SetMusicVolume();



    }

    private  void SetMusicVolume()
    {
        audioManager= (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setSpeakerphoneOn(true);
        //audioManager.setMode(AudioManager.MODE_RINGTONE);
      //  audioManager.setMode(AudioManager.STREAM_VOICE_CALL);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);
        //设置物理键控制媒体音量
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

      //  MediaPlayer mMediaPlayer=new MediaPlayer();
      //  mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);


    }



    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {

        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);


     //   int type = this.getResources().getConfiguration().orientation;

//        if (type == Configuration.ORIENTATION_LANDSCAPE) {
//            UnityPlayer.UnitySendMessage("Controller","onConfigurationChanged","ORIENTATION_LANDSCAPE");
//        } else if (type == Configuration.ORIENTATION_PORTRAIT) {
//            UnityPlayer.UnitySendMessage("Controller","onConfigurationChanged","ORIENTATION_PORTRAIT");
//        }

    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   {

        if(audioManager==null)
            audioManager=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        if(audioManager!=null) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI);

                    //对应音量-操作
                    //  return true;

                case KeyEvent.KEYCODE_VOLUME_UP:
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI);
                    //对应音量+操作
                    //  return true;

                default:
                    break;
            }
        }

        return mUnityPlayer.injectEvent(event);
    }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }


   //===============================================
    //========  AIDL  Unity Callback For  Finished
    //=============================================

    //------------------------------------------avatars loaded

    public  void OnUnityAvatarsLoaded(String json)
    {
        try {
            if (UnityService.callBack != null) {
                UnityService.callBack.UnityAvatarsLoaded(json);
            }
            Log.d(TAG, "OnUnityAvatarsLoaded ===>> "+ json);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnUnityAvatarsLoaded error: "+ex.getMessage());
        }
    }

    //-----------------change  avatar-------------------------
    public  void OnResponseAvatarList(String json)
    {
        try {
            if (UnityService.callBack != null) {
                 UnityService.callBack.ResponseAvatarList(json);
            }
            Log.d(TAG, "OnResponseAvatarList ===>> "+ json);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnResponseAvatarList error: "+ex.getMessage());
        }
    }

    public  void OnChangeAvatarResult(String avatarName)
    {
        try {
            if (UnityService.callBack != null) {
                UnityService.callBack.ChangeAvatarResult(avatarName);
            }
            Log.d(TAG, "OnChangeAvatarResult ===>> "+ avatarName);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnChangeAvatarResult error: "+ex.getMessage());
        }
    }

    //---------------play tts , music and video---------------------------
    public  void  OnPlayVoiceStarted(String result, String file){
        Log.d(TAG, "OnPlayVoiceStarted===>> "+ result+"    "+ file);
        try {
            /*if (UnityService.callBack != null) {
                if (result == "ISPLAYING") {
                    UnityService.callBack.playStart(file);
                }
                else
                {
                    UnityService.callBack.playErro(file);
                }
            }*/
            //UnityService.callBack.playStart(file);
            //Log.d(TAG, "OnPlayVoiceStarted end===>> "+ result+"    "+ file);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnPlayVoiceStarted error: "+ex.getMessage());
        }
    }

    public  void  OnPlayVoiceFinished(String result, String file){

        try {
            if (UnityService.callBack != null) {
                if (result == "OK" || result=="CANCLE") {
                    UnityService.callBack.playFinish(file);
                }
                else
                {
                    UnityService.callBack.playErro(file);
                }
            }
            Log.d(TAG, "OnPlayVoiceFinished ===>> "+ result+"    "+ file);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnPlayVoiceFinished error: "+ex.getMessage());
        }
    }

    public void OnPlayMusicFinished(String result, String uri)
    {
        try {
            if(UnityService.callBack!=null) {
                if (result == "OK" || result=="CANCLE") {
                    UnityService.callBack.playFinish(uri);
                }
                else
                {
                    UnityService.callBack.playErro(uri);
                }
            }
            Log.d(TAG,"OnPlayMusicFinished ===>> "+result+"    "+ uri);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnPlayMusicFinished error: "+ex.getMessage());
        }


    }
    public void OnPlayVideoFinished(String result, String uri)
    {
        try {
            if(UnityService.callBack!=null) {
                if (result == "OK" || result=="CANCLE") {
                    UnityService.callBack.playFinish(uri);
                }
                else
                {
                    UnityService.callBack.playErro(uri);
                }
            }
            Log.d(TAG,"OnPlayVideoFinished ===>> "+result+"    "+ uri);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnPlayVideoFinished error: "+ex.getMessage());
        }


    }

    public  void  OnShowVoiceTxt(String txtIn){

        try {
            if (UnityService.callBack != null) {
                UnityService.callBack.showVoiceTxt(txtIn);

            }
            Log.d(TAG, "OnShowTxt ===>> "+ txtIn);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"OnShowTxt error: "+ex.getMessage());
        }
    }
    public  void SetMood(String mood)
    {
        UnityPlayer.UnitySendMessage("Controller","SetMood",mood);
    }
    public  void SetFPS( int fpsIn)
    {
        String fpsStr = "20";
        fpsStr = Integer.toString(fpsIn);
        UnityPlayer.UnitySendMessage("Controller","setFPS",fpsStr);
    }
    public  void  UnityToAndroidLog(String msgIn) {
        Log.d(TAG, "Unity-To-Android_Log===>> " + msgIn);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
     //=======================================
    //====此方法只用于我 模拟 测试 的， 可以删除
      public  void AndroidTestPlayMusic(String uri, String pic)
      {
          String json= "{\"musicUri\":\""+uri+"\", \"pictureUri\":\""+pic+"\"}";
          UnityPlayer.UnitySendMessage("Controller","PlayMusic",json);
      }




    public  void  UnityToAndroidTest01(String msgIn) {
        //String uri="/sdcard/cloudminds/tts/";
        //String file = "loudiatestaudio01.wav";
        String uri="http://192.168.1.101/voice/";
        String file = "vox_lp_01.wav";

        uri += file;
        Log.d(TAG, "UnityToAndroidTest01 begin===msgIn>> " + msgIn);
        String jsonStr= "{\"uri\":\""+uri+"\", \"mood\":\"0\", \"txt\":\"########----this is a hard problem，this is a hard problem，this is a hard problem，\"}";
        UnityPlayer.UnitySendMessage("Controller", "PlayVoice", jsonStr);
    }



    public  void  UnityToAndroidTest02(String msgIn) {
        //String uri="/sdcard/cloudminds/tts/";
        //String file = "loudiatestaudio01.wav";
        //String URL0="http://10.11.102.71/voice/";
        String URL0="http://192.168.1.105/voice/";
        String url1=URL0+"vr_yanjiang.mp3";
        String url2=URL0+"cloudiatestaudio01.wav";
        String url3=URL0+"tts.wav";

        String[] urlList = new  String[]{url1,url2,url3};
        String[] txtList = new  String[]{"this is txt 1111111", "this is txt 222222" , "this is txt 3333333" };

        Log.d(TAG, "UnityToAndroidTest02 begin===msgIn>> " + msgIn);
        String jsonStr= Tools.getJsonFromTTSInfo(urlList,"0",txtList,5,false);
        UnityPlayer.UnitySendMessage("Controller", "PlayVoiceList", jsonStr);
    }

    public  void  UnityToAndroidTest03(String msgIn) {
        SetFPS( 25);
        UnityPlayer.UnitySendMessage("Controller","RequestAvatarList","");
    }
    public  void  UnityToAndroidTest04(String msgIn) {
        testloadAvatarByURL(msgIn,5);
    }
    private void testloadAvatarByURL(String urlIn,int timeoutIn){
        String jsonStr=Tools.getJsonFromAvatarInfo(urlIn,timeoutIn);
        UnityPlayer.UnitySendMessage("Controller", "ChangeAvatarByURLBegin", jsonStr);
    }

    public void UnityToAndroidTest05(String msgIn){
        int modeTmp=0;
        if ("1".equals(msgIn)) modeTmp=0;
        if ("2".equals(msgIn)) modeTmp=1;
        if ("3".equals(msgIn)) modeTmp=2;
        if ("4".equals(msgIn)) modeTmp=3;
        SetBodySwinModeTest(modeTmp);
    }
    public void SetBodySwinModeTest(int modeIn) {
        Log.d("--sbin--", "--------------set  Body Swing Mode  ----------- "+modeIn);
        String indensityStr = "0";
        if (modeIn==0)indensityStr="0.0";
        if (modeIn==1)indensityStr="0.33";
        if (modeIn==2)indensityStr="0.67";
        if (modeIn==3)indensityStr="0.99";

        UnityPlayer.UnitySendMessage("Controller","SetBodyRotateIndensity",indensityStr);
    }
}
