package com.example.projetoapprotas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.projetoapprotas.ui.telas.admin.TelaAdminHome
import com.example.projetoapprotas.ui.telas.gerente.TelaCadastroPontos
import com.example.projetoapprotas.ui.telas.gerente.TelaCadastroRotas
import com.example.projetoapprotas.ui.telas.gerente.TelaRelatorios
import android.util.Log
import com.example.projetoapprotas.ui.telas.gerente.TelaAdicionarPonto


fun NavGraphBuilder.adminNavGraph(navController: NavController) {
    navigation(startDestination = "admin_home", route = "admin") {
        composable("admin_home") {
            TelaAdminHome(
                onCadastroPontosClick = { navController.navigate("admin_cadastro_pontos") },
                onCadastroRotasClick = { navController.navigate("admin_cadastro_rotas") },
                onRelatoriosClick = { navController.navigate("admin_relatorios") }
            )
        }
        composable("admin_cadastro_pontos") {
            TelaCadastroPontos(
                onVoltarClick = { navController.popBackStack() },
                onAdicionarPonto = { navController.navigate("admin_adicionar_ponto") } // navega para a tela adicionar ponto
            )
        }
        composable("admin_adicionar_ponto") {
            TelaAdicionarPonto(
                onVoltarClick = { navController.popBackStack() }
            )
        }
        composable("admin_cadastro_rotas") {
            TelaCadastroRotas(
                onVoltarClick = { navController.popBackStack() }
            )
        }
        composable("admin_relatorios") {
            TelaRelatorios(
                onVoltarClick = { navController.popBackStack() }
            )
        }
    }
}


