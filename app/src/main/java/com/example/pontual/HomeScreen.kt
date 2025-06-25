package com.example.pontual

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToDeliveryPoints: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToDrivers: () -> Unit,
    onNavigateToDeliveries: () -> Unit,
    onNavigateToAvailableRoutes: () -> Unit = {},
    onNavigateToMyRoute: () -> Unit = {},
    onNavigateToCepLookup: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName = PreferenceManager.getUserName(context) ?: "Usuário"
    val isAdmin = PreferenceManager.isAdmin(context)
    val isDriver = PreferenceManager.isDriver(context)
    val hasAssignedRoute = PreferenceManager.hasAssignedRoute(context)
    val assignedRouteName = PreferenceManager.getAssignedRouteName(context)
    val completedPointsCount = PreferenceManager.getCompletedPointsCount(context)

    Scaffold(
        topBar = {
            MinimalTopBar(onLogout = onLogout)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_pontuall),
                contentDescription = "Logo Pontuall",
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pontuall",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bem-vindo, $userName!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isAdmin) {
                MinimalActionButton(
                    text = "Pontos de Entrega",
                    icon = Icons.Default.LocationOn,
                    onClick = onNavigateToDeliveryPoints
                )
                MinimalActionButton(
                    text = "Rotas",
                    icon = Icons.Default.Route,
                    onClick = onNavigateToRoutes
                )
                MinimalActionButton(
                    text = "Motoristas",
                    icon = Icons.Default.People,
                    onClick = onNavigateToDrivers
                )
                MinimalActionButton(
                    text = "Entregas",
                    icon = Icons.Default.LocalShipping,
                    onClick = onNavigateToDeliveries
                )
            } else if (isDriver) {
                if (hasAssignedRoute) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rota Atribuída",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = assignedRouteName ?: "Rota sem nome",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (completedPointsCount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$completedPointsCount pontos concluídos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    
                    MinimalActionButton(
                        text = "Minha Rota",
                        icon = Icons.Default.Route,
                        onClick = onNavigateToMyRoute
                    )
                } else {
                    MinimalActionButton(
                        text = "Rotas Disponíveis",
                        icon = Icons.Default.Route,
                        onClick = onNavigateToAvailableRoutes
                    )
                }
                
                MinimalActionButton(
                    text = "Entregas",
                    icon = Icons.Default.LocalShipping,
                    onClick = onNavigateToDeliveries
                )
                
                MinimalActionButton(
                    text = "Consultar CEP",
                    icon = Icons.Default.Search,
                    onClick = onNavigateToCepLookup
                )
            } else {
                MinimalActionButton(
                    text = "Entregas",
                    icon = Icons.Default.LocalShipping,
                    onClick = onNavigateToDeliveries
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "© 2025 Pontuall",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun MinimalTopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        IconButton(onClick = onLogout) {
            Icon(
                Icons.Default.Logout,
                contentDescription = "Sair",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MinimalActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
} 