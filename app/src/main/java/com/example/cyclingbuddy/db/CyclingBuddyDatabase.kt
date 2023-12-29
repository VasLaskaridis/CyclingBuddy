package com.example.cyclingbuddy.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cyclingbuddy.db.Journey.JourneyData
import com.example.cyclingbuddy.db.Journey.JourneyDataDao
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.db.Ride.RideDataDAO

@Database(entities = [RideData::class, JourneyData::class], version = 1)
abstract class CyclingBuddyDatabase : RoomDatabase(){
    abstract fun getRideDao(): RideDataDAO
    abstract fun getJourneyDao(): JourneyDataDao
}