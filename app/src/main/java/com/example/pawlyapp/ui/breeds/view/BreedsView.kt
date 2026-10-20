
package com.example.pawlyapp.ui.breeds.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawlyapp.ui.breeds.model.RazaPerro
import com.example.pawlyapp.ui.breeds.viewmodel.BreedsViewModel
import com.example.pawlyapp.ui.theme.PawlyBrown
import com.example.pawlyapp.ui.theme.PawlyCream
import com.example.pawlyapp.ui.theme.PawlyDarkBrown
import com.example.pawlyapp.ui.theme.PawlySoftBeige
import com.example.pawlyapp.ui.theme.PawlyTextSoft
import com.example.pawlyapp.ui.theme.PawlyWhite

/**
 * Pantalla principal del apartado de Razas.
 *
 * Muestra las razas obtenidas por el ViewModel y permite
 * buscarlas y filtrarlas.
 *
 * Los cambios de diseño se aplican solamente a esta pantalla.
 */
@Composable
fun BreedsView(
    onBack: (() -> Unit)? = null,
    viewModel: BreedsViewModel = viewModel()
) {
    // Observa los estados del ViewModel.
    // Compose actualiza la pantalla cuando cambian.
    val breeds by viewModel.breeds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedCareLevel by viewModel.selectedCareLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Determina si hay una búsqueda o filtro activo.
    val isSearching = searchQuery.isNotBlank() ||
            (selectedSize != null && selectedSize != "Todos") ||
            (selectedCareLevel != null && selectedCareLevel != "Todos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PawlyCream)
    ) {
        // Encabezado reducido para aprovechar mejor el espacio.
        CompactBreedsHeader(onBack = onBack)

        // Buscador conectado al ViewModel.
        CompactSearchTextField(
            query = searchQuery,
            onQueryChange = {
                viewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
                )
        )

        // Filtros de tamaño y nivel de cuidado.
        CompactFilterControlsRow(
            selectedSize = selectedSize ?: "Todos",
            onSizeSelected = {
                viewModel.onSizeFilterChanged(it)
            },
            selectedCareLevel = selectedCareLevel ?: "Todos",
            onCareLevelSelected = {
                viewModel.onCareLevelFilterChanged(it)
            }
        )

        // Muestra la cantidad de razas disponibles.
        if (!isLoading && error == null && breeds.isNotEmpty()) {
            SubtleResultsCounter(
                count = breeds.size,
                isSearching = isSearching
            )
        }

        // Espacio restante para la lista y sus estados.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PawlyBrown
                    )
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ocurrió un error al cargar las razas:\n$error",
                            color = PawlyDarkBrown,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.fetchBreeds() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PawlyBrown
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Reintentar",
                                color = PawlyWhite
                            )
                        }
                    }
                }

                // Estado vacío cuando no hay coincidencias.
                breeds.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = PawlyTextSoft
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSearching) {
                                "No se encontraron razas que coincidan con tu búsqueda."
                            } else {
                                "No hay razas disponibles en este momento."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = PawlyDarkBrown,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // LazyColumn renderiza los elementos necesarios
                // conforme el usuario se desplaza por la lista.
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 4.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = breeds,
                            key = { breed -> breed.id }
                        ) { breed ->
                            CompactBreedCard(breed = breed)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Encabezado exclusivo de la pantalla de Razas.
 *
 * Utiliza una altura de 48 dp para ocupar menos espacio.
 * statusBarsPadding evita que el contenido quede debajo
 * de la barra de estado del dispositivo.
 */
@Composable
private fun CompactBreedsHeader(
    onBack: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PawlyBrown)
            .statusBarsPadding()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = PawlyWhite
                )
            }
        }

        Text(
            text = "Razas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PawlyWhite
        )
    }
}

/**
 * Buscador de razas.
 *
 * El texto se envía al ViewModel mediante onQueryChange.
 * El botón de cierre permite limpiar la búsqueda.
 */
@Composable
private fun CompactSearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Buscar razas...",
                color = PawlyTextSoft,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = PawlyBrown
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = PawlyBrown
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PawlyWhite,
            unfocusedContainerColor = PawlyWhite,
            disabledContainerColor = PawlyWhite,
            focusedBorderColor = PawlyBrown,
            unfocusedBorderColor = PawlySoftBeige,
            focusedLeadingIconColor = PawlyBrown,
            unfocusedLeadingIconColor = PawlyTextSoft
        )
    )
}

/**
 * Organiza los dos filtros en una sola fila horizontal.
 *
 * Cada filtro utiliza la mitad del ancho disponible.
 */
@Composable
private fun CompactFilterControlsRow(
    selectedSize: String,
    onSizeSelected: (String?) -> Unit,
    selectedCareLevel: String,
    onCareLevelSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompactFilterDropdownChip(
            label = "Tamaño",
            selectedValue = selectedSize,
            options = listOf(
                "Todos",
                "Pequeño",
                "Mediano",
                "Grande"
            ),
            onOptionSelected = onSizeSelected,
            modifier = Modifier.weight(1f)
        )

        CompactFilterDropdownChip(
            label = "Cuidado",
            selectedValue = selectedCareLevel,
            options = listOf(
                "Todos",
                "Bajo",
                "Medio",
                "Alto"
            ),
            onOptionSelected = onCareLevelSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Filtro desplegable reutilizable dentro del tab Razas.
 *
 * expanded controla la visibilidad del menú.
 * Cuando el usuario selecciona una opción, se comunica
 * el nuevo valor al ViewModel mediante el callback.
 */
@Composable
private fun CompactFilterDropdownChip(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Los filtros activos utilizan el color marrón.
    val isActive = selectedValue != "Todos"

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(20.dp),
            color = if (isActive) {
                PawlyBrown
            } else {
                PawlySoftBeige
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (isActive) {
                    PawlyBrown
                } else {
                    PawlySoftBeige
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isActive) {
                        "$label: $selectedValue"
                    } else {
                        "$label: Todos"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isActive) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                    color = if (isActive) {
                        PawlyWhite
                    } else {
                        PawlyDarkBrown
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isActive) {
                        PawlyWhite
                    } else {
                        PawlyDarkBrown
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Muestra las opciones disponibles para este filtro.
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(PawlyWhite)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option == selectedValue) {
                                PawlyBrown
                            } else {
                                PawlyDarkBrown
                            },
                            fontWeight = if (option == selectedValue) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

/**
 * Contador de resultados de búsqueda.
 *
 * Cambia el mensaje dependiendo de si el usuario
 * está buscando o visualizando todas las razas.
 */
@Composable
private fun SubtleResultsCounter(
    count: Int,
    isSearching: Boolean
) {
    Text(
        text = if (isSearching) {
            if (count == 1) {
                "1 raza encontrada"
            } else {
                "$count razas encontradas"
            }
        } else {
            "$count razas disponibles"
        },
        style = MaterialTheme.typography.labelMedium,
        color = PawlyTextSoft,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 4.dp
        )
    )
}

/**
 * Tarjeta individual de una raza.
 *
 * Muestra la fotografía a la izquierda y la información
 * principal a la derecha.
 *
 * Se utilizan dimensiones compactas para visualizar
 * más elementos en pantalla.
 */
@Composable
private fun CompactBreedCard(
    breed: RazaPerro
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = PawlyWhite
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coil carga la imagen desde la URL de la raza.
            AsyncImage(
                model = breed.imagenUrl,
                contentDescription = breed.nombre,
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PawlySoftBeige),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información principal de la raza.
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = breed.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PawlyDarkBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Origen: ${breed.origen}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PawlyTextSoft,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = breed.temperamento,
                    style = MaterialTheme.typography.bodySmall,
                    color = PawlyDarkBrown,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )

                // Etiquetas de tamaño y nivel de cuidado.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    CompactAttributeBadge(
                        label = breed.tamano,
                        containerColor = PawlySoftBeige,
                        textColor = PawlyDarkBrown
                    )

                    CompactAttributeBadge(
                        label = "Cuidado ${breed.nivelCuidado}",
                        containerColor = PawlyBrown,
                        textColor = PawlyWhite
                    )
                }
            }
        }
    }
}

/**
 * Etiqueta reutilizable que muestra atributos de la raza.
 *
 * Recibe el texto y los colores para adaptarse
 * tanto al tamaño como al nivel de cuidado.
 */
@Composable
private fun CompactAttributeBadge(
    label: String,
    containerColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 3.dp
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
