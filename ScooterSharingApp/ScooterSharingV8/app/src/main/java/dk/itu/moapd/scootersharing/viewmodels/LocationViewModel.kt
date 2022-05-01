package dk.itu.moapd.scootersharing.viewmodels

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import java.io.IOException
import java.util.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val geocoder = Geocoder(application.applicationContext, Locale.getDefault())

    fun toAddress(latitude: Double, longitude: Double) : String? {
        val stringBuilder = StringBuilder()
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses.forEach {
                stringBuilder.apply {
                    append(it.getAddressLine(0)).append(System.lineSeparator())
                    append(it.locality).append(System.lineSeparator())
                    append(it.postalCode).append(System.lineSeparator())
                    append(it.countryName).append(System.lineSeparator())
                }
            }
        }
        catch (ex: IOException) {
            Log.e(LocationViewModel::class.java.canonicalName, ex.message.toString())
        }
        return if (stringBuilder.isEmpty()) null else stringBuilder.toString()
    }
}