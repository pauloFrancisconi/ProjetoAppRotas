package com.example.projetoapprotas.ui.telas.motorista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetoapprotas.data.models.StatusRegistro
import com.example.projetoapprotas.ui.viewmodels.RegistroViewModel
import com.example.projetoapprotas.utils.SyncManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRegistroPonto(
    pontoColetaId: String,
    pontoColetaNome: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: RegistroViewModel = viewModel { RegistroViewModel(context) }
    val syncManager = remember { SyncManager(context) }
    
    // Estados do ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val coordenadasAtuais by viewModel.coordenadasAtuais.collectAsState()
    val fotosCapturadas by viewModel.fotosCapturadas.collectAsState()
    val observacoes by viewModel.observacoes.collectAsState()
    val statusSelecionado by viewModel.statusSelecionado.collectAsState()
    val registrosPendentes by viewModel.registrosPendentes.collectAsState()
    
    // Estados da sincronização
    val isSyncing by syncManager.isSyncing.collectAsState()
    val syncErrors by syncManager.syncErrors.collectAsState()
    
    // Efeito para iniciar sincronização automática
    LaunchedEffect(Unit) {
        syncManager.startAutoSync()
    }
    
    // Efeito para limpar o SyncManager quando a tela for destruída
    DisposableEffect(Unit) {
        onDispose {
            syncManager.destroy()
        }
    }
    
    // Simular obtenção de coordenadas (em um app real, usaria LocationManager)
    LaunchedEffect(Unit) {
        // Coordenadas de exemplo - em um app real, obteria do GPS
        viewModel.atualizarCoordenadas(-23.5505, -46.6333, 10.0f)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Ponto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Indicador de sincronização
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        val pendingCount = registrosPendentes.count { !it.sincronizado }
                        if (pendingCount > 0) {
                            Badge(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("$pendingCount")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Informações do ponto
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ponto de Coleta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pontoColetaNome,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Coordenadas
                    coordenadasAtuais?.let { coords ->
                        Text(
                            text = "Coordenadas: ${String.format("%.6f", coords.latitude)}, ${String.format("%.6f", coords.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        coords.precisao?.let { precisao ->
                            Text(
                                text = "Precisão: ${precisao}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Seleção de status
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Status da Coleta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    viewModel.getStatusOptions().forEach { status ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = statusSelecionado == status,
                                onClick = { viewModel.selecionarStatus(status) }
                            )
                            Text(
                                text = viewModel.getStatusDisplayName(status),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Fotos
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fotos (${fotosCapturadas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedButton(
                            onClick = {
                                // Em um app real, abriria a câmera
                                viewModel.adicionarFoto("foto_${System.currentTimeMillis()}.jpg")
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar Foto")
                        }
                    }
                    
                    if (fotosCapturadas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        fotosCapturadas.forEach { foto ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = foto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.removerFoto(foto) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remover")
                                }
                            }
                        }
                    }
                }
            }
            
            // Observações
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Observações",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = observacoes,
                        onValueChange = viewModel::atualizarObservacoes,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Adicione observações sobre a coleta...") },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
            
            // Mensagens de erro e sucesso
            if (errorMessage.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            if (successMessage.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Erros de sincronização
            if (syncErrors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Erros de Sincronização:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        syncErrors.forEach { error ->
                            Text(
                                text = "• $error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            
            // Botões de ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearMessages()
                        syncManager.clearSyncErrors()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpar")
                }
                
                Button(
                    onClick = {
                        viewModel.registrarPonto(
                            pontoColetaId = pontoColetaId,
                            onSuccess = { registro ->
                                // Sucesso - pode navegar de volta ou mostrar confirmação
                            },
                            onError = { erro ->
                                // Erro já é tratado pelo ViewModel
                            }
                        )
                    },
                    modifier = Modifier.weight(2f),
                    enabled = !isLoading && viewModel.isFormularioValido()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrar Ponto")
                    }
                }
            }
            
            // Informações de sincronização
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sincronização",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val pendingCount = registrosPendentes.count { !it.sincronizado }
                    Text(
                        text = "Registros pendentes: $pendingCount",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    syncManager.getLastSyncTimeFormatted()?.let { lastSync ->
                        Text(
                            text = "Última sincronização: $lastSync",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (pendingCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                // Forçar sincronização
                                // Em um app real, isso seria feito em uma corrotina
                            },
                            enabled = !isSyncing && syncManager.isNetworkAvailable()
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sincronizar Agora")
                        }
                    }
                }
            }
        }
    }
} 