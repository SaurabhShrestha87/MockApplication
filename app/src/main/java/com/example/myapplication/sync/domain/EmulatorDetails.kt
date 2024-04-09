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

package com.example.myapplication.sync.domain

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * EmulatorDetails represent a user detail that can be used.
 */
data class EmulatorDetails(
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String,
    val lastModifiedBy: String,
    val id: Int,
    val emulatorName: String,
    val emulatorSsid: String,
    val fcmToken: String,
    val latitude: Double?,
    val longitude: Double?,
    val telephone: String,
    val status: String,
    val startLat: Double,
    val startLong: Double,
    val endLat: Double,
    val endLong: Double,
    val speed: Double,
    val currentTripPointIndex: Int,
    val tripStatus: TripStatus,
    val tripTime: Int
) {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String
        get() =  "$startLat, $startLong -> $endLat, $endLong"
    /**
     * notification Title is used for displaying notification's Title in the UI
     */
    val notificationTitle: String
        get() =  "$startLat, $startLong -> $endLat, $endLong"
    /**
     * notification Desc is used for displaying notification's Text/Desc in the UI
     */
    val notificationDesc: String
        get() =  "$speed M/Hr, $telephone, ($tripStatus)"
}