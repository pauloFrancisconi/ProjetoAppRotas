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
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.Route
import com.example.pontual.repository.RouteRepository
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.EmptyStateScreen
import com.example.pontual.components.PontualTopAppBar
import com.example.pontual.components.TopAppBarAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { RouteRepository() }
    
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadRoutes() {
        scope.launch {
            isLoading = true
            showError = false
            
            repository.getRoutes().fold(
                onSuccess = { routesList ->
                    routes = routesList
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
        loadRoutes()
    }

    Scaffold(
        topBar = {
            PontualTopAppBar(
                title = if (isAdmin) "Todas as Rotas" else "Minhas Rotas",
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
                    LoadingScreen(message = "Carregando rotas...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadRoutes() }
                    )
                }
                routes.isEmpty() -> {
                    EmptyStateScreen(
                        title = if (isAdmin) "Nenhuma rota encontrada" else "Nenhuma rota criada",
                        message = if (isAdmin) "Nenhuma rota foi criada no sistema" else "Adicione a primeira rota para começar"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(routes) { route ->
                            RouteCard(
                                route = route,
                                onEdit = { onNavigateToEdit(route.id) },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteRoute(route.id).fold(
                                            onSuccess = { loadRoutes() },
                                            onFailure = { exception ->
                                                showError = true
                                                errorMessage = exception.message ?: "Erro ao deletar"
                                            }
                                        )
                                    }
                                },
                                isAdmin = isAdmin
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
fun RouteCard(
    route: Route,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false
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
                        text = route.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (route.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = route.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "${route.points.size} pontos",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    if (isAdmin) {
                        Text(
                            text = "Criada por: ${route.userName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (route.driverName != null) {
                        Text(
                            text = "Motorista: ${route.driverName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (route.estimatedDuration != null) {
                        Text(
                            text = "Duração: ${route.estimatedDuration} min",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (route.totalDistance != null) {
                        Text(
                            text = "Distância: ${route.totalDistance} km",
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
                        text = if (route.isActive) "Ativa" else "Inativa",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (route.isActive) 
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
            text = { Text("Tem certeza que deseja excluir a rota '${route.name}'?") },
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