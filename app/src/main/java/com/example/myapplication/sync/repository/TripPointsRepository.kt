/*
 * Copyright (C) 2019 Google Inc.
 *gi
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

package com.example.myapplication.sync.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.example.myapplication.sync.database.TrackSpotMobileDatabase
import com.example.myapplication.sync.domain.EmulatorDetails
import com.example.myapplication.sync.domain.StopPoint
import com.example.myapplication.sync.domain.TripPoint
import com.example.myapplication.sync.domain.TripPointStatus
import com.example.myapplication.sync.entity.DatabaseEmulatorDetails
import com.example.myapplication.sync.entity.DatabaseStopPoint
import com.example.myapplication.sync.entity.DatabaseTripPoint

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
class TripPointsRepository(private val database: TrackSpotMobileDatabase) {

    private val TAG = "@SERVICE MockGpsService"

    var tripPoints: LiveData<List<TripPoint>> =
        database.tripPointDao.getTripPoints().map {
            it.asDomainTripPointModel()
        }


    var route: LiveData<List<TripPoint>> = MediatorLiveData<List<TripPoint>>().apply {
        var tripPoints: List<TripPoint>? = null
        var stopPoints: List<StopPoint>? = null
        var emulatorDetails: EmulatorDetails? = null

        val updateRoute: () -> Unit = {
            val modifiedRoute = modifyRoute(tripPoints, stopPoints, emulatorDetails)
            value = modifiedRoute
        }

        addSource(database.tripPointDao.getTripPoints()) { newTripPoints ->
            tripPoints = newTripPoints?.asDomainTripPointModel()
            updateRoute()
        }

        addSource(database.stopPointDao.getStopPoints()) { newStopPoints ->
            stopPoints = newStopPoints?.asDomainDatabaseStopPointModel()
            updateRoute()
        }

        addSource(database.emulatorDao.getEmulatorDetails()) { newEmulatorDetails ->
            emulatorDetails = newEmulatorDetails?.asDomainEmulatorDetailsModel()?.firstOrNull()
            updateRoute()
        }
    }

    private fun modifyRoute(
        tripPoints: List<TripPoint>?,
        stopPoints: List<StopPoint>?,
        emulatorDetails: EmulatorDetails?
    ): List<TripPoint> {
        val modifiedTripPoints = tripPoints?.toMutableList() ?: mutableListOf()
        emulatorDetails.let {
            if (it != null) {
                for (tripPoint in modifiedTripPoints) {
                    // Update the status based on tripPointIndex and emulator's currentTripPointIndex
                    when {
                        tripPoint.tripPointIndex < it.currentTripPointIndex -> {
                            tripPoint.status = TripPointStatus.PASSED
                        }

                        tripPoint.tripPointIndex == it.currentTripPointIndex -> {
                            tripPoint.status = TripPointStatus.CURRENT
                        }

                        else -> {
                            if (it.currentTripPointIndex < 0) {
                                tripPoint.status = TripPointStatus.UNAWARE
                            }
                            tripPoint.status = TripPointStatus.FUTURE
                        }
                    }
                }
            }
        }
        // Modify the trip points based on the emulator details
        return modifiedTripPoints
    }

    private fun List<DatabaseTripPoint>.asDomainTripPointModel(): List<TripPoint> {
        return map {
            TripPoint(
                tripPointIndex = it.tripPointIndex,
                lat = it.lat,
                lng = it.lng,
                bearing = it.bearing,
                updated = "UPDATED"
            )
        }
    }

    private fun List<DatabaseEmulatorDetails>.asDomainEmulatorDetailsModel(): List<EmulatorDetails> {
        return map {
            EmulatorDetails(
                id = it.id,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                createdBy = it.createdBy,
                lastModifiedBy = it.lastModifiedBy,
                emulatorName = it.emulatorName,
                emulatorSsid = it.emulatorSsid,
                fcmToken = it.fcmToken,
                latitude = it.latitude,
                longitude = it.longitude,
                telephone = it.telephone,
                status = it.status,
                startLat = it.startLat,
                startLong = it.startLong,
                endLat = it.endLat,
                endLong = it.endLong,
                speed = it.speed,
                currentTripPointIndex = it.currentTripPointIndex,
                tripStatus = it.tripStatus,
                tripTime = it.tripTime
            )
        }
    }

    /**
     * Map DatabaseVideos to domain entities
     */
    private fun List<DatabaseStopPoint>.asDomainDatabaseStopPointModel(): List<StopPoint> {
        return map {
            StopPoint(
                lat = it.lat, lng = it.lng, bearing = it.bearing, updated = "UPDATED"
            )
        }
    }
}