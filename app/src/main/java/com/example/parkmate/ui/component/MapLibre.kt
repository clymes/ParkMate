package com.example.parkmate.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.layers.LocationIndicatorLayer
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.LocalMapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle

@Composable
fun MapLibre(modifier: Modifier = Modifier) {
    val locationProvider = rememberDefaultLocationProvider()
    val headingProvider = rememberDefaultHeadingProvider()

    val locationState = rememberLocationState(
        provider = locationProvider,
        headingProvider = headingProvider,
    )

    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
    ) {
        val currentMapState = checkNotNull(LocalMapState.current)

        LocationIndicatorLayer(
            id = "user",
            locationState = locationState,
        )

        LocationTrackingEffect(locationState = locationState) {
            currentMapState.animateCamera(CameraUpdate(center = currentLocation.position, zoom = 15.0))
        }
    }

    MaplibreMap(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        state = mapState
    ) {
        LocationTrackingEffect(locationState = locationState) {
            mapState.animateCamera(
                CameraUpdate(center = currentLocation.position, zoom = 15.0)
            )
        }
    }
}
