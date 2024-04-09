package com.example.myapplication.sync.network.dto

import com.example.myapplication.sync.domain.TripStatus
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkEmulatorDetails(
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
    val user: NetworkUser?,
    val startLat: Double,
    val startLong: Double,
    val endLat: Double,
    val endLong: Double,
    val speed: Double,
    val currentTripPointIndex: Int,
    val tripStatus: TripStatus,
    val tripTime: Int
) {
    @JsonClass(generateAdapter = true)
    data class NetworkUser(
        val createdAt: String,
        val updatedAt: String,
        val createdBy: String,
        val lastModifiedBy: String,
        val id: Int,
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val telephone: String,
        val status: String,
        val emulatorCount: Int?,
        val authorities: List<Authority>,
        val enabled: Boolean,
        val username: String,
        val accountNonExpired: Boolean,
        val accountNonLocked: Boolean,
        val credentialsNonExpired: Boolean
    ) {
        @JsonClass(generateAdapter = true)
        data class Authority(
            val authority: String
        )
    }
}