package com.example.projetoapprotas.data.repository

import com.example.projetoapprotas.data.remote.ViaCepService
import com.example.projetoapprotas.data.remote.dto.EnderecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PontoRepository(private val cepService: ViaCepService) {

    suspend fun buscarEndereco(cep: String): Result<EnderecoResponse> = runCatching {
        withContext(Dispatchers.IO) { cepService.buscarCep(cep) }
    }

    suspend fun salvarPonto(): Result<Unit> = runCatching {
    }
}
