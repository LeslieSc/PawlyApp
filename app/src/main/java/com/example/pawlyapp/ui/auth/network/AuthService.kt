package com.example.pawlyapp.ui.auth.network

import com.example.pawlyapp.ui.auth.model.LoginRequest
import com.example.pawlyapp.ui.auth.model.LoginResponse
import com.example.pawlyapp.ui.auth.model.LogoutRequest
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface AuthService {

    @POST("api/auth/login/")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @POST("api/auth/logout/")
    suspend fun logout(
        @Header("Authorization") bearer: String,
        @Body body: LogoutRequest
    ): Response<Unit>
}

object AuthRetrofitClient {

    private const val BASE_URL =
        "https://androidbasics-auth-api.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val authService: AuthService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
}