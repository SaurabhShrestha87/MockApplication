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

import com.example.myapplication.helper.HelperUtils
import com.example.myapplication.retrofit.EmulatorData
import com.example.myapplication.sync.network.dto.AddressUpdateRequest
import com.example.myapplication.sync.network.dto.CurrentLocationUpdateRequest
import com.example.myapplication.sync.network.dto.CurrentLocationUpdateResult
import com.example.myapplication.sync.network.dto.NetworkEmulatorDetails
import com.example.myapplication.sync.network.dto.NextTripPointRequest
import com.example.myapplication.sync.network.dto.NextTripPointResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface RetrofitService {
    @GET("sync/{userId}")
    suspend fun syncDatabase(@Path("userId") param: String): NetworkTripPointContainer

//    @POST("updateNextTripPointIndex")
//    suspend fun updateNextTripPointIndex(@Body request: NextTripPointRequest): NextTripPointResult

    @POST("updateAddress")
    suspend fun updateAddress(@Body request: AddressUpdateRequest): NextTripPointResult
}

/**
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
object SyncNetworkRetrofit {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
            .baseUrl(HelperUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

}
