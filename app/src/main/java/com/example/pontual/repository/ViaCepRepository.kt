package com.example.pontual.repository

import com.example.pontual.api.ViaCepClient
import com.example.pontual.api.models.ViaCepAddress
import retrofit2.Response

class ViaCepRepository {
    private val service = ViaCepClient.service
    
    suspend fun getAddress(cep: String): Response<ViaCepAddress> {
        val cleanCep = cep.replace("-", "").replace(" ", "")
        return service.getAddress(cleanCep)
    }
    
    fun isValidCep(cep: String): Boolean {
        val cleanCep = cep.replace("-", "").replace(" ", "")
        return cleanCep.length == 8 && cleanCep.all { it.isDigit() }
    }
} 