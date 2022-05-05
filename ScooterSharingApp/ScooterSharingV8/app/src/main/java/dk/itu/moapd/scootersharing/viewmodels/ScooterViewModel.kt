package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Scooter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScooterViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)

    fun getClosestScooter(location: Location) : Scooter? {
        val scooters = db.scooterDao().getAllAvailable()
        if(scooters.isEmpty())
            return null

        return db.scooterDao().getAllAvailable().stream().min { x, y ->
            //if x is less than y, then x
            //if y is less than x, then y
            val xLocation = Location(LocationManager.GPS_PROVIDER)
            val yLocation = Location(LocationManager.GPS_PROVIDER)
            xLocation.latitude = x.lat
            xLocation.longitude = x.lon
            yLocation.latitude = y.lat
            yLocation.longitude = y.lon

            location.distanceTo(xLocation).compareTo(location.distanceTo(yLocation))
        }.get()
    }

    fun getAvailableScooters() : LiveData<List<Scooter>> {
        return db.scooterDao().getAllAvailableLive()
    }

    fun getScooter(scooterId: Long) = db.scooterDao().getScooter(scooterId)

    fun isScooterAvailable(scooterId: Long) = db.scooterDao().getAvailableScooter(scooterId) == 1

    fun updateLocation(scooterId: Long, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            db.scooterDao().update(scooterId, latitude, longitude)
        }
    }

    fun updateAvailability(scooterId: Long, isAvailable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            db.scooterDao().update(scooterId, isAvailable)
        }
    }

    fun updateBattery(scooterId: Long, battery: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.scooterDao().update(scooterId, battery)
        }
    }
}