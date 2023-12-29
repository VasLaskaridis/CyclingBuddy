package com.example.cyclingbuddy.ui.viewmodels


import androidx.lifecycle.MediatorLiveData
import com.example.cyclingbuddy.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyclingbuddy.db.Journey.JourneyData
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.util.SortingHeader
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: Repository
):ViewModel () {
    private val ridesSortedByDate = repository.getAllRunsSortedByDate()
    private val ridesSortedByDistance = repository.getAllRunsSortedByDistance()
    private val ridesSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val ridesSortedByRunTime = repository.getAllRunsSortedByRunTime()
    private val ridesSortedByAverageSpeed = repository.getAllRunsSortedByAverageSpeed()

    val rides = MediatorLiveData<List<RideData>>()

    var sortingHeader = SortingHeader.DATE

    init {
        rides.addSource(ridesSortedByDate) { result ->
            if(sortingHeader == SortingHeader.DATE) {
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByAverageSpeed) { result ->
            if(sortingHeader == SortingHeader.AVERAGE_SPEED) {
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByCaloriesBurned) { result ->
            if(sortingHeader == SortingHeader.CALORIES_BURNED) {
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByDistance) { result ->
            if(sortingHeader == SortingHeader.DISTANCE) {
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByRunTime) { result ->
            if(sortingHeader == SortingHeader.RUNNING_TIME) {
                result?.let { rides.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortingHeader) = when(sortingHeader) {
        SortingHeader.DATE -> ridesSortedByDate.value?.let { rides.value = it }
        SortingHeader.RUNNING_TIME -> ridesSortedByRunTime.value?.let { rides.value = it }
        SortingHeader.AVERAGE_SPEED -> ridesSortedByAverageSpeed.value?.let { rides.value = it }
        SortingHeader.DISTANCE -> ridesSortedByDistance.value?.let { rides.value = it }
        SortingHeader.CALORIES_BURNED -> ridesSortedByCaloriesBurned.value?.let { rides.value = it }
    }.also {
        this.sortingHeader = sortingHeader
    }

    fun insertRun(ride: RideData) = viewModelScope.launch {
        repository.insertRide(ride)
    }

    fun insertJourney(journey: JourneyData) = viewModelScope.launch {
        repository.insertJourney(journey)
    }

    fun getJourney(timestamp: Long) = repository.getJourney(timestamp)

}