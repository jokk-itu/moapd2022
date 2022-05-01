package dk.itu.moapd.scootersharing.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Ride::class, Scooter::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
    abstract fun scooterDao(): ScooterDao

    companion object {

        private val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //SCOOTERS
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Ninja', 55.9318627, 12.2998268, 0, 100, 'scooter1')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Voi', 55.9318627, 12.2998270, 1, 100, 'scooter2')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Lite', 55.9318627, 12.2998270, 1, 100, 'scooter3')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Pingu', 55.9618627, 12.2998270, 1, 100, 'scooter2')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Pingus Dad', 55.9328627, 12.2998270, 1, 100, 'scooter1')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Pingus Mom', 55.9319627, 12.2998270, 1, 100, 'scooter4')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Pingus Pet Fish', 55.9318647, 12.2998270, 1, 100, 'scooter3')")
                database.execSQL("INSERT INTO scooters (name, lat, lon, isAvailable, battery, picture) VALUES('Pingus Sister', 55.9318620, 12.2938270, 1, 100, 'scooter4')")

                //RIDES
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9318630, 12.2998268, CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 2, 55.9318630, 12.2998268, CURRENT_TIMESTAMP, 55.9318400, 12.2998268, CURRENT_TIMESTAMP, 50)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 2, 55.9318630, 12.2998273, CURRENT_TIMESTAMP, 55.9318630, 12.2998300, CURRENT_TIMESTAMP, 14)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9318630, 12.2998278, CURRENT_TIMESTAMP, 55.9318630, 12.2998400, CURRENT_TIMESTAMP, 21)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4998500, CURRENT_TIMESTAMP, 18)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4998490, CURRENT_TIMESTAMP, 31)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4998460, CURRENT_TIMESTAMP, 39)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4998200, CURRENT_TIMESTAMP, 10)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4998300, CURRENT_TIMESTAMP, 25)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startLat, startLon, start, endLat, endLon, `end`, price) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 55.9618630, 12.3998278, CURRENT_TIMESTAMP, 55.9418630, 12.4999400, CURRENT_TIMESTAMP, 43)")
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
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}