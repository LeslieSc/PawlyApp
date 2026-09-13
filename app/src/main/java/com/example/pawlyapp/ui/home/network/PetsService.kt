package com.example.pawlyapp.ui.home.network

import com.example.pawlyapp.ui.home.model.PetsHomeResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface PetsService {

    @GET("api/home/pets/")
    suspend fun getPetsHome(): PetsHomeResponse
}

object PetsRetrofitClient {

    private const val BASE_URL =
        "https://androidbasics-auth-api.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val petsService: PetsService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PetsService::class.java)
}

