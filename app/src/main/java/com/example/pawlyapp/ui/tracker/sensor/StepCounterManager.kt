
package com.example.pawlyapp.ui.tracker.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat

class StepCounterManager(
    context: Context,
    private val onStepsChanged: (Int) -> Unit
) : SensorEventListener {

    private val appContext = context.applicationContext

    private val sensorManager =
        appContext.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    // SENSOR NATIVO DE PASOS
    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    private var totalSteps = 0

    private var lastReading: Int? = null

    private var isListening = false

    val isAvailable: Boolean
        get() = stepSensor != null

    // COMPROBAR PERMISO
    private fun hasPermission(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // INICIAR O REANUDAR SENSOR
    fun start(): Boolean {

        if (isListening) return true

        if (!isAvailable || !hasPermission()) {
            return false
        }

        // Nueva referencia para excluir pasos de la pausa
        lastReading = null

        val sensor = stepSensor ?: return false

        isListening = sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        return isListening
    }

    // PAUSAR SENSOR
    fun pause() {
        stop()
    }

    // DETENER SENSOR
    fun stop() {

        if (isListening) {
            sensorManager.unregisterListener(this)
        }

        isListening = false
        lastReading = null
    }

    // REINICIAR CONTEO DEL PASEO
    fun reset() {

        stop()

        totalSteps = 0
        lastReading = null

        onStepsChanged(0)
    }

    // RECIBIR PASOS DEL TELEFONO
    override fun onSensorChanged(event: SensorEvent?) {

        if (!isListening) return

        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) {
            return
        }

        val currentReading = event.values[0].toInt()

        val previousReading = lastReading

        if (
            previousReading != null &&
            currentReading >= previousReading
        ) {

            val newSteps =
                currentReading - previousReading

            totalSteps += newSteps
        }

        // Guardar referencia para la siguiente lectura
        lastReading = currentReading

        // Actualizar pantalla
        onStepsChanged(totalSteps)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // No requiere cambios
    }
}