package dk.itu.moapd.scootersharing

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import java.util.jar.Manifest

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var locationService: LocationService
    private var isBound : Boolean = false
    private val locationListener = object: LocationListener {
        override fun onLocationChanged(location: Location) {
            val intent = Intent(R.string.location_event.toString())
            intent.putExtra("latitude", location.latitude)
            intent.putExtra("longitude", location.longitude)
            LocalBroadcastManager.getInstance(this@ScooterSharingActivity).sendBroadcast(intent)
        }
    }

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            val binderBridge = service as LocationService.LocalBinder
            locationService = binderBridge.getService()
            locationService.subscribe(locationListener)
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
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
        setupBottomNav()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.topNav.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {
                    auth.signOut()
                    binding.navHostFragment.getFragment<NavHostFragment>().navController.navigate(R.id.signInFragment)
                    true
                }
                else -> false
            }
        }

        requestUserPermissions()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setupWithNavController(
            binding.navHostFragment.getFragment<NavHostFragment>().navController)
    }

    private fun requestUserPermissions() {
        val permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = ArrayList<String>()
        for(permission in permissions)
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission)

        if(permissionsToRequest.size > 0)
            requestPermissions(permissionsToRequest.toTypedArray(), 1011)
    }
}