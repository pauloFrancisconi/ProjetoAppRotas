package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.models.DeliveryPoint
import com.example.pontual.api.models.DeliveryPointRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeliveryPointRepository {
    private val apiService = ApiClient.apiService

    suspend fun getDeliveryPoints(): Result<List<DeliveryPoint>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDeliveryPoints()
                if (response.isSuccessful) {
                    response.body()?.let { points ->
                        Result.success(points)
                    } ?: Result.failure(Exception("Lista vazia"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getDeliveryPoint(id: Int): Result<DeliveryPoint> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDeliveryPoint(id)
                if (response.isSuccessful) {
                    response.body()?.let { point ->
                        Result.success(point)
                    } ?: Result.failure(Exception("Ponto não encontrado"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createDeliveryPoint(request: DeliveryPointRequest): Result<DeliveryPoint> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createDeliveryPoint(request)
                if (response.isSuccessful) {
                    response.body()?.let { point ->
                        Result.success(point)
                    } ?: Result.failure(Exception("Erro ao criar ponto"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateDeliveryPoint(id: Int, request: DeliveryPointRequest): Result<DeliveryPoint> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateDeliveryPoint(id, request)
                if (response.isSuccessful) {
                    response.body()?.let { point ->
                        Result.success(point)
                    } ?: Result.failure(Exception("Erro ao atualizar ponto"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDeliveryPoint(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteDeliveryPoint(id)
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
} 