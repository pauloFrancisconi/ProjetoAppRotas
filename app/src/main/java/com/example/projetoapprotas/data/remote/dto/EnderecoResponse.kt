package com.example.projetoapprotas.data.remote.dto

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
) {
    val erro: Any = TODO()
}
