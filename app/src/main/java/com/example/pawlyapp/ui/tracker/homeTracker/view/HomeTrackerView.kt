
package com.example.pawlyapp.ui.tracker.homeTracker.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.pawlyapp.ui.tracker.homeTracker.model.TrackerMapType
import com.example.pawlyapp.ui.tracker.homeTracker.viewmodel.HomeTrackerViewModel

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTrackerView(
    homeViewModel: HomeTrackerViewModel = viewModel()
) {

    // ========================================
    // ESTADO DEL TRACKER
    // ========================================

    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ========================================
    // COLORES PAWLYAPP
    // ========================================

    val backgroundColor = Color(0xFFF7F1EA)
    val cardColor = Color.White
    val primaryBrown = Color(0xFF8B6B4F)
    val darkBrown = Color(0xFF4E342E)
    val softBeige = Color(0xFFE8D8C3)
    val textSoft = Color(0xFF8D7B68)

    // ========================================
    // ESTADOS PARA LOS MENSAJES
    // ========================================

    var showStepPermissionInfo by remember {
        mutableStateOf(false)
    }

    var showLocationPermissionInfo by remember {
        mutableStateOf(false)
    }

    var showSensorUnavailable by remember {
        mutableStateOf(false)
    }

    var showCannotStart by remember {
        mutableStateOf(false)
    }

    // ========================================
    // VERIFICAR PERMISO DE PASOS
    // ========================================

    fun hasStepPermission(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ========================================
    // VERIFICAR PERMISO DE UBICACION
    // ========================================

    fun hasLocationPermission(): Boolean {

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

        return fineGranted || coarseGranted
    }

    // ========================================
    // VERIFICAR SI PODEMOS INICIAR
    // ========================================

    fun canStartWalk(): Boolean {

        val gpsAvailable = hasLocationPermission()

        val stepsAvailable =
            homeViewModel.hasStepSensor &&
                    hasStepPermission()

        return gpsAvailable || stepsAvailable
    }

    // ========================================
    // PERMISO DE NOTIFICACIONES
    // ========================================

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ ->

            // Android permite iniciar el servicio
            // aunque el usuario rechace notificaciones.
            if (canStartWalk()) {

                homeViewModel.startTracking()

            } else {

                showCannotStart = true
            }
        }

    // ========================================
    // INICIAR SERVICIO DEL PASEO
    // ========================================

    val startWalkService: () -> Unit = {

        if (!canStartWalk()) {

            showCannotStart = true

        } else {

            val needsNotificationPermission =
                Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED

            if (needsNotificationPermission) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

            } else {

                homeViewModel.startTracking()
            }
        }
    }

    // ========================================
    // PERMISO DE UBICACION
    // ========================================

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->

            // Revisar los permisos finales
            // antes de iniciar el servicio.
            startWalkService()
        }

    // ========================================
    // REVISAR GPS ANTES DEL PASEO
    // ========================================

    val continueWithLocationCheck: () -> Unit = {

        if (hasLocationPermission()) {

            startWalkService()

        } else {

            showLocationPermissionInfo = true
        }
    }

    // ========================================
    // PERMISO DEL CONTADOR DE PASOS
    // ========================================

    val stepPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { _ ->

            // Con permiso o sin permiso
            // seguimos comprobando el GPS.
            continueWithLocationCheck()
        }

    // ========================================
    // PANTALLA PRINCIPAL
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // ====================================
        // MAPA OPENSTREETMAP
        // ====================================

        PawlyOsmMap(
            modifier = Modifier.fillMaxSize(),
            route = uiState.route,
            latitude = uiState.latitude,
            longitude = uiState.longitude
        )

        // ====================================
        // PANEL INFERIOR DEL PASEO
        // ====================================

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
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // TITULO
            Text(
                text = "Paseo Pawly",
                color = darkBrown,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // ESTADO ACTUAL
            Text(
                text = when {

                    uiState.isFinished ->
                        "Paseo finalizado"

                    uiState.isPaused ->
                        "Paseo en pausa"

                    uiState.isTracking ->
                        "Paseo en progreso"

                    else ->
                        "¿Listos para salir a caminar?"
                },
                color = textSoft,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // =================================
            // ESTADISTICAS DEL PASEO
            // =================================

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
                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    // TIEMPO
                    TrackerInfo(
                        value = formatTime(
                            uiState.elapsedSeconds
                        ),
                        label = "Tiempo",
                        darkBrown = darkBrown,
                        textSoft = textSoft
                    )

                    // DISTANCIA
                    TrackerInfo(
                        value = String.format(
                            Locale.US,
                            "%.2f km",
                            uiState.distanceKm
                        ),
                        label = "Distancia",
                        darkBrown = darkBrown,
                        textSoft = textSoft
                    )

                    // PASOS
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

            // =================================
            // BOTONES DEL PASEO
            // =================================

            if (!uiState.isTracking) {

                // INICIAR PASEO
                Button(
                    onClick = {

                        when {

                            // Telefono sin sensor de pasos
                            !homeViewModel.hasStepSensor -> {

                                showSensorUnavailable = true
                            }

                            // Falta permiso de actividad fisica
                            !hasStepPermission() -> {

                                showStepPermissionInfo = true
                            }

                            // Continuar revisando GPS
                            else -> {

                                continueWithLocationCheck()
                            }
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

                // TERMINAR PASEO
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

            // =================================
            // RESUMEN AL FINALIZAR
            // =================================

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
                        horizontalAlignment =
                            Alignment.CenterHorizontally
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
                            text = "Tiempo: ${
                                formatTime(uiState.elapsedSeconds)
                            }",
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
                                Locale.US,
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

    // ========================================
    // EXPLICACION DEL PERMISO DE PASOS
    // ========================================

    if (showStepPermissionInfo) {

        AlertDialog(
            onDismissRequest = {
                showStepPermissionInfo = false
            },
            title = {
                Text("Permiso de actividad física 🐾")
            },
            text = {
                Text(
                    "Pawly necesita permiso para contar " +
                            "los pasos durante el paseo.\n\n" +
                            "Si no lo permites, puedes continuar " +
                            "sin registrar pasos."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        showStepPermissionInfo = false

                        stepPermissionLauncher.launch(
                            Manifest.permission.ACTIVITY_RECOGNITION
                        )
                    }
                ) {

                    Text("Permitir")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        showStepPermissionInfo = false

                        continueWithLocationCheck()
                    }
                ) {

                    Text("Continuar sin pasos")
                }
            }
        )
    }

    // ========================================
    // TELEFONO SIN SENSOR DE PASOS
    // ========================================

    if (showSensorUnavailable) {

        AlertDialog(
            onDismissRequest = {
                showSensorUnavailable = false
            },
            title = {
                Text("Sensor no disponible")
            },
            text = {
                Text(
                    "Este teléfono no tiene un contador " +
                            "de pasos compatible.\n\n" +
                            "Puedes registrar el paseo utilizando GPS."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        showSensorUnavailable = false

                        continueWithLocationCheck()
                    }
                ) {

                    Text("Continuar")
                }
            }
        )
    }

    // ========================================
    // EXPLICACION DEL PERMISO GPS
    // ========================================

    if (showLocationPermissionInfo) {

        AlertDialog(
            onDismissRequest = {
                showLocationPermissionInfo = false
            },
            title = {
                Text("Ubicación del paseo 🐾")
            },
            text = {
                Text(
                    "Pawly necesita acceder a tu ubicación " +
                            "para calcular la distancia y dibujar " +
                            "el recorrido sobre OpenStreetMap.\n\n" +
                            "Si no lo permites, podrás continuar " +
                            "solamente con el contador de pasos."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        showLocationPermissionInfo = false

                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {

                    Text("Permitir ubicación")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        showLocationPermissionInfo = false

                        // Iniciar si tenemos permiso de pasos
                        startWalkService()
                    }
                ) {

                    Text("Continuar sin GPS")
                }
            }
        )
    }

    // ========================================
    // NO HAY PERMISOS PARA EL SERVICIO
    // ========================================

    if (showCannotStart) {

        AlertDialog(
            onDismissRequest = {
                showCannotStart = false
            },
            title = {
                Text("No se pudo iniciar el paseo")
            },
            text = {
                Text(
                    "Para iniciar el seguimiento necesitas " +
                            "autorizar al menos la ubicación o " +
                            "el contador de pasos.\n\n" +
                            "Puedes activar estos permisos desde " +
                            "los ajustes de PawlyApp e intentarlo otra vez."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {
                        showCannotStart = false
                    }
                ) {

                    Text("Entendido")
                }
            }
        )
    }
}

// ========================================
// COMPONENTE DE ESTADISTICAS
// ========================================

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

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            fontSize = 13.sp,
            color = textSoft
        )
    }
}

// ========================================
// FORMATO DEL CRONOMETRO
// ========================================

private fun formatTime(seconds: Long): String {

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {

        String.format(
            Locale.US,
            "%02d:%02d:%02d",
            hours,
            minutes,
            remainingSeconds
        )

    } else {

        String.format(
            Locale.US,
            "%02d:%02d",
            minutes,
            remainingSeconds
        )
    }
}
