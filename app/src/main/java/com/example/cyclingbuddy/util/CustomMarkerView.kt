package com.example.cyclingbuddy.util

import android.content.Context
import android.widget.TextView
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.db.Ride.RideData
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomMarkerView(
    val rides: List<RideData>,
    c: Context,
    layoutId: Int
    ) : MarkerView(c, layoutId) {

        var tvDate: TextView
        var tvAvgSpeed: TextView
        var tvDistance: TextView
        var tvDuration: TextView
        var tvCaloriesBurned: TextView

        init{
            tvDate =  findViewById(R.id.date_tv)
            tvAvgSpeed =  findViewById(R.id.avgSpeed_tv)
            tvDistance =  findViewById(R.id.distance_tv)
            tvDuration =  findViewById(R.id.duration_tv)
            tvCaloriesBurned =  findViewById(R.id.caloriesBurned_tv)
        }

        override fun getOffset(): MPPointF {
            return MPPointF(-width / 2f, -height.toFloat())
        }

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            super.refreshContent(e, highlight)
            if(e == null) {
                return
            }
            val curRunId = e.x.toInt()
            val run = rides[curRunId]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.runStartTimestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.averageSpeed}km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distance / 1000f}km"
            tvDistance.text = distanceInKm

            tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.runTime)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            tvCaloriesBurned.text = caloriesBurned
        }
}