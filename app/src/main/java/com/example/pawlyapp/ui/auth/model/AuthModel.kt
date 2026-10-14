package com.example.pawlyapp.ui.auth.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LogoutRequest(
    val refresh: String
)

data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String
)

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)