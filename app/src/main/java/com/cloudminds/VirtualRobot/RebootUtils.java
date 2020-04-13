package com.cloudminds.VirtualRobot;

import android.os.Process;
import android.util.Log;

public class RebootUtils {
    private static final String TAG = "UnityRebootUtil";

    public static void handlerAppReboot(final int timeout) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: kill myself");
                Process.killProcess(Process.myPid());
            }
        }).start();
    }
}
