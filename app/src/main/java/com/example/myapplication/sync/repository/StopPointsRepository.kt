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
import com.example.myapplication.sync.domain.StopPoint
import com.example.myapplication.sync.entity.DatabaseStopPoint

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
class StopPointsRepository(private val database: TrackSpotMobileDatabase) {

    var stopPoints: LiveData<List<StopPoint>> = database.stopPointDao.getStopPoints().map {
        it.asDomainModel()
    }

    /**
     * Map DatabaseVideos to domain entities
     */
    private fun List<DatabaseStopPoint>.asDomainModel(): List<StopPoint> {
        return map {
            StopPoint(
                lat = it.lat, lng = it.lng, bearing = it.bearing, updated = "UPDATED"
            )
        }
    }

}