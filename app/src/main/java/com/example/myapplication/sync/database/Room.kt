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

package com.example.myapplication.sync.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.sync.dao.EmulatorDao
import com.example.myapplication.sync.dao.StopPointDao
import com.example.myapplication.sync.dao.TripPointDao
import com.example.myapplication.sync.entity.DatabaseEmulatorDetails
import com.example.myapplication.sync.entity.DatabaseStopPoint
import com.example.myapplication.sync.entity.DatabaseTripPoint


@Database(
    entities = [DatabaseTripPoint::class, DatabaseStopPoint::class, DatabaseEmulatorDetails::class],
    version = 1,
    exportSchema = false
)
abstract class TrackSpotMobileDatabase : RoomDatabase() {
    abstract val tripPointDao: TripPointDao
    abstract val stopPointDao: StopPointDao
    abstract val emulatorDao: EmulatorDao
}

private lateinit var INSTANCE: TrackSpotMobileDatabase

fun getDatabase(context: Context): TrackSpotMobileDatabase {
    synchronized(TrackSpotMobileDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TrackSpotMobileDatabase::class.java,
                "videos"
            ).build()
        }
    }
    return INSTANCE
}
