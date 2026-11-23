package com.example.parkmate.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Location
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LocationIndicatorLayer
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.LocalMapState
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle

@Composable
fun MapLibre(
    modifier: Modifier = Modifier,
    mapState: MapState,
    showCenterPin: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            state = mapState,
            overlay = {}
        )

        if (showCenterPin) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Punto selezionato",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            )
        }
    }
}

@Composable
fun getMapState(
    isTracking: Boolean = true,
    activeParkings: List<Location> = emptyList()
): MapState {
    val locationProvider = rememberDefaultLocationProvider()
    val headingProvider = rememberDefaultHeadingProvider()

    val locationState = rememberLocationState(
        provider = locationProvider,
        headingProvider = headingProvider,
    )

    return rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/bright"),
    ) {
        val currentMapState = checkNotNull(LocalMapState.current)

        // parcheggi attivi
        if (activeParkings.isNotEmpty()) {
            val featuresJson = activeParkings.joinToString(",") { loc ->
                """
                {
                  "type": "Feature",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [${loc.long}, ${loc.lat}]
                  }
                }
                """.trimIndent()
            }
            val geoJson = """{"type":"FeatureCollection","features":[$featuresJson]}"""

            val source = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(geoJson)
            )

            CircleLayer(
                id = "active_parkings_layer",
                source = source,
                color = const(Color.Red),
                radius = const(8.dp),
                strokeColor = const(Color.White),
                strokeWidth = const(2.dp)
            )
        }

        LocationIndicatorLayer(
            id = "user",
            locationState = locationState,
        )

        if (isTracking) {
            LocationTrackingEffect(locationState = locationState) {
                currentMapState.animateCamera(
                    CameraUpdate(center = this.currentLocation.position, zoom = 15.0)
                )
            }
        }
    }
}
