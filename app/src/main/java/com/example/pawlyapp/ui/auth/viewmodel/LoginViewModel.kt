package com.example.pawlyapp.ui.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawlyapp.ui.auth.data.SessionPreferences
import com.example.pawlyapp.ui.auth.model.LoginRequest
import com.example.pawlyapp.ui.auth.model.LoginResponse
import com.example.pawlyapp.ui.auth.model.LoginUiState
import com.example.pawlyapp.ui.auth.network.AuthRetrofitClient
import androidx.biometric.BiometricManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val session = SessionPreferences(application)
    private val authService = AuthRetrofitClient.authService

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(
        username: String,
        password: String
    ) {

        if (username.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    error = "Ingresa usuario y contraseña"
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {

                val response = loginWithRetry(
                    LoginRequest(
                        username = username.trim(),
                        password = password
                    )
                )

                session.saveSession(
                    response.access,
                    response.refresh,
                    response.user,
                    password = password
                )

                val canUseBiometrics = BiometricManager.from(getApplication()).canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
                ) == BiometricManager.BIOMETRIC_SUCCESS

                if (canUseBiometrics && !session.isBiometricEnabled() && !session.hasAskedBiometric()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showBiometricPromptDialog = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true
                        )
                    }
                }

            } catch (e: HttpException) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = messageForHttp(e.code())
                    )
                }

            } catch (e: SocketTimeoutException) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "El servidor está iniciando. Vuelve a intentar."
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Sin conexión. Revisa tu internet e intenta de nuevo."
                    )
                }
            }
        }
    }

    fun enableBiometricAndProceed() {
        session.setBiometricEnabled(true)
        _uiState.update {
            it.copy(
                showBiometricPromptDialog = false,
                isLoggedIn = true
            )
        }
    }

    fun disableBiometricAndProceed() {
        session.setBiometricEnabled(false)
        _uiState.update {
            it.copy(
                showBiometricPromptDialog = false,
                isLoggedIn = true
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    fun loginWithBiometric() {
        val username = session.username()
        val password = session.savedPassword()

        if (session.isBiometricEnabled() && !username.isNullOrBlank() && !password.isNullOrBlank()) {
            login(username, password)
        } else {
            _uiState.update {
                it.copy(
                    error = "Debes activar el acceso biométrico e iniciar sesión con contraseña al menos una vez"
                )
            }
        }
    }

    fun setCustomError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    private suspend fun loginWithRetry(
        request: LoginRequest
    ): LoginResponse {

        return try {
            authService.login(request)
        } catch (e: SocketTimeoutException) {
            authService.login(request)
        }
    }

    private fun messageForHttp(
        code: Int
    ): String {

        return when (code) {
            401 -> "Usuario o contraseña incorrectos"
            400 -> "Revisa los datos ingresados"
            else -> "No se pudo iniciar sesión (error $code)"
        }
    }
}