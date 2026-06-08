package com.example.pawlyapp.ui.mainmenu.firstapirequest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawlyapp.ui.mainmenu.firstapirequest.model.RazaPerro
import com.example.pawlyapp.ui.mainmenu.firstapirequest.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirstApiRequestViewModel : ViewModel() {

    private val _breeds = MutableStateFlow<List<RazaPerro>>(emptyList())

    val breeds: StateFlow<List<RazaPerro>> = _breeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchBreeds()
    }

    private fun fetchBreeds() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _breeds.value = RetrofitClient.gistService.getBreeds()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}