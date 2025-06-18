package com.example.pontual.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.Delivery
import com.example.pontual.api.models.DeliveryStatus
import com.example.pontual.repository.DeliveryRepository
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.EmptyStateScreen
import com.example.pontual.components.PontualTopAppBar
import com.example.pontual.components.TopAppBarAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DeliveryRepository() }
    
    var deliveries by remember { mutableStateOf<List<Delivery>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadDeliveries() {
        scope.launch {
            isLoading = true
            showError = false
            repository.getDeliveries().fold(
                onSuccess = { deliveriesList ->
                    deliveries = deliveriesList
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro desconhecido"
                }
            )
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadDeliveries()
    }

    Scaffold(
        topBar = {
            PontualTopAppBar(
                title = "Entregas",
                onNavigateBack = onNavigateBack,
                actions = {
                    TopAppBarAction(
                        icon = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        onClick = onNavigateToCreate
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingScreen(message = "Carregando entregas...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadDeliveries() }
                    )
                }
                deliveries.isEmpty() -> {
                    EmptyStateScreen(
                        title = "Nenhuma entrega encontrada",
                        message = "Adicione a primeira entrega para começar",
                        icon = Icons.Default.LocalShipping
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(deliveries) { delivery ->
                            DeliveryCard(
                                delivery = delivery,
                                onEdit = { onNavigateToEdit(delivery.id) },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteDelivery(delivery.id).fold(
                                            onSuccess = { loadDeliveries() },
                                            onFailure = { exception ->
                                                showError = true
                                                errorMessage = exception.message ?: "Erro ao deletar"
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryCard(
    delivery: Delivery,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Entrega #${delivery.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow("Rota", delivery.routeName, Icons.Default.Route)
                    
                    if (!delivery.driverName.isNullOrBlank()) {
                        InfoRow("Motorista", delivery.driverName, Icons.Default.Person)
                    }
                    
                    InfoRow("Agendada", delivery.scheduledDate, Icons.Default.Schedule)
                    
                    if (!delivery.completedDate.isNullOrBlank()) {
                        InfoRow("Concluída", delivery.completedDate, Icons.Default.CheckCircle)
                    }
                    
                    if (!delivery.notes.isNullOrBlank()) {
                        InfoRow("Observações", delivery.notes, Icons.Default.Info)
                    }
                }
                
                Column {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = statusLabel(delivery.status),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (delivery.status ?: DeliveryStatus.PENDING) {
                            DeliveryStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                            DeliveryStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer
                            DeliveryStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            DeliveryStatus.FAILED, DeliveryStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    "Confirmar exclusão",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = { 
                Text(
                    "Tem certeza que deseja excluir a entrega #${delivery.id}?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Excluir",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal
        )
    }
}

private fun statusLabel(status: DeliveryStatus?): String = when (status ?: DeliveryStatus.PENDING) {
    DeliveryStatus.COMPLETED -> "Concluída"
    DeliveryStatus.IN_PROGRESS -> "Em Andamento"
    DeliveryStatus.PENDING -> "Pendente"
    DeliveryStatus.FAILED -> "Falhou"
    DeliveryStatus.CANCELLED -> "Cancelada"
} 