package com.example.pawlyapp.common.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "pawly_preferences",
            Context.MODE_PRIVATE
        )

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