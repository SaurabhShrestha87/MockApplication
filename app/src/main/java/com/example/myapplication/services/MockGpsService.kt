package com.example.myapplication.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Latlng
import com.example.myapplication.R
import com.example.myapplication.helper.APPLICATION_ID
import com.example.myapplication.helper.DEVICE_ID
import com.example.myapplication.sync.database.getDatabase
import com.example.myapplication.sync.domain.StopPoint
import com.example.myapplication.sync.domain.TripPoint
import com.example.myapplication.sync.domain.TripStatus
import com.example.myapplication.sync.entity.STATUS
import com.example.myapplication.sync.network.dto.AddressUpdateRequest
import com.example.myapplication.sync.repository.SyncRepository
import com.example.myapplication.sync.viewmodels.DevByteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class MockGpsService : LifecycleService() {
    private var latLng: Latlng? = null
    private val TAG = "MockGpsService"
    private lateinit var locationManager: LocationManager

    private var viewModel: DevByteViewModel? = null

    // Custom CoroutineScope tied to the service's lifecycle
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var destroyService = false

    //    private var emulatorDetails: EmulatorDetails? = null
    var tripPoints: List<TripPoint>? = null

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    //经纬度字符串
    private var latLngInfo: String? = null
    private var isCustomLocation: Boolean = false
    private var isNewTrip: Boolean = false

    companion object {
        lateinit var notificationManager: NotificationManager

        //        lateinit var emulatorRepository: EmulatorRepository
//        lateinit var tripPointsRepository: TripPointsRepository
//        lateinit var stopPointsRepository: StopPointsRepository
        lateinit var syncRepository: SyncRepository
        lateinit var sharedPreferences: SharedPreferences

        const val NOTIFICATION_ID: Int = 1
    }

    //log debug
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).i("onCreate: STARTED!")
        sharedPreferences = getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        syncRepository = SyncRepository(getDatabase(application), sharedPreferences)
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        viewModel = DevByteViewModel(application, sharedPreferences)
        rmNetworkTestProvider()
        rmGPSTestProvider()
        setNetworkTestProvider()
        setGPSTestProvider()

        eventNetworkError.observe(this) { networkError ->
            if (networkError) {
                displayToast("Network Connection Not available.")
            }
        }

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (isCustomLocation) {
            setGpsLocation()
            setNetworkLocation()
        }
        serviceScope.launch {
            // Schedule periodic location updates
            while (!destroyService) {
                setMockLocation(LocationManager.NETWORK_PROVIDER)
                setMockLocation(LocationManager.GPS_PROVIDER)
                delay(100)
            }
        }
        viewModel?.sseEvents?.observe(this) {
            it?.let { event ->
                when (event.status) {
                    STATUS.OPEN -> {
                        Timber.tag(TAG).i("SSE: session opened")
                        displayToast("Session opened")
                    }

                    STATUS.SUCCESS -> {
                        Timber.tag(TAG).i("SSE: Data received")
                        displayToast("Data received" + event.latLng.toString())
                        if (event.latLng != null) {
                            this.latLng = event.latLng
                            locationManager.setTestProviderLocation(
                                LocationManager.NETWORK_PROVIDER,
                                generateLocation(event.latLng, LocationManager.NETWORK_PROVIDER)
                            )

                            locationManager.setTestProviderLocation(
                                LocationManager.GPS_PROVIDER,
                                generateLocation(event.latLng, LocationManager.GPS_PROVIDER)
                            )

                            sendNewAddressString(event.latLng)
                        } else {
                            displayToast("No image received")
                        }
                    }

                    STATUS.ERROR -> {
                        Timber.tag(TAG).e("onCreate: Error Connecting to SSE")
                        displayToast("Session Error, reconnecting...")
                    }

                    STATUS.CLOSED -> {
                        Timber.tag(TAG).e("onCreate: Session None")
                        displayToast("Session closed")
                    }

                    else -> {
                        // STATUS.NONE
                        Timber.tag(TAG).e("onCreate: Session None")
                        displayToast("Session None")
                    }
                }
            }
        }
        viewModel?.getSSEEvents()
    }

    private fun sendNewAddressString(latLng: Latlng) {
        val request = sharedPreferences.getString(DEVICE_ID, null)?.let {
            AddressUpdateRequest(
                emulatorSsid = it, address = getAddressString(
                    latLng.latitude, latLng.longitude
                )
            )
        }
        if (request == null) {
            displayToast("Unable to update address")
            return
        }
        try {
            displayToast("Updating emulator Address")
            CoroutineScope(Dispatchers.IO).launch {
                syncRepository.updateAddress(request)
            }
        } catch (error: IOException) {
            error.printStackTrace()
            displayToast("Unable to update address")
        }
    }

    private fun getAddressString(latitude: Double, longitude: Double): String {
        try {
            val geocoder = Geocoder(this) // Replace 'context' with your actual context
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            var addressString = ""
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                // Extract the AddressComponentType for each address component
                for ((index, i) in (0..address.maxAddressLineIndex).withIndex()) {
                    if (index != address.maxAddressLineIndex) {
                        addressString = addressString + (address.getAddressLine(i)) + " ,"
                    } else {
                        addressString += (address.getAddressLine(i))
                    }
                }
            }
            Timber.e("addressString : %s", addressString)
            return addressString
        } catch (e: Exception) {
            return "N/A"
        }
    }

    // Function to set mock location based on the provider (NETWORK_PROVIDER or GPS_PROVIDER)
    private fun setMockLocation(provider: String) {
        if (latLng == null) {
            return
        }
        try {
            locationManager.setTestProviderLocation(
                provider, generateLocation(latLng!!, provider)
            )
        } catch (e: Exception) {
            Timber.e("setMockLocation error: $provider")
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        latLngInfo = intent?.getStringExtra("key")
        isCustomLocation = intent?.getBooleanExtra("isCustomLocation", false) == true
        isNewTrip = intent?.getBooleanExtra("isNewTrip", false) == true
        val channelId = resources.getString(R.string.custom_notification_channel_id)
        val name = resources.getString(R.string.custom_notification_channel_name)
        val title = if (isNewTrip) {
            "Mocking New Trip"
        } else {
            "Mocking Trip "
        }
        val desc =
            "Speed :telephone "
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(mChannel)
            Notification.Builder(this).setChannelId(channelId).setContentTitle(title)
                .setContentText(desc).setSmallIcon(R.mipmap.ic_launcher).build()
        } else {
            val notificationBuilder =
                NotificationCompat.Builder(this).setContentTitle(title).setContentText(desc)
                    .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
                    .setChannelId(channelId) //无效
            notificationBuilder.build()
        }
        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        destroyService = true
        //remove test provider
        rmNetworkTestProvider()
        rmGPSTestProvider()
        viewModel?.stopSSERequest()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    //remove network provider
    private fun rmNetworkTestProvider() { //#1
        try {
            val providerStr = LocationManager.NETWORK_PROVIDER
            if (locationManager.isProviderEnabled(providerStr)) {
                Log.d(TAG, "now remove NetworkProvider")
                locationManager.removeTestProvider(providerStr)
            } else {
                Log.d(TAG, "NetworkProvider is not enabled")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "rmNetworkProvider error")
        }
    }

    // for test: set GPS provider
    private fun rmGPSTestProvider() { //#2
        try {
            val providerStr = LocationManager.GPS_PROVIDER
            if (locationManager.isProviderEnabled(providerStr)) {
                Log.d(TAG, "now remove GPSProvider")
                locationManager.removeTestProvider(providerStr)
            } else {
                Log.d(TAG, "GPSProvider is not enabled")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "rmGPSProvider error")
        }
    }

    //set new network provider
    private fun setNetworkTestProvider() { //#3
        val providerStr = LocationManager.NETWORK_PROVIDER
        try {
            locationManager.addTestProvider(
                providerStr,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                1,
                ProviderProperties.ACCURACY_FINE
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        if (!locationManager.isProviderEnabled(providerStr)) {
            try {
                locationManager.setTestProviderEnabled(providerStr, true)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "setTestProviderEnabled[NETWORK_PROVIDER] error")
            }
        }
    }

    private fun setGPSTestProvider() {//#4
        try {
            locationManager.addTestProvider(
                LocationManager.GPS_PROVIDER, false, true, true, false, true, true, true, 1, 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        locationManager.setTestProviderStatus(
            LocationManager.GPS_PROVIDER,
            LocationProvider.AVAILABLE,
            null,
            System.currentTimeMillis()
        )
    }

    private fun setNetworkLocation() {
        if(latLng == null) {
            return
        }
        val providerStr = LocationManager.NETWORK_PROVIDER
        try {
            locationManager.setTestProviderLocation(
                providerStr, generateLocation(latLng!!, providerStr)
            )
        } catch (e: Exception) {
            Log.d(TAG, "setNetworkLocation error")
            e.printStackTrace()
        }
    }

    private fun setGpsLocation() {
        if(latLng == null) {
            return
        }
        val providerStr = LocationManager.GPS_PROVIDER
        try {
            locationManager.setTestProviderLocation(
                providerStr, generateLocation(latLng!!, providerStr)
            )
        } catch (e: Exception) {
            Log.d(TAG, "setNetworkLocation error")
            e.printStackTrace()
        }
    }

    private fun generateLocation(latLng: Latlng, provider: String): Location { //#6
        val loc = Location(provider)
        loc.accuracy = 2.0f
        loc.altitude = 55.0
        loc.bearing = latLng.bearing
        val bundle = Bundle()
        bundle.putInt("satellites", 7)
        loc.extras = bundle
        loc.latitude = latLng.latitude
        loc.longitude = latLng.longitude
        loc.time = System.currentTimeMillis()
        loc.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        return loc
    }

    private fun displayToast(str: String?) {
//        val toast = Toast.makeText(this, str, Toast.LENGTH_SHORT)
//        toast.show()
    }
}