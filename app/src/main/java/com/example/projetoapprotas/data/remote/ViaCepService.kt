package com.example.projetoapprotas.data.remote

import com.example.projetoapprotas.data.remote.dto.EnderecoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): EnderecoResponse
}
