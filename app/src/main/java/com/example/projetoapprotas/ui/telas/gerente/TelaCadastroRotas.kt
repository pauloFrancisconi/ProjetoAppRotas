package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
data class RotaCadastrada(
    val id: String,
    val numero: String,
    val nome: String,
    val origem: String,
    val destino: String,
    val pontos: Int,
    val tempoMedio: String,
    val status: StatusRota
)

enum class StatusRota {
    ATIVA, INATIVA, MANUTENCAO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastroRotas(
    onVoltarClick: () -> Unit,
    onAdicionarRota: () -> Unit = {},
    rotasCadastradas: List<RotaCadastrada> = listOf(
        RotaCadastrada("1", "101", "Centro - Bairro Alto", "Terminal Central", "Bairro Alto", 12, "45 min", StatusRota.ATIVA),
        RotaCadastrada("2", "202", "Shopping - Universidade", "Shopping Center", "Campus Universitário", 8, "30 min", StatusRota.ATIVA),
        RotaCadastrada("3", "303", "Aeroporto - Centro", "Aeroporto", "Centro da Cidade", 15, "60 min", StatusRota.MANUTENCAO)
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
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "${rotasCadastradas.size} rotas",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título principal
                Text(
                    text = "Gerenciar Rotas",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Text(
                    text = "Configure e gerencie as rotas do sistema",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF718096),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cards de estatísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardEstatistica(
                        titulo = "Ativas",
                        valor = rotasCadastradas.count { it.status == StatusRota.ATIVA }.toString(),
                        cor = Color(0xFF10B981),
                        icone = Icons.Default.LocationOn,
                        modifier = Modifier.weight(1f)
                    )

                    CardEstatistica(
                        titulo = "Manutenção",
                        valor = rotasCadastradas.count { it.status == StatusRota.MANUTENCAO }.toString(),
                        cor = Color(0xFFF59E0B),
                        icone = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    )

                    CardEstatistica(
                        titulo = "Inativas",
                        valor = rotasCadastradas.count { it.status == StatusRota.INATIVA }.toString(),
                        cor = Color(0xFFEF4444),
                        icone = Icons.Default.Info,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão adicionar rota
                BotaoAdicionarRota(
                    onAdicionarRota = onAdicionarRota,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de rotas
                if (rotasCadastradas.isNotEmpty()) {
                    Text(
                        text = "Rotas Cadastradas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(rotasCadastradas) { rota ->
                            CardRota(
                                rota = rota,
                                onEditarClick = { /* Implementar edição */ },
                                onExcluirClick = { /* Implementar exclusão */ }
                            )
                        }
                    }
                } else {
                    // Estado vazio
                    EstadoVazioRotas(
                        onAdicionarRota = onAdicionarRota,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BotaoAdicionarRota(
    onAdicionarRota: () -> Unit,
    texto: String = "Criar Nova Rota",
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onAdicionarRota,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF10B981).copy(alpha = 0.25f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF10B981),
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

@Composable
fun CardEstatistica(
    titulo: String,
    valor: String,
    cor: Color,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = cor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icone,
                        contentDescription = null,
                        tint = cor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRota(
    rota: RotaCadastrada,
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
                    // Badge do número da rota
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = rota.numero,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rota.nome,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF718096),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${rota.origem} → ${rota.destino}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF718096)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Badge de status
                            Surface(
                                color = when (rota.status) {
                                    StatusRota.ATIVA -> Color(0xFF10B981).copy(alpha = 0.1f)
                                    StatusRota.INATIVA -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                    StatusRota.MANUTENCAO -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = when (rota.status) {
                                        StatusRota.ATIVA -> "Ativa"
                                        StatusRota.INATIVA -> "Inativa"
                                        StatusRota.MANUTENCAO -> "Manutenção"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (rota.status) {
                                        StatusRota.ATIVA -> Color(0xFF10B981)
                                        StatusRota.INATIVA -> Color(0xFFEF4444)
                                        StatusRota.MANUTENCAO -> Color(0xFFF59E0B)
                                    },
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Info adicional
                            Text(
                                text = "${rota.pontos} pontos • ${rota.tempoMedio}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF718096)
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
fun EstadoVazioRotas(
    onAdicionarRota: () -> Unit,
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
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nenhuma rota cadastrada",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748)
        )

        Text(
            text = "Crie sua primeira rota para começar a gerenciar o transporte",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF718096),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        BotaoAdicionarRota(
            onAdicionarRota = onAdicionarRota,
            texto = "Criar Primeira Rota"
        )
    }
}