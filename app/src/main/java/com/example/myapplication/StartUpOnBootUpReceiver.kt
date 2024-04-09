package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.myapplication.services.MockGpsService


class StartUpOnBootUpReceiver : BroadcastReceiver() {
    private var localReceiver: LocalReceiver = LocalReceiver()
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val mockLocServiceIntent = Intent(context, MockGpsService::class.java)
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(mockLocServiceIntent) else context.startService(
                mockLocServiceIntent
            )
            localReceiver.startMainService(context) //It will create a receiver to receive the broadcast & start your service in it's `onReceive()`.
        }
    }
}
