package com.example.projetoapprotas.repository

import android.content.Context
import com.example.projetoapprotas.data.models.*
import com.example.projetoapprotas.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RelatorioRepository(private val context: Context) {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _ultimoRelatorio = MutableStateFlow<RelatorioResponse?>(null)
    val ultimoRelatorio: StateFlow<RelatorioResponse?> = _ultimoRelatorio.asStateFlow()
    
    suspend fun obterRelatorioFiltrado(
        filtro: FiltroRelatorio,
        pagina: Int = 1,
        limite: Int = 50
    ): Result<RelatorioResponse> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            _isLoading.value = true
            val response = ApiClient.relatorioService.obterRelatorioFiltrado(
                "Bearer $token",
                filtro,
                pagina,
                limite
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val relatorio = response.body()!!.data!!
                _ultimoRelatorio.value = relatorio
                Result.success(relatorio)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao obter relatório"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun obterRelatorioMotorista(
        motoristaId: String,
        dataInicio: String,
        dataFim: String
    ): Result<RelatorioDetalhado> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            _isLoading.value = true
            val response = ApiClient.relatorioService.obterRelatorioMotorista(
                "Bearer $token",
                motoristaId,
                dataInicio,
                dataFim
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val relatorio = response.body()!!.data!!
                Result.success(relatorio)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao obter relatório do motorista"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun obterEstatisticasGerais(
        dataInicio: String? = null,
        dataFim: String? = null,
        motoristaId: String? = null
    ): Result<EstatisticasRelatorio> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            val response = ApiClient.relatorioService.obterEstatisticasGerais(
                "Bearer $token",
                dataInicio,
                dataFim,
                motoristaId
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val estatisticas = response.body()!!.data!!
                Result.success(estatisticas)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao obter estatísticas"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportarRelatorio(
        formato: String = "PDF",
        motoristaId: String? = null,
        dataInicio: String? = null,
        dataFim: String? = null
    ): Result<ExportResponse> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            val response = ApiClient.relatorioService.exportarRelatorio(
                "Bearer $token",
                formato,
                motoristaId,
                dataInicio,
                dataFim
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val exportResponse = response.body()!!.data!!
                Result.success(exportResponse)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao exportar relatório"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun obterDadosDashboard(periodo: String = "HOJE"): Result<DashboardData> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            val response = ApiClient.relatorioService.obterDadosDashboard(
                "Bearer $token",
                periodo
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dashboardData = response.body()!!.data!!
                Result.success(dashboardData)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao obter dados do dashboard"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Métodos auxiliares para filtros comuns
    suspend fun obterRelatorioHoje(motoristaId: String? = null): Result<RelatorioResponse> {
        val hoje = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        
        val filtro = FiltroRelatorio(
            motoristaId = motoristaId,
            dataInicio = hoje,
            dataFim = hoje
        )
        
        return obterRelatorioFiltrado(filtro)
    }
    
    suspend fun obterRelatorioSemana(motoristaId: String? = null): Result<RelatorioResponse> {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        
        // Início da semana (segunda-feira)
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
        val inicioSemana = dateFormat.format(calendar.time)
        
        // Fim da semana (domingo)
        calendar.add(java.util.Calendar.DAY_OF_WEEK, 6)
        val fimSemana = dateFormat.format(calendar.time)
        
        val filtro = FiltroRelatorio(
            motoristaId = motoristaId,
            dataInicio = inicioSemana,
            dataFim = fimSemana
        )
        
        return obterRelatorioFiltrado(filtro)
    }
    
    suspend fun obterRelatorioMes(motoristaId: String? = null): Result<RelatorioResponse> {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        
        // Primeiro dia do mês
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val inicioMes = dateFormat.format(calendar.time)
        
        // Último dia do mês
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        val fimMes = dateFormat.format(calendar.time)
        
        val filtro = FiltroRelatorio(
            motoristaId = motoristaId,
            dataInicio = inicioMes,
            dataFim = fimMes
        )
        
        return obterRelatorioFiltrado(filtro)
    }
    
    suspend fun obterRelatorioPorStatus(
        status: StatusRegistro,
        motoristaId: String? = null,
        dataInicio: String? = null,
        dataFim: String? = null
    ): Result<RelatorioResponse> {
        val filtro = FiltroRelatorio(
            motoristaId = motoristaId,
            dataInicio = dataInicio,
            dataFim = dataFim,
            status = status
        )
        
        return obterRelatorioFiltrado(filtro)
    }
    
    private fun getAuthToken(): String? {
        val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return authPrefs.getString("auth_token", null)
    }
}