
package com.example.pawlyapp.ui.tracker.homeTracker.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.pawlyapp.ui.tracker.homeTracker.model.TrackerMapType
import com.example.pawlyapp.ui.tracker.homeTracker.viewmodel.HomeTrackerViewModel

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTrackerView(
    homeViewModel: HomeTrackerViewModel = viewModel()
) {

    val uiState by homeViewModel.uiState.collectAsState()

    // COLORES DE PAWLYAPP
    val backgroundColor = Color(0xFFF7F1EA)
    val cardColor = Color.White
    val primaryBrown = Color(0xFF8B6B4F)
    val darkBrown = Color(0xFF4E342E)
    val softBeige = Color(0xFFE8D8C3)
    val textSoft = Color(0xFF8D7B68)

    // UBICACION TEMPORAL PARA PROBAR GOOGLE MAPS
    val initialPosition = LatLng(
        19.4326,
        -99.1332
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialPosition,
            15f
        )
    }

    // TIPO DE MAPA
    val googleMapType = when (uiState.selectedMapType) {
        TrackerMapType.STANDARD -> MapType.NORMAL
        TrackerMapType.SATELLITE -> MapType.SATELLITE
        TrackerMapType.HYBRID -> MapType.HYBRID
        TrackerMapType.TERRAIN -> MapType.TERRAIN
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // MAPA GOOGLE MAPS
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = googleMapType
            )
        )

        // CONTROLES SUPERIORES
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 24.dp,
                    end = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // CAMBIAR TIPO DE MAPA
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        homeViewModel.openMapOptions()
                    },
                shape = CircleShape,
                color = cardColor,
                shadowElevation = 6.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Tipo de mapa",
                        tint = primaryBrown,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // BOTON 3D
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        homeViewModel.toggle3DMode()
                    },
                shape = CircleShape,
                color = if (uiState.is3DMode) {
                    primaryBrown
                } else {
                    cardColor
                },
                shadowElevation = 6.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3D",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.is3DMode) {
                            Color.White
                        } else {
                            primaryBrown
                        }
                    )
                }
            }
        }

        // PANEL INFERIOR DEL PASEO
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp
                    )
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Paseo Pawly",
                color = darkBrown,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // ESTADO DEL PASEO
            Text(
                text = when {
                    uiState.isFinished -> "Paseo finalizado"
                    uiState.isPaused -> "Paseo en pausa"
                    uiState.isTracking -> "Paseo en progreso"
                    else -> "¿Listos para salir a caminar?"
                },
                color = textSoft,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // INFORMACION DEL PASEO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    TrackerInfo(
                        value = formatTime(
                            uiState.elapsedSeconds
                        ),
                        label = "Tiempo",
                        darkBrown = darkBrown,
                        textSoft = textSoft
                    )

                    TrackerInfo(
                        value = String.format(
                            java.util.Locale.US,
                            "%.2f km",
                            uiState.distanceKm
                        ),
                        label = "Distancia",
                        darkBrown = darkBrown,
                        textSoft = textSoft
                    )

                    TrackerInfo(
                        value = uiState.steps.toString(),
                        label = "Pasos",
                        darkBrown = darkBrown,
                        textSoft = textSoft
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // BOTONES DEL PASEO
            if (!uiState.isTracking) {

                // INICIAR UN NUEVO PASEO
                Button(
                    onClick = {
                        homeViewModel.startTracking()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBrown,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = if (uiState.isFinished) {
                            "Iniciar otro paseo"
                        } else {
                            "Iniciar paseo"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            } else {

                // PAUSAR O REANUDAR
                Button(
                    onClick = {
                        if (uiState.isPaused) {
                            homeViewModel.resumeTracking()
                        } else {
                            homeViewModel.pauseTracking()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBrown,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = if (uiState.isPaused) {
                            "Reanudar paseo"
                        } else {
                            "Pausar paseo"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // FINALIZAR PASEO
                Button(
                    onClick = {
                        homeViewModel.finishTracking()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBrown,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "Terminar paseo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // RESUMEN CUANDO TERMINA EL PASEO
            if (uiState.isFinished) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = softBeige
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "¡Paseo terminado! 🐾",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBrown
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Tiempo: ${formatTime(uiState.elapsedSeconds)}",
                            fontSize = 14.sp,
                            color = darkBrown
                        )

                        Text(
                            text = "Pasos: ${uiState.steps}",
                            fontSize = 14.sp,
                            color = darkBrown
                        )

                        Text(
                            text = String.format(
                                java.util.Locale.US,
                                "Distancia: %.2f km",
                                uiState.distanceKm
                            ),
                            fontSize = 14.sp,
                            color = darkBrown
                        )
                    }
                }
            }
        }
    }

    // PANEL DE TIPOS DE MAPA
    if (uiState.showMapOptions) {

        ModalBottomSheet(
            onDismissRequest = {
                homeViewModel.closeMapOptions()
            },
            containerColor = backgroundColor
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 32.dp)
            ) {

                Text(
                    text = "Tipo de mapa",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                MapTypeOption(
                    title = "Estándar",
                    selected = uiState.selectedMapType ==
                            TrackerMapType.STANDARD,
                    primaryBrown = primaryBrown,
                    softBeige = softBeige,
                    darkBrown = darkBrown
                ) {
                    homeViewModel.selectMapType(
                        TrackerMapType.STANDARD
                    )
                }

                Spacer(Modifier.height(10.dp))

                MapTypeOption(
                    title = "Satélite",
                    selected = uiState.selectedMapType ==
                            TrackerMapType.SATELLITE,
                    primaryBrown = primaryBrown,
                    softBeige = softBeige,
                    darkBrown = darkBrown
                ) {
                    homeViewModel.selectMapType(
                        TrackerMapType.SATELLITE
                    )
                }

                Spacer(Modifier.height(10.dp))

                MapTypeOption(
                    title = "Híbrido",
                    selected = uiState.selectedMapType ==
                            TrackerMapType.HYBRID,
                    primaryBrown = primaryBrown,
                    softBeige = softBeige,
                    darkBrown = darkBrown
                ) {
                    homeViewModel.selectMapType(
                        TrackerMapType.HYBRID
                    )
                }

                Spacer(Modifier.height(10.dp))

                MapTypeOption(
                    title = "Terreno",
                    selected = uiState.selectedMapType ==
                            TrackerMapType.TERRAIN,
                    primaryBrown = primaryBrown,
                    softBeige = softBeige,
                    darkBrown = darkBrown
                ) {
                    homeViewModel.selectMapType(
                        TrackerMapType.TERRAIN
                    )
                }
            }
        }
    }
}

// INFORMACION DE TIEMPO, DISTANCIA Y PASOS
@Composable
private fun TrackerInfo(
    value: String,
    label: String,
    darkBrown: Color,
    textSoft: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = textSoft
        )
    }
}

// OPCIONES DE MAPA
@Composable
private fun MapTypeOption(
    title: String,
    selected: Boolean,
    primaryBrown: Color,
    softBeige: Color,
    darkBrown: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) {
                    softBeige
                } else {
                    Color.White
                },
                shape = RoundedCornerShape(18.dp)
            )
            .clickable {
                onClick()
            }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(18.dp)
                .background(
                    color = if (selected) {
                        primaryBrown
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                )
        )

        Spacer(Modifier.size(14.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = darkBrown
        )
    }
}

// FORMATO DEL CRONOMETRO
private fun formatTime(seconds: Long): String {

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {
        String.format(
            java.util.Locale.US,
            "%02d:%02d:%02d",
            hours,
            minutes,
            remainingSeconds
        )
    } else {
        String.format(
            java.util.Locale.US,
            "%02d:%02d",
            minutes,
            remainingSeconds
        )
    }
}
