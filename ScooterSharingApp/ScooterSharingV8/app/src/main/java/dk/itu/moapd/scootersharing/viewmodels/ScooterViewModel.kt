package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Scooter

class ScooterViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val scooters = db.scooterDao().getAll()
    private val availableScooters = db.scooterDao().getAllAvailable()

    fun getAll() : LiveData<List<Scooter>> {
        return scooters
    }

    fun getAvailableScooters() : LiveData<List<Scooter>> {
        return availableScooters
    }

    fun isScooterAvailable(scooterId: Long) = db.scooterDao().getAvailableScooter(scooterId) == 1

    fun getScooter(scooterId: Long) = db.scooterDao().getScooter(scooterId)
}