package com.example.pontual.api.models

import com.google.gson.annotations.SerializedName

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("is_active")
    val isActive: Boolean
) 