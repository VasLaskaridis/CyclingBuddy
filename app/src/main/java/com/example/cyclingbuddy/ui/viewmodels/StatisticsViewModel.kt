package com.example.cyclingbuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cyclingbuddy.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: Repository
):ViewModel () {

    val totalRunTime = repository.getTotalRunTime()
    val totalDistance = repository.getTotalDistance()
    val totalCaloriesBurned = repository.getTotalCaloriesBurned()
    val totalAverageSpeed = repository.getTotalAverageSpeed()

    val runsSortedByDate = repository.getAllRunsSortedByDate()
}