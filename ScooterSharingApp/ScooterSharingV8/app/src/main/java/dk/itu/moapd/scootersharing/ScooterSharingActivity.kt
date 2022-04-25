package dk.itu.moapd.scootersharing

import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.services.LocationListener
import dk.itu.moapd.scootersharing.services.LocationService
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import kotlinx.coroutines.runBlocking

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var rideViewModel: RideViewModel
    private lateinit var scooterViewModel: ScooterViewModel
    private lateinit var locationService: LocationService
    private var currentLocation: Location? = null
    private var isBound: Boolean = false
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Thread {
                val ride = runBlocking { rideViewModel.getCurrentRide() } ?: return@Thread

                val updatedRide = Ride(
                    id = ride.id,
                    scooterId = ride.scooterId,
                    userId = ride.userId,
                    start = ride.start,
                    startLat = ride.startLat,
                    startLon = ride.startLon,
                    currentLat = location.latitude,
                    currentLon = location.longitude,
                )
                rideViewModel.updateRide(updatedRide)
            }.start()
            currentLocation = location
            val intent = Intent(R.string.location_event.toString())
            intent.putExtra("latitude", location.latitude)
            intent.putExtra("longitude", location.longitude)
            LocalBroadcastManager.getInstance(this@ScooterSharingActivity).sendBroadcast(intent)
        }
    }
    private val startRideReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scooterId = intent?.getLongExtra("scooterId", 0)

            if (scooterId == null || scooterId == 0L)
                Toast.makeText(this@ScooterSharingActivity, "QRCode is invalid", Toast.LENGTH_LONG)
                    .show()

            if (!scooterViewModel.isScooterAvailable(scooterId!!))
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "Scooter is not available",
                    Toast.LENGTH_LONG
                ).show()

            while (currentLocation == null) {
                //Empty on purpose
            }

            val ride = Ride(
                scooterId = scooterId,
                userId = auth.currentUser!!.uid,
                start = System.currentTimeMillis(),
                startLat = currentLocation!!.latitude,
                startLon = currentLocation!!.longitude,
                currentLat = currentLocation!!.latitude,
                currentLon = currentLocation!!.longitude
            )
            rideViewModel.insertRide(ride)
            Toast.makeText(this@ScooterSharingActivity, "Ride started", Toast.LENGTH_SHORT).show()
        }
    }
    private val endRideReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scooterId = intent?.getLongExtra("scooterId", 0)

            if (scooterId == null || scooterId == 0L)
                Toast.makeText(this@ScooterSharingActivity, "QRCode is invalid", Toast.LENGTH_LONG)
                    .show()

            while (currentLocation == null) {
                //Empty on purpose
            }

            val ride = runBlocking { rideViewModel.getCurrentRide() } ?: return
            val updatedRide = Ride(
                id = ride.id,
                scooterId = ride.scooterId,
                userId = ride.userId,
                start = ride.start,
                startLat = ride.startLat,
                startLon = ride.startLon,
                currentLat = currentLocation!!.latitude,
                currentLon = currentLocation!!.longitude,
                end = System.currentTimeMillis(),
                endLat = currentLocation!!.latitude,
                endLon = currentLocation!!.longitude
            )
            rideViewModel.updateRide(updatedRide)
        }
    }

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            val binderBridge = service as LocationService.LocalBinder
            locationService = binderBridge.getService()
            locationService.subscribe(locationListener)
            isBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            locationService.unsubscribe(locationListener)
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
        bindService(intent, locationServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(locationServiceConnection)
        isBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScooterSharingBinding.inflate(layoutInflater)
        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]
        scooterViewModel = ViewModelProvider(this)[ScooterViewModel::class.java]
        setupBottomNav()
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(startRideReceiver, IntentFilter(R.string.start_ride_event.toString()))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(endRideReceiver, IntentFilter(R.string.end_ride_event.toString()))

        auth = FirebaseAuth.getInstance()
        binding.topNav.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {
                    auth.signOut()
                    binding.navHostFragment.getFragment<NavHostFragment>().navController.navigate(
                        R.id.signInFragment
                    )
                    true
                }
                else -> false
            }
        }

        requestUserPermissions()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setupWithNavController(
            binding.navHostFragment.getFragment<NavHostFragment>().navController
        )
    }

    private fun requestUserPermissions() {
        val permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA
        )

        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions)
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission)

        if (permissionsToRequest.size > 0)
            requestPermissions(permissionsToRequest.toTypedArray(), 1011)
    }
}