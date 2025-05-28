package com.example.projetoapprotas.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call

data class EnderecoResponse(
    val cep: String?,
    val logradouro: String?,
    val complemento: String?,
    val bairro: String?,
    val localidade: String?,
    val uf: String?,
    val ibge: String?,
    val gia: String?,
    val ddd: String?,
    val siafi: String?
)

interface ViaCepService {
    @GET("{cep}/json/")
    fun buscarEndereco(@Path("cep") cep: String): Call<EnderecoResponse>
}
