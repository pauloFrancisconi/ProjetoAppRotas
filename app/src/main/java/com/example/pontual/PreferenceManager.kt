package com.example.pontual

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREF_NAME = "PontualPrefs"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_PASSWORD = "user_password"
    private const val KEY_TOKEN = "user_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_ASSIGNED_ROUTE_ID = "assigned_route_id"
    private const val KEY_ASSIGNED_ROUTE_NAME = "assigned_route_name"
    private const val KEY_COMPLETED_POINTS = "completed_points"
    private const val KEY_POINT_PHOTOS = "point_photos"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserCredentials(context: Context, email: String, password: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun saveUserData(context: Context, token: String, userId: Int, userName: String, userRole: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_USER_ROLE, userRole)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getUserEmail(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getUserPassword(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_PASSWORD, null)
    }

    fun getToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserId(context: Context): Int {
        val prefs = getSharedPreferences(context)
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun getUserName(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun getUserRole(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isAdmin(context: Context): Boolean {
        val role = getUserRole(context)
        return role == "admin"
    }

    fun clearUserData(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().clear().apply()
    }

    fun logout(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_ROLE)
            .remove(KEY_ASSIGNED_ROUTE_ID)
            .remove(KEY_ASSIGNED_ROUTE_NAME)
            .apply()
    }

    fun assignRoute(context: Context, routeId: Int, routeName: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putInt(KEY_ASSIGNED_ROUTE_ID, routeId)
            .putString(KEY_ASSIGNED_ROUTE_NAME, routeName)
            .apply()
    }

    fun getAssignedRouteId(context: Context): Int? {
        val prefs = getSharedPreferences(context)
        val routeId = prefs.getInt(KEY_ASSIGNED_ROUTE_ID, -1)
        return if (routeId == -1) null else routeId
    }

    fun getAssignedRouteName(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_ASSIGNED_ROUTE_NAME, null)
    }

    fun hasAssignedRoute(context: Context): Boolean {
        return getAssignedRouteId(context) != null
    }

    fun finishRoute(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .remove(KEY_ASSIGNED_ROUTE_ID)
            .remove(KEY_ASSIGNED_ROUTE_NAME)
            .remove(KEY_COMPLETED_POINTS)
            .remove(KEY_POINT_PHOTOS)
            .apply()
    }

    fun isDriver(context: Context): Boolean {
        val role = getUserRole(context)
        return role == "user" || role == "driver"
    }

    fun completePoint(context: Context, pointId: Int, photoUri: String? = null) {
        val prefs = getSharedPreferences(context)
        val completedPoints = getCompletedPoints(context).toMutableSet()
        completedPoints.add(pointId)
        
        prefs.edit()
            .putStringSet(KEY_COMPLETED_POINTS, completedPoints.map { it.toString() }.toSet())
            .apply()
            
        if (photoUri != null) {
            savePointPhoto(context, pointId, photoUri)
        }
    }

    fun isPointCompleted(context: Context, pointId: Int): Boolean {
        return getCompletedPoints(context).contains(pointId)
    }

    fun getCompletedPoints(context: Context): Set<Int> {
        val prefs = getSharedPreferences(context)
        return prefs.getStringSet(KEY_COMPLETED_POINTS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }

    fun getCompletedPointsCount(context: Context): Int {
        return getCompletedPoints(context).size
    }

    fun areAllPointsCompleted(context: Context, totalPoints: Int): Boolean {
        return getCompletedPointsCount(context) >= totalPoints
    }

    private fun savePointPhoto(context: Context, pointId: Int, photoUri: String) {
        val prefs = getSharedPreferences(context)
        val currentPhotos = getPointPhotos(context).toMutableMap()
        currentPhotos[pointId] = photoUri
        
        val photosString = currentPhotos.entries.joinToString(";") { "${it.key}:${it.value}" }
        prefs.edit()
            .putString(KEY_POINT_PHOTOS, photosString)
            .apply()
    }

    fun getPointPhoto(context: Context, pointId: Int): String? {
        return getPointPhotos(context)[pointId]
    }

    private fun getPointPhotos(context: Context): Map<Int, String> {
        val prefs = getSharedPreferences(context)
        val photosString = prefs.getString(KEY_POINT_PHOTOS, "") ?: ""
        
        if (photosString.isEmpty()) return emptyMap()
        
        return photosString.split(";")
            .mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    parts[0].toIntOrNull()?.let { it to parts[1] }
                } else null
            }
            .toMap()
    }
} 