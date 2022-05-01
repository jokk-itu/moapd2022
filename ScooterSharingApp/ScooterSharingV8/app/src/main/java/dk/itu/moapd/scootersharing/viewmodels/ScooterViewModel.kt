package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Scooter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScooterViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val scooters = db.scooterDao().getAll()
    private val availableScooters = db.scooterDao().getAllAvailable()

    fun getAvailableScooters() : LiveData<List<Scooter>> {
        return availableScooters
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