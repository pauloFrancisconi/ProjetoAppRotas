package com.example.projetoapprotas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.projetoapprotas.ui.telas.motorista.TelaHomeMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaRotaDiaMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaSemRotaDiaMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaRotaFinalizadaMotorista

fun NavGraphBuilder.motoristaNavGraph(navController: NavController) {
    navigation(startDestination = "motorista_home", route = "motorista") {
        composable("motorista_home") {
            TelaHomeMotorista(
                onRotaDiaClick = { navController.navigate("rota_do_dia") },
                onRelatoriosClick = { navController.navigate("motorista_relatorios") },
                onSemRotaClick = { navController.navigate("motorista_sem_rota") },
                onRotaFinalizadaClick = { navController.navigate("motorista_rota_finalizada") },
            )
        }

        composable("rota_do_dia") {
            TelaRotaDiaMotorista()
        }

        composable("motorista_sem_rota") {
            TelaSemRotaDiaMotorista()
        }

        composable("motorista_rota_finalizada") {
            TelaRotaFinalizadaMotorista()
        }
    }
}
