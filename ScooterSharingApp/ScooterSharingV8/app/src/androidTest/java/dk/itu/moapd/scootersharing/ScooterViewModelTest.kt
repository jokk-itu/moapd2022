package dk.itu.moapd.scootersharing

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import dk.itu.moapd.scootersharing.util.LifecycleOwnerUtil
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class ScooterViewModelTest {

    private lateinit var context: Context
    private lateinit var scooterViewModel: ScooterViewModel
    private val lifecycleOwner: LifecycleOwnerUtil = LifecycleOwnerUtil()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        scooterViewModel = ScooterViewModel(context as Application)
    }

    @Before
    fun start() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            lifecycleOwner.start()
        }
    }

    @After
    fun stop() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            lifecycleOwner.stop()
        }
    }

    @Test
    fun getClosestScooter() {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 55.932271
        location.longitude = 12.295771
        val actual = scooterViewModel.getClosestScooter(location)
        assert(actual.name == "Pingus Sister")
    }
}