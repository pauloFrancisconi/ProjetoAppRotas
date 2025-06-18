package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class DeliveryPoint(
    val id: Int,
    val name: String,
    val address: String,
    @SerializedName("contact_name")
    val contactName: String?,
    @SerializedName("contact_phone")
    val contactPhone: String?,
    val notes: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class DeliveryPointRequest(
    val name: String,
    val address: String,
    @SerializedName("contact_name")
    val contactName: String? = null,
    @SerializedName("contact_phone")
    val contactPhone: String? = null,
    val notes: String? = null
) 