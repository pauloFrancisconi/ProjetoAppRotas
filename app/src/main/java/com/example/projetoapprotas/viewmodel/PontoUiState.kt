package com.example.projetoapprotas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.data.repository.PontoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PontoUiState(
    val cep: String = "",
    val nome: String = "",
    val descricao: String = "",
    val observacoes: String = "",
    val loadingCep: Boolean = false,
    val salvando: Boolean = false,
    val sucesso: String? = null,
    val error: String? = null
) {
    val canSearchCep get() = cep.length == 8 && !loadingCep
    val formValido get() = cep.length == 8 && nome.isNotBlank() &&
            descricao.isNotBlank()
}

class AdicionarPontoViewModel(
    private val repo: PontoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PontoUiState())
    val state: StateFlow<PontoUiState> = _state
    fun onCepChange(value: String)     { _state.value = _state.value.copy(cep = value.take(8).filter(Char::isDigit)) }
    fun onNomeChange(value: String)    { _state.value = _state.value.copy(nome = value) }
    fun onDescricaoChange(v: String)   { _state.value = _state.value.copy(descricao = v) }
    fun onObservacoesChange(v: String) { _state.value = _state.value.copy(observacoes = v) }

    fun dismissMessage() = _state.update { it.copy(error = null, sucesso = null) }
    fun buscarEndereco() = viewModelScope.launch {
        _state.update { it.copy(loadingCep = true, error = null, sucesso = null) }
        repo.buscarEndereco(_state.value.cep)
            .onSuccess { end ->
                val desc = "${end.bairro}, ${end.localidade} - ${end.uf}"
                _state.update {
                    it.copy(
                        nome = end.logradouro ?: "",
                        descricao = desc,
                        loadingCep = false,
                        sucesso = "Endereço encontrado!"
                    )
                }
            }
            .onFailure { e ->
                _state.update { it.copy(loadingCep = false, error = e.message ?: "Falha ao buscar CEP") }
            }
    }

    suspend fun salvarPonto(): Boolean {
        _state.update { it.copy(salvando = true, error = null, sucesso = null) }
        return repo.salvarPonto()
            .onSuccess {
                _state.update { it.copy(salvando = false, sucesso = "Ponto salvo!") }
            }
            .onFailure { e ->
                _state.update { it.copy(salvando = false, error = e.message ?: "Falha ao salvar") }
            }
            .isSuccess
    }
}
