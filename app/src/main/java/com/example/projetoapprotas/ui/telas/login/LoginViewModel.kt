package com.example.projetoapprotas.ui.telas.login

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UsuarioMockado(
    val email: String,
    val senha: String,
    val tipo: String,
    val nome: String
)

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
        context: Context,
        onSuccess: (String) -> Unit, // envia a rota desejada
        onError: (String) -> Unit
    ) {
        isLoading.value = true

        val emailInserido = email.value.trim()
        val senhaInserida = senha.value.trim()

        val mockUsuarios = listOf(
            UsuarioMockado("joao.silva@gmail.com", "1234", "motorista", "João"),
            UsuarioMockado("carlos.lima@gmail.com", "admin", "admin", "Carlos")
        )

        val usuarioValido = mockUsuarios.find { (login, senha, _, _) ->
            emailInserido == login && senhaInserida == senha
        }

        if (usuarioValido != null) {
            val (email, _, tipo, nome) = usuarioValido

            // Salva no SharedPreferences
            salvarUsuarioNoSharedPreferences(context, email, nome)

            isLoading.value = false
            onSuccess(tipo) // "admin" ou "motorista"
        } else {
            isLoading.value = false
            onError("Usuário ou senha inválidos")
        }
    }

    private fun salvarUsuarioNoSharedPreferences(context: Context, email: String, nome: String) {
        val sharedPref = context.getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("email", email)
            putString("nome", nome)
            apply()
        }
    }

    fun setErrorMessage(message: String) {
        errorMessage.value = message
    }
}
