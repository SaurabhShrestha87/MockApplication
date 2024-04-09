package com.example.myapplication.helper

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.example.myapplication.retrofit.EmulatorData

const val APPLICATION_ID = "com.videomaster.editor"
const val DATA_POSTED = "data posted"
const val DEVICE_ID = "device id"
const val DEVICE_NAME = "device name"
const val FCM_TOKEN = "fcm token"
const val IS_LOGGED_IN = "is logged in"
const val TELEPHONE = "telephone no"


object SharedPreferenceHelper {

    fun setSharedPreference(ctx: Context, Key: String?, Value: String?) {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(Key, Value)
        editor.apply()
    }

    fun getSharedPreference(ctx: Context, Key: String?): String? {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        return if (pref.contains(Key)) {
            pref.getString(Key, "")
        } else ""
    }

    fun setBooleanSharedPreference(ctx: Context, Key: String?, Value: Boolean) {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Key, Value)
        editor.apply()
    }

    fun getBooleanSharedPreference(ctx: Context, Key: String?, defaultValue: Boolean): Boolean {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        return if (pref.contains(Key)) {
            pref.getBoolean(Key, defaultValue)
        } else defaultValue
    }

    @SuppressLint("HardwareIds")
    fun getEmulatorSharedPreference(ctx: Context): EmulatorData {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        var deviceId: String? = null
        var deviceName: String? = Settings.Secure.getString(
            ctx.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        var telephone: String? = "1234567890"
        var fcmToken: String? = "to be added"
        var uuid: String? = ""

        if (pref.contains(DEVICE_NAME)) {
            deviceName = getSharedPreference(ctx, DEVICE_NAME)
        }
        if (pref.contains(DEVICE_ID)) {
            deviceId = getSharedPreference(ctx, DEVICE_ID)
        }
        if (pref.contains(FCM_TOKEN)) {
            fcmToken = getSharedPreference(ctx, FCM_TOKEN)
        }
        if (pref.contains(TELEPHONE)) {
            telephone = getSharedPreference(ctx, TELEPHONE)
        }
        return EmulatorData(
            emulatorName = deviceName,
            emulatorSsid = deviceId,
            fcmToken = fcmToken,
            telephone = telephone,
            latitude = "29",
            longitude = "30"
        )
    }

}
