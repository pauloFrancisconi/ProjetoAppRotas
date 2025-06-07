package com.example.projetoapprotas.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.projetoapprotas.data.models.*
import com.example.projetoapprotas.network.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    init {
        // Verificar se há usuário logado ao inicializar
        loadStoredUser()
    }
    
    suspend fun login(email: String, senha: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, senha)
            val response = ApiClient.authService.login(loginRequest)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val loginResponse = response.body()!!
                
                // Salvar token e usuário
                loginResponse.token?.let { token ->
                    saveAuthToken(token)
                    ApiClient.setAuthToken(token)
                }
                
                loginResponse.usuario?.let { usuario ->
                    saveUser(usuario)
                    _currentUser.value = usuario
                    _isAuthenticated.value = true
                }
                
                Result.success(loginResponse)
            } else {
                val errorMessage = response.body()?.message ?: "Login falhou"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Boolean> {
        return try {
            val token = getAuthToken()
            if (token != null) {
                val response = ApiClient.authService.logout("Bearer $token")
                // Independente da resposta do servidor, limpar dados locais
            }
            
            clearAuthData()
            Result.success(true)
        } catch (e: Exception) {
            // Mesmo com erro, limpar dados locais
            clearAuthData()
            Result.success(true)
        }
    }
    
    suspend fun verifyToken(): Result<Boolean> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                Result.failure(Exception("Token não encontrado"))
            } else {
                val response = ApiClient.authService.verifyToken("Bearer $token")
                
                if (response.isSuccessful && response.body()?.valid == true) {
                    response.body()?.usuario?.let { usuario ->
                        _currentUser.value = usuario
                        _isAuthenticated.value = true
                    }
                    Result.success(true)
                } else {
                    clearAuthData()
                    Result.failure(Exception("Token inválido"))
                }
            }
        } catch (e: Exception) {
            clearAuthData()
            Result.failure(e)
        }
    }
    
    suspend fun refreshToken(): Result<Boolean> {
        return try {
            val refreshToken = getRefreshToken()
            if (refreshToken == null) {
                Result.failure(Exception("Refresh token não encontrado"))
            } else {
                val response = ApiClient.authService.refreshToken(RefreshTokenRequest(refreshToken))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!
                    
                    loginResponse.token?.let { token ->
                        saveAuthToken(token)
                        ApiClient.setAuthToken(token)
                    }
                    
                    loginResponse.usuario?.let { usuario ->
                        saveUser(usuario)
                        _currentUser.value = usuario
                    }
                    
                    Result.success(true)
                } else {
                    clearAuthData()
                    Result.failure(Exception("Falha ao renovar token"))
                }
            }
        } catch (e: Exception) {
            clearAuthData()
            Result.failure(e)
        }
    }
    
    fun hasRole(requiredRole: UserRole): Boolean {
        val currentUserRole = _currentUser.value?.role
        return when (requiredRole) {
            UserRole.MOTORISTA -> currentUserRole in listOf(UserRole.MOTORISTA, UserRole.GERENTE, UserRole.ADMIN)
            UserRole.GERENTE -> currentUserRole in listOf(UserRole.GERENTE, UserRole.ADMIN)
            UserRole.ADMIN -> currentUserRole == UserRole.ADMIN
        }
    }
    
    fun getCurrentUserId(): String? = _currentUser.value?.id
    
    fun getCurrentUserRole(): UserRole? = _currentUser.value?.role
    
    private fun loadStoredUser() {
        val userJson = prefs.getString("current_user", null)
        val token = prefs.getString("auth_token", null)
        
        if (userJson != null && token != null) {
            try {
                val user = gson.fromJson(userJson, Usuario::class.java)
                _currentUser.value = user
                _isAuthenticated.value = true
                ApiClient.setAuthToken(token)
            } catch (e: Exception) {
                clearAuthData()
            }
        }
    }
    
    private fun saveUser(usuario: Usuario) {
        val userJson = gson.toJson(usuario)
        prefs.edit().putString("current_user", userJson).apply()
    }
    
    private fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }
    
    private fun saveRefreshToken(refreshToken: String) {
        prefs.edit().putString("refresh_token", refreshToken).apply()
    }
    
    private fun getAuthToken(): String? = prefs.getString("auth_token", null)
    
    private fun getRefreshToken(): String? = prefs.getString("refresh_token", null)
    
    private fun clearAuthData() {
        prefs.edit().clear().apply()
        _currentUser.value = null
        _isAuthenticated.value = false
        ApiClient.setAuthToken(null)
    }
} 