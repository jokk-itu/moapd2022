package dk.itu.moapd.scootersharing.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel

class MapFragment : Fragment(), OnMapsSdkInitializedCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var scooterViewModel: ScooterViewModel
    private var currentLocation: Location? = null
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0f.toDouble())
            val longitude = intent?.getDoubleExtra("longitude", 0f.toDouble())

            if (latitude == null || latitude == 0f.toDouble())
                return

            if (longitude == null || longitude == 0f.toDouble())
                return

            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = latitude
            location.longitude = longitude
            currentLocation = location
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(locationReceiver, IntentFilter(R.string.location_event.toString()))
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        scooterViewModel = ViewModelProvider(requireActivity())[ScooterViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = binding.map.getFragment<SupportMapFragment>()
        mapFragment.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback {
        it.mapType = GoogleMap.MAP_TYPE_NORMAL
        val isLocationPermissionsCheck = isLocationPermissionsChecked()
        it.isMyLocationEnabled = isLocationPermissionsCheck
        it.uiSettings.isMyLocationButtonEnabled = isLocationPermissionsCheck
        it.uiSettings.isZoomControlsEnabled = true
        setConstraintsOnMap(it)
        addAvailableScooterMarkers(it)
    }

    private fun setConstraintsOnMap(map: GoogleMap) {
        map.setMinZoomPreference(8.0f)
        map.setMaxZoomPreference(16.0f)
        val bounds = LatLngBounds(
            LatLng(54.452560, 8.123232),
            LatLng(57.814117, 13.056093)
        )
        map.setLatLngBoundsForCameraTarget(bounds)
    }

    private fun addAvailableScooterMarkers(map: GoogleMap) {
        scooterViewModel.getAvailableScooters().observe(viewLifecycleOwner) { scooters ->
            scooters.forEach {
                val marker = MarkerOptions()
                    .position(LatLng(it.lat, it.lon))
                    .title(it.name)
                map.addMarker(marker)
            }
        }
    }

    private fun isLocationPermissionsChecked(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST ->
                Log.d(MapFragment::class.java.name, "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY ->
                Log.d(MapFragment::class.java.name, "The legacy version of the renderer is used.")
        }
    }
}