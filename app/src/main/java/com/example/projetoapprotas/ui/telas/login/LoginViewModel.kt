package com.example.projetoapprotas.ui.telas.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _senha = MutableStateFlow("")
    val senha = _senha.asStateFlow()

    private val _cargoSelecionado = MutableStateFlow<String?>(null)
    val cargoSelecionado = _cargoSelecionado.asStateFlow()

    fun onEmailChange(novoEmail: String) {
        _email.value = novoEmail
    }

    fun onSenhaChange(novaSenha: String) {
        _senha.value = novaSenha
    }

    fun fazerLogin(
        onSuccess: (String) -> Unit, // envia a rota desejada
        onError: (String) -> Unit
    ) {
        isLoading.value = true

        val emailInserido = email.value.trim()
        val senhaInserida = senha.value.trim()

        // Mock de usuários
        val mockUsuarios = listOf(
            Triple("Borges@gmail.com", "1234", "motorista"),
            Triple("Lucas@gmail.com", "admin", "admin")
        )

        val usuarioValido = mockUsuarios.find { (login, senha, _) ->
            emailInserido == login && senhaInserida == senha
        }

        if (usuarioValido != null) {
            val (_, _, cargo) = usuarioValido
            isLoading.value = false
            onSuccess(cargo) // "admin" ou "motorista"
        } else {
            isLoading.value = false
            onError("Usuário ou senha inválidos")
        }
    }

    fun setErrorMessage(message: String) {
        errorMessage.value = message
    }


}