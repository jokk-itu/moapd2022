package dk.itu.moapd.scootersharing

import android.content.Context
import androidx.room.Room
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import dk.itu.moapd.scootersharing.data.model.*
import dk.itu.moapd.scootersharing.util.LifecycleOwnerUtil
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@SmallTest
@RunWith(AndroidJUnit4::class)
class RideTest {

    private lateinit var rideDao: RideDao
    private lateinit var scooterDao: ScooterDao
    private lateinit var db: AppDatabase
    private lateinit var context: Context
    private val lifecycleOwner: LifecycleOwnerUtil = LifecycleOwnerUtil()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        rideDao = db.rideDao()
        scooterDao = db.scooterDao()
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
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            start = System.currentTimeMillis()
        )
        runBlocking { rideDao.insert(ride) }
        val newRide = rideDao.getCurrentRide("1234")
        assert(ride.scooterId == newRide!!.scooterId)
    }

    @Test
    fun updateEndRide() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble(),
            start = System.currentTimeMillis()
        )
        runBlocking { rideDao.insert(ride) }
        runBlocking {
            rideDao.update(
                1L,
                50f.toDouble(),
                50f.toDouble(),
                System.currentTimeMillis(),
                10f.toDouble()
            )
        }
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
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            start = System.currentTimeMillis(),
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble()
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
    fun getCurrentRide() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        var currentRide = rideDao.getCurrentRide("1234")
        assert(currentRide == null)

        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            start = System.currentTimeMillis(),
            startLat = 0f.toDouble(),
            startLon = 0f.toDouble()
        )
        runBlocking { rideDao.insert(ride) }
        currentRide = rideDao.getCurrentRide("1234")
        assert(currentRide != null)
    }
}