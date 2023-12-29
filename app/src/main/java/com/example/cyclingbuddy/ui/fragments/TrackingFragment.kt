package com.example.cyclingbuddy.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.databinding.FragmentTrackingBinding
import com.example.cyclingbuddy.db.Journey.JourneyData
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.services.TrackingService
import com.example.cyclingbuddy.ui.viewmodels.MainViewModel
import com.example.cyclingbuddy.util.Constants.ACTION_PAUSE_SERVICE
import com.example.cyclingbuddy.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.cyclingbuddy.util.Constants.ACTION_STOP_SERVICE
import com.example.cyclingbuddy.util.Constants.KEY_NAME
import com.example.cyclingbuddy.util.TrackingUtility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking){

    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false

    private var curTimeInMillis = 0L
    private var curDistance = 0L
    var locations: ArrayList<Location>? = null

     var menu: Menu? = null

    @Inject
    lateinit var sharedPref: SharedPreferences

    var weight = 80f

    private var _binding: FragmentTrackingBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        val view = binding.root

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(m: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.toolbar_tracking_menu, m)
                menu = m
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                when(menuItem.itemId) {
                    R.id.cancelTracking_menu -> {
                        showCancelTrackingDialog()
                    }
                }
                return true
            }

            override fun onPrepareMenu(m: Menu) {
                super.onPrepareMenu(m)
                if(curTimeInMillis > 0L) {
                    menu?.getItem(0)?.isVisible = true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        val name=sharedPref.getString(KEY_NAME,"")
        binding.startTv.setText("Let's go, ${name}!")
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleRideBtn.setOnClickListener {
            toggleRun()
        }

        binding.finishRideTv.setOnClickListener {
            endRunAndSaveToDb()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it, TrackingService.isFirstRun)
        })

        TrackingService.locationList.observe(viewLifecycleOwner, Observer {
            locations= it as ArrayList<Location>?
            if (it!!.size < 1) {
                curDistance = 0L
            } else {
                curDistance = (it[0].distanceTo(it[it.size - 1]) ).toLong()
            }
            binding.distanceTv.text = curDistance.toString()+" m"
            var averageSpeed=0L
            if((curTimeInMillis/3600)>0){
                averageSpeed= (curDistance)/(curTimeInMillis/3600)
            }
            binding.avgspeedTv.text=averageSpeed.toString()+" km/h"
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            binding.timerTv.text = formattedTime
        })

    }

    private fun toggleRun() {
        if(isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
            isTracking=false
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            isTracking=true
        }
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().popBackStack()
    }

    private fun updateTracking(isTracking: Boolean, isFirstRun: Boolean) {
        this.isTracking = isTracking
        if(isFirstRun) {
            binding.toggleRideBtn.text = "Start"
            binding.finishRideTv.visibility = View.GONE
            binding.toggleRideBtn.backgroundTintList = requireContext().resources.getColorStateList(R.color.eggplant, requireContext().theme)
        }else{
            if (!isTracking) {
                binding.toggleRideBtn.text = "Start"
                binding.finishRideTv.visibility = View.VISIBLE
                binding.toggleRideBtn.backgroundTintList = requireContext().resources.getColorStateList(R.color.eggplant, requireContext().theme)
            } else {
                binding.toggleRideBtn.text = "Stop"
                binding.toggleRideBtn.backgroundTintList = requireContext().resources.getColorStateList(R.color.colorAccent, requireContext().theme)
                menu?.getItem(0)?.isVisible = true
                binding.finishRideTv.visibility = View.GONE
            }
        }
    }

    private fun endRunAndSaveToDb() {
            if(curDistance != 0L){
                val avgSpeed = round((curDistance / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
                val dateTimestamp = Calendar.getInstance().timeInMillis
                val caloriesBurned = ((curDistance / 1000f) * weight).toInt()
                val run = RideData(dateTimestamp, avgSpeed, curDistance.toInt(), curTimeInMillis, caloriesBurned)
                viewModel.insertRun(run)
                saveJourney(dateTimestamp)
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Run saved successfully",
                    Snackbar.LENGTH_LONG
                ).show()
            }else{
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Run not saved. Distance traveled is 0",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            stopRun()
    }

    fun saveJourney(dateTimestamp:Long){
        if(locations !=null){
            for (l in locations!!) {
                val journeyData = JourneyData(l.latitude,l.longitude, dateTimestamp)
                viewModel.insertJourney(journeyData)
            }
        }
    }

    private fun sendCommandToService(action: String){
        val intent = Intent( requireContext(), TrackingService::class.java)
        intent.action=action
        requireContext().startService(intent)
    }
}