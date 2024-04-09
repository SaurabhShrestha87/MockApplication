package com.example.myapplication.sync.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.myapplication.sync.domain.TripStatus

/**
 * DatabaseVideo represents a video entity in the database.
 */
@Entity
data class DatabaseEmulatorDetails constructor(
    @PrimaryKey val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String,
    val lastModifiedBy: String,
    val emulatorName: String,
    val emulatorSsid: String,
    val fcmToken: String,
    val latitude: Double?,
    val longitude: Double?,
    val telephone: String,
    val status: String,
    val userId: Int?,
    val startLat: Double,
    val startLong: Double,
    val endLat: Double,
    val endLong: Double,
    val speed: Double,
    val currentTripPointIndex: Int,
    val tripStatus: TripStatus,
    val tripTime: Int
) {

    @Ignore
    var user: DatabaseUser? = null

    @Entity
    data class DatabaseUser(
        @PrimaryKey val id: Int,
        val email: String,
        val firstName: String,
        val lastName: String,
        val telephone: String,
        val status: String,
        // Other user fields
    )
}