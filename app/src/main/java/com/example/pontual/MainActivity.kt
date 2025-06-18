package com.example.pontual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.pontual.navigation.AppNavigation
import com.example.pontual.navigation.Screen
import com.example.pontual.navigation.ScreenWithParams
import com.example.pontual.ui.theme.PontualTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PontualTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<ScreenWithParams>(ScreenWithParams(Screen.Login)) }
    var screenStack by remember { mutableStateOf(listOf<ScreenWithParams>()) }

    LaunchedEffect(Unit) {
        if (PreferenceManager.isLoggedIn(context)) {
            currentScreen = ScreenWithParams(Screen.Home)
            screenStack = listOf(ScreenWithParams(Screen.Home))
        }
    }

    fun navigateTo(screenWithParams: ScreenWithParams) {
        screenStack = screenStack + screenWithParams
        currentScreen = screenWithParams
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
            currentScreen = screenStack.last()
        } else {
            if (currentScreen.screen != Screen.Home) {
                currentScreen = ScreenWithParams(Screen.Home)
                screenStack = listOf(ScreenWithParams(Screen.Home))
            }
        }
    }

    fun logout() {
        PreferenceManager.logout(context)
        currentScreen = ScreenWithParams(Screen.Login)
        screenStack = listOf(ScreenWithParams(Screen.Login))
    }

    AppNavigation(
        currentScreen = currentScreen,
        onNavigate = { screenWithParams -> navigateTo(screenWithParams) },
        onNavigateBack = { navigateBack() },
        onLogout = { logout() }
    )
}