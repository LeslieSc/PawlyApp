
package com.example.pawlyapp.ui.home.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlyTextSoft

@Composable
fun WalksEmptyState(
    onStartWalk: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ILUSTRACION DE HUELLA
            Canvas(
                modifier = Modifier.size(110.dp)
            ) {

                val width = size.width
                val height = size.height

                // Almohadilla principal
                drawCircle(
                    color = PawlyBrown,
                    radius = width * 0.21f,
                    center = Offset(
                        width * 0.50f,
                        height * 0.63f
                    )
                )

                // Dedos de la huella
                val toes = listOf(
                    Offset(width * 0.24f, height * 0.40f),
                    Offset(width * 0.41f, height * 0.23f),
                    Offset(width * 0.62f, height * 0.23f),
                    Offset(width * 0.79f, height * 0.40f)
                )

                toes.forEach { position ->

                    drawCircle(
                        color = PawlyBrown,
                        radius = width * 0.095f,
                        center = position
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // TITULO
            Text(
                text = "Aún no hay paseos",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = PawlyDarkBrown,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // DESCRIPCION
            Text(
                text = "¡Cada aventura comienza con un paso! " +
                        "Inicia tu primer paseo con tu mascota " +
                        "y comienza a registrar sus recorridos.",
                fontSize = 14.sp,
                color = PawlyTextSoft,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // BOTON PARA IR AL TRACKER
            Button(
                onClick = onStartWalk,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PawlyBrown,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Iniciar paseo 🐾",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
