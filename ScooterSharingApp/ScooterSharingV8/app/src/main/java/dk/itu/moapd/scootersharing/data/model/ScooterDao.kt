package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScooterDao {
    @Query("SELECT * FROM scooters")
    fun getAll(): LiveData<List<Scooter>>

    @Query("SELECT DISTINCT(s.id), s.lat, s.lon, s.name FROM scooters s INNER JOIN rides r ON r.scooterId = s.id WHERE r.`end` IS NOT NULL")
    fun getAllAvailable(): LiveData<List<Scooter>>

    @Query("SELECT COUNT(*) FROM scooters s INNER JOIN rides r ON r.scooterId = s.id WHERE s.id = :scooterId AND r.`end` IS NULL")
    fun getAvailableScooter(scooterId : Long) : Int

    @Query("SELECT * FROM scooters WHERE id = :scooterId LIMIT 1")
    fun getScooter(scooterId : Long) : Scooter

    @Insert
    suspend fun insert(scooter: Scooter)

    @Update
    suspend fun update(scooter: Scooter)
}