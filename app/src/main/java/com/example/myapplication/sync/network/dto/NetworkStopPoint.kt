package com.example.myapplication.sync.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkStopPoint(
    val tripPointIndex: Int,
    val lat: Double,
    val lng: Double,
    val bearing: Double,
    val address: List<Address>,
    val gasStation: List<GasStation>,
    val tripPoints: List<TripPoint>
) {
    @JsonClass(generateAdapter = true)
    data class Address(
        val types: List<String>,
        val long_name: String,
        val short_name: String
    )

    @JsonClass(generateAdapter = true)
    data class GasStation(
        val types: List<String>,
        val long_name: String,
        val short_name: String
    )

    @JsonClass(generateAdapter = true)
    data class TripPoint(
        val lat: Double,
        val lng: Double,
        val bearing: Double
    )
}