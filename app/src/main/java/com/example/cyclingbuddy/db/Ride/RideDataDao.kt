package com.example.cyclingbuddy.db.Ride

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RideDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RideData)

    @Delete
    suspend fun deleteRun(run: RideData)

    @Query("SELECT * FROM user_rides_table ORDER BY runStartTimestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<RideData>>

    @Query("SELECT * FROM user_rides_table ORDER BY runTime DESC")
    fun getAllRunsSortedByRunTime(): LiveData<List<RideData>>

    @Query("SELECT * FROM user_rides_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<RideData>>

    @Query("SELECT * FROM user_rides_table ORDER BY averageSpeed DESC")
    fun getAllRunsSortedByAverageSpeed(): LiveData<List<RideData>>

    @Query("SELECT * FROM user_rides_table ORDER BY distance DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<RideData>>

    @Query("SELECT SUM(runTime) FROM user_rides_table")
    fun getTotalRunTime(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM user_rides_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM user_rides_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(averageSpeed) FROM user_rides_table")
    fun getTotalAverageSpeed(): LiveData<Float>
}