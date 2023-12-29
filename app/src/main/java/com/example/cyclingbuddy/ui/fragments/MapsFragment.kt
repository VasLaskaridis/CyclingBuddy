package com.example.cyclingbuddy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var mMap: GoogleMap? = null
    private var journeyID: Long = 0

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap


        viewModel.getJourney(journeyID).observe(viewLifecycleOwner, Observer {

            it?.let {
                val line = PolylineOptions().clickable(false).width(4F).color(requireContext().getResources().getColor(R.color.colorAccent));
                val firstLoc: LatLng? = LatLng(it.get(0).longitude,it.get(0).latitude)
                val lastLoc: LatLng? = LatLng(it.get(it.size-1).longitude,it.get(it.size-1).latitude)
                for( location in it){
                    val loc = LatLng(location.longitude,location.latitude)
                    line.add(loc)
                }

                val zoom = 15.0f
                if (lastLoc != null && firstLoc != null) {
                    mMap!!.addMarker(MarkerOptions().position(firstLoc).title("Start"))
                    mMap!!.addMarker(MarkerOptions().position(lastLoc).title("End"))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLoc, zoom))
                }
                mMap!!.addPolyline(line)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        journeyID= arguments?.get("timestamp") as Long

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        return view
    }

}