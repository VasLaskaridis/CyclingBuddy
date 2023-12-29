package com.example.cyclingbuddy.ui.fragments

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.adapter.Adapter
import com.example.cyclingbuddy.databinding.FragmentRideBinding
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.ui.viewmodels.MainViewModel
import com.example.cyclingbuddy.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.cyclingbuddy.util.SortingHeader
import com.example.cyclingbuddy.util.TrackingUtility
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


@AndroidEntryPoint
class RideFragment : Fragment(R.layout.fragment_ride), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: Adapter

    private var _binding: FragmentRideBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRideBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setupRecyclerView()

        when(viewModel.sortingHeader) {
            SortingHeader.DATE -> binding.filterSp.setSelection(0)
            SortingHeader.RUNNING_TIME -> binding.filterSp.setSelection(1)
            SortingHeader.DISTANCE -> binding.filterSp.setSelection(2)
            SortingHeader.AVERAGE_SPEED -> binding.filterSp.setSelection(3)
            SortingHeader.CALORIES_BURNED -> binding.filterSp.setSelection(4)
        }

        binding.filterSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos) {
                    0 -> viewModel.sortRuns(SortingHeader.DATE)
                    1 -> viewModel.sortRuns(SortingHeader.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortingHeader.DISTANCE)
                    3 -> viewModel.sortRuns(SortingHeader.AVERAGE_SPEED)
                    4 -> viewModel.sortRuns(SortingHeader.CALORIES_BURNED)
                }
            }
        }

        viewModel.rides.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_rideFragment_to_trackingFragment)
        }
    }

    private fun setupRecyclerView() = binding.ridesRv.apply {
        runAdapter = Adapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
        runAdapter.setRideClickListener(object:Adapter.onRideClickListener{
            override fun onRideClick(ride: RideData, position: Int) {
                val bundle =Bundle()
                bundle.putLong("timestamp", ride.runStartTimestamp)
                bundle.putFloat("averageSpeed", ride.averageSpeed)
                bundle.putInt("distance", ride.distance)
                bundle.putLong("runTime", ride.runTime)
                bundle.putInt("caloriesBurned", ride.caloriesBurned)

                findNavController().navigate(R.id.action_rideFragment_to_journeyFragment,bundle)
            }
        })
    }

    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        else {
            EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this,
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            .setRationale("You need to accept location permissions to use this app.")
                .setPositiveButtonText("OK")
                .setNegativeButtonText("CANCEL")
                .setTheme(R.style.AlertDialogTheme)
                .build());
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (!isGPSEnabled()) {
            turnOnGPS()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun turnOnGPS() {

        var locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(2000)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 112)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }
}