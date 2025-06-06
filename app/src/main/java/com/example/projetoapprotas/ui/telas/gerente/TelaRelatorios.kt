package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projetoapprotas.ui.componentes.BotaoVoltar

// Modelos de dados para relatórios
data class MetricaPrincipal(
    val titulo: String,
    val valor: String,
    val subtitulo: String,
    val cor: Color,
    val icone: ImageVector,
    val tendencia: String = "",
    val positiva: Boolean = true
)

data class RelatorioItem(
    val id: String,
    val titulo: String,
    val descricao: String,
    val icone: ImageVector,
    val cor: Color,
    val tipo: TipoRelatorio
)

enum class TipoRelatorio {
    OPERACIONAL, FINANCEIRO, PERFORMANCE, MANUTENCAO
}

data class DadoGrafico(
    val periodo: String,
    val valor: Float,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatorios(
    onVoltarClick: () -> Unit,
    onGerarRelatorio: (String) -> Unit = {}
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F9FA),
            Color(0xFFE9ECEF)
        )
    )

    // Dados de exemplo
    val metricasPrincipais = listOf(
        MetricaPrincipal(
            titulo = "Entregas Hoje",
            valor = "2.847",
            subtitulo = "+12% vs ontem",
            cor = Color(0xFF10B981),
            icone = Icons.Default.Person,
            tendencia = "+12%",
            positiva = true
        ),
        MetricaPrincipal(
            titulo = "Rotas Ativas",
            valor = "24",
            subtitulo = "3 em manutenção",
            cor = Color(0xFF3B82F6),
            icone = Icons.Default.LocationOn
        ),
        MetricaPrincipal(
            titulo = "Receita Hoje",
            valor = "R$ 8.542",
            subtitulo = "+5% vs ontem",
            cor = Color(0xFF8B5CF6),
            icone = Icons.Default.ThumbUp,
            tendencia = "+5%",
            positiva = true
        ),
        MetricaPrincipal(
            titulo = "Eficiência",
            valor = "94.2%",
            subtitulo = "Pontualidade média",
            cor = Color(0xFFF59E0B),
            icone = Icons.Default.KeyboardArrowUp
        )
    )

    val relatoriosDisponiveis = listOf(
        RelatorioItem(
            "op_diario", "Relatório Operacional Diário",
            "Entregas, rotas e desempenho do dia",
            Icons.Default.DateRange, Color(0xFF10B981), TipoRelatorio.OPERACIONAL
        ),
        RelatorioItem(
            "fin_mensal", "Relatório Financeiro Mensal",
            "Receitas, custos e análise financeira",
            Icons.Default.Info, Color(0xFF8B5CF6), TipoRelatorio.FINANCEIRO
        ),
        RelatorioItem(
            "perf_rotas", "Performance das Rotas",
            "Análise de eficiência por rota",
            Icons.Default.FavoriteBorder, Color(0xFF3B82F6), TipoRelatorio.PERFORMANCE
        ),
        RelatorioItem(
            "manut_veiculos", "Manutenção de Veículos",
            "Status e histórico de manutenções",
            Icons.Default.Build, Color(0xFFF59E0B), TipoRelatorio.MANUTENCAO
        ),
        RelatorioItem(
            "satisfacao", "Satisfação dos Clientes",
            "Feedback e avaliações dos entregas",
            Icons.Default.ThumbUp, Color(0xFFEC4899), TipoRelatorio.OPERACIONAL
        ),
        RelatorioItem(
            "combustivel", "Consumo de Combustível",
            "Análise de consumo e custos",
            Icons.Default.MoreVert, Color(0xFF06B6D4), TipoRelatorio.OPERACIONAL
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BotaoVoltar(onVoltarClick = onVoltarClick)

                        Spacer(modifier = Modifier.weight(1f))

                        // Badge de última atualização
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Atualizado agora",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    // Título principal
                    Column {
                        Text(
                            text = "Dashboard de Relatórios",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )

                        Text(
                            text = "Análises e métricas do sistema de transporte",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF718096),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    // Métricas principais
                    Text(
                        text = "Visão Geral",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(metricasPrincipais) { metrica ->
                            CardMetrica(
                                metrica = metrica,
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                }

                item {
                    // Gráfico de exemplo (placeholder)
                    CardGrafico()
                }

                item {
                    // Relatórios disponíveis
                    Text(
                        text = "Relatórios Disponíveis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(relatoriosDisponiveis.chunked(2)) { linha ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        linha.forEach { relatorio ->
                            CardRelatorio(
                                relatorio = relatorio,
                                onGerarClick = { onGerarRelatorio(relatorio.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Se a linha tem apenas 1 item, adiciona espaço
                        if (linha.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    // Espaço final
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CardMetrica(
    metrica: MetricaPrincipal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = metrica.cor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = metrica.icone,
                            contentDescription = null,
                            tint = metrica.cor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (metrica.tendencia.isNotEmpty()) {
                    Surface(
                        color = if (metrica.positiva)
                            Color(0xFF10B981).copy(alpha = 0.1f)
                        else
                            Color(0xFFEF4444).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = metrica.tendencia,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (metrica.positiva) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = metrica.valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = metrica.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A5568),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = metrica.subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun CardGrafico(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Entregas por Dia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "Últimos 7 dias",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF718096)
                    )
                }

                IconButton(onClick = { /* Ver gráfico completo */ }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Expandir",
                        tint = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Placeholder para gráfico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = Color(0xFF3B82F6).copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gráfico de Entregas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRelatorio(
    relatorio: RelatorioItem,
    onGerarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onGerarClick,
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
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = relatorio.cor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = relatorio.icone,
                            contentDescription = null,
                            tint = relatorio.cor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Badge do tipo
                Surface(
                    color = relatorio.cor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (relatorio.tipo) {
                            TipoRelatorio.OPERACIONAL -> "Operacional"
                            TipoRelatorio.FINANCEIRO -> "Financeiro"
                            TipoRelatorio.PERFORMANCE -> "Performance"
                            TipoRelatorio.MANUTENCAO -> "Manutenção"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = relatorio.cor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = relatorio.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = relatorio.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gerar",
                    style = MaterialTheme.typography.labelMedium,
                    color = relatorio.cor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = relatorio.cor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}