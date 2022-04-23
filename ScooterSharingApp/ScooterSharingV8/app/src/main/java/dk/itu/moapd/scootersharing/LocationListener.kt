package dk.itu.moapd.scootersharing

import android.location.Location

interface LocationListener {
    fun onLocationChanged(location: Location)
}