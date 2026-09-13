package com.example.pawlyapp.ui.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawlyapp.ui.home.viewmodel.PetsHomeViewModel
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyCream
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlyTextSoft

@Composable
fun PetsHomeView(
    viewModel: PetsHomeViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    var showAllTips by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PawlyCream)
    ) {

        when {

            // Estado de carga
            uiState.isLoading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = PawlyBrown
                )
            }

            // Estado de error
            uiState.error != null -> {

                HomeError(
                    message = uiState.error ?: "",
                    onRetry = viewModel::loadPetsHome
                )
            }

            // Contenido
            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 30.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(
                        26.dp
                    )
                ) {

                    // Encabezado
                    item {
                        HomeHeader()
                    }


                    // Paseo más reciente
                    uiState.walks
                        .firstOrNull()
                        ?.let { walk ->

                            item {

                                Column(
                                    verticalArrangement =
                                        Arrangement.spacedBy(
                                            12.dp
                                        )
                                ) {

                                    SectionTitle(
                                        "Paseo más reciente"
                                    )

                                    WalkCard(
                                        walk = walk,
                                        featured = true
                                    )
                                }
                            }
                        }


                    // Paseos recientes
                    if (uiState.walks.size > 1) {

                        item {

                            Column(
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        12.dp
                                    )
                            ) {

                                SectionTitle(
                                    "Paseos recientes"
                                )

                                LazyRow(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(
                                            14.dp
                                        )
                                ) {

                                    items(
                                        items = uiState.walks.drop(1),
                                        key = {
                                            it.id
                                        }
                                    ) { walk ->

                                        WalkCard(
                                            walk = walk
                                        )
                                    }
                                }
                            }
                        }
                    }


                    // Consejos
                    if (uiState.tips.isNotEmpty()) {

                        item {

                            SectionTitle(
                                "Consejos para tu mascota"
                            )
                        }


                        // Primer consejo siempre visible
                        item {

                            TipCard(
                                tip = uiState.tips.first()
                            )
                        }


                        // Mostrar los demás consejos
                        // solamente si se presiona el botón
                        if (showAllTips) {

                            items(
                                items = uiState.tips.drop(1),
                                key = {
                                    it.id
                                }
                            ) { tip ->

                                TipCard(
                                    tip = tip
                                )
                            }
                        }


                        // Botón para mostrar u ocultar
                        // los demás consejos
                        if (uiState.tips.size > 1) {

                            item {

                                OutlinedButton(
                                    onClick = {
                                        showAllTips = !showAllTips
                                    },

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),

                                    shape = RoundedCornerShape(
                                        16.dp
                                    ),

                                    colors =
                                        ButtonDefaults.outlinedButtonColors(
                                            contentColor = PawlyBrown
                                        )
                                ) {

                                    Text(
                                        text =
                                            if (showAllTips) {
                                                "Ver menos consejos"
                                            } else {
                                                "Ver más consejos"
                                            },

                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun HomeHeader() {

    Column {

        Text(
            text = "Pawly",

            style =
                MaterialTheme.typography
                    .headlineLarge,

            fontWeight = FontWeight.Bold,

            color = PawlyDarkBrown
        )

        Spacer(
            modifier = Modifier.height(
                4.dp
            )
        )

        Text(
            text =
                "Actividad y consejos para tus mascotas",

            style =
                MaterialTheme.typography
                    .bodyLarge,

            color = PawlyTextSoft
        )
    }
}


@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,

        style =
            MaterialTheme.typography
                .titleLarge,

        fontWeight = FontWeight.Bold,

        color = PawlyDarkBrown
    )
}


@Composable
private fun HomeError(
    message: String,
    onRetry: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                24.dp
            ),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "No se pudo cargar la información",

            style =
                MaterialTheme.typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold,

            color =
                PawlyDarkBrown
        )


        Spacer(
            modifier = Modifier.height(
                8.dp
            )
        )


        Text(
            text = message,

            style =
                MaterialTheme.typography
                    .bodyMedium,

            color =
                PawlyTextSoft
        )


        Spacer(
            modifier = Modifier.height(
                18.dp
            )
        )


        Button(
            onClick = onRetry,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        PawlyBrown
                ),

            shape = RoundedCornerShape(
                16.dp
            )
        ) {

            Text(
                text = "Intentar de nuevo"
            )
        }
    }
}