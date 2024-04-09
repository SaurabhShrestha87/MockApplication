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

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.myapplication.sync.database.TrackSpotMobileDatabase
import com.example.myapplication.sync.domain.EmulatorDetails
import com.example.myapplication.sync.entity.DatabaseEmulatorDetails

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
class EmulatorRepository(private val database: TrackSpotMobileDatabase) {

    var emulatorDetails: LiveData<List<EmulatorDetails>> =
        database.emulatorDao.getEmulatorDetails().map { it.asDomainModel() }

    /**
     * Map DatabaseEmulatorDetails to domain entities
     */
    private fun List<DatabaseEmulatorDetails>.asDomainModel(): List<EmulatorDetails> {
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
}