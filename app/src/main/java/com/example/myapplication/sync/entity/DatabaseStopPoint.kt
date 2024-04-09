package com.example.myapplication.sync.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * DatabaseStopPoint represents a stop entity in the database.
 */
@Entity
@TypeConverters(
    StringListConverter::class,
    DatabaseStopPoint.AddressConverter::class,
    DatabaseStopPoint.GasStationConverter::class,
    DatabaseStopPoint.TripPointConverter::class
)
data class DatabaseStopPoint constructor(
    @PrimaryKey val lat: Double,
    val lng: Double,
    val bearing: Double,
    val address: List<DatabaseAddress>,
    val gasStation: List<DatabaseGasStation>,
    val tripPoints: List<DatabaseTripPoint>
) {
    @Entity
    @TypeConverters(AddressConverter::class)
    data class DatabaseAddress(
        val types: List<String>, val long_name: String, val short_name: String
    )

    @Entity
    data class DatabaseGasStation(
        val types: List<String>, val long_name: String, val short_name: String
    )

    @Entity
    data class DatabaseTripPoint(
        val lat: Double, val lng: Double, val bearing: Double
    )

    class AddressConverter {
        @TypeConverter
        fun fromString(value: String?): List<DatabaseAddress>? {
            return value?.let { jsonString ->
                val typeToken = object : TypeToken<List<DatabaseAddress>>() {}.type
                Gson().fromJson(jsonString, typeToken)
            }
        }

        @TypeConverter
        fun toString(addressList: List<DatabaseAddress>?): String? {
            return addressList?.let { Gson().toJson(it) }
        }
    }

    class GasStationConverter {
        @TypeConverter
        fun fromString(value: String?): List<DatabaseGasStation>? {
            return value?.let { jsonString ->
                val typeToken = object : TypeToken<List<DatabaseGasStation>>() {}.type
                Gson().fromJson(jsonString, typeToken)
            }
        }

        @TypeConverter
        fun toString(addressList: List<DatabaseGasStation>?): String? {
            return addressList?.let { Gson().toJson(it) }
        }
    }

    class TripPointConverter {
        @TypeConverter
        fun fromString(value: String?): List<DatabaseTripPoint>? {
            return value?.let { jsonString ->
                val typeToken = object : TypeToken<List<DatabaseTripPoint>>() {}.type
                Gson().fromJson(jsonString, typeToken)
            }
        }

        @TypeConverter
        fun toString(addressList: List<DatabaseTripPoint>?): String? {
            return addressList?.let { Gson().toJson(it) }
        }
    }

}