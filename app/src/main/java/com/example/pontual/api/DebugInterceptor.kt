package com.example.pontual.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DebugInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        Log.d("API_DEBUG", "URL: ${request.url}")
        Log.d("API_DEBUG", "Method: ${request.method}")
        Log.d("API_DEBUG", "Headers: ${request.headers}")
        
        val response = chain.proceed(request)
        
        Log.d("API_DEBUG", "Response Code: ${response.code}")
        Log.d("API_DEBUG", "Response Message: ${response.message}")
        Log.d("API_DEBUG", "Response Headers: ${response.headers}")
        
        return response
    }
} 