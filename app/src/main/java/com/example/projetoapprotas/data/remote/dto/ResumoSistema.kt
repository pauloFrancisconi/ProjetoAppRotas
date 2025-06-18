package com.example.projetoapprotas.data.remote.dto

import com.squareup.moshi.Json

data class ResumoSistemaDto(
    @Json(name = "pontos")     val pontos: Int,
    @Json(name = "rotas")      val rotas: Int,
    @Json(name = "motoristas") val motoristas: Int
)
