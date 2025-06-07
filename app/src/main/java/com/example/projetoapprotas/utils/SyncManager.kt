package com.example.projetoapprotas.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.projetoapprotas.repository.RegistroRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncManager(private val context: Context) {
    private val registroRepository = RegistroRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()
    
    private val _syncErrors = MutableStateFlow<List<String>>(emptyList())
    val syncErrors: StateFlow<List<String>> = _syncErrors.asStateFlow()
    
    private var autoSyncJob: Job? = null
    
    companion object {
        private const val AUTO_SYNC_INTERVAL = 30_000L // 30 segundos
        private const val RETRY_DELAY = 5_000L // 5 segundos
        private const val MAX_RETRIES = 3
    }
    
    fun startAutoSync() {
        stopAutoSync()
        
        autoSyncJob = scope.launch {
            while (isActive) {
                if (isNetworkAvailable()) {
                    syncPendingData()
                }
                delay(AUTO_SYNC_INTERVAL)
            }
        }
    }
    
    fun stopAutoSync() {
        autoSyncJob?.cancel()
        autoSyncJob = null
    }
    
    suspend fun syncPendingData(): Result<Int> {
        if (_isSyncing.value) {
            return Result.failure(Exception("Sincronização já em andamento"))
        }
        
        return try {
            _isSyncing.value = true
            _syncErrors.value = emptyList()
            
            val resultado = registroRepository.sincronizarRegistrosPendentes()
            
            resultado.onSuccess { sincronizados ->
                _lastSyncTime.value = System.currentTimeMillis()
                if (sincronizados > 0) {
                    registroRepository.limparRegistrosSincronizados()
                }
            }.onFailure { exception ->
                val errors = _syncErrors.value.toMutableList()
                errors.add(exception.message ?: "Erro na sincronização")
                _syncErrors.value = errors
            }
            
            resultado
        } finally {
            _isSyncing.value = false
        }
    }
    
    suspend fun forceSyncWithRetry(): Result<Int> {
        var lastException: Exception? = null
        
        repeat(MAX_RETRIES) { attempt ->
            if (isNetworkAvailable()) {
                val resultado = syncPendingData()
                if (resultado.isSuccess) {
                    return resultado
                } else {
                    lastException = resultado.exceptionOrNull() as? Exception
                    if (attempt < MAX_RETRIES - 1) {
                        delay(RETRY_DELAY * (attempt + 1)) // Backoff exponencial
                    }
                }
            } else {
                lastException = Exception("Sem conexão com a internet")
                delay(RETRY_DELAY)
            }
        }
        
        return Result.failure(lastException ?: Exception("Falha na sincronização após $MAX_RETRIES tentativas"))
    }
    
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    fun getPendingRegistrosCount(): Int {
        return registroRepository.getRegistrosPendentesCount()
    }
    
    fun getLastSyncTimeFormatted(): String? {
        val lastSync = _lastSyncTime.value ?: return null
        val now = System.currentTimeMillis()
        val diffMinutes = (now - lastSync) / (1000 * 60)
        
        return when {
            diffMinutes < 1 -> "Agora mesmo"
            diffMinutes < 60 -> "${diffMinutes}min atrás"
            diffMinutes < 1440 -> "${diffMinutes / 60}h atrás"
            else -> "${diffMinutes / 1440}d atrás"
        }
    }
    
    fun clearSyncErrors() {
        _syncErrors.value = emptyList()
    }
    
    fun destroy() {
        stopAutoSync()
        scope.cancel()
    }
} 