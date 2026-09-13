package com.example.pawlyapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawlyapp.ui.home.model.PetsHomeUiState
import com.example.pawlyapp.ui.home.network.PetsRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class PetsHomeViewModel : ViewModel() {

    private val petsService =
        PetsRetrofitClient.petsService

    private val _uiState =
        MutableStateFlow(PetsHomeUiState())

    val uiState: StateFlow<PetsHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadPetsHome()
    }

    fun loadPetsHome() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }

            try {

                val response =
                    petsService.getPetsHome()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        walks = response.walks,
                        tips = response.tips
                    )
                }

            } catch (e: HttpException) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo cargar la información (${e.code()})"
                    )
                }

            } catch (_: SocketTimeoutException) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "El servidor está iniciando. Intenta nuevamente."
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Revisa tu conexión e intenta de nuevo."
                    )
                }
            }
        }
    }
}