package dk.itu.moapd.scootersharing.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "rides")
data class Ride(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val userId: String,
    @ColumnInfo val scooterId: Long,
    @ColumnInfo val startLat: Double,
    @ColumnInfo val startLon: Double,
    @ColumnInfo val start: Long,
    @ColumnInfo val endLat: Double? = null,
    @ColumnInfo val endLon: Double? = null,
    @ColumnInfo val end: Long? = null,
    @ColumnInfo val price: Double? = null
    ) {

    fun startToDate() : String {
        val format = "yyyy:MM:dd"
        val formatter = SimpleDateFormat(format, Locale.ENGLISH)
        return formatter.format(start)
    }

    fun endToDate() : String? {
        if(end == null)
            return null

        val format = "yyyy:MM:dd"
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(end)
    }
}