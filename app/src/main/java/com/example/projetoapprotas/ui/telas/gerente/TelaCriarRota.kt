package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
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

// Modelo de dados para a rota
data class NovaRota(
    val nome: String,
    val pontosSelecionados: List<PontoCadastrado>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCriarRota(
    onVoltarClick: () -> Unit,
    onSalvarRota: (NovaRota) -> Unit,
    pontosDisponiveis: List<PontoCadastrado> = listOf(
        PontoCadastrado("1", "Terminal Central", "Av. Principal, 123", "Terminal"),
        PontoCadastrado("2", "Praça da Cidade", "Rua das Flores, 456", "Parada"),
        PontoCadastrado("3", "Shopping Center", "Av. Comercial, 789", "Parada"),
        PontoCadastrado("4", "Hospital Municipal", "Rua da Saúde, 321", "Parada"),
        PontoCadastrado("5", "Universidade", "Av. Educação, 654", "Parada")
    )
) {
    var nomeRota by remember { mutableStateOf("") }
    var pontosSelecionados by remember { mutableStateOf<List<PontoCadastrado>>(emptyList()) }
    var mostrarSeletorPontos by remember { mutableStateOf(false) }

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotaoVoltar(onVoltarClick = onVoltarClick)

                    Spacer(modifier = Modifier.weight(1f))

                    // Badge com contador de pontos selecionados
                    if (pontosSelecionados.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "${pontosSelecionados.size} pontos",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título
                Text(
                    text = "Criar Nova Rota",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Text(
                    text = "Configure os pontos e a ordem da rota",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF718096),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo nome da rota
                OutlinedTextField(
                    value = nomeRota,
                    onValueChange = { nomeRota = it },
                    label = { Text("Nome da Rota") },
                    placeholder = { Text("Ex: Rota Centro-Bairro") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        focusedLabelColor = Color(0xFF10B981)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Seção de pontos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pontos da Rota",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )

                    TextButton(
                        onClick = { mostrarSeletorPontos = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar Pontos")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de pontos selecionados
                if (pontosSelecionados.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(pontosSelecionados) { index, ponto ->
                            CardPontoRota(
                                ponto = ponto,
                                ordem = index + 1,
                                onRemoverClick = {
                                    pontosSelecionados = pontosSelecionados.filter { it.id != ponto.id }
                                }
                            )
                        }
                    }
                } else {
                    // Estado vazio
                    EstadoVazioRota(
                        onAdicionarPontos = { mostrarSeletorPontos = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão salvar
                Button(
                    onClick = {
                        if (nomeRota.isNotBlank() && pontosSelecionados.isNotEmpty()) {
                            onSalvarRota(NovaRota(nomeRota, pontosSelecionados))
                        }
                    },
                    enabled = nomeRota.isNotBlank() && pontosSelecionados.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = if (nomeRota.isNotBlank() && pontosSelecionados.isNotEmpty()) 4.dp else 0.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0xFF10B981).copy(alpha = 0.25f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF9CA3AF),
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Salvar Rota",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Modal seletor de pontos
            if (mostrarSeletorPontos) {
                SeletorPontos(
                    pontosDisponiveis = pontosDisponiveis,
                    pontosSelecionados = pontosSelecionados,
                    onPontosSelecionados = { pontos ->
                        pontosSelecionados = pontos
                        mostrarSeletorPontos = false
                    },
                    onDismiss = { mostrarSeletorPontos = false }
                )
            }
        }
    }
}

@Composable
fun CardPontoRota(
    ponto: PontoCadastrado,
    ordem: Int,
    onRemoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número da ordem
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = ordem.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Ícone do ponto
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Informações do ponto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ponto.nome,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = ponto.endereco,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF718096)
                )
            }

            // Ícone de arrastar (visual apenas)
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botão remover
            IconButton(
                onClick = onRemoverClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EstadoVazioRota(
    onAdicionarPontos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
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
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nenhum ponto adicionado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748)
        )

        Text(
            text = "Adicione pontos para criar sua rota",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF718096),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onAdicionarPontos,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF10B981)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Adicionar Pontos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorPontos(
    pontosDisponiveis: List<PontoCadastrado>,
    pontosSelecionados: List<PontoCadastrado>,
    onPontosSelecionados: (List<PontoCadastrado>) -> Unit,
    onDismiss: () -> Unit
) {
    var pontosTemporarios by remember { mutableStateOf(pontosSelecionados) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .heightIn(max = 600.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header do modal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selecionar Pontos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }

                    Text(
                        text = "Selecione os pontos que farão parte da rota",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lista de pontos
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(pontosDisponiveis) { ponto ->
                            val isSelected = pontosTemporarios.any { it.id == ponto.id }

                            Card(
                                onClick = {
                                    pontosTemporarios = if (isSelected) {
                                        pontosTemporarios.filter { it.id != ponto.id }
                                    } else {
                                        pontosTemporarios + ponto
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        Color(0xFF10B981).copy(alpha = 0.1f)
                                    } else {
                                        Color(0xFFF9FAFB)
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) {
                                    CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF10B981), Color(0xFF10B981))
                                        )
                                    )
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checkbox
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF10B981)
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Ícone
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFF10B981) else Color(0xFF6B7280),
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Informações do ponto
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ponto.nome,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Text(
                                            text = ponto.endereco,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF718096)
                                        )
                                    }

                                    // Badge do tipo
                                    Surface(
                                        color = when (ponto.tipo) {
                                            "Terminal" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                            else -> Color(0xFF3B82F6).copy(alpha = 0.1f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
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
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botões de ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = { onPontosSelecionados(pontosTemporarios) },
                            enabled = pontosTemporarios.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            )
                        ) {
                            Text("Confirmar (${pontosTemporarios.size})")
                        }
                    }
                }
            }
        }
    }
}