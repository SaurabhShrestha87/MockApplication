package com.example.myapplication.sync.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.sync.entity.DatabaseStopPoint

@Dao
interface StopPointDao {
    @Query("select * from databasestoppoint")
    fun getStopPoints(): LiveData<List<DatabaseStopPoint>>

    @Query("DELETE FROM databasestoppoint")
    fun truncateTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( videos: List<DatabaseStopPoint>)
}