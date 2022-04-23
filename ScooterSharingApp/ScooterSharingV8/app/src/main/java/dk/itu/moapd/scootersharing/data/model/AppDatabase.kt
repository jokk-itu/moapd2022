package dk.itu.moapd.scootersharing.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2

@Database(entities = [Ride::class, Scooter::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
    abstract fun scooterDao(): ScooterDao

    companion object {

        private val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //SCOOTERS
                database.execSQL("INSERT INTO scooters (name, lat, lon) VALUES('Ninja', 55.9318627, 12.2998268)")
                database.execSQL("INSERT INTO scooters (name, lat, lon) VALUES('Voi', 55.9318627,12.2998270)")

                //RIDES
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, currentLat, currentLon, start) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 0, 55.9318630, 12.2998268, 55.9318700, 12.2998268, CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, currentLat, currentLon, start, endLat, endLon, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9318630, 12.2998268, 55.9318400, 12.2998268, CURRENT_TIMESTAMP, 55.9318400, 12.2998268, CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, currentLat, currentLon, start, endLat, endLon, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9318630, 12.2998273, 55.9318630, 12.2998300, CURRENT_TIMESTAMP, 55.9318630, 12.2998300, CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, currentLat, currentLon, start, endLat, endLon, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 0, 55.9318630, 12.2998278, 55.9318630, 12.2998400, CURRENT_TIMESTAMP, 55.9318630, 12.2998400, CURRENT_TIMESTAMP)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "scootersharing"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}