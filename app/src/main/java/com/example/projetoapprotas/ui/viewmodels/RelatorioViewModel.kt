package com.example.projetoapprotas.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.data.models.*
import com.example.projetoapprotas.repository.AuthRepository
import com.example.projetoapprotas.repository.RelatorioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RelatorioViewModel(context: Context) : ViewModel() {
    private val relatorioRepository = RelatorioRepository(context)
    private val authRepository = AuthRepository(context)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    private val _relatorioAtual = MutableStateFlow<RelatorioResponse?>(null)
    val relatorioAtual: StateFlow<RelatorioResponse?> = _relatorioAtual.asStateFlow()
    
    private val _relatorioDetalhado = MutableStateFlow<RelatorioDetalhado?>(null)
    val relatorioDetalhado: StateFlow<RelatorioDetalhado?> = _relatorioDetalhado.asStateFlow()
    
    private val _estatisticas = MutableStateFlow<EstatisticasRelatorio?>(null)
    val estatisticas: StateFlow<EstatisticasRelatorio?> = _estatisticas.asStateFlow()
    
    private val _dashboardData = MutableStateFlow<DashboardData?>(null)
    val dashboardData: StateFlow<DashboardData?> = _dashboardData.asStateFlow()
    
    private val _filtroAtual = MutableStateFlow(FiltroRelatorio())
    val filtroAtual: StateFlow<FiltroRelatorio> = _filtroAtual.asStateFlow()
    
    // Observar dados do repositório
    val ultimoRelatorio = relatorioRepository.ultimoRelatorio
    
    fun aplicarFiltro(
        motoristaId: String? = null,
        dataInicio: String? = null,
        dataFim: String? = null,
        status: StatusRegistro? = null,
        pontoColetaId: String? = null
    ) {
        val novoFiltro = FiltroRelatorio(
            motoristaId = motoristaId,
            dataInicio = dataInicio,
            dataFim = dataFim,
            status = status,
            pontoColetaId = pontoColetaId
        )
        _filtroAtual.value = novoFiltro
        obterRelatorioFiltrado(novoFiltro)
    }
    
    fun obterRelatorioFiltrado(
        filtro: FiltroRelatorio = _filtroAtual.value,
        pagina: Int = 1,
        limite: Int = 50,
        onSuccess: (RelatorioResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = relatorioRepository.obterRelatorioFiltrado(filtro, pagina, limite)
            
            resultado.onSuccess { relatorio ->
                _relatorioAtual.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun obterRelatorioMotorista(
        motoristaId: String,
        dataInicio: String,
        dataFim: String,
        onSuccess: (RelatorioDetalhado) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = relatorioRepository.obterRelatorioMotorista(motoristaId, dataInicio, dataFim)
            
            resultado.onSuccess { relatorio ->
                _relatorioDetalhado.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório do motorista"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun obterEstatisticasGerais(
        dataInicio: String? = null,
        dataFim: String? = null,
        motoristaId: String? = null,
        onSuccess: (EstatisticasRelatorio) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = relatorioRepository.obterEstatisticasGerais(dataInicio, dataFim, motoristaId)
            
            resultado.onSuccess { estatisticas ->
                _estatisticas.value = estatisticas
                onSuccess(estatisticas)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter estatísticas"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun exportarRelatorio(
        formato: String = "PDF",
        motoristaId: String? = null,
        dataInicio: String? = null,
        dataFim: String? = null,
        onSuccess: (ExportResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = relatorioRepository.exportarRelatorio(formato, motoristaId, dataInicio, dataFim)
            
            resultado.onSuccess { exportResponse ->
                onSuccess(exportResponse)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao exportar relatório"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun obterDadosDashboard(
        periodo: String = "HOJE",
        onSuccess: (DashboardData) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = relatorioRepository.obterDadosDashboard(periodo)
            
            resultado.onSuccess { dashboard ->
                _dashboardData.value = dashboard
                onSuccess(dashboard)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter dados do dashboard"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    // Métodos de conveniência para relatórios comuns
    fun obterRelatorioHoje(onSuccess: (RelatorioResponse) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val motoristaId = if (authRepository.hasRole(UserRole.MOTORISTA)) {
                authRepository.getCurrentUserId()
            } else null
            
            val resultado = relatorioRepository.obterRelatorioHoje(motoristaId)
            
            resultado.onSuccess { relatorio ->
                _relatorioAtual.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório de hoje"
                _errorMessage.value = erro
                onError(erro)
            }
        }
    }
    
    fun obterRelatorioSemana(onSuccess: (RelatorioResponse) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val motoristaId = if (authRepository.hasRole(UserRole.MOTORISTA)) {
                authRepository.getCurrentUserId()
            } else null
            
            val resultado = relatorioRepository.obterRelatorioSemana(motoristaId)
            
            resultado.onSuccess { relatorio ->
                _relatorioAtual.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório da semana"
                _errorMessage.value = erro
                onError(erro)
            }
        }
    }
    
    fun obterRelatorioMes(onSuccess: (RelatorioResponse) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val motoristaId = if (authRepository.hasRole(UserRole.MOTORISTA)) {
                authRepository.getCurrentUserId()
            } else null
            
            val resultado = relatorioRepository.obterRelatorioMes(motoristaId)
            
            resultado.onSuccess { relatorio ->
                _relatorioAtual.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório do mês"
                _errorMessage.value = erro
                onError(erro)
            }
        }
    }
    
    fun obterRelatorioPorStatus(
        status: StatusRegistro,
        dataInicio: String? = null,
        dataFim: String? = null,
        onSuccess: (RelatorioResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val motoristaId = if (authRepository.hasRole(UserRole.MOTORISTA)) {
                authRepository.getCurrentUserId()
            } else null
            
            val resultado = relatorioRepository.obterRelatorioPorStatus(status, motoristaId, dataInicio, dataFim)
            
            resultado.onSuccess { relatorio ->
                _relatorioAtual.value = relatorio
                onSuccess(relatorio)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter relatório por status"
                _errorMessage.value = erro
                onError(erro)
            }
        }
    }
    
    fun limparFiltros() {
        _filtroAtual.value = FiltroRelatorio()
        _relatorioAtual.value = null
        _errorMessage.value = ""
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
    
    // Métodos auxiliares
    fun hasRole(role: UserRole): Boolean {
        return authRepository.hasRole(role)
    }
    
    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
    
    fun getFormatosExportacao(): List<String> {
        return listOf("PDF", "EXCEL", "CSV")
    }
    
    fun getPeriodosDashboard(): List<String> {
        return listOf("HOJE", "SEMANA", "MES", "ANO")
    }
} 