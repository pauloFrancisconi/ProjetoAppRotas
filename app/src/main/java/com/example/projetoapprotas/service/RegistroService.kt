package com.example.projetoapprotas.service

import com.example.projetoapprotas.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface RegistroService {
    @POST("registros")
    suspend fun enviarRegistroPonto(
        @Header("Authorization") token: String,
        @Body registro: RegistroPontoRequest
    ): Response<ApiResponse<RegistroPonto>>
    
    @POST("registros/bulk")
    suspend fun enviarRegistrosLote(
        @Header("Authorization") token: String,
        @Body registros: List<RegistroPontoRequest>
    ): Response<ApiResponse<List<RegistroPonto>>>
    
    @POST("fotos/upload")
    suspend fun uploadFoto(
        @Header("Authorization") token: String,
        @Body foto: FotoUpload
    ): Response<ApiResponse<FotoResponse>>
    
    @POST("fotos/upload/multiple")
    suspend fun uploadFotos(
        @Header("Authorization") token: String,
        @Body fotos: List<FotoUpload>
    ): Response<ApiResponse<List<FotoResponse>>>
    
    @GET("registros/motorista/{motoristaId}")
    suspend fun obterRegistrosMotorista(
        @Header("Authorization") token: String,
        @Path("motoristaId") motoristaId: String,
        @Query("dataInicio") dataInicio: String? = null,
        @Query("dataFim") dataFim: String? = null
    ): Response<ApiResponse<List<RegistroPonto>>>
    
    @PUT("registros/{id}/sincronizar")
    suspend fun marcarComoSincronizado(
        @Header("Authorization") token: String,
        @Path("id") registroId: String
    ): Response<ApiResponse<Unit>>
} 