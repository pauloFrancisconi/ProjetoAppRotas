package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.projetoapprotas.ui.componentes.BotaoVoltar

// Modelo de dados para Motorista
data class Motorista(
    val id: String,
    val nome: String,
    val cnh: String,
    val telefone: String,
    val status: StatusMotorista = StatusMotorista.DISPONIVEL
)

enum class StatusMotorista {
    DISPONIVEL, OCUPADO, INATIVO
}

// Modelo atualizado para Rota com motorista atribuído
data class RotaCadastrada(
    val id: String,
    val numero: String,
    val nome: String,
    val origem: String,
    val destino: String,
    val pontos: Int,
    val tempoMedio: String,
    val status: StatusRota,
    val motoristaAtribuido: Motorista? = null
)

// Modelo para atribuição
data class AtribuicaoRota(
    val rotaId: String,
    val motoristaId: String,
    val dataAtribuicao: String,
    val observacoes: String = ""
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
        RotaCadastrada("1", "101", "Centro - Bairro Alto", "Terminal Central", "Bairro Alto", 12, "45 min", StatusRota.ATIVA,
            Motorista("m1", "João Silva", "12345678901", "(11) 99999-9999", StatusMotorista.OCUPADO)),
        RotaCadastrada("2", "202", "Shopping - Universidade", "Shopping Center", "Campus Universitário", 8, "30 min", StatusRota.ATIVA),
        RotaCadastrada("3", "303", "Aeroporto - Centro", "Aeroporto", "Centro da Cidade", 15, "60 min", StatusRota.MANUTENCAO,
            Motorista("m2", "Maria Santos", "09876543210", "(11) 88888-8888", StatusMotorista.OCUPADO))
    ),
    motoristasDisponiveis: List<Motorista> = listOf(
        Motorista("m3", "Carlos Oliveira", "11122233344", "(11) 77777-7777", StatusMotorista.DISPONIVEL),
        Motorista("m4", "Ana Costa", "55566677788", "(11) 66666-6666", StatusMotorista.DISPONIVEL),
        Motorista("m5", "Pedro Lima", "99988877766", "(11) 55555-5555", StatusMotorista.DISPONIVEL)
    )
) {
    var showAtribuicaoDialog by remember { mutableStateOf(false) }
    var rotaSelecionada by remember { mutableStateOf<RotaCadastrada?>(null) }
    var rotas by remember { mutableStateOf(rotasCadastradas) }

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
                            text = "${rotas.size} rotas",
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
                        valor = rotas.count { it.status == StatusRota.ATIVA }.toString(),
                        cor = Color(0xFF10B981),
                        icone = Icons.Default.LocationOn,
                        modifier = Modifier.weight(1f)
                    )

                    CardEstatistica(
                        titulo = "Com Motorista",
                        valor = rotas.count { it.motoristaAtribuido != null }.toString(),
                        cor = Color(0xFF3B82F6),
                        icone = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )

                    CardEstatistica(
                        titulo = "Sem Motorista",
                        valor = rotas.count { it.motoristaAtribuido == null }.toString(),
                        cor = Color(0xFFF59E0B),
                        icone = Icons.Default.Face,
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
                if (rotas.isNotEmpty()) {
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
                        items(rotas) { rota ->
                            CardRota(
                                rota = rota,
                                onEditarClick = { /* Implementar edição */ },
                                onExcluirClick = { /* Implementar exclusão */ },
                                onAtribuirMotoristaClick = {
                                    rotaSelecionada = rota
                                    showAtribuicaoDialog = true
                                },
                                onRemoverMotoristaClick = {
                                    rotas = rotas.map {
                                        if (it.id == rota.id) it.copy(motoristaAtribuido = null)
                                        else it
                                    }
                                }
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

    // Dialog de atribuição de motorista
    if (showAtribuicaoDialog && rotaSelecionada != null) {
        DialogAtribuicaoMotorista(
            rota = rotaSelecionada!!,
            motoristasDisponiveis = motoristasDisponiveis,
            onDismiss = {
                showAtribuicaoDialog = false
                rotaSelecionada = null
            },
            onAtribuir = { rota, motorista ->
                rotas = rotas.map {
                    if (it.id == rota.id) it.copy(motoristaAtribuido = motorista)
                    else it
                }
                showAtribuicaoDialog = false
                rotaSelecionada = null
            }
        )
    }
}

@Composable
fun DialogAtribuicaoMotorista(
    rota: RotaCadastrada,
    motoristasDisponiveis: List<Motorista>,
    onDismiss: () -> Unit,
    onAtribuir: (RotaCadastrada, Motorista) -> Unit
) {
    var motoristaSelecionado by remember { mutableStateOf<Motorista?>(null) }
    var observacoes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atribuir Motorista",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Info da rota
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Rota ${rota.numero} - ${rota.nome}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )
                        Text(
                            text = "${rota.origem} → ${rota.destino}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF718096),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Lista de motoristas
                Text(
                    text = "Selecione um motorista:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(motoristasDisponiveis) { motorista ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { motoristaSelecionado = motorista },
                            colors = CardDefaults.cardColors(
                                containerColor = if (motoristaSelecionado?.id == motorista.id)
                                    Color(0xFF10B981).copy(alpha = 0.1f)
                                else Color(0xFFF9FAFB)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = motoristaSelecionado?.id == motorista.id,
                                    onClick = { motoristaSelecionado = motorista },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF10B981)
                                    )
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = motorista.nome,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2D3748)
                                    )
                                    Text(
                                        text = "CNH: ${motorista.cnh}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF718096)
                                    )
                                    Text(
                                        text = motorista.telefone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF718096)
                                    )
                                }

                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Disponível",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo de observações
                OutlinedTextField(
                    value = observacoes,
                    onValueChange = { observacoes = it },
                    label = { Text("Observações (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        focusedLabelColor = Color(0xFF10B981)
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botões
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            motoristaSelecionado?.let { motorista ->
                                onAtribuir(rota, motorista)
                            }
                        },
                        enabled = motoristaSelecionado != null,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Atribuir")
                    }
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
    onAtribuirMotoristaClick: () -> Unit,
    onRemoverMotoristaClick: () -> Unit,
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

                        // Info do motorista atribuído
                        if (rota.motoristaAtribuido != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Motorista: ${rota.motoristaAtribuido.nome}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Medium
                                )
                            }
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
                Column {
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

                    // Botão para gerenciar motorista
                    if (rota.motoristaAtribuido == null) {
                        Button(
                            onClick = onAtribuirMotoristaClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Atribuir",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Atribuir",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onRemoverMotoristaClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Remover",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
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