package com.example.myapplication.sync.entity

import com.example.myapplication.Latlng

data class SSEEventData(
    val status: STATUS? = null,
    val latLng: Latlng? = null
)

enum class STATUS {
    SUCCESS,
    ERROR,
    NONE,
    CLOSED,
    OPEN
}