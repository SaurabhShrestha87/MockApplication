package com.example.myapplication.sync.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.myapplication.helper.DEVICE_ID
import com.example.myapplication.helper.HelperUtils.SSE_URL
import com.example.myapplication.helper.IS_LOGGED_IN
import com.example.myapplication.sync.entity.SSEEventData
import com.example.myapplication.sync.entity.STATUS
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class SSERepository(sharedPref: SharedPreferences) {

    private val sseClient =
        OkHttpClient.Builder().connectTimeout(6, TimeUnit.SECONDS).readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES).build()
    private val id = sharedPref.getString(DEVICE_ID, null)
    private val sseRequest =
        Request.Builder().method("GET", null).url(SSE_URL + id).header("Accept", "application/json")
            .addHeader("Accept", "text/event-stream").build()

    lateinit var eventSource: EventSource

    var sseEventsFlow = MutableStateFlow(SSEEventData(STATUS.NONE))
        private set

    private val sseEventSourceListener = object : EventSourceListener() {
        override fun onClosed(eventSource: EventSource) {
            super.onClosed(eventSource)
            Log.d(TAG, "onClosed: $eventSource")
            val event = SSEEventData(STATUS.CLOSED)
            sseEventsFlow.tryEmit(event)
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            super.onEvent(eventSource, id, type, data)
            Log.d(TAG, "onEvent: $data")
            if (data.isNotEmpty()) {
                val imageData = Gson().fromJson(data, SSEEventData::class.java)
                sseEventsFlow.tryEmit(imageData)
            }
        }

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            super.onFailure(eventSource, t, response)
            t?.printStackTrace()
            Log.i(TAG, "onFailure: ${sharedPref.getBoolean(IS_LOGGED_IN, true)}")
            if (sharedPref.getBoolean(IS_LOGGED_IN, true)) {
                // wait for 5 seconds and try to reconnect
                CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    initEventSource()
                }
                val event = SSEEventData(STATUS.ERROR)
                sseEventsFlow.tryEmit(event)
                return
            }
            val event = SSEEventData(STATUS.ERROR)
            sseEventsFlow.tryEmit(event)
            return
        }

        override fun onOpen(eventSource: EventSource, response: Response) {
            super.onOpen(eventSource, response)
            Log.d(TAG, "onOpen: $eventSource")
            val event = SSEEventData(STATUS.OPEN)
            sseEventsFlow.tryEmit(event)
        }
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            initEventSource()
        }
    }

    fun initEventSource() {
        val event = SSEEventData(STATUS.NONE)
        sseEventsFlow.tryEmit(event)
        eventSource = EventSources.createFactory(sseClient)
            .newEventSource(request = sseRequest, listener = sseEventSourceListener)
    }

    fun stopSSERequest() {
        eventSource.cancel()
        sseClient.dispatcher.executorService.shutdown()
    }

    companion object {
        private const val TAG = "SSERepository"
    }

}