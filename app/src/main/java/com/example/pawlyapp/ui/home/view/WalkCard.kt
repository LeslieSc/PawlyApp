
package com.example.pawlyapp.ui.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage

import com.example.pawlyapp.ui.home.model.Walk
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlyTextSoft
import com.example.pawlyapp.ui.theme.PawlyWhite

import java.util.Locale
import kotlin.math.roundToInt

// ========================================
// TARJETA DE PASEO
// ========================================

@Composable
fun WalkCard(
    walk: Walk,
    modifier: Modifier = Modifier,
    featured: Boolean = false
) {

    // Distancia formateada correctamente
    val distanceText = formatWalkDistance(
        walk.distanceKm
    )

    ElevatedCard(
        modifier = if (featured) {
            modifier.fillMaxWidth()
        } else {
            modifier.width(215.dp)
        },

        shape = RoundedCornerShape(
            if (featured) 24.dp else 20.dp
        ),

        colors = CardDefaults.elevatedCardColors(
            containerColor = PawlyWhite
        ),

        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column {

            // =================================
            // IMAGEN DEL PASEO
            // =================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (featured) {
                            210.dp
                        } else {
                            125.dp
                        }
                    )
                    .clipToBounds()
                    .background(PawlyBrown)
            ) {

                // Mostrar imagen si existe
                if (walk.image.isNotBlank()) {

                    AsyncImage(
                        model = walk.image,
                        contentDescription =
                            "Paseo de ${walk.petName}",

                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.18f),

                        contentScale = ContentScale.Crop
                    )
                }

                // Capa oscura para leer el texto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(
                                        alpha = 0.65f
                                    )
                                )
                            )
                        )
                )

                // =================================
                // NOMBRE Y FECHA
                // =================================

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {

                    Text(
                        text = walk.petName,

                        style = if (featured) {
                            MaterialTheme.typography
                                .headlineSmall
                        } else {
                            MaterialTheme.typography
                                .titleMedium
                        },

                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = formatPetDate(
                            walk.date,
                            withTime = featured
                        ),

                        style = MaterialTheme.typography
                            .bodySmall,

                        color = Color.White
                    )
                }
            }

            // =================================
            // PASEO MAS RECIENTE
            // =================================

            if (featured) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    // PASOS
                    WalkStat(
                        icon = Icons.Default.DirectionsWalk,

                        value = String.format(
                            Locale.getDefault(),
                            "%,d",
                            walk.steps
                        ),

                        label = "pasos"
                    )

                    // DISTANCIA CORREGIDA
                    WalkStat(
                        icon = Icons.Default.LocationOn,
                        value = distanceText,
                        label = "distancia"
                    )

                    // TIEMPO
                    WalkStat(
                        icon = Icons.Default.Timer,
                        value = walk.durationText,
                        label = "tiempo"
                    )
                }

            } else {

                // =================================
                // TARJETAS DE PASEOS ANTERIORES
                // =================================

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {

                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%,d pasos",
                            walk.steps
                        ),

                        fontWeight = FontWeight.SemiBold,
                        color = PawlyBrown
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    // Distancia y tiempo formateados
                    Text(
                        text = "$distanceText · ${walk.durationText}",

                        style = MaterialTheme.typography
                            .bodySmall,

                        color = PawlyTextSoft
                    )
                }
            }
        }
    }
}

// ========================================
// COMPONENTE DE ESTADISTICAS
// ========================================

@Composable
private fun WalkStat(
    icon: ImageVector,
    value: String,
    label: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.spacedBy(3.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PawlyBrown
        )

        Text(
            text = value,

            style = MaterialTheme.typography
                .titleMedium,

            fontWeight = FontWeight.Bold,
            color = PawlyDarkBrown
        )

        Text(
            text = label,

            style = MaterialTheme.typography
                .bodySmall,

            color = PawlyTextSoft
        )
    }
}

// ========================================
// FORMATEAR DISTANCIA DEL PASEO
// ========================================

private fun formatWalkDistance(
    distanceKm: Double
): String {

    // Evitar valores invalidos
    if (!distanceKm.isFinite() || distanceKm < 0.0) {
        return "0 m"
    }

    // Menos de 10 metros: mostrar en metros
    if (distanceKm < 0.01) {

        val meters = (
                distanceKm * 1000.0
                ).roundToInt()

        return "$meters m"
    }

    // A partir de 10 metros: mostrar kilometros
    // con solamente dos decimales
    return String.format(
        Locale.US,
        "%.2f km",
        distanceKm
    )
}
