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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle

@Composable
fun MapLibre(
    modifier: Modifier = Modifier,
    mapState: MapState = getMapState(),
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
fun getMapState(): MapState {
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

        LocationIndicatorLayer(
            id = "user",
            locationState = locationState,
        )

        LocationTrackingEffect(locationState = locationState) {
            currentMapState.animateCamera(
                CameraUpdate(center = this.currentLocation.position, zoom = 15.0)
            )
        }
    }
}