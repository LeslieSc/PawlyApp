
package com.example.pawlyapp.ui.tracker.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.SystemClock

import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat

import com.example.pawlyapp.MainActivity
import com.example.pawlyapp.ui.tracker.homeTracker.model.WalkPoint
import com.example.pawlyapp.ui.tracker.location.WalkLocationManager
import com.example.pawlyapp.ui.tracker.sensor.StepCounterManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.pawlyapp.ui.tracker.storage.WalkStorage



// ==========================================
// ESTADO DEL PASEO
// ==========================================

data class WalkSessionState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val steps: Int = 0,
    val distanceKm: Double = 0.0,
    val route: List<WalkPoint> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)

// Datos compartidos entre el servicio
// y las pantallas de PawlyApp.
object WalkSessionStore {

    private val _state =
        MutableStateFlow(WalkSessionState())

    val state = _state.asStateFlow()

    fun update(
        change: (WalkSessionState) -> WalkSessionState
    ) {
        _state.update(change)
    }

    fun reset() {
        _state.value = WalkSessionState()
    }
}

// ==========================================
// SERVICIO PRINCIPAL
// ==========================================

class WalkTrackingService : Service() {

    companion object {

        const val ACTION_START = "pawly.walk.START"
        const val ACTION_PAUSE = "pawly.walk.PAUSE"
        const val ACTION_RESUME = "pawly.walk.RESUME"
        const val ACTION_FINISH = "pawly.walk.FINISH"
        const val ACTION_REFRESH = "pawly.walk.REFRESH"

        private const val CHANNEL_ID =
            "pawly_walk_channel"

        private const val NOTIFICATION_ID = 1001

        // Metodo para enviar acciones al servicio.
        fun send(context: Context, action: String) {

            val intent = Intent(
                context,
                WalkTrackingService::class.java
            ).apply {
                this.action = action
            }

            if (action == ACTION_START) {

                ContextCompat.startForegroundService(
                    context,
                    intent
                )

            } else {

                context.startService(intent)
            }
        }
    }

    // ======================================
    // VARIABLES DEL SERVICIO
    // ======================================

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private var timerJob: Job? = null

    private var accumulatedMs = 0L
    private var startedAtMs = 0L

    private var lastLocation: Location? = null

    private lateinit var stepManager: StepCounterManager

    private lateinit var locationManager: WalkLocationManager

    // ======================================
    // CREAR SERVICIO
    // ======================================

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        // CONTADOR DE PASOS
        stepManager = StepCounterManager(this) { steps ->

            WalkSessionStore.update { current ->

                if (current.isTracking && !current.isPaused) {

                    current.copy(steps = steps)

                } else {

                    current
                }
            }
        }

        // SEGUIMIENTO GPS
        locationManager = WalkLocationManager(this) { location ->

            handleLocation(location)
        }
    }

    // ======================================
    // RECIBIR ACCIONES DE LA PANTALLA
    // ======================================

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> startWalk()

            ACTION_PAUSE -> pauseWalk()

            ACTION_RESUME -> resumeWalk()

            ACTION_FINISH -> finishWalk()

            ACTION_REFRESH -> refreshSensors()
        }

        return START_NOT_STICKY
    }

    // ======================================
    // COMPROBAR PERMISOS
    // ======================================

    private fun hasPermission(
        permission: String
    ): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ======================================
    // ACTIVAR SERVICIO EN PRIMER PLANO
    // ======================================

    private fun promoteToForeground(): Boolean {

        val hasActivityPermission =
            Build.VERSION.SDK_INT <
                    Build.VERSION_CODES.Q ||
                    hasPermission(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )

        val hasLocationPermission =
            hasPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ||
                    hasPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

        var serviceTypes = 0

        // Servicio de ubicacion
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q &&
            hasLocationPermission
        ) {

            serviceTypes = serviceTypes or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        }

        // Servicio de actividad fisica
        if (
            Build.VERSION.SDK_INT >= 34 &&
            hasActivityPermission
        ) {

            serviceTypes = serviceTypes or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        }

        // Android 14+ requiere un tipo valido.
        if (
            Build.VERSION.SDK_INT >= 34 &&
            serviceTypes == 0
        ) {
            return false
        }

        return try {

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                createNotification(),
                serviceTypes
            )

            true

        } catch (e: Exception) {

            false
        }
    }

    // ======================================
    // INICIAR PASEO
    // ======================================

    private fun startWalk() {

        if (WalkSessionStore.state.value.isTracking) {
            return
        }

        // Mostrar notificacion permanente
        if (!promoteToForeground()) {

            stopSelf()
            return
        }

        accumulatedMs = 0L

        startedAtMs =
            SystemClock.elapsedRealtime()

        lastLocation = null

        WalkSessionStore.reset()

        WalkSessionStore.update {
            it.copy(isTracking = true)
        }

        // Reiniciar pasos
        stepManager.reset()

        // Activar sensores disponibles
        stepManager.start()
        locationManager.start()

        // Iniciar cronometro
        startTimer()
    }

    // ======================================
    // PAUSAR PASEO
    // ======================================

    private fun pauseWalk() {

        val current =
            WalkSessionStore.state.value

        if (
            !current.isTracking ||
            current.isPaused
        ) {
            return
        }

        accumulatedMs +=
            SystemClock.elapsedRealtime() -
                    startedAtMs

        timerJob?.cancel()

        stepManager.pause()

        locationManager.stop()

        lastLocation = null

        WalkSessionStore.update {

            it.copy(
                isPaused = true,
                elapsedSeconds = accumulatedMs / 1000
            )
        }

        updateNotification()
    }

    // ======================================
    // REANUDAR PASEO
    // ======================================

    private fun resumeWalk() {

        val current =
            WalkSessionStore.state.value

        if (
            !current.isTracking ||
            !current.isPaused
        ) {
            return
        }

        startedAtMs =
            SystemClock.elapsedRealtime()

        lastLocation = null

        WalkSessionStore.update {

            it.copy(isPaused = false)
        }

        refreshSensors()

        startTimer()
    }

    // ======================================
    // ACTUALIZAR SENSORES
    // ======================================

    private fun refreshSensors() {

        val current =
            WalkSessionStore.state.value

        if (!current.isTracking) {
            return
        }

        if (!promoteToForeground()) {
            return
        }

        if (!current.isPaused) {

            stepManager.start()

            locationManager.start()
        }

        updateNotification()
    }


    // ======================================
    // TERMINAR Y GUARDAR PASEO - F04
    // ======================================

    private fun finishWalk() {

        val current = WalkSessionStore.state.value

        // Comprobar que exista un paseo activo
        if (!current.isTracking) {
            return
        }

        // Calcular el tiempo final del paseo
        if (!current.isPaused) {
            accumulatedMs +=
                SystemClock.elapsedRealtime() - startedAtMs
        }

        // Detener el cronometro
        timerJob?.cancel()

        // Detener el contador de pasos
        stepManager.stop()

        // Detener el GPS
        locationManager.stop()

        lastLocation = null

        // Actualizar los resultados finales
        WalkSessionStore.update {
            it.copy(
                isTracking = false,
                isPaused = false,
                isFinished = true,
                elapsedSeconds = accumulatedMs / 1000
            )
        }

        // Guardar el paseo en el telefono
        val finishedWalk = WalkSessionStore.state.value

        try {
            WalkStorage.saveWalk(
                context = applicationContext,
                session = finishedWalk
            )
        } catch (e: Exception) {
            android.util.Log.e(
                "PawlyWalk",
                "Error al guardar el paseo",
                e
            )
        }

        // Quitar la notificacion permanente
        stopForeground(STOP_FOREGROUND_REMOVE)

        // Detener el servicio
        stopSelf()
    }


    // ======================================
    // CRONOMETRO
    // ======================================

    private fun startTimer() {

        timerJob?.cancel()

        timerJob = serviceScope.launch {

            while (isActive) {

                val elapsed =
                    accumulatedMs +
                            SystemClock.elapsedRealtime() -
                            startedAtMs

                WalkSessionStore.update {

                    it.copy(
                        elapsedSeconds = elapsed / 1000
                    )
                }

                delay(250)
            }
        }
    }

    // ======================================
    // CALCULAR DISTANCIA DEL GPS
    // ======================================

    private fun handleLocation(
        location: Location
    ) {

        val current =
            WalkSessionStore.state.value

        if (
            !current.isTracking ||
            current.isPaused
        ) {
            return
        }

        val previous = lastLocation

        // Primera coordenada
        if (previous == null) {

            lastLocation = Location(location)

            WalkSessionStore.update {

                it.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    route = it.route + WalkPoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        startsNewSegment = true
                    )
                )
            }

            return
        }

        val meters =
            previous.distanceTo(location)

        // Ignorar movimientos muy pequenos
        if (meters < 3f) {
            return
        }

        val seconds =
            (
                    location.elapsedRealtimeNanos -
                            previous.elapsedRealtimeNanos
                    ) / 1_000_000_000.0

        if (seconds <= 0.0) {
            return
        }

        // Filtrar saltos muy grandes de GPS
        if (meters / seconds > 8.0) {
            return
        }

        lastLocation = Location(location)

        // Guardar distancia y coordenadas
        WalkSessionStore.update {

            it.copy(
                latitude = location.latitude,
                longitude = location.longitude,
                distanceKm =
                    it.distanceKm + meters / 1000.0,
                route = it.route + WalkPoint(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }
    }

    // ======================================
    // CANAL DE NOTIFICACIONES
    // ======================================

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= 26) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Paseos Pawly",
                NotificationManager.IMPORTANCE_LOW
            ).apply {

                description =
                    "Seguimiento de paseos activos"
            }

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    // ======================================
    // NOTIFICACION DEL PASEO
    // ======================================

    private fun createNotification(): Notification {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val paused =
            WalkSessionStore.state.value.isPaused

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(
                android.R.drawable.ic_menu_mylocation
            )
            .setContentTitle(
                "PawlyApp - Paseo"
            )
            .setContentText(
                if (paused) {
                    "Paseo en pausa"
                } else {
                    "Registrando tu paseo"
                }
            )
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .build()
    }

    // ======================================
    // ACTUALIZAR NOTIFICACION
    // ======================================

    private fun updateNotification() {

        val manager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            NOTIFICATION_ID,
            createNotification()
        )
    }

    // ======================================
    // METODOS DEL SERVICIO
    // ======================================

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    override fun onDestroy() {

        timerJob?.cancel()

        stepManager.stop()

        locationManager.stop()

        serviceScope.cancel()

        super.onDestroy()
    }
}