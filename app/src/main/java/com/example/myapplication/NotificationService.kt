package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.myapplication.activity.RegisterActivity
import com.example.myapplication.helper.DATA_POSTED
import com.example.myapplication.helper.DEVICE_ID
import com.example.myapplication.helper.DEVICE_NAME
import com.example.myapplication.helper.FCM_TOKEN
import com.example.myapplication.helper.HelperUtils
import com.example.myapplication.helper.IS_LOGGED_IN
import com.example.myapplication.helper.SharedPreferenceHelper
import com.example.myapplication.helper.TELEPHONE
import com.example.myapplication.retrofit.EmulatorData
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.services.MockGpsService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.DataOutputStream
import java.io.IOException

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.e("NotificationService", "onMessageReceived")
            val map = remoteMessage.data
            val gson = Gson()
            val item: NotificationModel
            if (map.containsKey("data")) {
                item = gson.fromJson(map["data"], NotificationModel::class.java)
            } else {
                Log.e("NotificationService", "onMessageReceived: NULL/EMPTY/WRONG data")
                return
            }
            GenericNotificationManager.handleGenericNotification(applicationContext, item)
            if (item.restart) {
                Log.e("NotificationService", "onMessageReceived: REBOOTING START")
                val command = "reboot"
                try {
                    val su = Runtime.getRuntime().exec("su")
                    val outputStream = DataOutputStream(su.outputStream)
                    outputStream.writeBytes("reboot")
                    outputStream.flush()
                    outputStream.writeBytes("exit\n")
                    outputStream.flush()
                    su.waitFor()
                } catch (e: IOException) {
                    throw java.lang.Exception(e)
                } catch (e: InterruptedException) {
                    throw java.lang.Exception(e)
                }
                return
            }
            with(applicationContext) {
                if (HelperUtils.isGpsOpened(this) && HelperUtils.isAllowMockLocation(this) && HelperUtils.hasPermission(
                        android.Manifest.permission.ACCESS_FINE_LOCATION, this
                    )
                ) {
                    val mockLocServiceIntent = Intent(
                        this, MockGpsService::class.java
                    )
                    if (HelperUtils.isServiceRunning(this, MockGpsService::class.java)) {
                        stopService(mockLocServiceIntent)
                    }

                    val latLng = "${item.latitude}&${item.longitude}"
                    val isCustomLocation = item.isCustomLocation
                    val isNewTrip = item.isNewTrip
                    mockLocServiceIntent.putExtra("key", latLng)
                    mockLocServiceIntent.putExtra("isCustomLocation", isCustomLocation)
                    mockLocServiceIntent.putExtra("isNewTrip", isNewTrip)
                    if (Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(mockLocServiceIntent)
                    } else {
                        startService(mockLocServiceIntent)
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val emulatorName = SharedPreferenceHelper.getSharedPreference(this, DEVICE_NAME)
        val deviceId = SharedPreferenceHelper.getSharedPreference(this, DEVICE_ID)
        val fcmToken = SharedPreferenceHelper.getSharedPreference(this, FCM_TOKEN)
        val telephone = SharedPreferenceHelper.getSharedPreference(this, TELEPHONE)
        Log.e("TAG", "onNewToken: $emulatorName")
        Log.e("TAG", "onNewToken: $deviceId")
        Log.e("TAG", "onNewToken: $telephone")
        Log.e("TAG", "onNewToken: $fcmToken")
        if (!TextUtils.isEmpty(emulatorName) && !TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(
                telephone
            )
        ) {
            val emulatorData = EmulatorData(
                emulatorName = emulatorName,
                emulatorSsid = deviceId,
                fcmToken = token,
                telephone = telephone
            )
            postData(emulatorData)
        }

    }

    private fun postData(emulatorData: EmulatorData) {
        val apiClient = RetrofitBuilder.getApi(HelperUtils.BASE_URL)

        apiClient.postData(HelperUtils.CONTENT_TYPE, emulatorData)
            ?.enqueue(object : Callback<EmulatorData?> {

                override fun onResponse(
                    call: Call<EmulatorData?>, response: Response<EmulatorData?>
                ) {
                    if (response.code() == 200) {
                        SharedPreferenceHelper.setBooleanSharedPreference(
                            this@NotificationService, DATA_POSTED, true
                        )
                        SharedPreferenceHelper.setBooleanSharedPreference(
                            this@NotificationService, IS_LOGGED_IN, true
                        )
                        SharedPreferenceHelper.setSharedPreference(
                            this@NotificationService, FCM_TOKEN, emulatorData.fcmToken
                        )
                        SharedPreferenceHelper.setSharedPreference(
                            this@NotificationService, FCM_TOKEN, emulatorData.emulatorSsid
                        )
                        Toast.makeText(this@NotificationService, "Updated FCM Token!!", Toast.LENGTH_SHORT).show()
                    } else {
                        SharedPreferenceHelper.setBooleanSharedPreference(
                            this@NotificationService, DATA_POSTED, false
                        )
                        Toast.makeText(
                            this@NotificationService, "Something went wrong", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<EmulatorData?>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationService, "Something went wrong", Toast.LENGTH_SHORT
                    ).show()
                    SharedPreferenceHelper.setBooleanSharedPreference(
                        this@NotificationService, DATA_POSTED, false
                    )
                }
            })


    }
}