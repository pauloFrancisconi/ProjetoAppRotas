package com.example.pontual.repository

import com.example.pontual.api.ApiClient
import com.example.pontual.api.models.Route
import com.example.pontual.api.models.RouteRequest
import com.example.pontual.api.models.RouteAssignmentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteRepository {
    private val apiService = ApiClient.apiService

    suspend fun getRoutes(): Result<List<Route>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoutes()
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

    suspend fun getRoute(id: Int): Result<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoute(id)
                if (response.isSuccessful) {
                    response.body()?.let { route ->
                        Result.success(route)
                    } ?: Result.failure(Exception("Rota não encontrada"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createRoute(request: RouteRequest): Result<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createRoute(request)
                if (response.isSuccessful) {
                    response.body()?.let { route ->
                        Result.success(route)
                    } ?: Result.failure(Exception("Erro ao criar rota"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateRoute(id: Int, request: RouteRequest): Result<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateRoute(id, request)
                if (response.isSuccessful) {
                    response.body()?.let { route ->
                        Result.success(route)
                    } ?: Result.failure(Exception("Erro ao atualizar rota"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteRoute(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteRoute(id)
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

    suspend fun assignRoute(request: RouteAssignmentRequest): Result<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.assignRoute(request)
                if (response.isSuccessful) {
                    response.body()?.let { route ->
                        Result.success(route)
                    } ?: Result.failure(Exception("Erro ao atribuir motorista"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 