package com.example.pawlyapp.ui.tracker.homeTracker.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawlyapp.ui.tracker.homeTracker.model.HomeTrackerModel
import com.example.pawlyapp.ui.tracker.homeTracker.model.TrackerMapType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeTrackerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeTrackerModel())

    val uiState: StateFlow<HomeTrackerModel> =
        _uiState.asStateFlow()

    private var timerJob: Job? = null

    private var accumulatedTimeMs = 0L
    private var startedAtMs = 0L

    // INICIAR PASEO
    fun startTracking() {
        if (_uiState.value.isTracking) return

        timerJob?.cancel()
        accumulatedTimeMs = 0L
        startedAtMs = SystemClock.elapsedRealtime()

        _uiState.update {
            it.copy(
                isTracking = true,
                isPaused = false,
                isFinished = false,
                elapsedSeconds = 0L,
                steps = 0,
                distanceKm = 0.0
            )
        }

        startTimer()
    }

    // PAUSAR PASEO
    fun pauseTracking() {
        val state = _uiState.value
        if (!state.isTracking || state.isPaused) return

        accumulatedTimeMs +=
            SystemClock.elapsedRealtime() - startedAtMs

        timerJob?.cancel()

        _uiState.update {
            it.copy(
                isPaused = true,
                elapsedSeconds = accumulatedTimeMs / 1000
            )
        }
    }

    // REANUDAR PASEO
    fun resumeTracking() {
        val state = _uiState.value
        if (!state.isTracking || !state.isPaused) return

        startedAtMs = SystemClock.elapsedRealtime()

        _uiState.update {
            it.copy(isPaused = false)
        }

        startTimer()
    }

    // TERMINAR PASEO
    fun finishTracking() {
        val state = _uiState.value
        if (!state.isTracking) return

        if (!state.isPaused) {
            accumulatedTimeMs +=
                SystemClock.elapsedRealtime() - startedAtMs
        }

        timerJob?.cancel()

        _uiState.update {
            it.copy(
                isTracking = false,
                isPaused = false,
                isFinished = true,
                elapsedSeconds = accumulatedTimeMs / 1000
            )
        }
    }

    // CRONOMETRO
    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (isActive) {
                val currentTime =
                    SystemClock.elapsedRealtime() - startedAtMs

                _uiState.update {
                    it.copy(
                        elapsedSeconds =
                            (accumulatedTimeMs + currentTime) / 1000
                    )
                }

                delay(250)
            }
        }
    }

    // OPCIONES DEL MAPA
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

    fun selectMapType(mapType: TrackerMapType) {
        _uiState.update {
            it.copy(
                selectedMapType = mapType,
                showMapOptions = false
            )
        }
    }

    fun toggle3DMode() {
        _uiState.update {
            it.copy(is3DMode = !it.is3DMode)
        }
    }

    // UBICACION
    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    // DATOS DEL PASEO
    fun updateTrackingData(
        elapsedSeconds: Long,
        distanceKm: Double,
        steps: Int
    ) {
        _uiState.update {
            it.copy(
                elapsedSeconds = elapsedSeconds,
                distanceKm = distanceKm,
                steps = steps
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}