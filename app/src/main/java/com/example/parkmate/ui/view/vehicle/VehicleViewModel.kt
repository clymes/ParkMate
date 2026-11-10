package com.example.parkmate.ui.view.vehicle

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Vehicle
import com.example.parkmate.model.VehicleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehicleViewModel : ViewModel() {
    fun validateVehicle(name: String, type: String, plate: String): String? {
        if (name.isBlank()) return "Il nome del veicolo non può essere vuoto."
        if (type.isBlank()) return "Il tipo di veicolo non può essere vuoto."
        if (plate.isBlank()) return "La targa non può essere vuota."
        return null
    }

    fun vehicleTypeToString(type: VehicleType?): String {
        if (type == null) {
            return ""
        }
        return type.displayName.uppercase()
    }

    fun addVehicle(
        context: Context,
        name: String,
        type: VehicleType?,
        plate: String,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit,
    ) {
        val typeString = vehicleTypeToString(type)
        val errorMessage = validateVehicle(name, typeString, plate)
        val successMessage = "Veicolo salvato con successo"

        if (errorMessage != null) {
            onError(errorMessage)
            return
        }

        val db = AppDatabase.getInstance(context)
        viewModelScope.launch(Dispatchers.IO) {
            db.vehicleDao().addVehicle(
                Vehicle(
                    name = name.trim(),
                    type = typeString,
                    plate = plate.trim(),
                )
            )
            withContext(Dispatchers.Main) {
                onSuccess(successMessage)
            }
        }
    }
}
