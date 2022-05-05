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

    private fun readableTimestamp() : String {
        val date = Calendar.getInstance().timeInMillis
        val formatter = SimpleDateFormat("yyyy:MM:DD", Locale.getDefault())
        return formatter.format(date)
    }
}