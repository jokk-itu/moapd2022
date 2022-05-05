package dk.itu.moapd.scootersharing

import dk.itu.moapd.scootersharing.data.model.Ride
import org.junit.Test

class RideTest {

    @Test
    fun startToDate() {
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 1f.toDouble(),
            startLon = 1f.toDouble(),
            start = 1651436425366L
        )
        val date = ride.startToDate()
        assert(date == "2022:05:01")
    }

    @Test
    fun endToDate() {
        val ride = Ride(
            userId = "1234",
            scooterId = 1L,
            startLat = 1f.toDouble(),
            startLon = 1f.toDouble(),
            start = 1651436425366L,
            endLat = 1f.toDouble(),
            endLon = 1f.toDouble(),
            end = 1651436425366L
        )
        val date = ride.endToDate()
        assert(date == "2022:05:01")
    }
}