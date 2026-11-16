package com.example.parkmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parkmate.model.Vehicle
import com.example.parkmate.model.Location
import com.example.parkmate.model.Parking

@Database(
    entities = [Vehicle::class, Location::class, Parking::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun locationDao(): LocationDao
    abstract fun parkingDao(): ParkingDao

    companion object {
        private var istance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if(istance != null) {
                return istance!!
            }

            val newIstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database.db"
            ).fallbackToDestructiveMigration(false).build()

            istance = newIstance
            return newIstance
        }
    }
}
