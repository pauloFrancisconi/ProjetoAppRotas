package com.example.projetoapprotas.data.models

// Classes compartilhadas entre serviços

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

data class FotoResponse(
    val id: String,
    val url: String,
    val nomeArquivo: String,
    val tamanho: Long,
    val uploadedAt: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class VerifyTokenResponse(
    val valid: Boolean,
    val usuario: Usuario? = null,
    val message: String? = null
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)

data class ExportResponse(
    val downloadUrl: String,
    val nomeArquivo: String,
    val formato: String,
    val tamanho: Long
)

data class DashboardData(
    val registrosHoje: Int,
    val registrosSemana: Int,
    val registrosMes: Int,
    val motoristasAtivos: Int,
    val pontosColetados: Int,
    val percentualConclusao: Double,
    val ultimaAtualizacao: String
) 