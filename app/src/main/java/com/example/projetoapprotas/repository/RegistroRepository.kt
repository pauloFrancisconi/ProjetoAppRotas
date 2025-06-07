package com.example.projetoapprotas.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.projetoapprotas.data.models.*
import com.example.projetoapprotas.network.ApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class RegistroRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("registro_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _registrosPendentes = MutableStateFlow<List<RegistroPonto>>(emptyList())
    val registrosPendentes: StateFlow<List<RegistroPonto>> = _registrosPendentes.asStateFlow()
    
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()
    
    init {
        loadRegistrosPendentes()
    }
    
    suspend fun registrarPonto(
        pontoColetaId: String,
        coordenadas: Coordenadas,
        fotos: List<String> = emptyList(),
        observacoes: String? = null,
        status: StatusRegistro,
        motoristaId: String
    ): Result<RegistroPonto> {
        return try {
            val registro = RegistroPonto(
                id = UUID.randomUUID().toString(),
                motoristaId = motoristaId,
                pontoColetaId = pontoColetaId,
                coordenadas = coordenadas,
                timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(Date()),
                fotos = fotos,
                observacoes = observacoes,
                status = status,
                sincronizado = false
            )
            
            // Salvar localmente primeiro
            salvarRegistroLocal(registro)
            
            // Tentar enviar para o servidor
            enviarRegistroParaServidor(registro)
            
            Result.success(registro)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadFotos(fotos: List<FotoUpload>): Result<List<FotoResponse>> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            _isUploading.value = true
            val response = ApiClient.registroService.uploadFotos("Bearer $token", fotos)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val fotosResponse = response.body()!!.data ?: emptyList()
                Result.success(fotosResponse)
            } else {
                val errorMessage = response.body()?.error ?: "Falha no upload das fotos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isUploading.value = false
        }
    }
    
    suspend fun sincronizarRegistrosPendentes(): Result<Int> {
        return try {
            val registrosPendentes = _registrosPendentes.value.filter { !it.sincronizado }
            var sincronizados = 0
            
            for (registro in registrosPendentes) {
                val resultado = enviarRegistroParaServidor(registro)
                if (resultado.isSuccess) {
                    sincronizados++
                    marcarComoSincronizado(registro.id)
                }
            }
            
            Result.success(sincronizados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun obterRegistrosMotorista(
        motoristaId: String,
        dataInicio: String? = null,
        dataFim: String? = null
    ): Result<List<RegistroPonto>> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            val response = ApiClient.registroService.obterRegistrosMotorista(
                "Bearer $token",
                motoristaId,
                dataInicio,
                dataFim
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val registros = response.body()!!.data ?: emptyList()
                Result.success(registros)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao obter registros"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun enviarRegistroParaServidor(registro: RegistroPonto): Result<RegistroPonto> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("Token de autenticação não encontrado"))
            }
            
            val request = RegistroPontoRequest(
                pontoColetaId = registro.pontoColetaId,
                coordenadas = registro.coordenadas,
                fotos = registro.fotos,
                observacoes = registro.observacoes,
                status = registro.status
            )
            
            val response = ApiClient.registroService.enviarRegistroPonto("Bearer $token", request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val registroSincronizado = response.body()!!.data!!
                marcarComoSincronizado(registro.id)
                Result.success(registroSincronizado)
            } else {
                val errorMessage = response.body()?.error ?: "Falha ao enviar registro"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun salvarRegistroLocal(registro: RegistroPonto) {
        val registrosAtuais = _registrosPendentes.value.toMutableList()
        registrosAtuais.add(registro)
        _registrosPendentes.value = registrosAtuais
        
        // Salvar no SharedPreferences
        val registrosJson = gson.toJson(registrosAtuais)
        prefs.edit().putString("registros_pendentes", registrosJson).apply()
    }
    
    private fun marcarComoSincronizado(registroId: String) {
        val registrosAtuais = _registrosPendentes.value.toMutableList()
        val index = registrosAtuais.indexOfFirst { it.id == registroId }
        
        if (index != -1) {
            registrosAtuais[index] = registrosAtuais[index].copy(sincronizado = true)
            _registrosPendentes.value = registrosAtuais
            
            // Atualizar SharedPreferences
            val registrosJson = gson.toJson(registrosAtuais)
            prefs.edit().putString("registros_pendentes", registrosJson).apply()
        }
    }
    
    private fun loadRegistrosPendentes() {
        val registrosJson = prefs.getString("registros_pendentes", null)
        if (registrosJson != null) {
            try {
                val type = object : TypeToken<List<RegistroPonto>>() {}.type
                val registros = gson.fromJson<List<RegistroPonto>>(registrosJson, type)
                _registrosPendentes.value = registros
            } catch (e: Exception) {
                _registrosPendentes.value = emptyList()
            }
        }
    }
    
    private fun getAuthToken(): String? {
        val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return authPrefs.getString("auth_token", null)
    }
    
    fun getRegistrosPendentesCount(): Int {
        return _registrosPendentes.value.count { !it.sincronizado }
    }
    
    fun limparRegistrosSincronizados() {
        val registrosNaoSincronizados = _registrosPendentes.value.filter { !it.sincronizado }
        _registrosPendentes.value = registrosNaoSincronizados
        
        val registrosJson = gson.toJson(registrosNaoSincronizados)
        prefs.edit().putString("registros_pendentes", registrosJson).apply()
    }
} 