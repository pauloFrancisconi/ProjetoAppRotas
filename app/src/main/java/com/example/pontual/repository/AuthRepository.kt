package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.LoginRequest
import com.example.pontual.api.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val apiService = ApiClient.apiService

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)
                
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.success) {
                            Result.success(loginResponse)
                        } else {
                            Result.failure(Exception(loginResponse.message))
                        }
                    } ?: Result.failure(Exception("Resposta vazia do servidor"))
                } else {
                    Result.failure(Exception("Erro na requisição: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 