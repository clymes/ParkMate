package com.example.parkmate.model

import kotlinx.serialization.Serializable
@Serializable
data class PhotonResponse(
    val features: List<PhotonFeature> = emptyList()
)

@Serializable
data class PhotonFeature(
    val geometry: PhotonGeometry,
    val properties: PhotonProperties
) {
    val lat: Double?
        get() = geometry.coordinates.getOrNull(1)

    val long: Double?
        get() = geometry.coordinates.getOrNull(0)

    fun toDisplayString(): String {
        val addressPartsList = mutableListOf<String>()

        // Nome luogo || Via
        if (!properties.name.isNullOrBlank() && properties.name != properties.street) {
            addressPartsList.add(properties.name)
        }

        // Via Roma + 10
        val street = listOfNotNull(properties.street, properties.housenumber)
            .joinToString(" ")

        if (street.isNotBlank()) {
            addressPartsList.add(street)
        }

        // + 20121
        if (!properties.postcode.isNullOrBlank()) {
            addressPartsList.add(properties.postcode)
        }

        // + Milano
        if (!properties.city.isNullOrBlank()) {
            addressPartsList.add(properties.city)
        }

        // "Via Roma 10, 20121, Milano"
        return addressPartsList.joinToString(", ")
    }
}

@Serializable
data class PhotonGeometry(
    val coordinates: List<Double> = emptyList()
)

@Serializable
data class PhotonProperties(
    val name: String? = null,
    val street: String? = null,
    val housenumber: String? = null,
    val postcode: String? = null,
    val city: String? = null
)
