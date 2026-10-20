
package com.example.pawlyapp.ui.home.viewmodel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.pawlyapp.ui.home.model.PetsHomeUiState
import com.example.pawlyapp.ui.home.model.Walk
import com.example.pawlyapp.ui.home.network.PetsRetrofitClient
import com.example.pawlyapp.ui.tracker.storage.WalkStorage

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import retrofit2.HttpException
import java.net.SocketTimeoutException

// ========================================
// VIEWMODEL DE HOME
// ========================================

class PetsHomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ====================================
    // API DE PAWLYAPP
    // ====================================

    private val petsService =
        PetsRetrofitClient.petsService

    // Contexto para leer paseos locales
    private val appContext =
        application.applicationContext

    // ====================================
    // ESTADO DE HOME
    // ====================================

    private val _uiState =
        MutableStateFlow(PetsHomeUiState())

    val uiState: StateFlow<PetsHomeUiState> =
        _uiState.asStateFlow()

    // Paseos obtenidos desde la API
    private var apiWalks: List<Walk> = emptyList()

    // ====================================
    // INICIALIZAR HOME
    // ====================================

    init {

        // Primero mostrar los paseos
        // que ya estan guardados en el telefono
        refreshSavedWalks()

        // Escuchar cuando F04 guarde
        // un nuevo paseo
        viewModelScope.launch {

            WalkStorage.updates.collect {

                refreshSavedWalks()
            }
        }

        // Cargar datos desde la API
        loadPetsHome()
    }

    // ====================================
    // COMBINAR PASEOS
    // ====================================

    private fun getAllWalks(): List<Walk> {

        // Paseos guardados desde F04
        val savedWalks =
            WalkStorage.getSavedWalks(appContext)

        // Obtener sus identificadores
        val savedIds =
            savedWalks.map { it.id }.toSet()

        // Evitar duplicados con la API
        val remoteWalks = apiWalks.filter { walk ->
            walk.id !in savedIds
        }

        // Paseos locales primero
        return savedWalks + remoteWalks
    }

    // ====================================
    // ACTUALIZAR PASEOS LOCALES
    // ====================================

    private fun refreshSavedWalks() {

        val allWalks = getAllWalks()

        _uiState.update { current ->

            current.copy(
                walks = allWalks
            )
        }
    }

    // ====================================
    // MANEJO DE ERRORES DE LA API
    // ====================================

    private fun handleLoadError(
        message: String
    ) {

        // Conservar los paseos locales
        // incluso cuando falle la API
        val allWalks = getAllWalks()

        _uiState.update { current ->

            // Si tenemos contenido disponible,
            // no bloquear Home con un error
            val hasContent =
                allWalks.isNotEmpty() ||
                        current.tips.isNotEmpty()

            current.copy(
                isLoading = false,
                walks = allWalks,
                error = if (hasContent) {
                    null
                } else {
                    message
                }
            )
        }
    }

    // ====================================
    // CARGAR HOME DESDE LA API
    // ====================================

    fun loadPetsHome() {

        viewModelScope.launch {

            _uiState.update { current ->

                current.copy(
                    isLoading =
                        current.walks.isEmpty() &&
                                current.tips.isEmpty(),
                    error = null
                )
            }

            try {

                // Obtener paseos y consejos
                // desde el servidor
                val response =
                    petsService.getPetsHome()

                // Guardar paseos remotos
                apiWalks = response.walks

                // Combinar paseos remotos
                // con los guardados en F04
                val allWalks = getAllWalks()

                _uiState.update { current ->

                    current.copy(
                        isLoading = false,
                        error = null,
                        walks = allWalks,
                        tips = response.tips
                    )
                }

            } catch (e: CancellationException) {

                // Respetar la cancelacion
                // de corrutinas
                throw e

            } catch (e: HttpException) {

                handleLoadError(
                    "No se pudo cargar la información (${e.code()})"
                )

            } catch (e: SocketTimeoutException) {

                handleLoadError(
                    "El servidor está iniciando. Intenta nuevamente."
                )

            } catch (e: Exception) {

                handleLoadError(
                    "Revisa tu conexión e intenta de nuevo."
                )
            }
        }
    }
}
