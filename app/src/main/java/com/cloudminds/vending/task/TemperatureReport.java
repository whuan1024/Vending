package com.cloudminds.vending.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.cloudminds.vending.client.VendingClient;
import com.cloudminds.vending.utils.LogUtil;
import com.midea.cabinet.sdk4data.MideaCabinetSDK;

public class TemperatureReport extends Service {

    private static final int TIME_INTERVAL = 30 * 60 * 1000;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int temperature = MideaCabinetSDK.INSTANCE.getTempture();
        LogUtil.i("[TemperatureReport] current temperature = " + temperature);
        VendingClient.getInstance(this).reportStatus("temperature", temperature);
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_INTERVAL, mPendingIntent);
        return START_STICKY;
    }
}
