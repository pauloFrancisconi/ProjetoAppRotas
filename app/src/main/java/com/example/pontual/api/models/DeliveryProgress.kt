package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class DeliveryPointCompletion(
    val id: Int,
    @SerializedName("delivery_id")
    val deliveryId: Int,
    @SerializedName("delivery_point_id")
    val deliveryPointId: Int,
    @SerializedName("delivery_point_name")
    val deliveryPointName: String,
    @SerializedName("delivery_point_address")
    val deliveryPointAddress: String,
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("driver_name")
    val driverName: String,
    @SerializedName("completed_at")
    val completedAt: String,
    @SerializedName("photo_url")
    val photoUrl: String?,
    val notes: String?,
    val signature: String?,
    @SerializedName("created_at")
    val createdAt: String
)

data class DeliveryHistory(
    val id: Int,
    @SerializedName("delivery_id")
    val deliveryId: Int,
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("driver_name")
    val driverName: String,
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("route_name")
    val routeName: String,
    val status: String,
    @SerializedName("action_date")
    val actionDate: String,
    val notes: String?,
    @SerializedName("total_points")
    val totalPoints: Int,
    @SerializedName("completed_points")
    val completedPoints: Int,
    @SerializedName("total_distance")
    val totalDistance: Double?,
    @SerializedName("total_duration")
    val totalDuration: Int?
)

data class DeliveryProgress(
    @SerializedName("delivery_id")
    val deliveryId: Int,
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("route_name")
    val routeName: String,
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("driver_name")
    val driverName: String,
    @SerializedName("total_points")
    val totalPoints: Int,
    @SerializedName("completed_points")
    val completedPoints: Int,
    @SerializedName("progress_percentage")
    val progressPercentage: Double,
    val status: String,
    @SerializedName("started_at")
    val startedAt: String,
    @SerializedName("completed_at")
    val completedAt: String?,
    @SerializedName("completed_points_list")
    val completedPointsList: List<DeliveryPointCompletion>
)

data class DeliveryPointCompletionRequest(
    @SerializedName("delivery_id")
    val deliveryId: Int,
    @SerializedName("delivery_point_id")
    val deliveryPointId: Int,
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    val notes: String? = null,
    val signature: String? = null
) 