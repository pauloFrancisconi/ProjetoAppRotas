package com.example.projetoapprotas.data.models

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val role: UserRole,
    val ativo: Boolean = true,
    val criadoEm: String? = null
)

enum class UserRole(val displayName: String) {
    MOTORISTA("Motorista"),
    GERENTE("Gerente"),
    ADMIN("Administrador")
}

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val usuario: Usuario? = null,
    val token: String? = null
)

data class AuthToken(
    val token: String,
    val expiresAt: Long,
    val refreshToken: String? = null
) 