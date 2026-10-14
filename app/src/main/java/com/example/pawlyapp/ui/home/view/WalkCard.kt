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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawlyapp.ui.home.model.Walk
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlyTextSoft
import com.example.pawlyapp.ui.theme.PawlyWhite

@Composable
fun WalkCard(
    walk: Walk,
    modifier: Modifier = Modifier,
    featured: Boolean = false
) {

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
            ) {

                AsyncImage(
                    model = walk.image,
                    contentDescription = "Paseo de ${walk.petName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.18f),
                    contentScale = ContentScale.Crop
                )

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

            if (featured) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    WalkStat(
                        icon = Icons.Default.DirectionsWalk,
                        value = "%,d".format(
                            walk.steps
                        ),
                        label = "pasos"
                    )

                    WalkStat(
                        icon = Icons.Default.LocationOn,
                        value = "${walk.distanceKm} km",
                        label = "distancia"
                    )

                    WalkStat(
                        icon = Icons.Default.Timer,
                        value = walk.durationText,
                        label = "tiempo"
                    )
                }

            } else {

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {

                    Text(
                        text = "%,d pasos".format(
                            walk.steps
                        ),
                        fontWeight = FontWeight.SemiBold,
                        color = PawlyBrown
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "${walk.distanceKm} km · ${walk.durationText}",
                        style = MaterialTheme.typography
                            .bodySmall,
                        color = PawlyTextSoft
                    )
                }
            }
        }
    }
}

@Composable
private fun WalkStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally,
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