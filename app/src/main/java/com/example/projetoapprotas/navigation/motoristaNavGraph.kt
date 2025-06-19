package com.example.projetoapprotas.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.projetoapprotas.ui.telas.motorista.TelaHomeMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaRotaDiaMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaSemRotaDiaMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaRotaFinalizadaMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaCapturaFoto
import com.example.projetoapprotas.ui.telas.motorista.TelaConfiguracoes
import com.example.projetoapprotas.ui.telas.motorista.TelaSobre
import com.example.projetoapprotas.ui.telas.motorista.TelaPerfilMotorista
import com.example.projetoapprotas.ui.telas.motorista.TelaRelatoriosMotorista
import com.example.projetoapprotas.ui.telas.login.LoginViewModel
import androidx.core.content.edit

fun NavGraphBuilder.motoristaNavGraph(
    navController: NavController,
    loginViewModel: LoginViewModel? = null
) {
    navigation(startDestination = "motorista_home", route = "motorista") {
        composable("motorista_home") {
            val context = LocalContext.current
            TelaHomeMotorista(
                onRotaDiaClick = { navController.navigate("rota_do_dia") },
                onRelatoriosClick = { navController.navigate("motorista_relatorios") },
                onPerfilClick = { navController.navigate("motorista_perfil") },
                onConfiguracoesClick = { navController.navigate("motorista_configuracoes") },
                onSobreClick = { navController.navigate("motorista_sobre") },
                onLogoutClick = {
                    // Limpa os dados do LoginViewModel
                    loginViewModel?.let { viewModel ->
                        viewModel.clearLoginData()
                    }

                    // Limpar SharedPreferences
                    context.getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
                        .edit() {
                            clear()
                        }

                    // Navegar para login limpando todo o stack
                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }

        composable("rota_do_dia") {
            TelaRotaDiaMotorista(
                onVoltarClick = {
                    navController.popBackStack()
                },
                onEnviarFoto = { pontoId, nomePonto ->
                    // Navega para tela de captura de foto passando os parâmetros
                    navController.navigate("captura_foto/$pontoId/$nomePonto")
                },
                onRegistrarPonto = { pontoId ->
                    // Lógica para registrar ponto
                    // Pode mostrar um SnackBar ou Toast
                },
                onFinalizarRota = {
                    navController.navigate("motorista_rota_finalizada")
                }
            )
        }

        // Nova rota para captura de foto
        composable("captura_foto/{pontoId}/{nomePonto}") { backStackEntry ->
            val pontoId = backStackEntry.arguments?.getString("pontoId") ?: ""
            val nomePonto = backStackEntry.arguments?.getString("nomePonto") ?: ""

            TelaCapturaFoto(
                pontoId = pontoId,
                nomePonto = nomePonto,
                onFotoCapturada = { fotoComLocalizacao ->
                    // Aqui você pode salvar a foto no banco de dados
                    // ou processar conforme necessário

                    // Por enquanto, volta para a tela de rota
                    navController.popBackStack()

                    // Opcional: Mostrar mensagem de sucesso
                    // Você pode implementar um sistema de mensagens ou SnackBar
                },
                onVoltarClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("motorista_sem_rota") {
            TelaSemRotaDiaMotorista(
                onVoltarHome = {
                    navController.popBackStack()
                },
                onBuscarRotas = {
                    navController.popBackStack()
                }
            )
        }

        composable("motorista_rota_finalizada") {
            TelaRotaFinalizadaMotorista(
                onVoltarHome = {
                    navController.navigate("motorista_home") {
                        popUpTo("motorista_home") { inclusive = true }
                    }
                },
                onNovaRota = {
                    navController.navigate("rota_do_dia") {
                        popUpTo("motorista_home")
                    }
                }
            )
        }

        // Nova tela de configurações
        composable("motorista_configuracoes") {
            TelaConfiguracoes(
                onVoltarClick = { navController.popBackStack() }
            )
        }

        // Tela Sobre
        composable("motorista_sobre") {
            TelaSobre(
                onVoltarClick = { navController.popBackStack() }
            )
        }

        // Tela Perfil do Motorista
        composable("motorista_perfil") {
            TelaPerfilMotorista(
                onVoltarClick = { navController.popBackStack() },
                onSalvarClick = { perfilAtualizado ->
                    // Aqui você pode implementar a lógica para salvar o perfil
                    // Por exemplo, salvar no banco de dados local ou sincronizar com servidor

                    // Exemplo de implementação futura:
                    // viewModel.salvarPerfil(perfilAtualizado)

                    println("Perfil salvo: $perfilAtualizado")
                },
                // Você pode passar dados do perfil atual do usuário aqui
                // perfilAtual = viewModel.perfilAtual.value ?: PerfilMotorista()
            )
        }

        // Tela Relatórios do Motorista
        composable("motorista_relatorios") {
            TelaRelatoriosMotorista(
                onVoltarClick = { navController.popBackStack() }
            )
        }
    }
}