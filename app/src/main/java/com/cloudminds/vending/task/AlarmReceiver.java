package com.cloudminds.vending.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cloudminds.vending.utils.LogUtil;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("[AlarmReceiver] report temperature again");
        Intent i = new Intent(context, TemperatureReport.class);
        context.startService(i);
    }
}
