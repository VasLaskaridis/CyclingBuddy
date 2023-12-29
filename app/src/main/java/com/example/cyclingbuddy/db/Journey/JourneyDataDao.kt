package com.example.cyclingbuddy.db.Journey

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JourneyDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journey: JourneyData)

    @Delete
    suspend fun deleteJourney(journey: JourneyData)

    @Query("SELECT * FROM journey_table WHERE timestamp =:timestamp ORDER BY id")
    fun getJourney(timestamp: Long): LiveData<List<JourneyData>>
}