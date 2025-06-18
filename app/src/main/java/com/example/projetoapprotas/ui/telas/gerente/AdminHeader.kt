package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminHeader(
    saudacao: String,
    nomeAdmin: String,
    dataExtenso: String,
    compact: Boolean,
    onLogoutClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape    = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(if (compact) 6.dp else 10.dp)
    ) {
        val padding = if (compact) 16.dp else 24.dp
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(if (compact) 48.dp else 64.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.width(if (compact) 12.dp else 16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = "$saudacao, $nomeAdmin!",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = if (compact) 18.sp else 20.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = dataExtenso,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "Sistema de Gestão de Rotas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Sair",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
