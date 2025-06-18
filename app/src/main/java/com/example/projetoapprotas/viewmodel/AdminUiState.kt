package com.example.projetoapprotas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.data.repository.ResumoSistemaRepository
import com.example.projetoapprotas.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val pontos: Int = 0,
    val rotas: Int = 0,
    val motoristas: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AdminHomeViewModel(
    private val repository: ResumoSistemaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    val saudacao = DateUtils.saudacaoAtual()
    val dataExtenso = DateUtils.dataExtenso()

    init { atualizarResumo() }

    fun atualizarResumo() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true, error = null)
        try {
            val dto = repository.carregarResumo()
            _state.value = AdminUiState(
                pontos = dto.pontos,
                rotas = dto.rotas,
                motoristas = dto.motoristas,
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = e.localizedMessage ?: "Erro desconhecido"
            )
        }
    }
}
