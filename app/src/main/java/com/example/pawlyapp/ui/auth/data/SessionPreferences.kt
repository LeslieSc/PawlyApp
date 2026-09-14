package com.example.pawlyapp.ui.auth.data

import android.content.Context
import com.example.pawlyapp.common.preferences.AppPreferences
import com.example.pawlyapp.ui.auth.model.UserDto

private const val KEY_ACCESS = "auth_access"
private const val KEY_REFRESH = "auth_refresh"
private const val KEY_USERNAME = "auth_username"

class SessionPreferences(context: Context) {

    private val appPreferences = AppPreferences(context)

    fun saveSession(
        access: String,
        refresh: String,
        user: UserDto
    ) {
        appPreferences.putString(KEY_ACCESS, access)
        appPreferences.putString(KEY_REFRESH, refresh)
        appPreferences.putString(KEY_USERNAME, user.username)
    }

    fun accessToken(): String? =
        appPreferences.getString(KEY_ACCESS)

    fun refreshToken(): String? =
        appPreferences.getString(KEY_REFRESH)

    fun username(): String? =
        appPreferences.getString(KEY_USERNAME)

    fun isLoggedIn(): Boolean =
        !refreshToken().isNullOrBlank()

    fun clearSession() {
        appPreferences.remove(
            KEY_ACCESS,
            KEY_REFRESH,
            KEY_USERNAME
        )
    }
}