package com.example.projetoapprotas.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun BotaoVoltar(
    onVoltarClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onVoltarClick,
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(4.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar"
        )
        Spacer(modifier = Modifier.width(4.dp))
    }
}
