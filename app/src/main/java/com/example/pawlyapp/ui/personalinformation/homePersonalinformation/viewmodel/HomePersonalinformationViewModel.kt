package com.example.pawlyapp.ui.personalinformation.homePersonalinformation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pawlyapp.ui.personalinformation.homePersonalinformation.model.HomePersonalinformationModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pawlyapp.ui.auth.data.SessionPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
class HomePersonalinformationViewModel(application: Application): AndroidViewModel(application) {
    val homeModel = HomePersonalinformationModel()
    private val session = SessionPreferences(application)


    private val _isBiometricEnabled = MutableStateFlow(session.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    fun toggleBiometric(enabled: Boolean) {
        session.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
    }

}