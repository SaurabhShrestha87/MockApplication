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

package com.example.myapplication.sync.network

import com.example.myapplication.sync.domain.TripPoint
import com.example.myapplication.sync.entity.DatabaseEmulatorDetails
import com.example.myapplication.sync.entity.DatabaseStopPoint
import com.example.myapplication.sync.entity.DatabaseTripPoint
import com.example.myapplication.sync.network.dto.NetworkEmulatorDetails
import com.example.myapplication.sync.network.dto.NetworkStopPoint
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkTripPointContainer(
    val tripPoints: List<NetworkTripPoint>?,
    val stopPoints: List<NetworkStopPoint>?,
    val emulatorDetails: NetworkEmulatorDetails
)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkTripPoint(
    val tripPointIndex: Int,
    val lat: Double,
    val lng: Double,
    val bearing: Double,
    val updated: String
)

data class NetworkSyncResult(
    val tripPoints: List<DatabaseTripPoint>?,
    val stopPoints: List<DatabaseStopPoint>?,
    val emulatorDetails: DatabaseEmulatorDetails
)

/**
 * Convert Network results to database objects
 */
fun NetworkTripPointContainer.asDomainModel(): List<TripPoint>? {
    return tripPoints?.map {
        TripPoint(
            tripPointIndex = it.tripPointIndex,
            lat = it.lat,
            lng = it.lng,
            bearing = it.bearing,
            updated = it.updated
        )
    }
}

/**
 * Convert Network results to database objects
 */
fun NetworkEmulatorDetails.asDatabaseModel(): DatabaseEmulatorDetails {
    return DatabaseEmulatorDetails(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        lastModifiedBy = lastModifiedBy,
        emulatorName = emulatorName,
        emulatorSsid = emulatorSsid,
        fcmToken = fcmToken,
        latitude = latitude,
        longitude = longitude,
        telephone = telephone,
        status = status,
        userId = user?.id,
        startLat = startLat,
        startLong = startLong,
        endLat = endLat,
        endLong = endLong,
        speed = speed,
        currentTripPointIndex = currentTripPointIndex,
        tripStatus = tripStatus,
        tripTime = tripTime
    )
}
fun NetworkTripPointContainer.asDatabaseModel(): NetworkSyncResult {
    val tripPointsDbModel = tripPoints?.map {
        DatabaseTripPoint(
            tripPointIndex = it.tripPointIndex, lat = it.lat, lng = it.lng, bearing = it.bearing
        )
    }

    val stopPointsDbModel = stopPoints?.map {
        val addressDbModel = it.address.map { address ->
            DatabaseStopPoint.DatabaseAddress(
                types = address.types,
                long_name = address.long_name,
                short_name = address.short_name
            )
        }

        val gasStationDbModel = it.gasStation.map { gasStation ->
            DatabaseStopPoint.DatabaseGasStation(
                types = gasStation.types,
                long_name = gasStation.long_name,
                short_name = gasStation.short_name
            )
        }

        val gasTripPointsDbModel = it.tripPoints.map { tripPoint ->
            DatabaseStopPoint.DatabaseTripPoint(
                lat = tripPoint.lat, lng = tripPoint.lng, bearing = tripPoint.bearing
            )
        }

        DatabaseStopPoint(
            lat = it.lat,
            lng = it.lng,
            bearing = it.bearing,
            address = addressDbModel,
            gasStation = gasStationDbModel,
            tripPoints = gasTripPointsDbModel
        )
    }

    val emulatorDetailsDbModel = DatabaseEmulatorDetails(
        id = emulatorDetails.id,
        createdAt = emulatorDetails.createdAt,
        updatedAt = emulatorDetails.updatedAt,
        createdBy = emulatorDetails.createdBy,
        lastModifiedBy = emulatorDetails.lastModifiedBy,
        emulatorName = emulatorDetails.emulatorName,
        emulatorSsid = emulatorDetails.emulatorSsid,
        fcmToken = emulatorDetails.fcmToken,
        latitude = emulatorDetails.latitude,
        longitude = emulatorDetails.longitude,
        telephone = emulatorDetails.telephone,
        status = emulatorDetails.status,
        userId = emulatorDetails.user?.id,
        startLat = emulatorDetails.startLat,
        startLong = emulatorDetails.startLong,
        endLat = emulatorDetails.endLat,
        endLong = emulatorDetails.endLong,
        speed = emulatorDetails.speed,
        currentTripPointIndex = emulatorDetails.currentTripPointIndex,
        tripStatus = emulatorDetails.tripStatus,
        tripTime = emulatorDetails.tripTime
    )

    return NetworkSyncResult(tripPointsDbModel, stopPointsDbModel, emulatorDetailsDbModel)
}

