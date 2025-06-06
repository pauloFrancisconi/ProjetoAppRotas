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
    NavHost(
        navController = navController,
        startDestination = "admin"       // Para testar telas do admin
        //startDestination = "motorista" // Para testar telas do motorista
        //startDestination = "login"     // Para testar o Login
    )
    {

        adminNavGraph(navController)
        //motoristaNavGraph(navController)
    }

//    {
//        // Tela de Login
//        composable("login") {
//            TelaLogin(
//                viewModel = viewModel<LoginViewModel>(),
//                onCadastroClick = {
//                    // Navegar para a tela de cadastro
//                    navController.navigate("cadastro")
//                },
//                onLoginSuccess = {
//                    // Navegar para a tela de admin (ou próxima tela após login)
//                    navController.navigate("admin") {
//                        // Evitar que o usuário possa voltar para a tela de login
//                        popUpTo("login") { inclusive = true }
//                    }
//                },
//                onEsqueceuSenhaClick = {
//                    // Navegar para a tela de recuperação de senha
//                    navController.navigate("recuperar_senha")
//                }
//            )
//        }
//    }
}
