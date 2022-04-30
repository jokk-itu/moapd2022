package dk.itu.moapd.scootersharing.services.location

import android.location.Location

interface LocationListener {
    fun onLocationChanged(location: Location)
}