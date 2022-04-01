package dk.itu.moapd.scootersharing.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScooterDao {
    @Query("SELECT * FROM scooters")
    fun getAll(): LiveData<List<Scooter>>

    @Insert
    fun insert(scooter: Scooter)

    @Update
    fun update(scooter: Scooter)
}