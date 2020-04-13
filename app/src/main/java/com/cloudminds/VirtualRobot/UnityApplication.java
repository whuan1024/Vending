package com.cloudminds.VirtualRobot;

import android.app.Application;
import android.content.Context;

public class UnityApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
