package com.example.myapplication.sync.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DatabaseVideo represents a video entity in the database.
 */
@Entity
data class DatabaseTripPoint constructor(
    @PrimaryKey val tripPointIndex: Int, val lat: Double, val lng: Double, val bearing: Double
)