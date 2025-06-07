package com.example.projetoapprotas.data.models

data class RegistroPonto(
    val id: String,
    val motoristaId: String,
    val pontoColetaId: String,
    val coordenadas: Coordenadas,
    val timestamp: String,
    val fotos: List<String> = emptyList(), // URLs das fotos
    val observacoes: String? = null,
    val status: StatusRegistro,
    val sincronizado: Boolean = false
)

data class Coordenadas(
    val latitude: Double,
    val longitude: Double,
    val precisao: Float? = null
)

enum class StatusRegistro {
    COLETADO,
    NAO_COLETADO,
    PARCIALMENTE_COLETADO,
    PROBLEMA_ACESSO
}

data class RegistroPontoRequest(
    val pontoColetaId: String,
    val coordenadas: Coordenadas,
    val fotos: List<String> = emptyList(),
    val observacoes: String? = null,
    val status: StatusRegistro
)

data class FotoUpload(
    val base64: String,
    val nomeArquivo: String,
    val tipo: String = "image/jpeg"
) 