// ui/telas/admin/AdminStats.kt
package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AdminStats(
    pontos: Int,
    rotas: Int,
    motoristas: Int,
    isLoading: Boolean,
    error: String?,
    compact: Boolean,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(if (compact) 16.dp else 20.dp)) {
            Text(
                "Resumo do Sistema",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = if (compact) 16.sp else 18.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(if (compact) 12.dp else 16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
                error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = onRetry) { Text("Tentar novamente") }
                    }
                }
                else -> {
                    if (compact) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatsCardSmall(
                                    icon = Icons.Default.LocationOn,
                                    title = "Pontos",
                                    value = pontos,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f)
                                )
                                StatsCardSmall(
                                    icon = Icons.Default.Settings,
                                    title = "Rotas",
                                    value = rotas,
                                    color = Color(0xFF2196F3),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            StatsCardSmall(
                                icon = Icons.Default.Person,
                                title = "Motoristas",
                                value = motoristas,
                                color = Color(0xFF9C27B0),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatsCardLarge(
                                icon = Icons.Default.LocationOn,
                                title = "Pontos",
                                value = pontos,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                            StatsCardLarge(
                                icon = Icons.Default.Settings,
                                title = "Rotas",
                                value = rotas,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.weight(1f)
                            )
                            StatsCardLarge(
                                icon = Icons.Default.Person,
                                title = "Motoristas",
                                value = motoristas,
                                color = Color(0xFF9C27B0),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/* --- Auxiliares de card de estatística ---------------------------- */

@Composable
private fun StatsCardSmall(
    icon: ImageVector,
    title: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape  = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("$value", style = MaterialTheme.typography.titleMedium)
                Text(title, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StatsCardLarge(
    icon: ImageVector,
    title: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape  = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text("$value", style = MaterialTheme.typography.titleLarge)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
