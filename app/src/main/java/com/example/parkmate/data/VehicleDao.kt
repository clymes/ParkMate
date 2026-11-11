package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parkmate.model.Vehicle
import kotlinx.coroutines.flow.Flow

// Data Access Object per i veicoli
@Dao
interface VehicleDao {
    @Insert
    suspend fun addVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleById(id: Int): Flow<Vehicle?>
}