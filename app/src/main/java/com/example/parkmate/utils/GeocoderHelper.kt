package com.example.parkmate.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Restituisce l'indirizzo sotto forma di stringa a partire da latitudine e longitudine
 *
 * @param context Il contesto dell'applicazione
 * @param lat La latitudine
 * @param long La longitudine
 * @return La stringa dell'indirizzo trovato, oppure un testo fallback in caso di errore
 */
suspend fun getAddressFromCoords(
    context: Context,
    lat: Double,
    long: Double,
): String = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(lat, long, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val firstAddress = addresses.firstOrNull()
                        val result: String

                        if (firstAddress != null) {
                            val stringAddress = firstAddress.getAddressLine(0)
                            if (stringAddress != null) {
                                result = stringAddress
                            } else {
                                result = "Indirizzo non trovato"
                            }
                        } else {
                            result = "Indirizzo non trovato"
                        }

                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) {
                            Log.e("GeocoderHelper", "Errore nel GeocoderHelper: $errorMessage")
                            continuation.resume("Lat: $lat, Long: $long")
                        }
                    }
                })
            }
        } else { // x i deprecati data la min SDK
            @Suppress("DEPRECATION")
            val addressList = geocoder.getFromLocation(lat, long, 1)
            val firstAddress = addressList?.firstOrNull()
            val stringAddress = firstAddress?.getAddressLine(0)
            stringAddress ?: "Indirizzo non trovato"
        }
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("GeocoderHelper", "Exception in getAddressFromCoords", e)
        "Lat: $lat, Long: $long"
    }
}

/**
 * Restituisce le coordinate lat e long a partire da un indirizzo stringa
 *
 * @param context Il contesto dell'applicazione
 * @param address L'indirizzo scritto in stringa leggibile
 * @return Una coppia [Pair] di valori "lat" e "long" se trovato, altrimenti null
 */
suspend fun getCoordsFromAddress(
    context: Context,
    address: String,
): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val firstAddress = addresses.firstOrNull()
                        val result: Pair<Double, Double>?

                        if (firstAddress != null) {
                            val lat = firstAddress.latitude
                            val long = firstAddress.longitude
                            result = Pair(lat, long)
                        } else {
                            result = null
                        }

                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) {
                            Log.e("GeocoderHelper", "Errore nel GeocoderHelper per indirizzo: $errorMessage")
                            continuation.resume(null)
                        }
                    }
                })
            }
        } else { // x i deprecati data la min SDK
            @Suppress("DEPRECATION")
            val addressList = geocoder.getFromLocationName(address, 1)
            val firstAddress = addressList?.firstOrNull()

            if (firstAddress != null) {
                val lat = firstAddress.latitude
                val long = firstAddress.longitude
                Pair(lat, long)
            } else {
                null
            }
        }
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("GeocoderHelper", "Exception in getCoordsFromAddress", e)
        null
    }
}
