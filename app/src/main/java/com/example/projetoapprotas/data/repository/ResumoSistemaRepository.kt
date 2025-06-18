package com.example.projetoapprotas.data.repository

import com.example.projetoapprotas.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResumoSistemaRepository(private val api: ApiService) {

    suspend fun carregarResumo() = withContext(Dispatchers.IO) {
        api.getResumoSistema()
    }
}
