package com.example.cyclingbuddy.repository


import androidx.lifecycle.LiveData
import com.example.cyclingbuddy.db.Journey.JourneyData
import com.example.cyclingbuddy.db.Journey.JourneyDataDao
import com.example.cyclingbuddy.db.Ride.RideDataDAO
import com.example.cyclingbuddy.db.Ride.RideData
import javax.inject.Inject

class Repository @Inject constructor(
    val rideDao: RideDataDAO,
    val journeyDao: JourneyDataDao
) {
    suspend fun insertRide(rideData: RideData) = rideDao.insertRun(rideData)

    suspend fun deleteRide(rideData: RideData) = rideDao.deleteRun(rideData)

    fun getAllRunsSortedByDate(): LiveData<List<RideData>>  { return rideDao.getAllRunsSortedByDate() }

    fun getTotalAverageSpeed() = rideDao.getTotalAverageSpeed()

    fun getTotalDistance() = rideDao.getTotalDistance()

    fun getTotalCaloriesBurned() = rideDao.getTotalCaloriesBurned()

    fun getTotalRunTime() = rideDao.getTotalRunTime()

    fun getJourney(timestamp: Long) =journeyDao.getJourney(timestamp)

    suspend fun insertJourney(journey: JourneyData) =journeyDao.insertJourney(journey)

    suspend fun deleteJourney(journey: JourneyData) =journeyDao.deleteJourney(journey)

}