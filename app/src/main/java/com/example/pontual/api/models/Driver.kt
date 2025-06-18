package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class Driver(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("license_number")
    val licenseNumber: String,
    @SerializedName("vehicle_plate")
    val vehiclePlate: String?,
    @SerializedName("vehicle_model")
    val vehicleModel: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("current_route_id")
    val currentRouteId: Int?,
    @SerializedName("current_route_name")
    val currentRouteName: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class DriverRequest(
    val name: String,
    val email: String,
    val phone: String? = null,
    @SerializedName("license_number")
    val licenseNumber: String? = null,
    @SerializedName("vehicle_plate")
    val vehiclePlate: String? = null,
    @SerializedName("vehicle_model")
    val vehicleModel: String? = null
)

data class RegisterDriverRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    val phone: String? = null,
    @SerializedName("license_number")
    val licenseNumber: String? = null,
    @SerializedName("vehicle_plate")
    val vehiclePlate: String? = null,
    @SerializedName("vehicle_model")
    val vehicleModel: String? = null
) 