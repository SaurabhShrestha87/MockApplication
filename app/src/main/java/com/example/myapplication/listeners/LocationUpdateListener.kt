package com.example.myapplication.listeners

import android.location.Location

interface LocationUpdateListener {
    fun onLocationUpdate(location: Location)
}