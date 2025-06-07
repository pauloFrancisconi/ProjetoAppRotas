package com.example.projetoapprotas.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.data.models.*
import com.example.projetoapprotas.repository.AuthRepository
import com.example.projetoapprotas.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(context: Context) : ViewModel() {
    private val registroRepository = RegistroRepository(context)
    private val authRepository = AuthRepository(context)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage.asStateFlow()
    
    private val _registroAtual = MutableStateFlow<RegistroPonto?>(null)
    val registroAtual: StateFlow<RegistroPonto?> = _registroAtual.asStateFlow()
    
    private val _coordenadasAtuais = MutableStateFlow<Coordenadas?>(null)
    val coordenadasAtuais: StateFlow<Coordenadas?> = _coordenadasAtuais.asStateFlow()
    
    private val _fotosCapturadas = MutableStateFlow<List<String>>(emptyList())
    val fotosCapturadas: StateFlow<List<String>> = _fotosCapturadas.asStateFlow()
    
    private val _observacoes = MutableStateFlow("")
    val observacoes: StateFlow<String> = _observacoes.asStateFlow()
    
    private val _statusSelecionado = MutableStateFlow<StatusRegistro?>(null)
    val statusSelecionado: StateFlow<StatusRegistro?> = _statusSelecionado.asStateFlow()
    
    // Observar dados do repositório
    val registrosPendentes = registroRepository.registrosPendentes
    val isUploading = registroRepository.isUploading
    
    fun atualizarCoordenadas(latitude: Double, longitude: Double, precisao: Float? = null) {
        _coordenadasAtuais.value = Coordenadas(latitude, longitude, precisao)
    }
    
    fun adicionarFoto(fotoUrl: String) {
        val fotosAtuais = _fotosCapturadas.value.toMutableList()
        fotosAtuais.add(fotoUrl)
        _fotosCapturadas.value = fotosAtuais
    }
    
    fun removerFoto(fotoUrl: String) {
        val fotosAtuais = _fotosCapturadas.value.toMutableList()
        fotosAtuais.remove(fotoUrl)
        _fotosCapturadas.value = fotosAtuais
    }
    
    fun atualizarObservacoes(novasObservacoes: String) {
        _observacoes.value = novasObservacoes
    }
    
    fun selecionarStatus(status: StatusRegistro) {
        _statusSelecionado.value = status
    }
    
    fun registrarPonto(
        pontoColetaId: String,
        onSuccess: (RegistroPonto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val coordenadas = _coordenadasAtuais.value
            val status = _statusSelecionado.value
            val motoristaId = authRepository.getCurrentUserId()
            
            if (coordenadas == null) {
                val erro = "Coordenadas não disponíveis"
                _errorMessage.value = erro
                onError(erro)
                return@launch
            }
            
            if (status == null) {
                val erro = "Status do registro não selecionado"
                _errorMessage.value = erro
                onError(erro)
                return@launch
            }
            
            if (motoristaId == null) {
                val erro = "Usuário não autenticado"
                _errorMessage.value = erro
                onError(erro)
                return@launch
            }
            
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = registroRepository.registrarPonto(
                pontoColetaId = pontoColetaId,
                coordenadas = coordenadas,
                fotos = _fotosCapturadas.value,
                observacoes = _observacoes.value.takeIf { it.isNotBlank() },
                status = status,
                motoristaId = motoristaId
            )
            
            resultado.onSuccess { registro ->
                _registroAtual.value = registro
                _successMessage.value = "Ponto registrado com sucesso!"
                limparFormulario()
                onSuccess(registro)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao registrar ponto"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun uploadFotos(
        fotos: List<FotoUpload>,
        onSuccess: (List<FotoResponse>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = registroRepository.uploadFotos(fotos)
            
            resultado.onSuccess { fotosResponse ->
                val urlsFotos = fotosResponse.map { it.url }
                _fotosCapturadas.value = urlsFotos
                _successMessage.value = "Fotos enviadas com sucesso!"
                onSuccess(fotosResponse)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro no upload das fotos"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun sincronizarRegistrosPendentes(
        onSuccess: (Int) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = registroRepository.sincronizarRegistrosPendentes()
            
            resultado.onSuccess { sincronizados ->
                _successMessage.value = "$sincronizados registros sincronizados com sucesso!"
                onSuccess(sincronizados)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro na sincronização"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun obterRegistrosMotorista(
        dataInicio: String? = null,
        dataFim: String? = null,
        onSuccess: (List<RegistroPonto>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val motoristaId = authRepository.getCurrentUserId()
            
            if (motoristaId == null) {
                val erro = "Usuário não autenticado"
                _errorMessage.value = erro
                onError(erro)
                return@launch
            }
            
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = registroRepository.obterRegistrosMotorista(
                motoristaId,
                dataInicio,
                dataFim
            )
            
            resultado.onSuccess { registros ->
                onSuccess(registros)
            }.onFailure { exception ->
                val erro = exception.message ?: "Erro ao obter registros"
                _errorMessage.value = erro
                onError(erro)
            }
            
            _isLoading.value = false
        }
    }
    
    fun getRegistrosPendentesCount(): Int {
        return registroRepository.getRegistrosPendentesCount()
    }
    
    fun limparRegistrosSincronizados() {
        registroRepository.limparRegistrosSincronizados()
    }
    
    private fun limparFormulario() {
        _coordenadasAtuais.value = null
        _fotosCapturadas.value = emptyList()
        _observacoes.value = ""
        _statusSelecionado.value = null
        _registroAtual.value = null
    }
    
    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
    
    // Métodos auxiliares para validação
    fun isFormularioValido(): Boolean {
        return _coordenadasAtuais.value != null && _statusSelecionado.value != null
    }
    
    fun getStatusOptions(): List<StatusRegistro> {
        return StatusRegistro.values().toList()
    }
    
    fun getStatusDisplayName(status: StatusRegistro): String {
        return when (status) {
            StatusRegistro.COLETADO -> "Coletado"
            StatusRegistro.NAO_COLETADO -> "Não Coletado"
            StatusRegistro.PARCIALMENTE_COLETADO -> "Parcialmente Coletado"
            StatusRegistro.PROBLEMA_ACESSO -> "Problema de Acesso"
        }
    }
} 