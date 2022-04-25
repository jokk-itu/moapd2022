package dk.itu.moapd.scootersharing.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * (startLat, startLon) is the starting location of the ride
 * (currentLat, currentLon) is the current location of the ride (is equal to start on start, is equal to end on end)
 * (endLat, endLon) is the ended location of the ride
 */

@Entity(tableName = "rides")
data class Ride(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val userId: String,
    @ColumnInfo val scooterId: Long,
    @ColumnInfo val startLat: Double,
    @ColumnInfo val startLon: Double,
    @ColumnInfo val currentLat: Double,
    @ColumnInfo val currentLon: Double,
    @ColumnInfo val start: Long,
    @ColumnInfo val endLat: Double? = null,
    @ColumnInfo val endLon: Double? = null,
    @ColumnInfo val end: Long? = null
    )