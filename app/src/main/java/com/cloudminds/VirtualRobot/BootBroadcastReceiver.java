package com.cloudminds.VirtualRobot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent new_intent = new Intent(context, UnityPlayerActivity.class);  // 要启动的Activity
            //1.如果自启动APP，参数为需要自动启动的应用包名
           // Intent new_intent = getPackageManager().getLaunchIntentForPackage(packageName);
            //下面这句话必须加上才能开机自动运行app的界面
            new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //2.如果自启动Activity
            context.startActivity(new_intent);
            //3.如果自启动服务
            //context.startService(new_intent);
        }
    }
}
