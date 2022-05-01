package dk.itu.moapd.scootersharing

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test

class LocationViewModelTest {

    private lateinit var context: Context
    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        locationViewModel = LocationViewModel(context as Application)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun toAddressSuccess() {
        val latitude = 55.932697
        val longitude = 12.298471
        val address = locationViewModel.toAddress(latitude, longitude)
        assert(address!!.startsWith("Slotsgade 59B, 3400 Hillerød"))
    }

    @Test
    fun toAddressFail() {
        val latitude = -1.0
        val longitude = -1.0
        val address = locationViewModel.toAddress(latitude, longitude)
        assert(address == null)
    }
}