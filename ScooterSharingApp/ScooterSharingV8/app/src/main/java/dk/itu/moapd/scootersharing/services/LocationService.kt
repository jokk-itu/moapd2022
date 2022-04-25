package dk.itu.moapd.scootersharing.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.*
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class LocationService : Service() {

    private val binder = LocalBinder()
    private val listeners = ArrayList<LocationListener>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val thread = HandlerThread(LocationService::class.qualifiedName, Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                listeners.forEach {
                    it.onLocationChanged(location)
                }
            }
        }

        startRequesting(thread)
    }

    @SuppressLint("MissingPermission")
    private fun startRequesting(thread: HandlerThread) {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(5)
            fastestInterval = TimeUnit.SECONDS.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, thread.looper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun subscribe(listener: LocationListener) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: LocationListener) {
        listeners.remove(listener)
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
}