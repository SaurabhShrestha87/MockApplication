package com.example.myapplication.sync.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.sync.entity.DatabaseTripPoint

@Dao
interface TripPointDao {
    @Query("select * from databasetrippoint")
    fun getTripPoints(): LiveData<List<DatabaseTripPoint>>

    @Query("DELETE FROM databasetrippoint")
    fun truncateTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( videos: List<DatabaseTripPoint>)
}