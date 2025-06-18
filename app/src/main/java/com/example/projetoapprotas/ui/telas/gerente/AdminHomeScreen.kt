package com.example.projetoapprotas.ui.telas.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetoapprotas.data.remote.RetrofitFactory
import com.example.projetoapprotas.data.repository.ResumoSistemaRepository
import com.example.projetoapprotas.ui.telas.gerente.AdminHeader
import com.example.projetoapprotas.ui.telas.gerente.AdminStats
import com.example.projetoapprotas.viewmodel.AdminHomeViewModel

@Composable
fun TelaAdminHome(
    baseUrl: String,
    nomeAdmin: String,
    onCadastroPontosClick: () -> Unit,
    onCadastroRotasClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPerfilClick: () -> Unit = {},
) {
    val api = remember(baseUrl) { RetrofitFactory.createApiService(baseUrl) }
    val repository = remember { ResumoSistemaRepository(api) }
    val viewModel: AdminHomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AdminHomeViewModel(repository) as T
        }
    )

    val state by viewModel.state.collectAsState()
    val scroll = rememberScrollState()
    val config = LocalConfiguration.current
    val compact = config.screenWidthDp.dp < 600.dp

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = if (compact) 16.dp else 24.dp, vertical = 16.dp)
        ) {
            /* Header --------------------------------------------------- */
            AdminHeader(
                saudacao = viewModel.saudacao,
                nomeAdmin = nomeAdmin,
                dataExtenso = viewModel.dataExtenso,
                compact = compact,
                onLogoutClick = onLogoutClick,
                onPerfilClick = onPerfilClick
            )

            Spacer(Modifier.height(if (compact) 16.dp else 24.dp))

            AdminStats(
                pontos = state.pontos,
                rotas = state.rotas,
                motoristas = state.motoristas,
                isLoading = state.isLoading,
                error = state.error,
                compact = compact,
                onRetry = viewModel::atualizarResumo
            )

            Spacer(Modifier.height(if (compact) 24.dp else 32.dp))

            FuncionalidadeCards(
                compact = compact,
                onCadastroPontosClick = onCadastroPontosClick,
                onCadastroRotasClick = onCadastroRotasClick,
                onRelatoriosClick = onRelatoriosClick,
                pontosAtivos = state.pontos,
                rotasConfiguradas = state.rotas,
            )
        }
    }
}
