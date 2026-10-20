
package com.example.pawlyapp.ui.tracker.homeTracker.view

import android.graphics.Color

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

import com.example.pawlyapp.ui.tracker.homeTracker.model.WalkPoint

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

@Composable
fun PawlyOsmMap(
    route: List<WalkPoint>,
    latitude: Double?,
    longitude: Double?,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // ========================================
    // CONFIGURAR OPENSTREETMAP
    // ========================================

    val mapView = remember(context) {

        // Identificar correctamente nuestra app
        // ante el servidor de OpenStreetMap.
        Configuration.getInstance().userAgentValue =
            "PawlyApp/1.0 (+https://github.com/LeslieSc/PawlyApp)"

        MapView(context).apply {

            // Mapa estandar de OpenStreetMap
            setTileSource(TileSourceFactory.MAPNIK)

            // Permitir zoom con los dedos
            setMultiTouchControls(true)

            // Nivel de zoom inicial
            controller.setZoom(16.0)

            // Ubicacion inicial temporal
            // Se actualiza al recibir GPS.
            controller.setCenter(
                GeoPoint(
                    19.4326,
                    -99.1332
                )
            )

            // Creditos obligatorios del mapa
            overlays.add(
                CopyrightOverlay(context)
            )
        }
    }

    // ========================================
    // GUARDAR LINEAS Y MARCADORES
    // ========================================

    val routeOverlays = remember(mapView) {
        mutableListOf<Overlay>()
    }

    // ========================================
    // CICLO DE VIDA DEL MAPA
    // ========================================

    DisposableEffect(mapView, context) {

        val owner = context as? LifecycleOwner

        val observer = LifecycleEventObserver { _, event ->

            when (event) {

                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }

                else -> Unit
            }
        }

        owner?.lifecycle?.addObserver(observer)

        if (
            owner?.lifecycle?.currentState
                ?.isAtLeast(Lifecycle.State.RESUMED) == true
        ) {
            mapView.onResume()
        }

        onDispose {

            owner?.lifecycle?.removeObserver(observer)

            mapView.onPause()

            mapView.onDetach()
        }
    }

    // ========================================
    // MOSTRAR MAPA EN JETPACK COMPOSE
    // ========================================

    AndroidView(
        modifier = modifier.fillMaxSize(),

        factory = {
            mapView
        },

        update = { map ->

            // =================================
            // LIMPIAR RUTAS ANTERIORES
            // =================================

            map.overlays.removeAll(
                routeOverlays
            )

            routeOverlays.clear()

            // =================================
            // AGRUPAR COORDENADAS POR TRAMOS
            // =================================

            val segments =
                mutableListOf<MutableList<GeoPoint>>()

            route.forEach { point ->

                // Crear tramo nuevo al reanudar
                // un paseo que estuvo pausado.
                if (
                    point.startsNewSegment ||
                    segments.isEmpty()
                ) {

                    segments.add(
                        mutableListOf()
                    )
                }

                segments.last().add(
                    GeoPoint(
                        point.latitude,
                        point.longitude
                    )
                )
            }

            // =================================
            // DIBUJAR RECORRIDO
            // =================================

            segments.forEach { points ->

                // Se necesitan al menos 2 puntos
                // para dibujar una linea.
                if (points.size >= 2) {

                    val line = Polyline().apply {

                        setPoints(points)

                        // Color cafe de PawlyApp
                        outlinePaint.color =
                            Color.rgb(
                                139,
                                107,
                                79
                            )

                        outlinePaint.strokeWidth = 10f
                    }

                    map.overlays.add(line)

                    routeOverlays.add(line)
                }
            }

            // =================================
            // MOSTRAR UBICACION ACTUAL
            // =================================

            if (
                latitude != null &&
                longitude != null
            ) {

                val currentPosition = GeoPoint(
                    latitude,
                    longitude
                )

                // Marcador GPS
                val marker = Marker(map).apply {

                    position = currentPosition

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )

                    title = "Ubicación del paseo"
                }

                map.overlays.add(marker)

                routeOverlays.add(marker)

                // Centrar en la ubicacion GPS
                map.controller.setCenter(
                    currentPosition
                )
            }

            // Actualizar el mapa
            map.invalidate()
        }
    )
}
