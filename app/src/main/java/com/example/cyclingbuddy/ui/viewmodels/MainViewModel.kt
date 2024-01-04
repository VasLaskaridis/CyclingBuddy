package com.example.cyclingbuddy.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
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

     var _rides: LiveData<List<RideData>>? = null
    val rides get() = _rides

    val journey=MediatorLiveData<List<JourneyData>>()

    var sortingHeader = SortingHeader.DATE

    init {
        getDataRides()
    }

    fun getDataRides(): LiveData<List<RideData>>? {
        _rides =  repository.getAllRunsSortedByDate()
        return rides
    }

    fun insertRun(ride: RideData) = viewModelScope.launch {
        repository.insertRide(ride)
    }

    fun insertJourney(journey: JourneyData) = viewModelScope.launch {
        repository.insertJourney(journey)
    }

    fun getJourney(timestamp: Long) = repository.getJourney(timestamp)

    fun getJourneyForDelete(timestamp: Long){
        val journey = MediatorLiveData<List<JourneyData>>()
        journey.addSource(repository.getJourney(timestamp), Observer {
            if(it!=null){
                deleteJourney(journey.value!!.get(0))
            }
        })
    }

    fun deleteRide(ride: RideData){
        viewModelScope.launch {
            getJourneyForDelete(ride.runStartTimestamp)
            repository.deleteRide(ride)
        }
    }
    fun deleteJourney(journey: JourneyData){
        viewModelScope.launch {
            repository.deleteJourney(journey)
        }
    }

}