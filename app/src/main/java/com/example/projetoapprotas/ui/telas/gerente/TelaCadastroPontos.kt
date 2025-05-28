package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projetoapprotas.ui.componentes.BotaoVoltar

@Composable
fun TelaCadastroPontos(onVoltarClick: () -> Unit, onAdicionarPonto: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            BotaoVoltar(onVoltarClick = onVoltarClick)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pontos Cadastrados",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            BotaoAdicionarPonto(onAdicionarPonto = onAdicionarPonto)
        }
    }
}


@Composable
fun BotaoAdicionarPonto(
    onAdicionarPonto: () -> Unit,
    texto: String = "Adicionar Ponto"
) {
    Button(
        onClick = onAdicionarPonto,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5C7AEA),
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}