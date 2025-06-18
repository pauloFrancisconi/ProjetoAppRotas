package com.example.projetoapprotas.ui.feature.addpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.domain.model.Point
import com.example.projetoapprotas.domain.usecase.FetchAddressUseCase
import com.example.projetoapprotas.domain.usecase.SavePointUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPointViewModel @Inject constructor(
    private val fetchAddress: FetchAddressUseCase,
    private val savePoint: SavePointUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddPointUiState())
    val state: StateFlow<AddPointUiState> = _state

    /* -------- Events -------- */
    fun onCepChange(v: String)     { _state.update { it.copy(cep = v.take(8).filter(Char::isDigit)) } }
    fun onNameChange(v: String)    { _state.update { it.copy(name = v) } }
    fun onDescChange(v: String)    { _state.update { it.copy(description = v) } }
    fun onNotesChange(v: String)   { _state.update { it.copy(notes = v) } }
    fun clearMessage()             { _state.update { it.copy(error = null, success = null) } }

    fun searchCep() = viewModelScope.launch {
        _state.update { it.copy(loadingCep = true, error = null, success = null) }
        fetchAddress(_state.value.cep)
            .onSuccess { a ->
                val desc = "${a.district}, ${a.city} - ${a.state}"
                _state.update {
                    it.copy(
                        name = a.street,
                        description = desc,
                        loadingCep = false,
                        success = "Endereço encontrado!"
                    )
                }
            }
            .onFailure { e ->
                _state.update { it.copy(loadingCep = false, error = e.message) }
            }
    }

    suspend fun save(onDone: () -> Unit) {
        _state.update { it.copy(saving = true, error = null, success = null) }
        val p = with(_state.value) {
            Point(cep, name, description, notes.takeIf { it.isNotBlank() })
        }
        savePoint(p)
            .onSuccess {
                _state.update { it.copy(saving = false, success = "Ponto salvo!") }
                onDone()
            }
            .onFailure { e ->
                _state.update { it.copy(saving = false, error = e.message) }
            }
    }
}
