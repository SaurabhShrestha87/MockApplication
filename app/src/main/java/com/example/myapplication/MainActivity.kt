package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.activity.RegisterActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.helper.DATA_POSTED
import com.example.myapplication.helper.DEVICE_ID
import com.example.myapplication.helper.DEVICE_NAME
import com.example.myapplication.helper.HelperUtils.isServiceRunning
import com.example.myapplication.helper.IS_LOGGED_IN
import com.example.myapplication.helper.SharedPreferenceHelper
import com.example.myapplication.helper.TELEPHONE
import com.example.myapplication.services.MockGpsService
import com.example.myapplication.sync.ui.DevByteActivity

class MainActivity : AppCompatActivity() {

    private val mainActivityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivityMainBinding.root)

        val deviceId = SharedPreferenceHelper.getSharedPreference(this@MainActivity, DEVICE_ID)

        if (!deviceId.isNullOrBlank()) {
            mainActivityMainBinding.deviceIdView.text = "SSID :\n $deviceId"
        }

        mainActivityMainBinding.enter2Button.setOnClickListener {
            startActivity(Intent(this, DevByteActivity::class.java))
        }

        mainActivityMainBinding.logoutButton.setOnClickListener {
            stopService(Intent(this@MainActivity, MockGpsService::class.java))
            SharedPreferenceHelper.setBooleanSharedPreference(
                this@MainActivity, IS_LOGGED_IN, false
            )
            SharedPreferenceHelper.setBooleanSharedPreference(this@MainActivity, DATA_POSTED, false)
            SharedPreferenceHelper.setSharedPreference(this@MainActivity, DEVICE_NAME, "")
            SharedPreferenceHelper.setSharedPreference(this@MainActivity, TELEPHONE, "")
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        setupMockGpsService()
    }

    private fun setupMockGpsService() {
        val mockLocServiceIntent = Intent(this, MockGpsService::class.java)
        if (isServiceRunning(this, MockGpsService::class.java)) {
            stopService(mockLocServiceIntent)
        }
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(mockLocServiceIntent)
        } else {
            startService(mockLocServiceIntent)
        }
        Toast.makeText(this@MainActivity, "Location mocking is on", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        // Do nothing
    }
}