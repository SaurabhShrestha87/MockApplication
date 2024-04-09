/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.sync.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.sync.database.getDatabase
import com.example.myapplication.sync.entity.SSEEventData
import com.example.myapplication.sync.entity.STATUS
import com.example.myapplication.sync.repository.EmulatorRepository
import com.example.myapplication.sync.repository.SSERepository
import com.example.myapplication.sync.repository.StopPointsRepository
import com.example.myapplication.sync.repository.SyncRepository
import com.example.myapplication.sync.repository.TripPointsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.IOException


/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class DevByteViewModel(application: Application, sharedPreferences: SharedPreferences) :
    AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val emulatorRepository = EmulatorRepository(getDatabase(application))
    private val tripPointsRepository = TripPointsRepository(getDatabase(application))
    private val stopPointsRepository = StopPointsRepository(getDatabase(application))
    private val syncRepository = SyncRepository(getDatabase(application), sharedPreferences)

    val tripPoints = tripPointsRepository.tripPoints
    val route = tripPointsRepository.route
    val emulatorDetails = emulatorRepository.emulatorDetails

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

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
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                syncRepository.syncDatabase()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (networkError: IOException) {
//            // Show a Toast error message and hide the progress bar.
                if (tripPoints.value.isNullOrEmpty()) _eventNetworkError.value = true
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application, val sharedPreferences: SharedPreferences) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DevByteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return DevByteViewModel(app, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    private val repository = SSERepository(sharedPreferences)

    var sseEvents = MutableLiveData<SSEEventData>()
        private set

    fun getSSEEvents() = viewModelScope.launch {
        repository.sseEventsFlow
            .onEach { sseEventData ->
                sseEvents.postValue(sseEventData)
            }
            .catch {
                sseEvents.postValue(SSEEventData(status = STATUS.ERROR))
            }
            .launchIn(viewModelScope)
    }
    fun stopSSERequest() {
        repository.stopSSERequest()
    }
}
