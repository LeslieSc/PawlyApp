package com.example.pawlyapp.ui.mainmenu.firstapirequest.network

import com.example.pawlyapp.ui.mainmenu.firstapirequest.model.RazaPerroResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GistService{
    @GET("Beca1804/0d58b73b492e9eb971c8a160989b59ff/raw/razas_pawly.json")
    suspend fun getBreeds(): RazaPerroResponse
}
object RetrofitClient{

val gistService: GistService = Retrofit.Builder()
    .baseUrl("https://gist.githubusercontent.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GistService::class.java)
}