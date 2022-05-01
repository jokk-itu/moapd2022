package dk.itu.moapd.scootersharing

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import dk.itu.moapd.scootersharing.data.model.AppDatabase
import dk.itu.moapd.scootersharing.data.model.Scooter
import dk.itu.moapd.scootersharing.data.model.ScooterDao
import dk.itu.moapd.scootersharing.util.LifecycleOwnerUtil
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@SmallTest
@RunWith(AndroidJUnit4::class)
class ScooterTest {
    private lateinit var scooterDao: ScooterDao
    private lateinit var db: AppDatabase
    private lateinit var context: Context
    private lateinit var scooterViewModel: ScooterViewModel
    private val lifecycleOwner: LifecycleOwnerUtil = LifecycleOwnerUtil()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        scooterDao = db.scooterDao()
        scooterViewModel = ScooterViewModel(context.applicationContext as Application)
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
    fun insertScooter() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        val newlyInsertedScooter = scooterDao.getScooter(1L)
        assert(scooter.name == newlyInsertedScooter.name)
    }

    @Test
    fun updateLocation() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        runBlocking { scooterDao.update(1L, 1f.toDouble(), 1f.toDouble()) }
        val updatedScooter = scooterDao.getScooter(1L)
        assert(updatedScooter.lat == 1f.toDouble())
        assert(updatedScooter.lon == 1f.toDouble())
    }

    @Test
    fun updateBattery() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        runBlocking { scooterDao.update(1L, 98) }
        val updatedScooter = scooterDao.getScooter(1L)
        assert(updatedScooter.battery == 98)
    }

    @Test
    fun updateAvailability() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        runBlocking { scooterDao.update(1L, false) }
        val updatedScooter = scooterDao.getScooter(1L)
        assert(!updatedScooter.isAvailable)
    }

    @Test
    @UiThreadTest
    fun getAvailableScooters() {
        val scooter1 = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter1",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        val scooter2 = Scooter(
            lat = 1f.toDouble(),
            lon = 1f.toDouble(),
            name = "Scooter2",
            isAvailable = false,
            battery = 100,
            picture = "scooter2"
        )
        runBlocking { scooterDao.insert(scooter1) }
        runBlocking { scooterDao.insert(scooter2) }
        scooterDao.getAllAvailable().observe(lifecycleOwner) { scooters ->
            scooters.forEach {
                assert(it.id == 1L)
            }
        }
        Thread.sleep(2000)
    }

    @Test
    fun getAvailableScooter() {
        val scooter = Scooter(
            lat = 0f.toDouble(),
            lon = 0f.toDouble(),
            name = "Scooter1",
            isAvailable = true,
            battery = 100,
            picture = "scooter1"
        )
        runBlocking { scooterDao.insert(scooter) }
        val isAvailable = scooterDao.getAvailableScooter(1L)
        assert(isAvailable == 1)
    }
}