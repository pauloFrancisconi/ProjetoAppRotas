package com.example.projetoapprotas.ui.telas.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TelaAdminHome(
    onCadastroPontosClick: () -> Unit,
    onCadastroRotasClick: () -> Unit,
    onRelatoriosClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)


    ) {
        Text(
            text = "Bem-vindo, Gerente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        AdminCard(
            title = "Cadastro de Pontos",
            description = "Cadastre novos pontos de parada com nome, observações e localização.",
            onClick = onCadastroPontosClick
        )

        AdminCard(
            title = "Cadastro de Rotas",
            description = "Defina rotas com pontos e atribua motoristas para cada dia da semana.",
            onClick = onCadastroRotasClick
        )

        AdminCard(
            title = "Relatórios",
            description = "Visualize relatórios detalhados das rotas realizadas por motorista.",
            onClick = onRelatoriosClick
        )
    }
}

@Composable
fun AdminCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
