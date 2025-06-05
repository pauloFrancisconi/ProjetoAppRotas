package com.example.projetoapprotas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        //startDestination = "admin"
        startDestination = "motorista"
    ) {

        //adminNavGraph(navController)
        motoristaNavGraph(navController)
    }
}
