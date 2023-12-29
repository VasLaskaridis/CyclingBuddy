package com.example.cyclingbuddy.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.databinding.FragmentJourneyBinding
import com.example.cyclingbuddy.util.TrackingUtility

import java.text.SimpleDateFormat

class JourneyFragment : Fragment() {

    private var _binding: FragmentJourneyBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timestamp= arguments?.get("timestamp")
        val averageSpeed= arguments?.get("averageSpeed")
        val distance= arguments?.get("distance")
        val runTime= arguments?.get("runTime")
        val caloriesBurned= arguments?.get("caloriesBurned")

        binding.averageSpeedTv.text = averageSpeed.toString()+" km/h"
        binding.totalDistanceTv.text = distance.toString()+" m"
        binding.totalTimeTv.text = TrackingUtility.getFormattedStopWatchTime(runTime as Long)
        binding.totalCaloriesTv.text = caloriesBurned.toString()+" kcal"
        val simpleDateFormat =SimpleDateFormat("dd/MM/yyyy")
        val dateString = simpleDateFormat.format(timestamp)
        binding.dateTv.text = dateString.toString()

        binding.mapBtn.setOnClickListener(View.OnClickListener {
            val bundle =Bundle()
            bundle.putLong("timestamp", timestamp as Long)
            findNavController().navigate(R.id.action_journeyFragment_to_mapsFragment,bundle)
        })
    }

}