package com.example.pawlyapp.common.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "pawly_encrypted_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: Exception) {
        context.getSharedPreferences("pawly_preferences", Context.MODE_PRIVATE)
    }

    fun putString(
        key: String,
        value: String
    ) {
        prefs.edit()
            .putString(key, value)
            .apply()
    }

    fun getString(
        key: String
    ): String? {
        return prefs.getString(key, null)
    }

    fun remove(
        vararg keys: String
    ) {
        prefs.edit().apply {
            keys.forEach {
                remove(it)
            }
        }.apply()
    }
}