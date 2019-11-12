package com.cloudminds.vending.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cloudminds.vending.utils.LogUtil;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                LogUtil.i("[BootReceiver] BOOT COMPLETED");
                context.startService(new Intent(context, TemperatureReport.class));
            }
        }
    }
}
