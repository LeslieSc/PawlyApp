package com.example.pawlyapp.ui.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pawlyapp.ui.auth.data.SessionPreferences
import com.example.pawlyapp.ui.auth.model.LogoutRequest
import com.example.pawlyapp.ui.auth.network.AuthRetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private val logoutScope =
    CoroutineScope(SupervisorJob() + Dispatchers.IO)

class LogoutViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val session = SessionPreferences(application)
    private val authService = AuthRetrofitClient.authService

    fun logout(
        onLoggedOut: () -> Unit
    ) {

        val access = session.accessToken()
        val refresh = session.refreshToken()

        session.clearSession()

        onLoggedOut()

        if (
            !access.isNullOrBlank() &&
            !refresh.isNullOrBlank()
        ) {
            logoutScope.launch {
                try {
                    authService.logout(
                        "Bearer $access",
                        LogoutRequest(refresh)
                    )
                } catch (_: Exception) {
                    // La sesión local ya se cerró
                }
            }
        }
    }
}