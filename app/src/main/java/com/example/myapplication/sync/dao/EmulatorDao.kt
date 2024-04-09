package com.example.myapplication.sync.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.sync.entity.DatabaseEmulatorDetails

@Dao
interface EmulatorDao {
    @Query("SELECT * FROM databaseemulatordetails")
    fun getEmulatorDetails(): LiveData<List<DatabaseEmulatorDetails>>

    @Query("DELETE FROM databaseemulatordetails")
    fun truncateTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userDetails: List<DatabaseEmulatorDetails>)
}