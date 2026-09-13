package com.example.pawlyapp.ui.home.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    var expanded by remember {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            }
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

            AsyncImage(
                model = tip.image,
                contentDescription = tip.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = translateCategory(
                            tip.category
                        ),
                        style =
                            MaterialTheme.typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.SemiBold,
                        color = PawlyDarkBrown,
                        modifier = Modifier
                            .background(
                                color =
                                    PawlySoftBeige,
                                shape =
                                    RoundedCornerShape(
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
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color = PawlyTextSoft
                    )
                }

                Text(
                    text = tip.title,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PawlyDarkBrown
                )

                Text(
                    text = tip.content,
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color = PawlyTextSoft,
                    maxLines = if (expanded) {
                        Int.MAX_VALUE
                    } else {
                        3
                    },
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text = if (expanded) {
                        "Ver menos"
                    } else {
                        "Ver más"
                    },
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color = PawlyBrown
                )
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