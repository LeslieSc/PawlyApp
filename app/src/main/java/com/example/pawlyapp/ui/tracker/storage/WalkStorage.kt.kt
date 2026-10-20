
package com.example.pawlyapp.ui.tracker.storage

import android.content.Context

import com.example.pawlyapp.ui.home.model.Walk
import com.example.pawlyapp.ui.tracker.homeTracker.model.WalkPoint
import com.example.pawlyapp.ui.tracker.service.WalkSessionState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import org.json.JSONArray
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// ========================================
// ALMACENAMIENTO DE PASEOS
// ========================================

object WalkStorage {

    private const val PREFS_NAME = "pawly_walk_storage"
    private const val WALKS_KEY = "saved_walks"

    // Avisar a Home cuando se guarde un paseo
    private val _updates = MutableStateFlow(0)

    val updates = _updates.asStateFlow()

    // ====================================
    // OBTENER ALMACENAMIENTO
    // ====================================

    private fun getPreferences(context: Context) =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    // ====================================
    // LEER JSON GUARDADO
    // ====================================

    private fun readJsonArray(context: Context): JSONArray {

        val savedText = getPreferences(context)
            .getString(WALKS_KEY, "[]") ?: "[]"

        return try {
            JSONArray(savedText)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    // ====================================
    // GUARDAR PASEO FINALIZADO
    // ====================================

    fun saveWalk(
        context: Context,
        session: WalkSessionState
    ) {

        // Solo guardar paseos terminados
        if (!session.isFinished) return

        // Calcular duracion en minutos
        val durationMinutes = maxOf(
            1,
            ((session.elapsedSeconds + 59L) / 60L).toInt()
        )

        // Fecha del paseo
        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale("es", "MX")
        )

        val dateText = dateFormat.format(Date())

        // ====================================
        // GUARDAR COORDENADAS DEL RECORRIDO
        // ====================================

        val routeArray = JSONArray()

        session.route.forEach { point ->

            val pointJson = JSONObject().apply {

                put("latitude", point.latitude)

                put("longitude", point.longitude)

                put(
                    "startsNewSegment",
                    point.startsNewSegment
                )
            }

            routeArray.put(pointJson)
        }

        // ====================================
        // CREAR REGISTRO DEL PASEO
        // ====================================

        val walkJson = JSONObject().apply {

            // Identificador unico
            put(
                "id",
                UUID.randomUUID().hashCode() and Int.MAX_VALUE
            )

            // Nombre temporal de la mascota
            put("petName", "Mi mascota")

            // Fecha
            put("date", dateText)

            // Pasos registrados
            put("steps", session.steps)

            // Duracion
            put("durationMin", durationMinutes)

            put(
                "durationText",
                "$durationMinutes min"
            )

            // Distancia recorrida
            put("distanceKm", session.distanceKm)

            // Imagen opcional
            put("image", "")

            // Coordenadas del trayecto
            put("route", routeArray)
        }

        // ====================================
        // CONSERVAR PASEOS ANTERIORES
        // ====================================

        val previousWalks = readJsonArray(context)

        val updatedWalks = JSONArray()

        // Agregar primero el paseo nuevo
        updatedWalks.put(walkJson)

        // Agregar el historial anterior
        for (i in 0 until previousWalks.length()) {

            updatedWalks.put(
                previousWalks.get(i)
            )
        }

        // ====================================
        // GUARDAR EN EL TELEFONO
        // ====================================

        getPreferences(context)
            .edit()
            .putString(
                WALKS_KEY,
                updatedWalks.toString()
            )
            .apply()

        // Avisar a Home que hay cambios
        _updates.value = _updates.value + 1
    }

    // ====================================
    // OBTENER PASEOS GUARDADOS
    // ====================================

    fun getSavedWalks(
        context: Context
    ): List<Walk> {

        val result = mutableListOf<Walk>()

        val savedWalks = readJsonArray(context)

        for (i in 0 until savedWalks.length()) {

            val json = savedWalks.optJSONObject(i)
                ?: continue

            val walk = Walk(

                id = json.optInt("id"),

                petName = json.optString(
                    "petName",
                    "Mi mascota"
                ),

                date = json.optString("date"),

                steps = json.optInt("steps"),

                durationMin = json.optInt(
                    "durationMin"
                ),

                durationText = json.optString(
                    "durationText"
                ),

                distanceKm = json.optDouble(
                    "distanceKm"
                ),

                image = json.optString("image")
            )

            result.add(walk)
        }

        return result
    }

    // ====================================
    // OBTENER RECORRIDO DE UN PASEO
    // ====================================

    fun getSavedRoute(
        context: Context,
        walkId: Int
    ): List<WalkPoint> {

        val walksArray = readJsonArray(context)

        for (i in 0 until walksArray.length()) {

            val walkJson =
                walksArray.optJSONObject(i)
                    ?: continue

            // Buscar el paseo por su ID
            if (walkJson.optInt("id") != walkId) {
                continue
            }

            // Obtener las coordenadas
            val routeArray =
                walkJson.optJSONArray("route")
                    ?: return emptyList()

            val points = mutableListOf<WalkPoint>()

            for (j in 0 until routeArray.length()) {

                val point =
                    routeArray.optJSONObject(j)
                        ?: continue

                points.add(
                    WalkPoint(

                        latitude = point.optDouble(
                            "latitude"
                        ),

                        longitude = point.optDouble(
                            "longitude"
                        ),

                        startsNewSegment =
                            point.optBoolean(
                                "startsNewSegment"
                            )
                    )
                )
            }

            return points
        }

        return emptyList()
    }
}
