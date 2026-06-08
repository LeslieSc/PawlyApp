package com.example.pawlyapp.ui.mainmenu.firstapirequest.view

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawlyapp.ui.mainmenu.firstapirequest.model.RazaPerro
import com.example.pawlyapp.ui.mainmenu.firstapirequest.viewmodel.FirstApiRequestViewModel
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyCream
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlySoftBeige
import com.example.pawlyapp.ui.theme.PawlyTextSoft
import com.example.pawlyapp.ui.theme.PawlyWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstApiRequestView(
    onBack: () -> Unit = {},
    viewModel: FirstApiRequestViewModel = viewModel()
) {
    val breeds by viewModel.breeds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        containerColor = PawlyCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Razas",
                        fontWeight = FontWeight.Bold,
                        color = PawlyWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = PawlyWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PawlyBrown
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PawlyCream)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PawlyBrown
                    )
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        color = PawlyDarkBrown,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                breeds.isEmpty() -> {
                    Text(
                        text = "No hay razas disponibles",
                        modifier = Modifier.align(Alignment.Center),
                        color = PawlyTextSoft,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(breeds) { breed ->
                            BreedCard(breed = breed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreedCard(
    breed: RazaPerro
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = PawlyWhite
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = breed.imagenUrl,
                contentDescription = breed.nombre,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = breed.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PawlyDarkBrown
                )

                Text(
                    text = "Origen: ${breed.origen}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PawlyTextSoft
                )

                Text(
                    text = "Tamaño: ${breed.tamano}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PawlyBrown,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = breed.temperamento,
                    style = MaterialTheme.typography.bodySmall,
                    color = PawlyTextSoft
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cuidado: ${breed.nivelCuidado}",
                    style = MaterialTheme.typography.labelMedium,
                    color = PawlyDarkBrown,
                    modifier = Modifier
                        .background(
                            color = PawlySoftBeige,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}