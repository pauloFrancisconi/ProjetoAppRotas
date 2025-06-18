package com.example.pontual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.Driver
import com.example.pontual.api.models.Route
import com.example.pontual.api.models.RouteAssignmentRequest
import com.example.pontual.repository.DriverRepository
import com.example.pontual.repository.RouteRepository
import com.example.pontual.components.ValidationError
import com.example.pontual.components.SuccessMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteAssignmentScreen(
    routeId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val routeRepository = remember { RouteRepository() }
    val driverRepository = remember { DriverRepository() }
    
    var route by remember { mutableStateOf<Route?>(null) }
    var availableDrivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var selectedDriverId by remember { mutableStateOf<Int?>(null) }
    
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    fun loadData() {
        scope.launch {
            isLoading = true
            showError = false
            
            val routeResult = routeRepository.getRoute(routeId)
            val driversResult = driverRepository.getDrivers()
            
            routeResult.fold(
                onSuccess = { routeData ->
                    route = routeData
                    selectedDriverId = routeData.driverId
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = "Erro ao carregar rota: ${exception.message}"
                }
            )
            
            driversResult.fold(
                onSuccess = { drivers ->
                    availableDrivers = drivers.filter { it.isActive }
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = "Erro ao carregar motoristas: ${exception.message}"
                }
            )
            
            isLoading = false
        }
    }

    fun assignDriver() {
        if (selectedDriverId == null) {
            showError = true
            errorMessage = "Selecione um motorista"
            return
        }

        scope.launch {
            isLoading = true
            showError = false
            showSuccess = false
            
            val request = RouteAssignmentRequest(
                routeId = routeId,
                driverId = selectedDriverId!!
            )

            routeRepository.assignRoute(request).fold(
                onSuccess = { updatedRoute ->
                    route = updatedRoute
                    showSuccess = true
                    successMessage = "Motorista atribuído com sucesso!"
                    kotlinx.coroutines.delay(1500)
                    onNavigateBack()
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro ao atribuir motorista"
                }
            )
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atribuir Motorista") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { assignDriver() },
                        enabled = !isLoading && selectedDriverId != null
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Salvar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (showError) {
                    ValidationError(message = errorMessage)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (showSuccess) {
                    SuccessMessage(message = successMessage)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                route?.let { routeData ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Rota: ${routeData.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            if (routeData.description != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = routeData.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Criada por: ${routeData.userName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            Text(
                                text = "${routeData.points.size} pontos de entrega",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            if (routeData.driverName != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text("Motorista atual: ${routeData.driverName}")
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Selecionar Motorista",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (availableDrivers.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Nenhum motorista disponível. Cadastre motoristas primeiro.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableDrivers) { driver ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedDriverId == driver.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedDriverId == driver.id,
                                        onClick = { selectedDriverId = driver.id }
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = driver.name,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = driver.email,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (driver.phone != null) {
                                            Text(
                                                text = "Tel: ${driver.phone}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        if (driver.vehiclePlate != null) {
                                            Text(
                                                text = "Veículo: ${driver.vehiclePlate}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { assignDriver() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && selectedDriverId != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Atribuir Motorista")
                    }
                }
            }
        }
    }
} 