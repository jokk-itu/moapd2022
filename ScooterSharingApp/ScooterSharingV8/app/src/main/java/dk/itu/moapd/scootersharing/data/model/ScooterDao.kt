package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScooterDao {
    @Query("SELECT * FROM scooters ORDER BY id")
    fun getAll(): LiveData<List<Scooter>>

    @Query("SELECT * FROM scooters WHERE isAvailable = 1 ORDER BY id")
    fun getAllAvailable(): LiveData<List<Scooter>>

    @Query("SELECT COUNT(*) FROM scooters s WHERE s.isAvailable = 1 AND s.id = :scooterId")
    fun getAvailableScooter(scooterId : Long) : Int

    @Query("SELECT * FROM scooters WHERE id = :scooterId LIMIT 1")
    fun getScooter(scooterId : Long) : Scooter

    @Insert
    suspend fun insert(scooter: Scooter)

    @Query("UPDATE scooters SET lat = :latitude, lon = :longitude, isAvailable = :isAvailable WHERE id = :scooterId")
    suspend fun update(scooterId: Long, latitude: Double, longitude: Double, isAvailable: Boolean)
}