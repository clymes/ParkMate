package com.example.parkmate.ui.view.vehicle

class VehicleViewModel {
    fun validateVehicle(name: String, type: String, plate: String): String? {
        if (name.isBlank()) return "Il nome del veicolo non può essere vuoto."
        if (type.isBlank()) return "Il tipo di veicolo non può essere vuoto."
        if (plate.isBlank()) return "La targa non può essere vuota."
        return null
    }
}
