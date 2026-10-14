package com.example.pawlyapp.ui.auth.data

import android.content.Context
import com.example.pawlyapp.common.preferences.AppPreferences
import com.example.pawlyapp.ui.auth.model.UserDto

private const val KEY_ACCESS = "auth_access"
private const val KEY_REFRESH = "auth_refresh"
private const val KEY_USERNAME = "auth_username"
private const val KEY_PASSWORD = "auth_password"
private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
private const val KEY_BIOMETRIC_ASKED = "biometric_asked"

class SessionPreferences(context: Context) {

    private val appPreferences = AppPreferences(context)

    fun saveSession(
        access: String,
        refresh: String,
        user: UserDto,
        password: String? = null
    ) {
        appPreferences.putString(KEY_ACCESS, access)
        appPreferences.putString(KEY_REFRESH, refresh)
        appPreferences.putString(KEY_USERNAME, user.username)
        if (!password.isNullOrBlank()) {
            appPreferences.putString(KEY_PASSWORD, password)
        }
    }

    fun accessToken(): String? =
        appPreferences.getString(KEY_ACCESS)

    fun refreshToken(): String? =
        appPreferences.getString(KEY_REFRESH)

    fun username(): String? =
        appPreferences.getString(KEY_USERNAME)

    fun savedPassword(): String? =
        appPreferences.getString(KEY_PASSWORD)

    fun isLoggedIn(): Boolean =
        !refreshToken().isNullOrBlank()

    fun isBiometricEnabled(): Boolean =
        appPreferences.getString(KEY_BIOMETRIC_ENABLED) == "true"

    fun hasAskedBiometric(): Boolean =
        appPreferences.getString(KEY_BIOMETRIC_ASKED) == "true" && isBiometricEnabled()

    fun setBiometricEnabled(enabled: Boolean) {
        appPreferences.putString(KEY_BIOMETRIC_ENABLED, if (enabled) "true" else "false")
        appPreferences.putString(KEY_BIOMETRIC_ASKED, "true")
    }

    fun clearSession() {
        appPreferences.remove(
            KEY_ACCESS,
            KEY_REFRESH
        )
    }
}