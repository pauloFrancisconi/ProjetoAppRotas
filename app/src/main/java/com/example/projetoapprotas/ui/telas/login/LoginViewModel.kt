package com.example.projetoapprotas.ui.telas.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoapprotas.data.models.UserRole
import com.example.projetoapprotas.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {
    private val authRepository = AuthRepository(context)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _senha = MutableStateFlow("")
    val senha = _senha.asStateFlow()

    private val _cargoSelecionado = MutableStateFlow<String?>(null)
    val cargoSelecionado = _cargoSelecionado.asStateFlow()
    
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()
    
    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole = _userRole.asStateFlow()

    // Observar estado de autenticação
    val isAuthenticated = authRepository.isAuthenticated
    val currentUser = authRepository.currentUser

    fun onEmailChange(novoEmail: String) {
        _email.value = novoEmail
        _errorMessage.value = ""
    }

    fun onSenhaChange(novaSenha: String) {
        _senha.value = novaSenha
        _errorMessage.value = ""
    }

    fun fazerLogin(onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            if (_email.value.isBlank() || _senha.value.isBlank()) {
                _errorMessage.value = "Por favor, preencha todos os campos"
                return@launch
            }
            
            _isLoading.value = true
            _errorMessage.value = ""
            
            val resultado = authRepository.login(_email.value.trim(), _senha.value)
            
            resultado.onSuccess { loginResponse ->
                _loginSuccess.value = true
                loginResponse.usuario?.let { usuario ->
                    _userRole.value = usuario.role
                    onSuccess(usuario.role)
                }
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Erro no login"
            }
            
            _isLoading.value = false
        }
    }
    
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val resultado = authRepository.logout()
            
            resultado.onSuccess {
                _loginSuccess.value = false
                _userRole.value = null
                _email.value = ""
                _senha.value = ""
                _errorMessage.value = ""
                onLogoutComplete()
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Erro no logout"
            }
            
            _isLoading.value = false
        }
    }
    
    fun verificarTokenValido(onTokenValid: (UserRole) -> Unit, onTokenInvalid: () -> Unit) {
        viewModelScope.launch {
            val resultado = authRepository.verifyToken()
            
            resultado.onSuccess {
                authRepository.currentUser.value?.let { usuario ->
                    onTokenValid(usuario.role)
                } ?: onTokenInvalid()
            }.onFailure {
                onTokenInvalid()
            }
        }
    }
    
    fun hasRole(requiredRole: UserRole): Boolean {
        return authRepository.hasRole(requiredRole)
    }
    
    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
    
    fun getCurrentUserRole(): UserRole? {
        return authRepository.getCurrentUserRole()
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}