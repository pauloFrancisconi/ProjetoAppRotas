package com.example.projetoapprotas.ui.telas.motorista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class EntregaHistorico(
    val id: String,
    val data: Date,
    val pontos: List<PontoRota>,
    val horaInicio: String,
    val horaFim: String?,
    val distanciaPercorrida: String,
    val status: StatusEntrega
)

enum class StatusEntrega(val texto: String, val cor: Color) {
    CONCLUIDA("Concluída", Color(0xFF4CAF50)),
    PARCIAL("Parcial", Color(0xFFFF9800)),
    CANCELADA("Cancelada", Color(0xFFE53935))
}

enum class FiltroTempo(val texto: String) {
    HOJE("Hoje"),
    ESTA_SEMANA("Esta Semana"),
    ESTE_MES("Este Mês"),
    ULTIMOS_30_DIAS("Últimos 30 dias")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatoriosMotorista(
    onVoltarClick: () -> Unit
) {
    var filtroSelecionado by remember { mutableStateOf(FiltroTempo.ESTE_MES) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dataInicio by remember { mutableStateOf<Date?>(null) }
    var dataFim by remember { mutableStateOf<Date?>(null) }
    var expandedFilter by remember { mutableStateOf(false) }

    // Dados de exemplo (em produção, viria do ViewModel/Repository)
    val entregasHistorico = remember { gerarDadosExemplo() }
    val entregasFiltradas = remember(filtroSelecionado, dataInicio, dataFim) {
        filtrarEntregas(entregasHistorico, filtroSelecionado, dataInicio, dataFim)
    }

    val estatisticas = remember(entregasFiltradas) {
        calcularEstatisticas(entregasFiltradas)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Relatórios",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Seção de Filtros
            FiltrosSection(
                filtroSelecionado = filtroSelecionado,
                onFiltroChange = { filtroSelecionado = it },
                expandedFilter = expandedFilter,
                onExpandedChange = { expandedFilter = it }
            )

            // Seção de Estatísticas
            EstatisticasSection(estatisticas = estatisticas)

            // Lista de Entregas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Histórico de Entregas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (entregasFiltradas.isEmpty()) {
                    item {
                        EmptyStateCard()
                    }
                } else {
                    items(entregasFiltradas) { entrega ->
                        EntregaCard(entrega = entrega)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosSection(
    filtroSelecionado: FiltroTempo,
    onFiltroChange: (FiltroTempo) -> Unit,
    expandedFilter: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filtrar por período",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedFilter,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = filtroSelecionado.texto,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Período") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFilter) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    FiltroTempo.values().forEach { filtro ->
                        DropdownMenuItem(
                            text = { Text(filtro.texto) },
                            onClick = {
                                onFiltroChange(filtro)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstatisticasSection(estatisticas: Estatisticas) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EstatisticaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Check,
            titulo = "Concluídas",
            valor = estatisticas.concluidas.toString(),
            cor = Color(0xFF4CAF50)
        )

        EstatisticaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocationOn,
            titulo = "Pontos",
            valor = estatisticas.totalPontos.toString(),
            cor = Color(0xFF2196F3)
        )

        EstatisticaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Star,
            titulo = "Distância",
            valor = "${estatisticas.distanciaTotal}km",
            cor = Color(0xFF9C27B0)
        )
    }
}

@Composable
fun EstatisticaCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    cor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = cor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = cor
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EntregaCard(entrega: EntregaHistorico) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header da entrega
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(entrega.data),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entrega.horaInicio} - ${entrega.horaFim ?: "Em andamento"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = entrega.status)
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (expanded) "Recolher" else "Expandir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Informações resumidas
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    texto = "${entrega.pontos.size} pontos"
                )
                InfoChip(
                    icon = Icons.Default.Star,
                    texto = entrega.distanciaPercorrida
                )
            }

            // Detalhes expandidos
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pontos de Entrega",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                entrega.pontos.forEach { ponto ->
                    PontoItem(ponto = ponto)
                    if (ponto != entrega.pontos.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: StatusEntrega) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = status.cor.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = status.texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = status.cor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PontoItem(ponto: PontoRota) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (ponto.concluido) Icons.Default.CheckCircle else Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (ponto.concluido) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ponto.nome,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = ponto.endereco,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nenhuma entrega encontrada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Tente alterar o filtro de período",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// Dados e funções auxiliares
data class Estatisticas(
    val concluidas: Int,
    val totalPontos: Int,
    val distanciaTotal: Double
)

fun gerarDadosExemplo(): List<EntregaHistorico> {
    val calendar = Calendar.getInstance()
    return listOf(
        EntregaHistorico(
            id = "1",
            data = calendar.time,
            pontos = listOf(
                PontoRota("1", "Centro Comercial Silva", "Rua das Flores, 123", true),
                PontoRota("2", "Residencial Jardins", "Av. Principal, 456", true),
                PontoRota("3", "Condomínio Sunset", "Rua do Sol, 789", true)
            ),
            horaInicio = "08:30",
            horaFim = "12:45",
            distanciaPercorrida = "45.2km",
            status = StatusEntrega.CONCLUIDA
        ),
        EntregaHistorico(
            id = "2",
            data = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time,
            pontos = listOf(
                PontoRota("4", "Empresa TechCorp", "Av. Tecnologia, 321", true),
                PontoRota("5", "Shopping Plaza", "Rua do Comércio, 567", false)
            ),
            horaInicio = "14:00",
            horaFim = "16:30",
            distanciaPercorrida = "28.7km",
            status = StatusEntrega.PARCIAL
        ),
        EntregaHistorico(
            id = "3",
            data = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.time,
            pontos = listOf(
                PontoRota("6", "Residencial Costa", "Rua da Praia, 890", true),
                PontoRota("7", "Condomínio Verde", "Av. das Árvores, 234", true),
                PontoRota("8", "Centro Médico", "Rua da Saúde, 678", true),
                PontoRota("9", "Escola Municipal", "Rua da Educação, 345", true)
            ),
            horaInicio = "09:15",
            horaFim = "15:20",
            distanciaPercorrida = "67.3km",
            status = StatusEntrega.CONCLUIDA
        )
    )
}

fun filtrarEntregas(
    entregas: List<EntregaHistorico>,
    filtro: FiltroTempo,
    dataInicio: Date?,
    dataFim: Date?
): List<EntregaHistorico> {
    val calendar = Calendar.getInstance()
    val hoje = calendar.time

    return when (filtro) {
        FiltroTempo.HOJE -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val inicioHoje = calendar.time

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val fimHoje = calendar.time

            entregas.filter { it.data.after(inicioHoje) && it.data.before(fimHoje) }
        }
        FiltroTempo.ESTA_SEMANA -> {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val inicioSemana = calendar.time

            entregas.filter { it.data.after(inicioSemana) }
        }
        FiltroTempo.ESTE_MES -> {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val inicioMes = calendar.time

            entregas.filter { it.data.after(inicioMes) }
        }
        FiltroTempo.ULTIMOS_30_DIAS -> {
            calendar.add(Calendar.DAY_OF_YEAR, -30)
            val trintaDiasAtras = calendar.time

            entregas.filter { it.data.after(trintaDiasAtras) }
        }
    }
}

fun calcularEstatisticas(entregas: List<EntregaHistorico>): Estatisticas {
    val concluidas = entregas.count { it.status == StatusEntrega.CONCLUIDA }
    val totalPontos = entregas.sumOf { it.pontos.size }
    val distanciaTotal = entregas.sumOf {
        it.distanciaPercorrida.replace("km", "").toDoubleOrNull() ?: 0.0
    }

    return Estatisticas(concluidas, totalPontos, distanciaTotal)
}

@Preview(showBackground = true)
@Composable
fun TelaRelatoriosMotoristaPrev() {
    MaterialTheme {
        TelaRelatoriosMotorista(
            onVoltarClick = {}
        )
    }
}