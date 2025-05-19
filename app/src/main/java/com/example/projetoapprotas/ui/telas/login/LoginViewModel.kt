package com.example.projetoapprotas.ui.telas.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel: ViewModel(){
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _senha = MutableStateFlow("")
    val senha = _senha.asStateFlow()

    private val _cargoSelecionado = MutableStateFlow<String?>(null)
    val cargoSelecionado = _cargoSelecionado.asStateFlow()

    fun onEmailChange(novoEmail: String){
        _email.value = novoEmail
    }

    fun onSenhaChange(novaSenha: String) {
        _senha.value = novaSenha
    }

    fun onCargoSelecionadoChange(cargo: String) {
        _cargoSelecionado.value = cargo
    }

    fun fazerLogin(onSuccess: () -> Unit) {
        // chamada back
        fun fazerLogin(onSuccess: () -> Unit) {
            if (
                email.value.isNotBlank() &&
                senha.value.length > 5 &&
                cargoSelecionado.value != null
            ) {
                onSuccess()
            }
        }

    }
}