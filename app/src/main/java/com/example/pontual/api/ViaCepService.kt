package com.example.pontual.api

import com.example.pontual.api.models.ViaCepAddress
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("ws/{cep}/json/")
    suspend fun getAddress(@Path("cep") cep: String): Response<ViaCepAddress>
} 