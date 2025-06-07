package com.example.projetoapprotas.service

import com.example.projetoapprotas.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface RelatorioService {
    @POST("relatorios/filtrar")
    suspend fun obterRelatorioFiltrado(
        @Header("Authorization") token: String,
        @Body filtro: FiltroRelatorio,
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 50
    ): Response<ApiResponse<RelatorioResponse>>
    
    @GET("relatorios/motorista/{motoristaId}")
    suspend fun obterRelatorioMotorista(
        @Header("Authorization") token: String,
        @Path("motoristaId") motoristaId: String,
        @Query("dataInicio") dataInicio: String,
        @Query("dataFim") dataFim: String
    ): Response<ApiResponse<RelatorioDetalhado>>
    
    @GET("relatorios/estatisticas")
    suspend fun obterEstatisticasGerais(
        @Header("Authorization") token: String,
        @Query("dataInicio") dataInicio: String? = null,
        @Query("dataFim") dataFim: String? = null,
        @Query("motoristaId") motoristaId: String? = null
    ): Response<ApiResponse<EstatisticasRelatorio>>
    
    @GET("relatorios/export")
    suspend fun exportarRelatorio(
        @Header("Authorization") token: String,
        @Query("formato") formato: String = "PDF", // PDF, EXCEL, CSV
        @Query("motoristaId") motoristaId: String? = null,
        @Query("dataInicio") dataInicio: String? = null,
        @Query("dataFim") dataFim: String? = null
    ): Response<ApiResponse<ExportResponse>>
    
    @GET("relatorios/dashboard")
    suspend fun obterDadosDashboard(
        @Header("Authorization") token: String,
        @Query("periodo") periodo: String = "HOJE" // HOJE, SEMANA, MES, ANO
    ): Response<ApiResponse<DashboardData>>
} 