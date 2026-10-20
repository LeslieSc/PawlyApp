package com.example.pawlyapp.ui.home.viewmodel

import android.util.Log
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

/**
 * ViewModel encargado de gestionar el estado y la lógica de negocio de la pantalla Home.
 * Se comunica con el servicio de red para obtener y actualizar los paseos y consejos de las mascotas.
 */
class PetsHomeViewModel : ViewModel() {

    private val TAG = "PetsHomeViewModel"

    private val petsService =
        PetsRetrofitClient.petsService

    private val _uiState =
        MutableStateFlow(PetsHomeUiState())

    val uiState: StateFlow<PetsHomeUiState> =
        _uiState.asStateFlow()

    init {
        loadPetsHome()
    }

    /**
     * Realiza la carga inicial de los datos del Home.
     * Utiliza [isLoading] para mostrar el indicador de carga principal (PawLoaderOverlay)
     * y maneja los errores iniciales mostrando una vista de error con opción de reintentar.
     */
    fun loadPetsHome() {

        viewModelScope.launch {

            // Activamos el estado de carga inicial y limpiamos errores previos
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }

            try {
                // Solicitamos los datos al servidor mediante Retrofit
                val response =
                    petsService.getPetsHome()

                // Actualizamos el estado con los nuevos datos y desactivamos la carga inicial
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        walks = response.walks,
                        tips = response.tips
                    )
                }

            } catch (e: HttpException) {
                Log.e(TAG, "Error HTTP en carga inicial: ${e.code()}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo cargar la información (${e.code()})"
                    )
                }

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout en carga inicial", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "El servidor está iniciando. Intenta nuevamente."
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Excepción en carga inicial", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Revisa tu conexión e intenta de nuevo."
                    )
                }
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

                // Si la petición es exitosa, actualizamos la información y ocultamos el indicador de refresco
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        walks = response.walks,
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
