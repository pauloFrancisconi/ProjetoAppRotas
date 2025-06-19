package com.example.projetoapprotas.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetoapprotas.ui.telas.login.LoginViewModel
import com.example.projetoapprotas.ui.telas.login.TelaLogin

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Tela de Login
        composable("login") {
            TelaLogin(
                viewModel = loginViewModel,
                onCadastroClick = {
                    navController.navigate("cadastro")
                },
                onLoginSuccess = { cargo ->
                    navController.navigate(cargo) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // NavGraphs
        adminNavGraph(
            navController = navController,
            loginViewModel = loginViewModel
        )
        motoristaNavGraph(
            navController = navController,
            loginViewModel = loginViewModel
        )
    }
}
