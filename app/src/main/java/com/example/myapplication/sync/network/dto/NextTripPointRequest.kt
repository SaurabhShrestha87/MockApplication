package com.example.myapplication.sync.network.dto

import com.google.gson.annotations.SerializedName

data class NextTripPointRequest(
    @SerializedName("emulatorId") val emulatorId: Long,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String,
    @SerializedName("nextTripPointIndex") val nextTripPointIndex: Int,
)

data class AddressUpdateRequest(
    @SerializedName("emulatorSsid") val emulatorSsid: String,
    @SerializedName("address") val address: String,
)

data class NextTripPointResult(
    @SerializedName("emulatorDetails") val emulatorDetails: NetworkEmulatorDetails,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
)
