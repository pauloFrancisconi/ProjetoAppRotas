package com.example.pontual.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ViaCepClient {
    private const val BASE_URL = "https://viacep.com.br/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: ViaCepService = retrofit.create(ViaCepService::class.java)
} 