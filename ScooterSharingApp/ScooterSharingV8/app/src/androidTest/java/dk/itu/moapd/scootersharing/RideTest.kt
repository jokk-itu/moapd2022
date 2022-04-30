package dk.itu.moapd.scootersharing

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import dk.itu.moapd.scootersharing.data.model.*
import dk.itu.moapd.scootersharing.util.LifecycleOwnerUtil
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RideTest {

    private lateinit var rideDao: RideDao
    private lateinit var scooterDao: ScooterDao
    private lateinit var db: AppDatabase
    private lateinit var context: Context
    private lateinit var rideViewModel: RideViewModel
    private lateinit var scooterViewModel: ScooterViewModel
    private val lifecycleOwner: LifecycleOwnerUtil = LifecycleOwnerUtil()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        rideDao = db.rideDao()
        scooterDao = db.scooterDao()
        rideViewModel = RideViewModel(context.applicationContext as Application)
    }

    @Before
    fun start() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            lifecycleOwner.start()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @After
    fun stop() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            lifecycleOwner.stop()
        }
    }

    @Test
    fun insertRide() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 0f.toDouble(),
            currentLon = 0f.toDouble(),
            start = System.currentTimeMillis()
        )
        runBlocking { rideDao.insert(ride) }
        val newRide = rideDao.getCurrentRide("1234")
        assert(ride.scooterId == newRide!!.scooterId)
    }

    @Test
    fun updateLocationForRide() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 0f.toDouble(),
            currentLon = 0f.toDouble(),
            start = System.currentTimeMillis()
        )
        runBlocking { rideDao.insert(ride) }
        runBlocking { rideDao.update(1L, 2f.toDouble(), 2f.toDouble())}
        val updatedRide = rideDao.getCurrentRide("1234")
        assert(updatedRide != null)
        assert(updatedRide?.currentLat == 2f.toDouble())
        assert(updatedRide?.currentLon == 2f.toDouble())
    }

    @Test
    fun updateEndRide() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 0f.toDouble(),
            currentLon = 0f.toDouble(),
            start = System.currentTimeMillis()
        )
        runBlocking { rideDao.insert(ride) }
        runBlocking { rideDao.update(1L, 50f.toDouble(), 50f.toDouble(), System.currentTimeMillis())}
        val updatedRide = rideDao.getCurrentRide("1234")
        assert(updatedRide == null)
    }

    @Test
    @UiThreadTest
    fun getAllFromUser() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            start = System.currentTimeMillis(),
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 5f.toDouble(),
            currentLon = 5f.toDouble()
        )
        runBlocking { rideDao.insert(ride) }
        rideDao.getAllFromUser("1234").observe(lifecycleOwner) { rides ->
            rides.forEach {
                assert(it.id == 1L)
            }
        }
        Thread.sleep(2000)
    }

    @Test
    fun getCurrentRide () {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }
        var currentRide = rideDao.getCurrentRide("1234")
        assert(currentRide == null)

        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            start = System.currentTimeMillis(),
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 5f.toDouble(),
            currentLon = 5f.toDouble()
        )
        runBlocking { rideDao.insert(ride) }
        currentRide = rideDao.getCurrentRide("1234")
        assert(currentRide != null)
    }

    @Test
    @UiThreadTest
    fun getLiveCurrentRide () {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true
        )
        runBlocking { scooterDao.insert(scooter) }

        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            start = System.currentTimeMillis(),
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            currentLat = 5f.toDouble(),
            currentLon = 5f.toDouble()
        )
        runBlocking { rideDao.insert(ride) }
        rideDao.getLiveCurrentRide("1234").observe(lifecycleOwner) {
            if(it != null) {
                assert(it.id == 1L)
            }
        }
        Thread.sleep(2000)
    }
}