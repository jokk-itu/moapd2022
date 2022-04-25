package dk.itu.moapd.scootersharing.services

import android.location.Location

interface LocationListener {
    fun onLocationChanged(location: Location)
}