package com.example.pontual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.DeliveryPoint
import com.example.pontual.repository.DeliveryPointRepository
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.EmptyStateScreen
import com.example.pontual.components.PontualTopAppBar
import com.example.pontual.components.TopAppBarAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPointsScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DeliveryPointRepository() }
    
    var deliveryPoints by remember { mutableStateOf<List<DeliveryPoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadDeliveryPoints() {
        scope.launch {
            isLoading = true
            showError = false
            
            repository.getDeliveryPoints().fold(
                onSuccess = { points ->
                    deliveryPoints = points
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
        loadDeliveryPoints()
    }

    Scaffold(
        topBar = {
            PontualTopAppBar(
                title = "Pontos de Entrega",
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
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
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
                    LoadingScreen(message = "Carregando pontos de entrega...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadDeliveryPoints() }
                    )
                }
                deliveryPoints.isEmpty() -> {
                    EmptyStateScreen(
                        title = "Nenhum ponto de entrega encontrado",
                        message = "Adicione o primeiro ponto de entrega para começar"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(deliveryPoints) { point ->
                            DeliveryPointCard(
                                deliveryPoint = point,
                                onEdit = { onNavigateToEdit(point.id) },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteDeliveryPoint(point.id).fold(
                                            onSuccess = { loadDeliveryPoints() },
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
fun DeliveryPointCard(
    deliveryPoint: DeliveryPoint,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = deliveryPoint.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deliveryPoint.address,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (deliveryPoint.contactName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Contato: ${deliveryPoint.contactName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (deliveryPoint.contactPhone != null) {
                        Text(
                            text = "Tel: ${deliveryPoint.contactPhone}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (deliveryPoint.notes != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = deliveryPoint.notes,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Deletar")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (deliveryPoint.isActive) "Ativo" else "Inativo",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (deliveryPoint.isActive) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir o ponto '${deliveryPoint.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 