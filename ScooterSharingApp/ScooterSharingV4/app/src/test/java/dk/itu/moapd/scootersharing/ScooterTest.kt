package dk.itu.moapd.scootersharing

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ScooterTest {
    @Test
    fun `test toString`() {
        val name = "Voi"
        val where = "Copenhagen"
        val time = System.currentTimeMillis()
        val scooter = Scooter("Voi", "Copenhagen", time)
        assert(scooter.toString() == "From ${scooter.readableTimestamp()}: $name is placed at $where")
    }

    @Test
    fun `test readableTimestamp`() {
        val time = System.currentTimeMillis()
        val scooter = Scooter(timestamp = time)
        val date = SimpleDateFormat("yyyy:MM:DD:hh:mm:ss", Locale.ENGLISH).format(Date(time))
        assert(scooter.readableTimestamp() == date)
    }
}