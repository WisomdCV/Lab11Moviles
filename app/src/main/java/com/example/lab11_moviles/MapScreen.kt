package com.example.lab11_moviles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker

// Encargado de la gestión del mapa
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    // Función para convertir un recurso de imagen en BitmapDescriptor con un tamaño ajustado
    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, size: Int): BitmapDescriptor {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(0, 0, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Lista de ubicaciones
    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994) // Zamacola
    )

    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984,-71.6836503), 12f), // Mover a Yura
            durationMs = 3000
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Añadir GoogleMap al layout
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = bitmapDescriptorFromVector(context, R.drawable.pokeparada, 100), // Icono personalizado con tamaño ajustado
                title = "Arequipa, Perú"
            )

            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Ubicación",
                    snippet = "Punto de interés"
                )
            }
        }
    }
}