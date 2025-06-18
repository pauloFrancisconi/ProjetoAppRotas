package com.example.pontual.navigation

import androidx.compose.runtime.*
import com.example.pontual.screens.DeliveryPointFormScreen
import com.example.pontual.screens.DeliveryPointsScreen
import com.example.pontual.screens.RoutesScreen
import com.example.pontual.screens.DriversScreen
import com.example.pontual.screens.DeliveriesScreen
import com.example.pontual.screens.RouteFormScreen
import com.example.pontual.screens.DriverFormScreen
import com.example.pontual.screens.DeliveryFormScreen

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object DeliveryPoints : Screen()
    object DeliveryPointForm : Screen()
    object Routes : Screen()
    object RouteForm : Screen()
    object Drivers : Screen()
    object DriverForm : Screen()
    object Deliveries : Screen()
    object DeliveryForm : Screen()
    object AvailableRoutes : Screen()
    object MyRoute : Screen()
}

data class ScreenWithParams(
    val screen: Screen,
    val params: Map<String, Any> = emptyMap()
)

@Composable
fun AppNavigation(
    currentScreen: ScreenWithParams,
    onNavigate: (ScreenWithParams) -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    when (currentScreen.screen) {
        is Screen.Login -> {
            com.example.pontual.LoginScreen(
                onLoginSuccess = { onNavigate(ScreenWithParams(Screen.Home)) }
            )
        }
        
        is Screen.Home -> {
            com.example.pontual.HomeScreen(
                onLogout = onLogout,
                onNavigateToDeliveryPoints = { onNavigate(ScreenWithParams(Screen.DeliveryPoints)) },
                onNavigateToRoutes = { onNavigate(ScreenWithParams(Screen.Routes)) },
                onNavigateToDrivers = { onNavigate(ScreenWithParams(Screen.Drivers)) },
                onNavigateToDeliveries = { onNavigate(ScreenWithParams(Screen.Deliveries)) },
                onNavigateToAvailableRoutes = { onNavigate(ScreenWithParams(Screen.AvailableRoutes)) },
                onNavigateToMyRoute = { onNavigate(ScreenWithParams(Screen.MyRoute)) }
            )
        }
        
        is Screen.DeliveryPoints -> {
            DeliveryPointsScreen(
                onNavigateToCreate = { onNavigate(ScreenWithParams(Screen.DeliveryPointForm)) },
                onNavigateToEdit = { pointId ->
                    onNavigate(ScreenWithParams(Screen.DeliveryPointForm, mapOf("pointId" to pointId)))
                },
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.DeliveryPointForm -> {
            val pointId = currentScreen.params["pointId"] as? Int
            DeliveryPointFormScreen(
                pointId = pointId,
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.Routes -> {
            RoutesScreen(
                onNavigateToCreate = { onNavigate(ScreenWithParams(Screen.RouteForm)) },
                onNavigateToEdit = { routeId ->
                    onNavigate(ScreenWithParams(Screen.RouteForm, mapOf("routeId" to routeId)))
                },
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.RouteForm -> {
            val routeId = currentScreen.params["routeId"] as? Int
            RouteFormScreen(
                routeId = routeId,
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.Drivers -> {
            DriversScreen(
                onNavigateToCreate = { onNavigate(ScreenWithParams(Screen.DriverForm)) },
                onNavigateToEdit = { driverId ->
                    onNavigate(ScreenWithParams(Screen.DriverForm, mapOf("driverId" to driverId)))
                },
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.DriverForm -> {
            val driverId = currentScreen.params["driverId"] as? Int
            DriverFormScreen(
                driverId = driverId,
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.Deliveries -> {
            DeliveriesScreen(
                onNavigateToCreate = { onNavigate(ScreenWithParams(Screen.DeliveryForm)) },
                onNavigateToEdit = { deliveryId ->
                    onNavigate(ScreenWithParams(Screen.DeliveryForm, mapOf("deliveryId" to deliveryId)))
                },
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.DeliveryForm -> {
            val deliveryId = currentScreen.params["deliveryId"] as? Int
            DeliveryFormScreen(
                deliveryId = deliveryId,
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.AvailableRoutes -> {
            com.example.pontual.screens.AvailableRoutesScreen(
                onNavigateBack = onNavigateBack
            )
        }
        
        is Screen.MyRoute -> {
            com.example.pontual.screens.MyRouteScreen(
                onNavigateBack = onNavigateBack
            )
        }
    }
} 