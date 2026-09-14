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

data class PetsHomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val walks: List<Walk> = emptyList(),
    val tips: List<Tip> = emptyList()
)