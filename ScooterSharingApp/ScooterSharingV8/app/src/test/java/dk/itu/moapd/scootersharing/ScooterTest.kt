package dk.itu.moapd.scootersharing

import dk.itu.moapd.scootersharing.data.model.Scooter
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ScooterTest {

    @Test
    fun testToString() {
        val scooter = Scooter(
            name = "Scooter1",
            lat = 1f.toDouble(),
            lon = 1f.toDouble(),
            battery = 100,
            isAvailable = true,
            picture = "Scooter1"
        )
        val toString = scooter.toString()
        val date = Calendar.getInstance().timeInMillis
        val formatter = SimpleDateFormat("yyyy:MM:DD", Locale.getDefault())
        val time = formatter.format(date)
        assert(toString == "From $time: ${scooter.name} is placed at (${scooter.lat}, ${scooter.lon})")
    }
}