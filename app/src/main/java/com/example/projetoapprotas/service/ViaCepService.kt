package com.example.projetoapprotas.data.remote.service

import com.example.projetoapprotas.data.remote.dto.AddressDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApi {
    @GET("{cep}/json/")
    suspend fun getAddress(@Path("cep") cep: String): AddressDto
}
