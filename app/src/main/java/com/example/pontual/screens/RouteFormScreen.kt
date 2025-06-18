package com.example.pontual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.DeliveryPoint
import com.example.pontual.api.models.Route
import com.example.pontual.api.models.RouteRequest
import com.example.pontual.repository.DeliveryPointRepository
import com.example.pontual.repository.RouteRepository
import com.example.pontual.components.ValidationError
import com.example.pontual.components.SuccessMessage
import com.example.pontual.components.LoadingButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFormScreen(
    routeId: Int? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val routeRepository = remember { RouteRepository() }
    val deliveryPointRepository = remember { DeliveryPointRepository() }
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedDuration by remember { mutableStateOf("") }
    var selectedPoints by remember { mutableStateOf<List<DeliveryPoint>>(emptyList()) }
    var availablePoints by remember { mutableStateOf<List<DeliveryPoint>>(emptyList()) }
    
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(routeId != null) }
    var showPointSelection by remember { mutableStateOf(false) }

    fun loadData() {
        scope.launch {
            isLoading = true
            showError = false
            
            deliveryPointRepository.getDeliveryPoints().fold(
                onSuccess = { points ->
                    availablePoints = points.filter { it.isActive }
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = "Erro ao carregar pontos: ${exception.message}"
                }
            )
            
            if (routeId != null) {
                routeRepository.getRoute(routeId).fold(
                    onSuccess = { route ->
                        name = route.name
                        description = route.description ?: ""
                        estimatedDuration = route.estimatedDuration?.toString() ?: ""
                        selectedPoints = route.points.map { it.deliveryPoint }
                    },
                    onFailure = { exception ->
                        showError = true
                        errorMessage = "Erro ao carregar rota: ${exception.message}"
                    }
                )
            }
            
            isLoading = false
        }
    }

    fun saveRoute() {
        if (name.isBlank()) {
            showError = true
            errorMessage = "Nome da rota é obrigatório"
            return
        }

        if (selectedPoints.isEmpty()) {
            showError = true
            errorMessage = "Selecione pelo menos um ponto de entrega"
            return
        }

        scope.launch {
            isLoading = true
            showError = false
            showSuccess = false
            
            val request = RouteRequest(
                name = name.trim(),
                description = description.trim().takeIf { it.isNotBlank() },
                pointIds = selectedPoints.map { it.id },
                estimatedDuration = estimatedDuration.toIntOrNull()
            )

            val result = if (isEditing) {
                routeRepository.updateRoute(routeId!!, request)
            } else {
                routeRepository.createRoute(request)
            }

            result.fold(
                onSuccess = {
                    showSuccess = true
                    successMessage = if (isEditing) "Rota atualizada com sucesso!" else "Rota criada com sucesso!"
                    kotlinx.coroutines.delay(1500)
                    onNavigateBack()
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro ao salvar rota"
                }
            )
            isLoading = false
        }
    }

    fun addPoint(point: DeliveryPoint) {
        if (!selectedPoints.any { it.id == point.id }) {
            selectedPoints = selectedPoints + point
        }
    }

    fun removePoint(point: DeliveryPoint) {
        selectedPoints = selectedPoints.filter { it.id != point.id }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Rota" else "Nova Rota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { saveRoute() },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Salvar")
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

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Rota *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = estimatedDuration,
                    onValueChange = { estimatedDuration = it },
                    label = { Text("Duração Estimada (minutos)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pontos de Entrega (${selectedPoints.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = { showPointSelection = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedPoints.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "Nenhum ponto selecionado. Clique em 'Adicionar' para selecionar pontos de entrega.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedPoints) { point ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = point.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = point.address,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    IconButton(onClick = { removePoint(point) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Remover")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                LoadingButton(
                    text = if (isEditing) "Atualizar" else "Criar",
                    isLoading = isLoading,
                    onClick = { saveRoute() }
                )
            }
        }
    }

    if (showPointSelection) {
        AlertDialog(
            onDismissRequest = { showPointSelection = false },
            title = { Text("Selecionar Pontos") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(availablePoints.filter { point ->
                        !selectedPoints.any { it.id == point.id }
                    }) { point ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = point.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = point.address,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(onClick = { 
                                    addPoint(point)
                                    showPointSelection = false
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPointSelection = false }) {
                    Text("Fechar")
                }
            }
        )
    }
} 