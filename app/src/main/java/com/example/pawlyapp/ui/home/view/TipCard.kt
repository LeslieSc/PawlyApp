package com.example.pawlyapp.ui.home.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawlyapp.ui.home.model.Tip
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlySoftBeige
import com.example.pawlyapp.ui.theme.PawlyTextSoft
import com.example.pawlyapp.ui.theme.PawlyWhite

@Composable
fun TipCard(
    tip: Tip,
    modifier: Modifier = Modifier
) {

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var saved by rememberSaveable {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = PawlyWhite
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column {

            // Imagen del consejo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clipToBounds()
            ) {

                AsyncImage(
                    model = tip.image,
                    contentDescription = tip.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(2.45f),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 14.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {

                // Texto de recomendado + guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Consejo recomendado",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PawlyBrown
                    )

                    IconButton(
                        onClick = {
                            saved = !saved
                        }
                    ) {

                        Icon(
                            imageVector = if (saved) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Outlined.BookmarkBorder
                            },
                            contentDescription = if (saved) {
                                "Quitar de guardados"
                            } else {
                                "Guardar consejo"
                            },
                            tint = PawlyBrown
                        )
                    }
                }

                // Categoría + fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp
                    )
                ) {

                    Text(
                        text = translateCategory(
                            tip.category
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PawlyDarkBrown,
                        modifier = Modifier
                            .background(
                                color = PawlySoftBeige,
                                shape = RoundedCornerShape(
                                    20.dp
                                )
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            )
                    )

                    Text(
                        text = formatPetDate(
                            tip.publishedAt
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = PawlyTextSoft
                    )
                }

                // Título
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PawlyDarkBrown
                )

                // Contenido
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PawlyTextSoft,
                    maxLines = if (expanded) {
                        Int.MAX_VALUE
                    } else {
                        3
                    },
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                // Expandir o contraer consejo
                TextButton(
                    onClick = {
                        expanded = !expanded
                    },
                    contentPadding = PaddingValues(
                        horizontal = 0.dp,
                        vertical = 4.dp
                    ),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PawlyBrown
                    )
                ) {

                    Text(
                        text = if (expanded) {
                            "Ver menos"
                        } else {
                            "Leer consejo →"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun translateCategory(
    category: String
): String {

    return when (category) {
        "Nutrition" -> "Nutrición"
        "Exercise" -> "Ejercicio"
        "Health" -> "Salud"
        "Behavior" -> "Comportamiento"
        "Grooming" -> "Cuidado"
        else -> category
    }
}