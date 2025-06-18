package com.example.pontual.api

import com.example.pontual.api.models.*
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("user/logout")
    suspend fun logout(): Response<Unit>
    
    @POST("user/refresh")
    suspend fun refreshToken(@Body refreshRequest: RefreshTokenRequest): Response<LoginResponse>
    
    @GET("users")
    suspend fun getUsers(): Response<List<UserData>>
    
    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserData>
    
    @GET("delivery-points")
    suspend fun getDeliveryPoints(): Response<List<DeliveryPoint>>
    
    @GET("delivery-points/{id}")
    suspend fun getDeliveryPoint(@Path("id") id: Int): Response<DeliveryPoint>
    
    @POST("delivery-points")
    suspend fun createDeliveryPoint(@Body request: DeliveryPointRequest): Response<DeliveryPoint>
    
    @PUT("delivery-points/{id}")
    suspend fun updateDeliveryPoint(
        @Path("id") id: Int,
        @Body request: DeliveryPointRequest
    ): Response<DeliveryPoint>
    
    @DELETE("delivery-points/{id}")
    suspend fun deleteDeliveryPoint(@Path("id") id: Int): Response<Unit>
    
    @GET("routes")
    suspend fun getRoutes(): Response<List<Route>>
    
    @GET("routes/{id}")
    suspend fun getRoute(@Path("id") id: Int): Response<Route>
    
    @POST("routes")
    suspend fun createRoute(@Body request: RouteRequest): Response<Route>
    
    @PUT("routes/{id}")
    suspend fun updateRoute(
        @Path("id") id: Int,
        @Body request: RouteRequest
    ): Response<Route>
    
    @DELETE("routes/{id}")
    suspend fun deleteRoute(@Path("id") id: Int): Response<Unit>
    
    @POST("routes/assign")
    suspend fun assignRoute(@Body request: RouteAssignmentRequest): Response<Route>
    
    @GET("drivers")
    suspend fun getDrivers(): Response<List<Driver>>
    
    @GET("drivers/{id}")
    suspend fun getDriver(@Path("id") id: Int): Response<Driver>
    
    @POST("drivers")
    suspend fun createDriver(@Body request: DriverRequest): Response<Driver>
    
    @PUT("drivers/{id}")
    suspend fun updateDriver(
        @Path("id") id: Int,
        @Body request: DriverRequest
    ): Response<Driver>
    
    @DELETE("drivers/{id}")
    suspend fun deleteDriver(@Path("id") id: Int): Response<Unit>
    
    @GET("drivers/{driverId}/routes")
    suspend fun getDriverRoutes(@Path("driverId") driverId: Int): Response<List<Route>>
    
    @GET("deliveries")
    suspend fun getDeliveries(
        @Query("status") status: String? = null,
        @Query("driverId") driverId: Int? = null,
        @Query("routeId") routeId: Int? = null
    ): Response<List<Delivery>>
    
    @GET("deliveries/{id}")
    suspend fun getDelivery(@Path("id") id: Int): Response<Delivery>
    
    @POST("deliveries")
    suspend fun createDelivery(@Body request: DeliveryRequest): Response<Delivery>
    
    @PUT("deliveries/{id}")
    suspend fun updateDelivery(
        @Path("id") id: Int,
        @Body request: DeliveryRequest
    ): Response<Delivery>
    
    @DELETE("deliveries/{id}")
    suspend fun deleteDelivery(@Path("id") id: Int): Response<Unit>
    
    @POST("deliveries/{deliveryId}/start")
    suspend fun startDelivery(@Path("deliveryId") deliveryId: Int): Response<DeliveryHistory>
    
    @POST("deliveries/{deliveryId}/points/{pointId}/complete")
    suspend fun completeDeliveryPoint(
        @Path("deliveryId") deliveryId: Int,
        @Path("pointId") pointId: Int,
        @Body request: DeliveryPointCompletionRequest
    ): Response<DeliveryPointCompletion>
    
    @POST("deliveries/{deliveryId}/points/{pointId}/photo")
    suspend fun uploadDeliveryPhoto(
        @Path("deliveryId") deliveryId: Int,
        @Path("pointId") pointId: Int,
        @Part photo: MultipartBody.Part
    ): Response<Map<String, String>>
    
    @POST("deliveries/{deliveryId}/complete")
    suspend fun completeDelivery(@Path("deliveryId") deliveryId: Int): Response<DeliveryHistory>
    
    @GET("deliveries/{deliveryId}/progress")
    suspend fun getDeliveryProgress(@Path("deliveryId") deliveryId: Int): Response<DeliveryProgress>
    
    @GET("drivers/{driverId}/deliveries/history")
    suspend fun getDriverDeliveryHistory(@Path("driverId") driverId: Int): Response<List<DeliveryHistory>>
    
    @GET("deliveries/history")
    suspend fun getAllDeliveryHistory(): Response<List<DeliveryHistory>>
    
    @POST("users/register_driver")
    suspend fun registerDriver(@Body request: RegisterDriverRequest): Response<UserData>

    @POST("delivery-points/{id}/complete")
    suspend fun completeDeliveryPoint(@Path("id") id: Int): Response<DeliveryPoint>

    @POST("delivery-points/{id}/start")
    suspend fun startDeliveryPoint(@Path("id") id: Int): Response<DeliveryPoint>

    @POST("delivery-points/{id}/progress")
    suspend fun updateDeliveryProgress(@Path("id") id: Int, @Body progress: DeliveryPointCompletionRequest): Response<DeliveryPoint>

    @GET("delivery-points/{id}/history")
    suspend fun getDeliveryHistory(@Path("id") id: Int): Response<List<DeliveryHistory>>

    @POST("delivery-points/{id}/image")
    suspend fun uploadDeliveryPointImage(@Path("id") id: Int, @Part image: MultipartBody.Part, @Part("description") description: RequestBody): Response<DeliveryPoint>
} 