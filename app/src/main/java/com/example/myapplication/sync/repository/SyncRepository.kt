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

import android.content.SharedPreferences
import android.util.Log
import com.example.myapplication.helper.DEVICE_ID
import com.example.myapplication.sync.database.TrackSpotMobileDatabase
import com.example.myapplication.sync.network.SyncNetworkRetrofit
import com.example.myapplication.sync.network.asDatabaseModel
import com.example.myapplication.sync.network.dto.AddressUpdateRequest
import com.example.myapplication.sync.network.dto.CurrentLocationUpdateRequest
import com.example.myapplication.sync.network.dto.NextTripPointRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
class SyncRepository(
    private val database: TrackSpotMobileDatabase, private val sharedPreferences: SharedPreferences
) {

    suspend fun syncDatabase() {
        withContext(Dispatchers.IO) {
            val syncedResult = sharedPreferences.getString(
                DEVICE_ID, null
            )?.let { SyncNetworkRetrofit.retrofitService.syncDatabase(it) } ?: return@withContext
            try {
                val networkSyncResult = syncedResult.asDatabaseModel()
                database.tripPointDao.truncateTable()
                networkSyncResult.tripPoints?.let { database.tripPointDao.insertAll(it) }
                database.stopPointDao.truncateTable()
                networkSyncResult.stopPoints?.let { database.stopPointDao.insertAll(it) }
                database.emulatorDao.truncateTable()
                database.emulatorDao.insertAll(listOf(networkSyncResult.emulatorDetails))
                Log.e("@SyncRepo", "refreshDataFromRepository: Refreshed")
            } catch (e : Exception) {
                Log.e("@SyncRepo", "refreshDataFromRepository: Exception : $e")
            }
        }
    }

//    suspend fun updateNextTripPoint(request: NextTripPointRequest) {
//        withContext(Dispatchers.IO) {
//            try {
//                Timber.i("updateNextTripPoint: Request : %s", request.toString())
//                SyncNetworkRetrofit.retrofitService.updateNextTripPointIndex(request)
//            } catch (e: HttpException) {
//                Timber.e("updateNextTripPoint: HttpException : %s", e)
//            } catch (e: Exception) {
//                Timber.e("updateNextTripPoint: Exception : %s", e )
//            }
//        }
//    }


    suspend fun updateAddress(request: AddressUpdateRequest) {
        withContext(Dispatchers.IO) {
            try {
                Timber.i("updateAddress: Request : %s", request.toString())
                SyncNetworkRetrofit.retrofitService.updateAddress(request)
            } catch (e: HttpException) {
                Timber.e("updateAddress: HttpException : %s", e)
            } catch (e: Exception) {
                Timber.e("updateAddress: Exception : %s", e )
            }
        }
    }
}