
package com.example.pawlyapp.ui.tracker.homeTracker.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager

import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.pawlyapp.ui.tracker.homeTracker.model.HomeTrackerModel
import com.example.pawlyapp.ui.tracker.homeTracker.model.TrackerMapType
import com.example.pawlyapp.ui.tracker.service.WalkSessionStore
import com.example.pawlyapp.ui.tracker.service.WalkTrackingService

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeTrackerViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ====================================
    // ESTADO DE LA PANTALLA
    // ====================================

    private val _uiState = MutableStateFlow(
        HomeTrackerModel()
    )

    val uiState: StateFlow<HomeTrackerModel> =
        _uiState.asStateFlow()

    // ====================================
    // COMPROBAR SENSOR DE PASOS
    // ====================================

    private val sensorManager =
        application.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    val hasStepSensor: Boolean
        get() = sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        ) != null

    // ====================================
    // SINCRONIZAR DATOS CON EL SERVICIO
    // ====================================

    init {

        viewModelScope.launch {

            WalkSessionStore.state.collect { session ->

                _uiState.update { current ->

                    current.copy(
                        isTracking = session.isTracking,
                        isPaused = session.isPaused,
                        isFinished = session.isFinished,

                        elapsedSeconds = session.elapsedSeconds,
                        steps = session.steps,
                        distanceKm = session.distanceKm,

                        route = session.route,

                        latitude = session.latitude,
                        longitude = session.longitude
                    )
                }
            }
        }
    }

    // ====================================
    // INICIAR PASEO
    // ====================================

    fun startTracking() {

        if (WalkSessionStore.state.value.isTracking) {
            return
        }

        WalkTrackingService.send(
            getApplication<Application>(),
            WalkTrackingService.ACTION_START
        )
    }

    // ====================================
    // PAUSAR PASEO
    // ====================================

    fun pauseTracking() {

        if (
            !WalkSessionStore.state.value.isTracking ||
            WalkSessionStore.state.value.isPaused
        ) {
            return
        }

        WalkTrackingService.send(
            getApplication<Application>(),
            WalkTrackingService.ACTION_PAUSE
        )
    }

    // ====================================
    // REANUDAR PASEO
    // ====================================

    fun resumeTracking() {

        if (
            !WalkSessionStore.state.value.isTracking ||
            !WalkSessionStore.state.value.isPaused
        ) {
            return
        }

        WalkTrackingService.send(
            getApplication<Application>(),
            WalkTrackingService.ACTION_RESUME
        )
    }

    // ====================================
    // TERMINAR PASEO
    // ====================================

    fun finishTracking() {

        if (!WalkSessionStore.state.value.isTracking) {
            return
        }

        WalkTrackingService.send(
            getApplication<Application>(),
            WalkTrackingService.ACTION_FINISH
        )
    }

    // ====================================
    // ACTIVAR GPS TRAS CONCEDER PERMISO
    // ====================================

    fun startLocationTracking(): Boolean {

        val context = getApplication<Application>()

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return false
        }

        if (!WalkSessionStore.state.value.isTracking) {
            return false
        }

        if (WalkSessionStore.state.value.isPaused) {
            return false
        }

        // Avisar al servicio que ya puede usar el GPS
        WalkTrackingService.send(
            context,
            WalkTrackingService.ACTION_REFRESH
        )

        return true
    }

    // ====================================
    // OPCIONES DEL MAPA
    // ====================================

    fun openMapOptions() {

        _uiState.update {
            it.copy(showMapOptions = true)
        }
    }

    fun closeMapOptions() {

        _uiState.update {
            it.copy(showMapOptions = false)
        }
    }

    fun selectMapType(
        mapType: TrackerMapType
    ) {

        _uiState.update {
            it.copy(
                selectedMapType = mapType,
                showMapOptions = false
            )
        }
    }

    fun toggle3DMode() {

        _uiState.update {
            it.copy(
                is3DMode = !it.is3DMode
            )
        }
    }

    // ====================================
    // ACTUALIZACION MANUAL DE UBICACION
    // ====================================

    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {

        // No sustituir coordenadas GPS
        // registradas por un paseo activo.
        if (_uiState.value.isTracking) {
            return
        }

        _uiState.update {

            it.copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    // ====================================
    // METODO DE COMPATIBILIDAD
    // ====================================

    fun updateTrackingData(
        elapsedSeconds: Long,
        distanceKm: Double,
        steps: Int
    ) {

        // Durante el paseo, solo el servicio
        // debe modificar estos datos.
        if (_uiState.value.isTracking) {
            return
        }

        _uiState.update {

            it.copy(
                elapsedSeconds = elapsedSeconds,
                distanceKm = distanceKm,
                steps = steps
            )
        }
    }
}