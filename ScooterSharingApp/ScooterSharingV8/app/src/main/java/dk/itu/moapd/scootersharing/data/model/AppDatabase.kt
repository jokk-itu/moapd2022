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
                database.execSQL("INSERT INTO scooters (name, `where`) VALUES('Ninja', 'Frederiksvaerk')")
                database.execSQL("INSERT INTO scooters (name, `where`) VALUES('Voi', 'Koebenhavn')")
                database.execSQL("INSERT INTO rides (userId, scooterId, startWhere, start) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 0, 'Frederiksvaerk', CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startWhere, start, endWhere, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 'Koebenhavn', CURRENT_TIMESTAMP, 'Ballerup', CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startWhere, start, endWhere, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 1, 'Koebenhavn', CURRENT_TIMESTAMP, 'Frederiksvaerk', CURRENT_TIMESTAMP)")
                database.execSQL("INSERT INTO rides (userId, scooterId, startWhere, start, endWhere, `end`) VALUES('3pr5qghZioZqMqBYfGQHvE3Goj03', 0, 'Koebenhavn', CURRENT_TIMESTAMP, 'Frederiksvaerk', CURRENT_TIMESTAMP)")
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