package com.example.pontual.api

import com.example.pontual.api.models.UserData
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserData?
) 