package com.example.pawlyapp.ui.home.model

data class Walk(
    val id: Int,
    val petName: String,
    val date: String,
    val steps: Int,
    val durationMin: Int,
    val durationText: String,
    val distanceKm: Double,
    val image: String
)

data class Tip(
    val id: Int,
    val title: String,
    val category: String,
    val content: String,
    val image: String,
    val publishedAt: String
)

data class PetsHomeResponse(
    val walks: List<Walk>,
    val tips: List<Tip>
)

/**
 * Estado de la UI para la pantalla principal (Home).
 * Contiene los datos de paseos y consejos, así como los indicadores de carga inicial (isLoading),
 * actualización manual (isRefreshing) y mensajes de error (error y refreshError).
 */
data class PetsHomeUiState(
    // Indica si se está realizando la carga inicial de la pantalla (muestra el indicador de carga principal PawLoaderOverlay).
    val isLoading: Boolean = false,
    // Indica si se está ejecutando la acción de actualización manual mediante Pull-to-Refresh.
    val isRefreshing: Boolean = false,
    // Mensaje de error para la carga inicial (cuando falla por completo y no hay datos disponibles).
    val error: String? = null,
    // Mensaje de error para el Pull-to-Refresh (se muestra mediante Snackbar sin borrar los datos previos).
    val refreshError: String? = null,
    // Lista de paseos obtenidos del servidor.
    val walks: List<Walk> = emptyList(),
    // Lista de consejos obtenidos del servidor.
    val tips: List<Tip> = emptyList()
)
