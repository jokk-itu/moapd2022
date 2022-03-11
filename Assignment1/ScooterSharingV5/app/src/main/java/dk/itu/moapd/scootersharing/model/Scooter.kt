package dk.itu.moapd.scootersharing.model

import java.text.SimpleDateFormat
import java.util.*

data class Scooter(
    var name: String = "",
    var where: String = "",
    var timestamp: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return "From ${readableTimestamp()}: $name is placed at $where"
    }

    fun readableTimestamp() : String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("yyy:MM:DD:hh:mm:ss", Locale.ENGLISH)
        return formatter.format(date)
    }
}