package com.example.pawlyapp.ui.mainmenu.firstapirequest.model

data class RazaPerro(
    val id: Int,
    val nombre: String,
    val origen: String,
    val tamano: String,
    val temperamento: String,
    val nivelCuidado: String,
    val descripcion: String,
    val recomendaciones: String,
    val imagenUrl: String
)

data class RazaPerroResponse(
    val breeds: List<RazaPerro>
)