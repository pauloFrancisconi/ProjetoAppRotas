package com.example.projetoapprotas.service

import com.example.projetoapprotas.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshRequest: RefreshTokenRequest): Response<LoginResponse>
    
    @GET("auth/verify")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<VerifyTokenResponse>
    
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>
    
    @GET("users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<Usuario>
} 