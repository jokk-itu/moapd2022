package dk.itu.moapd.scootersharing

import org.junit.Test

class ScooterTest {
    @Test
    fun `test toString`() {
        val scooter = Scooter("Voi", "Copenhagen")
        assert(scooter.toString() == "Voi is placed at Copenhagen")
    }
}