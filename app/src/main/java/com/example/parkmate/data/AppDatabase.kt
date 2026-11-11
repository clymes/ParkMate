package com.example.parkmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parkmate.model.Vehicle
import com.example.parkmate.model.Location

@Database(
    entities = [Vehicle::class, Location::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun locationDao(): LocationDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database.db"
            ).fallbackToDestructiveMigration(false).build()
        }
    }
}