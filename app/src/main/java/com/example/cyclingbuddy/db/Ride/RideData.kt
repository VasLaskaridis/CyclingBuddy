package com.example.cyclingbuddy.db.Ride

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_rides_table")
data class RideData(
    var runStartTimestamp:Long = 0,
    var averageSpeed:Float = 0f,
    var distance: Int = 0,
    var runTime:Long =0,
    var caloriesBurned: Int = 0)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
