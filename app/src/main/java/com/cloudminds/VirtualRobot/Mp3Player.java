package com.cloudminds.VirtualRobot;



import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

public class Mp3Player  {

    Context context;

    private MediaPlayer m_player;
    private boolean m_bIsReleased = false;
    private boolean m_bIsStreaming = true;

    private String m_strURL = "http://sc1.111ttt.cn/2017/1/05/09/298092035545.mp3";
    //private String m_strFileURL=  "http://172.16.13.135:32170/cms/zh-CN/2019-08-06/143123_15b841ad5629b68a.mp3";
   // private String m_strFileURL ="http://www.android-study.com/upload/media/android.mp3";
    private String m_strTempFilePath = "";
    /** Called when the activity is first created. */

    public Mp3Player(Context ctx) {

        context= ctx;

        m_player = new MediaPlayer();

        m_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 监听错误事件
        m_player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("player", "Error on Listener, what: " + what + "extra: " + extra);
                Mp3Player.this.Stop();
                UnityPlayer.UnitySendMessage("Controller","OnMp3PlayError",m_strURL);
                return false;
            }
        });
        // 监听缓冲事件
        m_player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d("player","MediaPlayer Update buffer: " + Integer.toString(percent) + "%");
            }
        });
        // 监听播放完毕事件
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("player","MediaPlayer Listener Completed");
                Mp3Player.this.Stop();
                UnityPlayer.UnitySendMessage("Controller","OnMp3PlayFinished",m_strURL);
            }
        });
        // 监听准备事件
        m_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("player","MediaPlayer Prepared Listener");
            }
        });
    }

    public void SetVolume(float v)
    {
        if (m_player != null)
            m_player.setVolume(v,v);
    }



    public void Play(final String strURL) {
        if (m_player != null && m_player.isPlaying() ) {
           // m_player.start();
            LogI(" Mp3 is Playing.");
            return;
        }
        m_strURL= strURL;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    if (m_bIsStreaming) {
                        m_player.setDataSource(context, Uri.parse(strURL));
                    } else {
                       LogI(" not is streamming play ");
                       // SetDataSource(strURL);
                    }
                    m_player.prepare();
                    LogI("Duration: " + m_player.getDuration());
                    m_player.start();
                    m_bIsReleased = false;
                } catch (Exception e) {
                    LogE(e.getMessage(), e);
                }
            }
        };
          new Thread(r).start();

    }

    public  void Stop()
    {
        if(m_player!=null )
        {
           if(m_player.isPlaying()) {
               m_player.stop();
           }
           m_player.reset();
        }

    }


    private void LogI(String str) {
        Log.i("Play_Web_Mp3", str);
    }
    private void LogE(String str, Throwable tr) {
        Log.e("Play_Web_Mp3", str, tr);
    }
    //    private void SetDataSource(String strURL) throws Exception {
//        if (!m_bIsReleased) {
//            URL url = new URL(strURL);
//            URLConnection conn = url.openConnection();
//            InputStream in = conn.getInputStream();
//            if (in == null)
//                throw new RuntimeException("stream is null");
//            File file = File.createTempFile("player_mp3_cache", ".mp3", Mp3Player.this.getCacheDir());
//            if (!file.exists())
//                file.createNewFile();
//            m_strTempFilePath = file.getAbsolutePath();
//            FileOutputStream fos = new FileOutputStream(file);
//            byte buffer[] = new byte[128];
//            do {
//                int nNumRead = in.read(buffer);
//                if (nNumRead <= 0)
//                    break;
//                fos.write(buffer);
//            } while (true);
//            m_player.setDataSource(m_strTempFilePath);
//            in.close();
//            fos.close();
//        }
//    }
}