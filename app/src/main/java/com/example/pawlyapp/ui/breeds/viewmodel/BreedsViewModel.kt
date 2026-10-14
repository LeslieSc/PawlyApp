package com.example.pawlyapp.ui.breeds.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawlyapp.ui.breeds.model.RazaPerro
import com.example.pawlyapp.ui.breeds.network.GistService
import com.example.pawlyapp.ui.breeds.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer

/**
 * ViewModel encargado de la gestión de datos y lógica de presentación para la lista de razas de perros.
 *
 * Utiliza [StateFlow] y el operador [combine] para filtrar en tiempo real la lista obtenida
 * desde GitHub Gist, soportando búsquedas insensibles a mayúsculas y acentos, además de
 * mantener la lista original intacta para restaurar resultados.
 *
 * @property gistService Servicio Retrofit para obtener las razas desde GitHub Gist.
 */
class BreedsViewModel(
    private val gistService: GistService = RetrofitClient.gistService
) : ViewModel() {

    // Lista completa original de razas obtenida desde la API.
    private val _allBreeds = MutableStateFlow<List<RazaPerro>>(emptyList())

    // StateFlow que contiene la consulta de búsqueda actual ingresada por el usuario.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow opcional para el filtro de tamaño ("Todos", "Pequeño", "Mediano", "Grande").
    private val _selectedSize = MutableStateFlow<String?>("Todos")
    val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()

    // StateFlow opcional para el filtro de nivel de cuidado ("Todos", "Bajo", "Medio", "Alto").
    private val _selectedCareLevel = MutableStateFlow<String?>("Todos")
    val selectedCareLevel: StateFlow<String?> = _selectedCareLevel.asStateFlow()

    // Estado de carga de la petición HTTP.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error en caso de fallo en la petición HTTP.
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Lista filtrada de razas expuesta hacia la UI como [StateFlow].
     */
    val breeds: StateFlow<List<RazaPerro>> = combine(
        _allBreeds,
        _searchQuery,
        _selectedSize,
        _selectedCareLevel
    ) { allBreedsList, query, sizeFilter, careFilter ->
        var filteredList = allBreedsList

        // Búsqueda por texto (insensible a mayúsculas y acentos)
        if (query.isNotBlank()) {
            val normalizedQuery = normalizeText(query)
            filteredList = filteredList.filter { breed ->
                normalizeText(breed.nombre).contains(normalizedQuery) ||
                normalizeText(breed.origen).contains(normalizedQuery) ||
                normalizeText(breed.temperamento).contains(normalizedQuery) ||
                normalizeText(breed.descripcion).contains(normalizedQuery)
            }
        }

        // Filtro opcional por tamaño
        if (!sizeFilter.isNullOrBlank() && sizeFilter != "Todos") {
            filteredList = filteredList.filter { breed ->
                breed.tamano.equals(sizeFilter, ignoreCase = true)
            }
        }

        // Filtro opcional por nivel de cuidado
        if (!careFilter.isNullOrBlank() && careFilter != "Todos") {
            filteredList = filteredList.filter { breed ->
                breed.nivelCuidado.equals(careFilter, ignoreCase = true)
            }
        }

        filteredList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        fetchBreeds()
    }

    /**
     * Recupera la lista de razas de perros desde el servicio GitHub Gist utilizando Retrofit.
     */
    fun fetchBreeds() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _allBreeds.value = gistService.getBreeds()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al obtener las razas"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la consulta de búsqueda.
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * Actualiza el filtro de tamaño de raza opcional.
     */
    fun onSizeFilterChanged(size: String?) {
        _selectedSize.value = size
    }

    /**
     * Actualiza el filtro de nivel de cuidado opcional.
     */
    fun onCareLevelFilterChanged(level: String?) {
        _selectedCareLevel.value = level
    }

    /**
     * Limpia la consulta de búsqueda y restaura los filtros a sus valores predeterminados.
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _selectedSize.value = "Todos"
        _selectedCareLevel.value = "Todos"
    }

    /**
     * Normaliza un texto removiendo acentos y convirtiéndolo a minúsculas.
     */
    fun normalizeText(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
    }
}
