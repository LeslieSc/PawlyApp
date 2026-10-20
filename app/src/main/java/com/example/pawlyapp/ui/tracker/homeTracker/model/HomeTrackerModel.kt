package com.example.pawlyapp.ui.tracker.homeTracker.model

enum class TrackerMapType {
    STANDARD,
    SATELLITE,
    HYBRID,
    TERRAIN
}

data class HomeTrackerModel(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,

    val elapsedSeconds: Long = 0L,
    val distanceKm: Double = 0.0,
    val steps: Int = 0,

    val selectedMapType: TrackerMapType = TrackerMapType.STANDARD,
    val showMapOptions: Boolean = false,
    val is3DMode: Boolean = false,

    val latitude: Double? = null,
    val longitude: Double? = null
)