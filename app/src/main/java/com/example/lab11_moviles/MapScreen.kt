package com.example.lab11_moviles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val ArequipaLocation = LatLng(-16.4040102, -71.559611)
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, size: Int): BitmapDescriptor {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(0, 0, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 12f))
        }
    }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    Column {
        MapTypeSelection(mapType = mapType) { selectedMapType ->
            mapType = selectedMapType
        }

        Button(onClick = { getCurrentLocation() }) {
            Text("Obtener ubicación actual")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = mapType)
            ) {
                Marker(
                    state = rememberMarkerState(position = ArequipaLocation),
                    icon = bitmapDescriptorFromVector(context, R.drawable.pokeparada, 100),
                    title = "Arequipa, Perú"
                )

                currentLocation?.let { loc ->
                    Marker(
                        state = rememberMarkerState(position = loc),
                        title = "Ubicación Actual"
                    )
                }

            }
        }
    }
}

@Composable
fun MapTypeSelection(mapType: MapType, onMapTypeSelected: (MapType) -> Unit) {
    Column {
        Text("Selecciona el tipo de mapa:")
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { onMapTypeSelected(MapType.NORMAL) },
                enabled = mapType != MapType.NORMAL
            ) {
                Text("Normal")
            }
            Button(
                onClick = { onMapTypeSelected(MapType.SATELLITE) },
                enabled = mapType != MapType.SATELLITE
            ) {
                Text("Satélite")
            }
            Button(
                onClick = { onMapTypeSelected(MapType.HYBRID) },
                enabled = mapType != MapType.HYBRID
            ) {
                Text("Híbrido")
            }
            Button(
                onClick = { onMapTypeSelected(MapType.TERRAIN) },
                enabled = mapType != MapType.TERRAIN
            ) {
                Text("Terreno")
            }
        }
    }
}
