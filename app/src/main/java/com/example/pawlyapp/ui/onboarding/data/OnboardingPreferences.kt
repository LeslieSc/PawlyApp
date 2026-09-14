package com.example.pawlyapp.ui.onboarding.data



import android.content.Context

class OnboardingPreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "onboarding_preferences",
            Context.MODE_PRIVATE
        )

    fun hasCompletedOnboarding(): Boolean {
        return preferences.getBoolean(
            "completed_onboarding",
            false
        )
    }

    fun setOnboardingCompleted() {
        preferences.edit()
            .putBoolean(
                "completed_onboarding",
                true
            )
            .apply()
    }
}