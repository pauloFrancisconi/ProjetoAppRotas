package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class Delivery(
    val id: Int,
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("route_name")
    val routeName: String,
    @SerializedName("driver_id")
    val driverId: Int,
    @SerializedName("driver_name")
    val driverName: String,
    @SerializedName("delivery_point_id")
    val deliveryPointId: Int,
    @SerializedName("delivery_point_name")
    val deliveryPointName: String,
    @SerializedName("delivery_point_address")
    val deliveryPointAddress: String,
    val status: DeliveryStatus,
    @SerializedName("scheduled_date")
    val scheduledDate: String,
    @SerializedName("completed_date")
    val completedDate: String?,
    val notes: String?,
    val signature: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

enum class DeliveryStatus(val value: String) {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELLED("cancelled")
}

data class DeliveryStatusUpdate(
    val status: DeliveryStatus,
    val notes: String? = null,
    val signature: String? = null
)

data class DeliveryRequest(
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("driver_id")
    val driverId: Int? = null,
    @SerializedName("scheduled_date")
    val scheduledDate: String,
    val notes: String? = null
) 