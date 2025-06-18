package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DeliveryProgressRepository {
    private val apiService = ApiClient.apiService

    suspend fun startDelivery(deliveryId: Int): Result<DeliveryHistory> {
        return try {
            val response = apiService.startDelivery(deliveryId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao iniciar entrega"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeDeliveryPoint(
        deliveryId: Int,
        pointId: Int,
        driverId: Int,
        photoUrl: String? = null,
        notes: String? = null,
        signature: String? = null
    ): Result<DeliveryPointCompletion> {
        return try {
            val request = DeliveryPointCompletionRequest(
                deliveryId = deliveryId,
                deliveryPointId = pointId,
                driverId = driverId,
                photoUrl = photoUrl,
                notes = notes,
                signature = signature
            )
            
            val response = apiService.completeDeliveryPoint(deliveryId, pointId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao completar ponto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadDeliveryPhoto(
        deliveryId: Int,
        pointId: Int,
        photoFile: File
    ): Result<String> {
        return try {
            val requestBody = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestBody)
            
            val response = apiService.uploadDeliveryPhoto(deliveryId, pointId, photoPart)
            if (response.isSuccessful) {
                val result = response.body()
                Result.success(result?.get("photo_url") ?: "")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao fazer upload da foto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeDelivery(deliveryId: Int): Result<DeliveryHistory> {
        return try {
            val response = apiService.completeDelivery(deliveryId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao finalizar entrega"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeliveryProgress(deliveryId: Int): Result<DeliveryProgress> {
        return try {
            val response = apiService.getDeliveryProgress(deliveryId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao obter progresso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDriverDeliveryHistory(driverId: Int): Result<List<DeliveryHistory>> {
        return try {
            val response = apiService.getDriverDeliveryHistory(driverId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao obter histórico"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllDeliveryHistory(): Result<List<DeliveryHistory>> {
        return try {
            val response = apiService.getAllDeliveryHistory()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Erro ao obter histórico"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 