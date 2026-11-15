package com.example.parkmate.network

import android.util.Log
import com.example.parkmate.model.PhotonFeature
import com.example.parkmate.model.PhotonResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

class PhotonService {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(UserAgent) {
            agent = "ParkMate/1.0 (Android)"
        }
    }

    suspend fun searchLocations(query: String): List<PhotonFeature> {
        if (query.trim().length < 3) return emptyList()
        return try {
            val response: PhotonResponse = client.get("https://photon.komoot.io/api/") {
                parameter("q", query.trim())
                parameter("limit", 5)
            }.body()
            response.features
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("PhotonService", "Errore nel fetch di Photon: $query. Error: ${e.message}", e)
            emptyList()
        }
    }
}
