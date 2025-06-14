package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.platform.LocalConfiguration
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
    OPERACIONAL, MOTORISTA, ROTA, ENTREGA
}

data class DadoRota(
    val pontoId: String,
    val nome: String,
    val endereco: String,
    val concluido: Boolean,
    val motorista: String,
    val horario: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatorios(
    onVoltarClick: () -> Unit,
    onGerarRelatorio: (String) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F9FA),
            Color(0xFFE9ECEF)
        )
    )

    // Dados baseados nas rotas dos motoristas
    val metricasPrincipais = listOf(
        MetricaPrincipal(
            titulo = "Pontos de Entrega",
            valor = "847",
            subtitulo = "Hoje realizados",
            cor = Color(0xFF10B981),
            icone = Icons.Default.LocationOn,
            tendencia = "+12%",
            positiva = true
        ),
        MetricaPrincipal(
            titulo = "Rotas Ativas",
            valor = "24",
            subtitulo = "Motoristas em campo",
            cor = Color(0xFF3B82F6),
            icone = Icons.Default.Star
        ),
        MetricaPrincipal(
            titulo = "Taxa de Conclusão",
            valor = "94.2%",
            subtitulo = "Pontos finalizados",
            cor = Color(0xFF8B5CF6),
            icone = Icons.Default.CheckCircle,
            tendencia = "+5%",
            positiva = true
        ),
        MetricaPrincipal(
            titulo = "Motoristas Ativos",
            valor = "18",
            subtitulo = "De 24 disponíveis",
            cor = Color(0xFFF59E0B),
            icone = Icons.Default.Person
        )
    )

    val relatoriosDisponiveis = listOf(
        RelatorioItem(
            "rotas_diarias", "Relatório de Rotas Diárias",
            "Progresso e status das rotas do dia",
            Icons.Default.Home, Color(0xFF10B981), TipoRelatorio.ROTA
        ),
        RelatorioItem(
            "pontos_entrega", "Pontos de Entrega",
            "Detalhamento dos pontos e endereços",
            Icons.Default.LocationOn, Color(0xFF3B82F6), TipoRelatorio.ENTREGA
        ),
        RelatorioItem(
            "desempenho_motoristas", "Desempenho dos Motoristas",
            "Análise individual de cada motorista",
            Icons.Default.Person, Color(0xFF8B5CF6), TipoRelatorio.MOTORISTA
        ),
        RelatorioItem(
            "tempo_conclusao", "Tempo de Conclusão",
            "Análise de tempo por ponto e rota",
            Icons.Default.Info, Color(0xFFF59E0B), TipoRelatorio.OPERACIONAL
        ),
        RelatorioItem(
            "fotos_evidencia", "Fotos de Evidência",
            "Relatório de fotos por ponto",
            Icons.Default.AccountBox, Color(0xFFEC4899), TipoRelatorio.ENTREGA
        ),
        RelatorioItem(
            "rotas_concluidas", "Rotas Finalizadas",
            "Histórico de rotas completadas",
            Icons.Default.Done, Color(0xFF06B6D4), TipoRelatorio.ROTA
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
                    .padding(horizontal = if (isTablet) 24.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isTablet) 32.dp else 24.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
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
                                modifier = Modifier.padding(
                                    horizontal = if (isTablet) 16.dp else 12.dp,
                                    vertical = if (isTablet) 8.dp else 6.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(if (isTablet) 16.dp else 14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Atualizado agora",
                                    style = if (isTablet) MaterialTheme.typography.bodyMedium
                                    else MaterialTheme.typography.bodySmall,
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
                            text = "Dashboard de Rotas",
                            style = if (isTablet) MaterialTheme.typography.displaySmall
                            else MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )

                        Text(
                            text = "Monitoramento e controle das rotas de entrega",
                            style = if (isTablet) MaterialTheme.typography.titleMedium
                            else MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF718096),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    // Métricas principais
                    Text(
                        text = "Resumo Operacional",
                        style = if (isTablet) MaterialTheme.typography.headlineSmall
                        else MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (isTablet && isLandscape) {
                        // Layout em grid para tablets em landscape
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp,
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(metricasPrincipais) { metrica ->
                                CardMetrica(
                                    metrica = metrica,
                                    isTablet = isTablet
                                )
                            }
                        }
                    } else {
                        // Layout horizontal para mobile e tablets em portrait
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(metricasPrincipais) { metrica ->
                                CardMetrica(
                                    metrica = metrica,
                                    modifier = Modifier.width(if (isTablet) 280.dp else 200.dp),
                                    isTablet = isTablet
                                )
                            }
                        }
                    }
                }

                item {
                    // Gráfico de rotas
                    CardGraficoRotas(isTablet = isTablet)
                }

                item {
                    // Relatórios disponíveis
                    Text(
                        text = "Relatórios de Rotas",
                        style = if (isTablet) MaterialTheme.typography.headlineSmall
                        else MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (isTablet) {
                    // Layout em grid para tablets
                    items(relatoriosDisponiveis.chunked(2)) { linha ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            linha.forEach { relatorio ->
                                CardRelatorio(
                                    relatorio = relatorio,
                                    onGerarClick = { onGerarRelatorio(relatorio.id) },
                                    modifier = Modifier.weight(1f),
                                    isTablet = true
                                )
                            }
                            if (linha.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // Layout vertical para mobile
                    items(relatoriosDisponiveis) { relatorio ->
                        CardRelatorio(
                            relatorio = relatorio,
                            onGerarClick = { onGerarRelatorio(relatorio.id) },
                            modifier = Modifier.fillMaxWidth(),
                            isTablet = false
                        )
                    }
                }

                item {
                    // Espaço final
                    Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 16.dp))
                }
            }
        }
    }
}

@Composable
fun CardMetrica(
    metrica: MetricaPrincipal,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = if (isTablet) 4.dp else 3.dp,
                shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 24.dp else 20.dp)
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
                    shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp),
                    modifier = Modifier.size(if (isTablet) 56.dp else 48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = metrica.icone,
                            contentDescription = null,
                            tint = metrica.cor,
                            modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                        )
                    }
                }

                if (metrica.tendencia.isNotEmpty()) {
                    Surface(
                        color = if (metrica.positiva)
                            Color(0xFF10B981).copy(alpha = 0.1f)
                        else
                            Color(0xFFEF4444).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(if (isTablet) 10.dp else 8.dp)
                    ) {
                        Text(
                            text = metrica.tendencia,
                            modifier = Modifier.padding(
                                horizontal = if (isTablet) 12.dp else 8.dp,
                                vertical = if (isTablet) 6.dp else 4.dp
                            ),
                            style = if (isTablet) MaterialTheme.typography.labelMedium
                            else MaterialTheme.typography.labelSmall,
                            color = if (metrica.positiva) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isTablet) 20.dp else 16.dp))

            Text(
                text = metrica.valor,
                style = if (isTablet) MaterialTheme.typography.displaySmall
                else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = metrica.titulo,
                style = if (isTablet) MaterialTheme.typography.titleMedium
                else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A5568),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = metrica.subtitulo,
                style = if (isTablet) MaterialTheme.typography.bodyMedium
                else MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun CardGraficoRotas(
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isTablet) 4.dp else 3.dp,
                shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 24.dp else 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progresso das Rotas",
                        style = if (isTablet) MaterialTheme.typography.headlineSmall
                        else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "Últimas 24 horas",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.bodySmall,
                        color = Color(0xFF718096)
                    )
                }

                IconButton(onClick = { /* Ver gráfico completo */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isTablet) 24.dp else 20.dp))

            // Placeholder para gráfico de progresso das rotas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 160.dp else 120.dp)
                    .background(
                        color = Color(0xFF3B82F6).copy(alpha = 0.05f),
                        shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(if (isTablet) 40.dp else 32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gráfico de Progresso das Rotas",
                        style = if (isTablet) MaterialTheme.typography.titleMedium
                        else MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "24 rotas ativas • 18 motoristas",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.bodySmall,
                        color = Color(0xFF718096),
                        modifier = Modifier.padding(top = 4.dp)
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
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    Card(
        onClick = onGerarClick,
        modifier = modifier
            .shadow(
                elevation = if (isTablet) 3.dp else 2.dp,
                shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp)
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
                    shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp),
                    modifier = Modifier.size(if (isTablet) 48.dp else 40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = relatorio.icone,
                            contentDescription = null,
                            tint = relatorio.cor,
                            modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                        )
                    }
                }

                // Badge do tipo
                Surface(
                    color = relatorio.cor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(if (isTablet) 8.dp else 6.dp)
                ) {
                    Text(
                        text = when (relatorio.tipo) {
                            TipoRelatorio.OPERACIONAL -> "Operacional"
                            TipoRelatorio.MOTORISTA -> "Motorista"
                            TipoRelatorio.ROTA -> "Rota"
                            TipoRelatorio.ENTREGA -> "Entrega"
                        },
                        modifier = Modifier.padding(
                            horizontal = if (isTablet) 10.dp else 6.dp,
                            vertical = if (isTablet) 5.dp else 3.dp
                        ),
                        style = if (isTablet) MaterialTheme.typography.labelMedium
                        else MaterialTheme.typography.labelSmall,
                        color = relatorio.cor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isTablet) 16.dp else 12.dp))

            Text(
                text = relatorio.titulo,
                style = if (isTablet) MaterialTheme.typography.titleMedium
                else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748)
            )

            Text(
                text = relatorio.descricao,
                style = if (isTablet) MaterialTheme.typography.bodyMedium
                else MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(if (isTablet) 16.dp else 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gerar Relatório",
                    style = if (isTablet) MaterialTheme.typography.labelLarge
                    else MaterialTheme.typography.labelMedium,
                    color = relatorio.cor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(if (isTablet) 6.dp else 4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = relatorio.cor,
                    modifier = Modifier.size(if (isTablet) 18.dp else 16.dp)
                )
            }
        }
    }
}