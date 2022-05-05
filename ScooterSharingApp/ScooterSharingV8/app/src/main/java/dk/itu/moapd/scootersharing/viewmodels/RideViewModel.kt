package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Ride
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RideViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val auth = FirebaseAuth.getInstance()
    private var rides : LiveData<List<Ride>>? = null

    fun getRides() : LiveData<List<Ride>> {
        if(rides == null) {
            rides = db.rideDao().getAllFromUser(auth.currentUser!!.uid)
        }
        return rides!!
    }

    fun startRide(ride: Ride) {
        viewModelScope.launch(Dispatchers.IO) {
            db.rideDao().insert(ride)
        }
    }

    fun endRide(rideId: Long, endLat: Double, endLon: Double, price: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            db.rideDao().update(rideId, endLat, endLon, System.currentTimeMillis(), price)
        }
    }

    fun getCurrentRide() = db.rideDao().getCurrentRide(auth.currentUser!!.uid)

    fun getLiveCurrentRide() = db.rideDao().getLiveCurrentRide(auth.currentUser!!.uid)
}