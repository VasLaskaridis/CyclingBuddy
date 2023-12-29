package com.example.cyclingbuddy.db.Journey

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journey_table")
data class JourneyData(
    var longitude: Double = 0.0,
    var latitude: Double =0.0,
    var timestamp: Long){

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
