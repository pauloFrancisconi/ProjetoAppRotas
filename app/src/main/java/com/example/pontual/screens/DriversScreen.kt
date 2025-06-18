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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.Driver
import com.example.pontual.repository.DriverRepository
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.EmptyStateScreen
import com.example.pontual.components.PontualTopAppBar
import com.example.pontual.components.TopAppBarAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DriverRepository() }
    
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadDrivers() {
        scope.launch {
            isLoading = true
            showError = false
            
            repository.getDrivers().fold(
                onSuccess = { driversList ->
                    drivers = driversList
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
        loadDrivers()
    }

    Scaffold(
        topBar = {
            PontualTopAppBar(
                title = "Motoristas",
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
                    LoadingScreen(message = "Carregando motoristas...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadDrivers() }
                    )
                }
                drivers.isEmpty() -> {
                    EmptyStateScreen(
                        title = "Nenhum motorista encontrado",
                        message = "Adicione o primeiro motorista para começar"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(drivers) { driver ->
                            DriverCard(
                                driver = driver,
                                onEdit = { onNavigateToEdit(driver.id) },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteDriver(driver.id).fold(
                                            onSuccess = { loadDrivers() },
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
fun DriverCard(
    driver: Driver,
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
                        text = driver.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = driver.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (driver.phone != null) {
                        Text(
                            text = "Tel: ${driver.phone}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (driver.licenseNumber != null) {
                        Text(
                            text = "CNH: ${driver.licenseNumber}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (driver.vehiclePlate != null) {
                        Text(
                            text = "Placa: ${driver.vehiclePlate}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (driver.vehicleModel != null) {
                        Text(
                            text = "Modelo: ${driver.vehicleModel}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (driver.currentRouteName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rota atual: ${driver.currentRouteName}",
                            style = MaterialTheme.typography.bodySmall
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
            
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = if (driver.isActive) "Ativo" else "Inativo",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (driver.isActive) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            )
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir o motorista '${driver.name}'?") },
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