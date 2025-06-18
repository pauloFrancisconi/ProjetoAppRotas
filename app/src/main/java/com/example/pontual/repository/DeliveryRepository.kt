package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.models.Delivery
import com.example.pontual.api.models.DeliveryRequest
import com.example.pontual.api.models.DeliveryHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeliveryRepository {
    private val apiService = ApiClient.apiService

    suspend fun getDeliveries(): Result<List<Delivery>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDeliveries()
                if (response.isSuccessful) {
                    response.body()?.let { deliveries ->
                        Result.success(deliveries)
                    } ?: Result.failure(Exception("Lista vazia"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getDelivery(id: Int): Result<Delivery> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDelivery(id)
                if (response.isSuccessful) {
                    response.body()?.let { delivery ->
                        Result.success(delivery)
                    } ?: Result.failure(Exception("Entrega não encontrada"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createDelivery(request: DeliveryRequest): Result<Delivery> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createDelivery(request)
                if (response.isSuccessful) {
                    response.body()?.let { delivery ->
                        Result.success(delivery)
                    } ?: Result.failure(Exception("Erro ao criar entrega"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateDelivery(id: Int, request: DeliveryRequest): Result<Delivery> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateDelivery(id, request)
                if (response.isSuccessful) {
                    response.body()?.let { delivery ->
                        Result.success(delivery)
                    } ?: Result.failure(Exception("Erro ao atualizar entrega"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDelivery(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteDelivery(id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun startDelivery(id: Int): Result<DeliveryHistory> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.startDelivery(id)
                if (response.isSuccessful) {
                    response.body()?.let { history ->
                        Result.success(history)
                    } ?: Result.failure(Exception("Erro ao iniciar entrega"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun completeDelivery(id: Int): Result<DeliveryHistory> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.completeDelivery(id)
                if (response.isSuccessful) {
                    response.body()?.let { history ->
                        Result.success(history)
                    } ?: Result.failure(Exception("Erro ao completar entrega"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 