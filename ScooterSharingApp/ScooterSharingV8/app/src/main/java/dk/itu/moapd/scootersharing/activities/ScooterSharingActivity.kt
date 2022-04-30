package dk.itu.moapd.scootersharing.activities

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
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.activities.login.LoginActivity
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.services.location.LocationListener
import dk.itu.moapd.scootersharing.services.location.LocationService
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel

/**
 * TEST START RIDE
 * TEST END RIDE
 *
 * UI TESTS
 */

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding
    private lateinit var rideViewModel: RideViewModel
    private lateinit var scooterViewModel: ScooterViewModel
    private lateinit var locationService: LocationService
    private var currentLocation: Location? = null
    private var isBound: Boolean = false
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Thread {
                if (auth.currentUser == null)
                    return@Thread

                val ride = rideViewModel.getCurrentRide() ?: return@Thread
                rideViewModel.updateLocation(
                    ride.id,
                    currentLocation!!.latitude,
                    currentLocation!!.longitude
                )
                scooterViewModel.updateScooter(
                    ride.scooterId,
                    location.latitude,
                    location.longitude,
                    false
                )
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
            val scooterId = intent?.getLongExtra("scooterId", -1L)

            if (scooterId == null || scooterId == -1L) {
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "QRCode is not correct",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (!scooterViewModel.isScooterAvailable(scooterId)) {
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "Scooter is not available",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

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
            rideViewModel.startRide(ride)
            scooterViewModel.updateScooter(
                scooterId,
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                false
            )
            Toast.makeText(this@ScooterSharingActivity, "Ride started", Toast.LENGTH_SHORT).show()
        }
    }
    private val endRideReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scooterId = intent?.getLongExtra("scooterId", -1L)

            if (scooterId == null || scooterId == -1L) {
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "QRCode is not correct",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            while (currentLocation == null) {
                //Empty on purpose
            }

            val ride = rideViewModel.getCurrentRide()

            if (ride == null) {
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "You don't have a current ride",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (ride.scooterId != scooterId) {
                Toast.makeText(
                    this@ScooterSharingActivity,
                    "The given QRCode does not correspond to the currentride",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            rideViewModel.endRide(ride.id, currentLocation!!.latitude, currentLocation!!.longitude)
            scooterViewModel.updateScooter(
                ride.id,
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                true
            )
            Toast.makeText(this@ScooterSharingActivity, "Ride ended", Toast.LENGTH_SHORT).show()
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
        if(checkUserSession())
            return

        binding = ActivityScooterSharingBinding.inflate(layoutInflater)
        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]
        scooterViewModel = ViewModelProvider(this)[ScooterViewModel::class.java]
        setupBottomNav()
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(startRideReceiver, IntentFilter(R.string.start_ride_event.toString()))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(endRideReceiver, IntentFilter(R.string.end_ride_event.toString()))

        binding.topNav.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {
                    auth.signOut()
                    checkUserSession()
                    true
                }
                else -> false
            }
        }
        requestUserPermissions()
    }

    private fun checkUserSession() : Boolean {
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return false
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