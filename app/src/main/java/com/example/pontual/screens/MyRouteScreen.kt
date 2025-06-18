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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.pontual.PreferenceManager
import com.example.pontual.api.models.Route
import com.example.pontual.api.models.RoutePoint
import com.example.pontual.repository.RouteRepository
import com.example.pontual.repository.DeliveryRepository
import com.example.pontual.api.models.DeliveryRequest
import com.example.pontual.components.LoadingScreen
import com.example.pontual.components.ErrorScreen
import com.example.pontual.components.PontualTopAppBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { RouteRepository() }
    val deliveryRepository = remember { DeliveryRepository() }
    
    val assignedRouteId = PreferenceManager.getAssignedRouteId(context)
    val assignedRouteName = PreferenceManager.getAssignedRouteName(context)
    
    var route by remember { mutableStateOf<Route?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showFinishDialog by remember { mutableStateOf(false) }
    var showCompletePointDialog by remember { mutableStateOf<RoutePoint?>(null) }
    var showPhotoOption by remember { mutableStateOf(false) }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var completedPointsCount by remember { mutableStateOf(0) }
    
    val currentCompletedCount = remember(route) {
        PreferenceManager.getCompletedPointsCount(context)
    }
    
    LaunchedEffect(showCompletePointDialog) {
        if (showCompletePointDialog == null) {
            completedPointsCount = PreferenceManager.getCompletedPointsCount(context)
        }
    }
    
    fun createImageUri(): android.net.Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        selectedPhotoUri = imageFile.absolutePath
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
        }
    }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoLauncher.launch(createImageUri())
        }
    }
    
    fun openCamera() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                photoLauncher.launch(createImageUri())
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun loadRoute() {
        assignedRouteId?.let { routeId ->
            scope.launch {
                isLoading = true
                showError = false
                
                repository.getRoute(routeId).fold(
                    onSuccess = { routeData ->
                        route = routeData
                        completedPointsCount = PreferenceManager.getCompletedPointsCount(context)
                    },
                    onFailure = { exception ->
                        showError = true
                        errorMessage = exception.message ?: "Erro desconhecido"
                    }
                )
                isLoading = false
            }
        }
    }

    LaunchedEffect(assignedRouteId) {
        if (assignedRouteId != null) {
            loadRoute()
        } else {
            isLoading = false
            showError = true
            errorMessage = "Nenhuma rota atribuída"
        }
    }

    LaunchedEffect(route) {
        if (route != null) {
            completedPointsCount = PreferenceManager.getCompletedPointsCount(context)
        }
    }

    Scaffold(
        topBar = {
            PontualTopAppBar(
                title = "Minha Rota",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (route != null) {
                        IconButton(
                            onClick = { 
                                if (completedPointsCount >= route!!.points.size) {
                                    showFinishDialog = true
                                } else {
                                    showError = true
                                    errorMessage = "Complete todos os pontos antes de finalizar a rota!"
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Finalizar Rota",
                                tint = if (completedPointsCount >= route!!.points.size)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
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
                    LoadingScreen(message = "Carregando sua rota...")
                }
                showError -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { loadRoute() }
                    )
                }
                route == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Rota não encontrada",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            RouteInfoCard(route = route!!)
                        }
                        
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                                            imageVector = Icons.Default.Map,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Sequência de Entregas",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Siga a ordem dos pontos para otimizar sua rota",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        
                        items(route!!.points.sortedBy { it.sequence }) { routePoint ->
                            RoutePointCard(
                                routePoint = routePoint,
                                position = routePoint.sequence,
                                isCompleted = PreferenceManager.isPointCompleted(context, routePoint.deliveryPointId),
                                photoUri = PreferenceManager.getPointPhoto(context, routePoint.deliveryPointId),
                                onComplete = { showCompletePointDialog = routePoint }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val allPointsCompleted = completedPointsCount >= route!!.points.size
                            val progressPercentage = if (route!!.points.size > 0) (completedPointsCount.toFloat() / route!!.points.size * 100).toInt() else 0
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (allPointsCompleted) 
                                        MaterialTheme.colorScheme.primaryContainer 
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (allPointsCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                                contentDescription = null,
                                                modifier = Modifier.size(28.dp),
                                                tint = if (allPointsCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = "Progresso da Rota",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = if (allPointsCompleted) "Rota Completa!" else "Em Progresso",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (allPointsCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                        
                                        Surface(
                                            shape = RoundedCornerShape(30.dp),
                                            color = if (allPointsCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            modifier = Modifier.size(60.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = "${progressPercentage}%",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (allPointsCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${completedPointsCount}/${route!!.points.size}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = if (allPointsCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    Column {
                                        LinearProgressIndicator(
                                            progress = if (route!!.points.size > 0) completedPointsCount.toFloat() / route!!.points.size else 0f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (allPointsCompleted) 
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                else 
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (allPointsCompleted) {
                                                    Icon(
                                                        imageVector = Icons.Default.TaskAlt,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = "🎉 Parabéns!",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Todos os pontos foram concluídos! Você pode finalizar a rota.",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Schedule,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = "Continue o bom trabalho!",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        Text(
                                                            text = "Complete todos os pontos para finalizar a rota.",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { showFinishDialog = true },
                                enabled = completedPointsCount >= route!!.points.size,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (completedPointsCount >= route!!.points.size) 
                                        "Finalizar Rota" else "Complete todos os pontos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text(
                    text = "Finalizar Rota",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Tem certeza que deseja finalizar a rota:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = assignedRouteName ?: "Rota sem nome",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta ação irá desatribuir a rota de você. Certifique-se de que todas as entregas foram concluídas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            route?.let { currentRoute ->
                                try {
                                    var deliveriesCreated = 0
                                    currentRoute.points.forEach { routePoint ->
                                        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).format(Date())
                                        val deliveryRequest = DeliveryRequest(
                                            routeId = currentRoute.id,
                                            driverId = null,
                                            scheduledDate = now,
                                            notes = "Entrega para: ${routePoint.deliveryPoint.name} - ${routePoint.deliveryPoint.address}"
                                        )
                                        val result = deliveryRepository.createDelivery(deliveryRequest)
                                        result.fold(
                                            onSuccess = { deliveriesCreated++ },
                                            onFailure = { exception ->
                                                println("Erro ao criar entrega: ${exception.message}")
                                            }
                                        )
                                    }
                                    
                                    if (deliveriesCreated == 0) {
                                        errorMessage = "Aviso: Não foi possível criar as entregas"
                                        showError = true
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Erro ao criar entregas: ${e.message}"
                                    showError = true
                                }
                            }
                            
                            PreferenceManager.finishRoute(context)
                            showFinishDialog = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Finalizar e Criar Entregas")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFinishDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    showCompletePointDialog?.let { routePoint ->
        AlertDialog(
            onDismissRequest = { 
                showCompletePointDialog = null
                selectedPhotoUri = null
            },
            title = {
                Text(
                    text = "Completar Ponto",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Confirma a conclusão do ponto:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = routePoint.deliveryPoint.name,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = routePoint.deliveryPoint.address,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Foto (Opcional)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (selectedPhotoUri != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Foto selecionada",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    TextButton(
                                        onClick = { selectedPhotoUri = null }
                                    ) {
                                        Text("Remover")
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { openCamera() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Adicionar Foto")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        PreferenceManager.completePoint(
                            context, 
                            routePoint.deliveryPointId, 
                            selectedPhotoUri
                        )
                        completedPointsCount = PreferenceManager.getCompletedPointsCount(context)
                        showCompletePointDialog = null
                        selectedPhotoUri = null
                    }
                ) {
                    Text("Completar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showCompletePointDialog = null
                        selectedPhotoUri = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RouteInfoCard(
    route: Route,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (route.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    text = "${route.points.size} pontos"
                )
                
                if (route.estimatedDuration != null) {
                    InfoChip(
                        icon = Icons.Default.AccessTime,
                        text = "${route.estimatedDuration} min"
                    )
                }
                
                if (route.totalDistance != null) {
                    InfoChip(
                        icon = Icons.Default.DirectionsCar,
                        text = "${route.totalDistance} km"
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable 
fun RoutePointCard(
    routePoint: RoutePoint,
    position: Int,
    isCompleted: Boolean = false,
    photoUri: String? = null,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 6.dp else 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Concluído",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = position.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = routePoint.deliveryPoint.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (isCompleted) {
                            Text(
                                text = "✅ Concluído",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "⏳ Pendente",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (!isCompleted) {
                    Button(
                        onClick = onComplete,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Completar",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = routePoint.deliveryPoint.address,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    if (routePoint.deliveryPoint.contactName != null || routePoint.deliveryPoint.contactPhone != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (routePoint.deliveryPoint.contactName != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = routePoint.deliveryPoint.contactName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        if (routePoint.deliveryPoint.contactPhone != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = routePoint.deliveryPoint.contactPhone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    if (routePoint.deliveryPoint.notes != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = routePoint.deliveryPoint.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            if (photoUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📸 Foto anexada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
} 