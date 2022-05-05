package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScooterDao {
    @Query("SELECT * FROM scooters")
    fun getAll(): List<Scooter>

    @Query("SELECT * FROM scooters ORDER BY id")
    fun getAllLive(): LiveData<List<Scooter>>

    @Query("SELECT * FROM scooters WHERE isAvailable = 1 AND battery > 0 ORDER BY id")
    fun getAllAvailableLive(): LiveData<List<Scooter>>

    @Query("SELECT * FROM scooters WHERE isAvailable = 1 AND battery > 0 ORDER BY id")
    fun getAllAvailable(): List<Scooter>

    @Query("SELECT COUNT(*) FROM scooters s WHERE s.isAvailable = 1 AND s.id = :scooterId AND battery > 0")
    fun getAvailableScooter(scooterId : Long) : Int

    @Query("SELECT * FROM scooters WHERE id = :scooterId LIMIT 1")
    fun getScooter(scooterId : Long) : Scooter

    @Insert
    suspend fun insert(scooter: Scooter)

    @Query("UPDATE scooters SET lat = :latitude, lon = :longitude WHERE id = :scooterId")
    suspend fun update(scooterId: Long, latitude: Double, longitude: Double)

    @Query("UPDATE scooters SET isAvailable = :isAvailable WHERE id = :scooterId")
    suspend fun update(scooterId: Long, isAvailable: Boolean)

    @Query("UPDATE scooters SET battery = :battery WHERE id = :scooterId")
    suspend fun update(scooterId: Long, battery: Int)
}