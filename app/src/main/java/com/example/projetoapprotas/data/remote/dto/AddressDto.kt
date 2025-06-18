package com.example.projetoapprotas.data.remote.dto

import com.squareup.moshi.Json

data class AddressDto(
    @Json(name = "logradouro") val street: String?,
    @Json(name = "bairro")     val district: String?,
    @Json(name = "localidade") val city: String?,
    @Json(name = "uf")         val state: String?,
    val erro: Boolean? = null
)
