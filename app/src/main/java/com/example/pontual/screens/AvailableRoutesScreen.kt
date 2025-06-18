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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pontual.PreferenceManager
import com.example.pontual.api.models.Route
import com.example.pontual.repository.RouteRepository
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.EmptyStateScreen
import com.example.pontual.components.PontualTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableRoutesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { RouteRepository() }
    
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showAssignDialog by remember { mutableStateOf<Route?>(null) }

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
                title = "Rotas Disponíveis",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingScreen(message = "Carregando rotas disponíveis...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadRoutes() }
                    )
                }
                routes.isEmpty() -> {
                    EmptyStateScreen(
                        title = "Nenhuma rota disponível",
                        message = "Não há rotas disponíveis para atribuição no momento",
                        icon = Icons.Default.DirectionsCar
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Selecione uma rota",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "A rota será atribuída apenas localmente. Você poderá visualizar os pontos e gerenciar as entregas.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        
                        items(routes) { route ->
                            AvailableRouteCard(
                                route = route,
                                onAssign = { showAssignDialog = route }
                            )
                        }
                    }
                }
            }
        }
    }

    showAssignDialog?.let { route ->
        AlertDialog(
            onDismissRequest = { showAssignDialog = null },
            title = {
                Text(
                    text = "Atribuir Rota",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Deseja se atribuir à rota:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = route.name,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${route.points.size} pontos de entrega",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (route.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = route.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        PreferenceManager.assignRoute(context, route.id, route.name)
                        showAssignDialog = null
                        onNavigateBack()
                    }
                ) {
                    Text("Atribuir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAssignDialog = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AvailableRouteCard(
    route: Route,
    onAssign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = route.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (route.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${route.points.size} pontos",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (route.estimatedDuration != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${route.estimatedDuration} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (route.totalDistance != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${route.totalDistance} km",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Criada por: ${route.userName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onAssign,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Atribuir Rota")
            }
        }
    }
} 