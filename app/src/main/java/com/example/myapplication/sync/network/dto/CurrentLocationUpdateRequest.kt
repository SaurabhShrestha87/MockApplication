package com.example.myapplication.sync.network.dto

import com.google.gson.annotations.SerializedName

data class CurrentLocationUpdateRequest(
    @SerializedName("emulatorId") val emulatorId: Long,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String
)

data class CurrentLocationUpdateResult(
    @SerializedName("emulatorId") val emulatorId: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)
