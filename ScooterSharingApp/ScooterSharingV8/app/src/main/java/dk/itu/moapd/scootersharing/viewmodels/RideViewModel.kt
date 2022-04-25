package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Ride
import kotlinx.coroutines.launch

class RideViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val auth = FirebaseAuth.getInstance()
    private val rides = db.rideDao().getAllFromUser(auth.currentUser!!.uid)

    fun getRides() : LiveData<List<Ride>> {
        return rides
    }

    fun deleteRide(ride: Ride) {
        viewModelScope.launch {
            db.rideDao().delete(ride)
        }
    }

    fun insertRide(ride: Ride) {
        viewModelScope.launch {
            db.rideDao().insert(ride)
        }
    }

    fun updateRide(ride: Ride) {
        viewModelScope.launch {
            db.rideDao().update(ride)
        }
    }

    suspend fun getCurrentRide() = db.rideDao().getCurrentRide(auth.currentUser!!.uid)

    fun getLiveCurrentRide() = db.rideDao().getLiveCurrentRide(auth.currentUser!!.uid)
}