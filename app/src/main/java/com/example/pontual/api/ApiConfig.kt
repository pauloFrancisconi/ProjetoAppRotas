package com.example.pontual.api

object ApiConfig {
    const val BASE_URL_EMULATOR = "http://10.0.2.2:3000/api/v1/"
    const val BASE_URL_DEVICE = "http://192.168.1.100:3000/"
    const val BASE_URL_PRODUCTION = "https://api.pontual.com/"

    const val BASE_URL = BASE_URL_EMULATOR
    
    // Autenticação
    const val LOGIN_ENDPOINT = "user/login"
    const val LOGOUT_ENDPOINT = "user/logout"
    const val REFRESH_TOKEN_ENDPOINT = "user/refresh"
    
    // Usuários
    const val USERS_ENDPOINT = "users"
    const val USER_PROFILE_ENDPOINT = "users/profile"
    
    // Pontos de Entrega
    const val DELIVERY_POINTS_ENDPOINT = "delivery-points"
    
    // Rotas
    const val ROUTES_ENDPOINT = "routes"
    const val ROUTE_ASSIGN_ENDPOINT = "routes/assign"
    
    // Motoristas
    const val DRIVERS_ENDPOINT = "drivers"
    const val DRIVER_ROUTES_ENDPOINT = "drivers/{driverId}/routes"
    
    // Entregas
    const val DELIVERIES_ENDPOINT = "deliveries"
    const val DELIVERY_STATUS_ENDPOINT = "deliveries/{deliveryId}/status"
    
    const val TIMEOUT_SECONDS = 30L
} 