package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScooterDao {
    @Query("SELECT * FROM scooters")
    fun getAll(): LiveData<List<Scooter>>

    @Insert
    suspend fun insert(scooter: Scooter)

    @Update
    suspend fun update(scooter: Scooter)
}