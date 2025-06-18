package com.example.projetoapprotas.data.remote

import com.example.projetoapprotas.data.remote.dto.ResumoSistemaDto
import retrofit2.http.GET

interface ApiService {
    @GET("api/relatorios/resumoSistema")
    suspend fun getResumoSistema(): ResumoSistemaDto
}
