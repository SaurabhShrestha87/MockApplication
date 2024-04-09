package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;

import com.example.myapplication.activity.RegisterActivity;
import com.example.myapplication.services.MockGpsService;

public class LocalReceiver extends BroadcastReceiver {

    private static final int REQUEST_CODE = 1212;
    long timeout = (long) 60;//It will keep the device awake & register the service within 1 minute time duration.

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager.WakeLock wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":MockGpsService");
        wakeLock.acquire(timeout);
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, MockGpsService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        wakeLock.release(); //Don't forget to add this line when using the wakelock
    }
    public void startMainService(Context context) {
        PendingIntent broadcast = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, RegisterActivity.class), PendingIntent.FLAG_IMMUTABLE);
    }
}