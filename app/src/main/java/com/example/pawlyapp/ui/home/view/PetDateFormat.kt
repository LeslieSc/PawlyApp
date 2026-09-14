package com.example.pawlyapp.ui.home.view

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Convierte una fecha ISO-8601 UTC (ej. `2026-09-03T07:15:00Z`) a un texto corto
 * en español (ej. `3 sep 2026 · 07:15`). Si el string no se puede parsear, se
 * devuelve tal cual para no romper la UI.
 *
 * Se comparte entre [WalkCard] y [TipCard], por eso vive suelto en el paquete
 * `view` en vez de dentro de un composable.
 */
fun formatPetDate(
    iso: String,
    withTime: Boolean = false
): String = try {

    val dt = OffsetDateTime.parse(iso)

    val month = dt.month.getDisplayName(
        TextStyle.SHORT,
        Locale("es")
    )

    val base =
        "${dt.dayOfMonth} $month ${dt.year}"

    if (withTime) {
        "$base · ${
            dt.format(
                DateTimeFormatter.ofPattern("HH:mm")
            )
        }"
    } else {
        base
    }

} catch (_: Exception) {
    iso
}