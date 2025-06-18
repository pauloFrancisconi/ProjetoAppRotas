package com.example.projetoapprotas.ui.telas.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FuncionalidadeCards(
    compact: Boolean,
    pontosAtivos: Int,
    rotasConfiguradas: Int,
    onCadastroPontosClick: () -> Unit,
    onCadastroRotasClick: () -> Unit,
    onRelatoriosClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp)) {

        FeatureCard(
            title       = "Cadastro de Pontos",
            description = "Cadastre novos pontos de coleta com localização precisa e informações detalhadas",
            icon        = Icons.Default.Place,
            stats       = "$pontosAtivos pontos ativos",
            colors      = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
            compact     = compact,
            onClick     = onCadastroPontosClick
        )

        FeatureCard(
            title       = "Cadastro de Rotas",
            description = "Defina rotas otimizadas e atribua motoristas para cada dia da semana",
            icon        = Icons.Default.Build,
            stats       = "$rotasConfiguradas rotas configuradas",
            colors      = listOf(Color(0xFF2196F3), Color(0xFF1565C0)),
            compact     = compact,
            onClick     = onCadastroRotasClick
        )

        FeatureCard(
            title       = "Relatórios Executivos",
            description = "Visualize relatórios detalhados de performance e análises das operações",
            icon        = Icons.Default.Menu,
            stats       = "Últimos 30 dias",
            colors      = listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A)),
            compact     = compact,
            onClick     = onRelatoriosClick
        )
    }
}


@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    stats: String,
    colors: List<Color>,
    compact: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = if (compact) 6.dp else 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape  = MaterialTheme.shapes.large
    ) {
        val padding = if (compact) 20.dp else 24.dp
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors.map { it.copy(alpha = 0.1f) }))
                .padding(padding)
        ) {
            if (compact) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FeatureIconBox(icon, colors.first(), 48.dp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                    Spacer(Modifier.height(12.dp))
                    FeatureStatsBadge(stats, colors.first())
                }
            } else {
                /* ------------------ Layout grande -------------------- */
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FeatureIconBox(icon, colors.first(), 64.dp)
                    Spacer(Modifier.width(20.dp))
                    Column(Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleLarge)
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        FeatureStatsBadge(stats, colors.first())
                    }
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureIconBox(icon: ImageVector, bgColor: Color, size: Dp) {
    Card(
        modifier = Modifier.size(size),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape  = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

@Composable
private fun FeatureStatsBadge(text: String, accent: Color) {
    Card(
        shape  = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = .2f))
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = accent,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
