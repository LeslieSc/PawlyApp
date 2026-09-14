package com.example.pawlyapp.ui.onboarding.model

import androidx.annotation.DrawableRes

data class OnboardingModel(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)