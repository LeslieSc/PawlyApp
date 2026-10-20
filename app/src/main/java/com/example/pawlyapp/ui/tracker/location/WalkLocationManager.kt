
package com.example.pawlyapp.ui.tracker.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat

class WalkLocationManager(
    context: Context,
    private val onNewLocation: (Location) -> Unit
) {

    private val appContext = context.applicationContext

    private val locationManager =
        appContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

    private var isListening = false

    // ESCUCHAR CAMBIOS DE UBICACION
    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            // Ignorar ubicaciones con poca precision
            if (location.hasAccuracy() && location.accuracy > 40f) {
                return
            }

            onNewLocation(location)
        }
    }

    // VERIFICAR PERMISOS
    private fun hasFinePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCoarsePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // INICIAR GPS
    fun start(): Boolean {

        if (isListening) return true

        val fine = hasFinePermission()
        val coarse = hasCoarsePermission()

        if (!fine && !coarse) return false

        val provider = when {

            fine && locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            ) -> LocationManager.GPS_PROVIDER

            locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            ) -> LocationManager.NETWORK_PROVIDER

            else -> return false
        }

        return try {

            locationManager.requestLocationUpdates(
                provider,
                2000L,
                2f,
                locationListener,
                Looper.getMainLooper()
            )

            isListening = true
            true

        } catch (e: SecurityException) {
            false
        }
    }

    // DETENER GPS
    fun stop() {

        if (isListening) {
            locationManager.removeUpdates(locationListener)
        }

        isListening = false
    }
}