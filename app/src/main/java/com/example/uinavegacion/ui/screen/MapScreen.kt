package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MapaScreen() {
    AndroidView(factory = { context ->
        org.osmdroid.views.MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)

            val ubicacion = org.osmdroid.util.GeoPoint(-33.36330556263367, -70.67827103459769)
            controller.setCenter(ubicacion)

            val marker = org.osmdroid.views.overlay.Marker(this).apply {
                position = ubicacion
                title = "Nuestra ubicación"
                subDescription = "Aquí estamos"
                setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
            }

            overlays.add(marker)
        }
    }, modifier = Modifier.fillMaxSize())
}