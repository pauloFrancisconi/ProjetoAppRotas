package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projetoapprotas.ui.componentes.BotaoVoltar

// Modelo de dados para exemplo
data class PontoCadastrado(
    val id: String,
    val nome: String,
    val endereco: String,
    val tipo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroPontos(
    onVoltarClick: () -> Unit,
    onAdicionarPonto: () -> Unit,
    pontosCadastrados: List<PontoCadastrado> = listOf(
        PontoCadastrado("1", "Terminal Central", "Av. Principal, 123", "Terminal"),
        PontoCadastrado("2", "Praça da Cidade", "Rua das Flores, 456", "Parada"),
        PontoCadastrado("3", "Shopping Center", "Av. Comercial, 789", "Parada")
    )
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F9FA),
            Color(0xFFE9ECEF)
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header com botão voltar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotaoVoltar(onVoltarClick = onVoltarClick)

                    Spacer(modifier = Modifier.weight(1f))

                    // Badge com contador
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF5C7AEA).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "${pontosCadastrados.size} pontos",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5C7AEA),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título principal
                Text(
                    text = "Gerenciar Pontos",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Text(
                    text = "Gerencie os pontos de parada das rotas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF718096),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botão adicionar ponto
                BotaoAdicionarPonto(
                    onAdicionarPonto = onAdicionarPonto,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de pontos
                if (pontosCadastrados.isNotEmpty()) {
                    Text(
                        text = "Pontos Cadastrados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(pontosCadastrados) { ponto ->
                            CardPonto(
                                ponto = ponto,
                                onEditarClick = { /* Implementar edição */ },
                                onExcluirClick = { /* Implementar exclusão */ }
                            )
                        }
                    }
                } else {
                    // Estado vazio
                    EstadoVazio(
                        onAdicionarPonto = onAdicionarPonto,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BotaoAdicionarPonto(
    onAdicionarPonto: () -> Unit,
    texto: String = "Adicionar Novo Ponto",
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onAdicionarPonto,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF5C7AEA).copy(alpha = 0.25f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5C7AEA),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPonto(
    ponto: PontoCadastrado,
    onEditarClick: () -> Unit,
    onExcluirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Ícone do tipo de ponto
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF5C7AEA).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF5C7AEA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ponto.nome,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )
                        Text(
                            text = ponto.endereco,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF718096),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        // Badge do tipo
                        Surface(
                            color = when (ponto.tipo) {
                                "Terminal" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                else -> Color(0xFF3B82F6).copy(alpha = 0.1f)
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = ponto.tipo,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (ponto.tipo) {
                                    "Terminal" -> Color(0xFF10B981)
                                    else -> Color(0xFF3B82F6)
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Botões de ação
                Row {
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onExcluirClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoVazio(
    onAdicionarPonto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF5C7AEA).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF5C7AEA),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nenhum ponto cadastrado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748)
        )

        Text(
            text = "Adicione seu primeiro ponto de parada para começar",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF718096),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        BotaoAdicionarPonto(
            onAdicionarPonto = onAdicionarPonto,
            texto = "Adicionar Primeiro Ponto"
        )
    }
}