package com.example.myapplication.helper

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object HelperUtils {
    const val READ_PHONE_REQUEST_CODE = 109
    const val CONTENT_TYPE = "application/json"
    const val BASE_URL_LIVE = "http://64.226.101.239:8080/emulator/"
    const val BASE_URL_LIVE_CLIENT = "https://www.logbookgps.com/api/emulator/"
    const val DEV_BASE_URL = "http://192.168.1.125:8080/emulator/"
    private const val DEV_BASE_URL_2 = "http://192.168.1.132:8080/emulator/"
    private const val BASE_URL_AWS = "https://logbookgps.com:8081/emulator/"
    const val BASE_URL = BASE_URL_AWS
    const val SSE_URL = BASE_URL + "sse/"
    fun isAllowMockLocation(context: Context): Boolean {
        var canMockPosition: Boolean
        try {
            val locationManager =
                context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager //获得LocationManager引用
            val providerStr = LocationManager.GPS_PROVIDER
            val provider = locationManager.getProvider(providerStr)
            try {
                locationManager.removeTestProvider(providerStr)
                Log.d("PERMISSION", "try to move test provider")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("PERMISSION", "try to move test provider")
            }
            if (provider != null) {
                try {
                    locationManager.addTestProvider(
                        provider.name,
                        provider.requiresNetwork(),
                        provider.requiresSatellite(),
                        provider.requiresCell(),
                        provider.hasMonetaryCost(),
                        provider.supportsAltitude(),
                        provider.supportsSpeed(),
                        provider.supportsBearing(),
                        provider.powerRequirement,
                        provider.accuracy
                    )
                    canMockPosition = true
                } catch (e: java.lang.Exception) {
                    Log.e("@Trackspot.Error", "add origin gps test provider error")
                    canMockPosition = false
                    e.printStackTrace()
                }
            } else {
                try {
                    locationManager.addTestProvider(
                        providerStr,
                        true,
                        true,
                        false,
                        false,
                        true,
                        true,
                        true,
                        Criteria.POWER_HIGH,
                        Criteria.ACCURACY_FINE
                    )
                    canMockPosition = true
                } catch (e: java.lang.Exception) {
                    Log.e("@Trackspot.Error", "add gps test provider error")
                    canMockPosition = false
                    e.printStackTrace()
                }
            }

            // 模拟位置可用
            if (canMockPosition) {
                locationManager.setTestProviderEnabled(providerStr, true)
                locationManager.setTestProviderStatus(
                    providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis()
                )
                //remove test provider
                locationManager.setTestProviderEnabled(providerStr, false)
                locationManager.removeTestProvider(providerStr)
            }
        } catch (e: SecurityException) {
            canMockPosition = false
            e.printStackTrace()
        }
        return canMockPosition
    }

    fun hasPermission(permission: String, context: Context): Boolean {
        try {
            if (ContextCompat.checkSelfPermission(
                    context, permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun isGpsOpened(context: Context): Boolean {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        return runningServices.any { it.service.className == serviceClass.name }

    }

    fun getFormattedTime(currentTimeMillis: Long): String {
       // format time to human readable using datetime formatter
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.US)
        return formatter.format(currentTimeMillis)
    }


}