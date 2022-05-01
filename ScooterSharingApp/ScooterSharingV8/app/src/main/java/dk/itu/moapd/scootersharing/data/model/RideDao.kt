package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RideDao {
    @Query("SELECT * FROM rides WHERE userId LIKE :userId ORDER BY id")
    fun getAllFromUser(userId: String): LiveData<List<Ride>>

    @Query("SELECT * FROM rides WHERE userId LIKE :userId ORDER BY id LIMIT 1 OFFSET :offset")
    fun getFromUser(userId: String, offset: Int): Ride

    @Query("SELECT * FROM rides WHERE userId LIKE :userId AND endLat IS NULL AND endLon IS NULL AND `end` IS NULL LIMIT 1")
    fun getCurrentRide(userId: String): Ride?

    /*@Query("SELECT * FROM rides WHERE userId LIKE :userId AND endLat IS NULL AND endLon IS NULL AND `end` IS NULL LIMIT 1")
    fun getLiveCurrentRide(userId: String): LiveData<Ride?>*/

    /*@Query("SELECT COUNT(*) FROM rides WHERE userId LIKE :userId")
    fun getCountFromUser(userId: String) : Int*/

    @Insert
    suspend fun insert(ride: Ride)

    @Query("UPDATE rides SET endLat = :endLat, endLon = :endLon, `end` = :end, price = :price WHERE id = :id")
    suspend fun update(id: Long, endLat: Double, endLon: Double, end: Long, price: Double)
}