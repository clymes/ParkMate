package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parkmate.model.Vehicle

// Data Access Object
@Dao
interface VehicleDao {
    @Insert
    suspend fun addVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles")
    suspend fun getAllVehicles(): List<Vehicle>
}