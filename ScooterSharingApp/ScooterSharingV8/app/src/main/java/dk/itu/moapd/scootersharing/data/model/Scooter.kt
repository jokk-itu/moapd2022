package dk.itu.moapd.scootersharing.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "scooters")
data class Scooter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val name: String,
    @ColumnInfo val lat: Double,
    @ColumnInfo val lon: Double,
    @ColumnInfo val isAvailable: Boolean,
    @ColumnInfo val battery: Int,
    @ColumnInfo val picture: String
) {
    override fun toString() : String {
        return "From ${readableTimestamp()}: $name is placed at ($lat, $lon)"
    }

    fun readableTimestamp() : String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyy:MM:DD:hh:mm:ss", Locale.ENGLISH)
        return formatter.format(date)
    }
}