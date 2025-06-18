package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class Route(
    val id: Int,
    val name: String,
    val description: String?,
    val points: List<RoutePoint>,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("driver_id")
    val driverId: Int?,
    @SerializedName("driver_name")
    val driverName: String?,
    @SerializedName("estimated_duration")
    val estimatedDuration: Int?,
    @SerializedName("total_distance")
    val totalDistance: Double?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class RoutePoint(
    val id: Int,
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("delivery_point_id")
    val deliveryPointId: Int,
    val sequence: Int,
    @SerializedName("delivery_point")
    val deliveryPoint: DeliveryPoint
)

data class RouteRequest(
    val name: String,
    val description: String? = null,
    @SerializedName("point_ids")
    val pointIds: List<Int>,
    @SerializedName("estimated_duration")
    val estimatedDuration: Int? = null
)

data class RouteAssignmentRequest(
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("driver_id")
    val driverId: Int
) 