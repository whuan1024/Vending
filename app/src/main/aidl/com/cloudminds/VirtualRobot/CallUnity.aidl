// CallUnity.aidl
package com.cloudminds.VirtualRobot;

import com.cloudminds.VirtualRobot.UnityCallBack;
// Declare any non-default types here with import statements

interface CallUnity {
   boolean playAudio(String filename,String pic);//播放mp3,
   boolean playVedio(String filename);//播放视频 url

   //  language  我不知道如何用，这个语言跟我没关系， 我不可能拿到中文去翻译为英文吧， 我只是直接显示 text 的内容， 是中文就中文，是日文就日文
   boolean playTts(String filename,String mood,String text,String language);//播放tts filename本地文件路径 ,mood:表情 ,text:播放的文本用与动画下方显示 默认为空"", languge:文本显示语言
   //根据webURL列表 播放TTS
   //urlListIn：URL列表；textListIn：文本列表；moodIn：情绪参数；timeoutIn：每段TTS加载的超时时间，单位：秒；isShowTxtIn：unity界面中是否显示TTS文本
   boolean playTtsList(in String[] urlListIn, in String[] textListIn, String moodIn, int timeoutIn,boolean isShowTxtIn);

   boolean stopPlay(int type); //停止播放   0: 语音    1， 音乐   2， 视频
   void sendMessage(in String str);//扩展
   void registerCallback( UnityCallBack cb);//注册回调
   void unregisterCallback( UnityCallBack cb);//反注册回调
   void setVolume(int volume);//设置音量


   void SetMood(int mood);//单独设置表情
   void SetFPS(int fpsIn);//单独设置FPS帧率
   void SetBodySwinMode(int modeIn);//单独设置身体转动幅度

   // 特别提示：  chineseGirl 因为历史制作原因，不支持头转动!
    void SetAvatarRotate(float angle);//  机器人头转动, angle:   -30.0f 到 +30.0f,

    void setClientBinder(IBinder clientBinder, boolean withClientAlive);

   // 查询角色
    void RequestAvatarList();
    //切换角色
    void ChangeAvatarBegin(String name);
    //通过Web URL切换角色
    void ChangeAvatarByURL(String urlIn,int timeoutIn);


    void ShowIdleText(String json);   //  显示空闲的滚动文本
    void ShowWakeupText(String json);  // 唤醒后显示滚动文本
    void StopGundong();    // 停止显示以上两种文本
}
