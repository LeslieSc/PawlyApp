
package com.example.pawlyapp.ui.home.viewmodel

import android.app.Application
import android.util.Log

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

    private val TAG = "PetsHomeViewModel"

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

                Log.e(TAG, "Error HTTP en carga inicial: ${e.code()}", e)
                handleLoadError(
                    "No se pudo cargar la información (${e.code()})"
                )

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout en carga inicial", e)
                handleLoadError(
                    "El servidor está iniciando. Intenta nuevamente."
                )

            } catch (e: Exception) {
                Log.e(TAG, "Excepción en carga inicial", e)
                handleLoadError(
                    "Revisa tu conexión e intenta de nuevo."
                )
            }
        }
    }


    /**
     * Actualiza los datos del Home mediante la acción de Pull-to-Refresh.
     * Se diferencia de [loadPetsHome] en que utiliza [isRefreshing] en lugar de [isLoading],
     * permitiendo que los datos existentes (paseos y consejos) permanezcan visibles en pantalla
     * mientras se realiza la petición en segundo plano. Si ocurre un fallo, se preservan los datos
     * anteriores y se notifica el error mediante un Snackbar.
     */
    fun refreshPetsHome() {
        // Previene peticiones simultáneas si ya se está actualizando o cargando inicialmente
        if (_uiState.value.isRefreshing || _uiState.value.isLoading) return

        viewModelScope.launch {
            // Activamos isRefreshing sin alterar el contenido actual (walks y tips se conservan)
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    refreshError = null
                )
            }

            try {
                // Reutilizamos el mismo servicio de red (getPetsHome) para obtener los datos actualizados
                val response = petsService.getPetsHome()

                apiWalks = response.walks
                val allWalks = getAllWalks()
                // Si la petición es exitosa, actualizamos la información y ocultamos el indicador de refresco
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        walks = allWalks,
                        tips = response.tips
                    )
                }
            } catch (e: Exception) {
                // Registramos los detalles técnicos del error en Logcat para diagnóstico durante el desarrollo
                Log.e(TAG, "Error al actualizar los datos en Pull-to-Refresh", e)

                // Si ocurre un error, ocultamos el indicador de refresco y mostramos el mensaje en el Snackbar,
                // preservando intactos los datos previamente cargados ([walks] y [tips] no se modifican).
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        refreshError = "No se pudo actualizar la información. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error de actualización para evitar que el Snackbar se dispare repetidamente.
     */
    fun clearRefreshError() {
        _uiState.update {
            it.copy(refreshError = null)
        }
    }

}
