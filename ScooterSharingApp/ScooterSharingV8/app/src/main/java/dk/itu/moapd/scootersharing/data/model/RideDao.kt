package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RideDao {
    @Query("SELECT * FROM rides WHERE userId LIKE :userId")
    fun getAllFromUser(userId: String): LiveData<List<Ride>>

    @Query("SELECT * FROM rides WHERE userId LIKE :userId ORDER BY id LIMIT 1 OFFSET :offset")
    suspend fun getFromUser(userId: String, offset: Int): Ride

    @Query("SELECT * FROM rides WHERE userId LIKE :userId AND endLat NOT NULL AND endLon NOT NULL AND `end` NOT NULL LIMIT 1")
    suspend fun getCurrentRide(userId: String): Ride

    @Query("SELECT COUNT(*) FROM rides WHERE userId LIKE :userId")
    suspend fun getCountFromUser(userId: String) : Int

    @Insert
    suspend fun insert(ride: Ride)

    @Update
    suspend fun update(ride: Ride)

    @Delete
    suspend fun delete(ride: Ride)
}