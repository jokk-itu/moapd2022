package dk.itu.moapd.scootersharing.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "rides")
data class Ride(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val userId: String,
    @ColumnInfo val scooterId: Long,
    @ColumnInfo val startWhere: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val start: Long,
    @ColumnInfo val endWhere: String?,
    @ColumnInfo val end: Long?
    )