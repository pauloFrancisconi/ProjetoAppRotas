package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.models.Driver
import com.example.pontual.api.models.DriverRequest
import com.example.pontual.api.models.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriverRepository {
    private val apiService = ApiClient.apiService

    suspend fun getDrivers(): Result<List<Driver>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDrivers()
                if (response.isSuccessful) {
                    response.body()?.let { drivers ->
                        Result.success(drivers)
                    } ?: Result.failure(Exception("Lista vazia"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getDriver(id: Int): Result<Driver> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDriver(id)
                if (response.isSuccessful) {
                    response.body()?.let { driver ->
                        Result.success(driver)
                    } ?: Result.failure(Exception("Motorista não encontrado"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createDriver(request: DriverRequest): Result<Driver> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createDriver(request)
                if (response.isSuccessful) {
                    response.body()?.let { driver ->
                        Result.success(driver)
                    } ?: Result.failure(Exception("Erro ao criar motorista"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateDriver(id: Int, request: DriverRequest): Result<Driver> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateDriver(id, request)
                if (response.isSuccessful) {
                    response.body()?.let { driver ->
                        Result.success(driver)
                    } ?: Result.failure(Exception("Erro ao atualizar motorista"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDriver(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteDriver(id)
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

    suspend fun getDriverRoutes(driverId: Int): Result<List<Route>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDriverRoutes(driverId)
                if (response.isSuccessful) {
                    response.body()?.let { routes ->
                        Result.success(routes)
                    } ?: Result.failure(Exception("Lista vazia"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 