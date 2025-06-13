package com.example.projetoapprotas.ui.telas.motorista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHomeMotorista(
    onRotaDiaClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onConfiguracoesClick: () -> Unit = {},
    onSobreClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    nomeMotorista: String = "João Silva"
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentTime = Calendar.getInstance()
    val greeting = when (currentTime.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
    val dateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTime.time)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                nomeMotorista = nomeMotorista,
                onPerfilClick = {
                    scope.launch { drawerState.close() }
                    onPerfilClick()
                },
                onRotaDiaClick = {
                    scope.launch { drawerState.close() }
                    onRotaDiaClick()
                },
                onRelatoriosClick = {
                    scope.launch { drawerState.close() }
                    onRelatoriosClick()
                },
                onConfiguracoesClick = {
                    scope.launch { drawerState.close() }
                    onConfiguracoesClick()
                },
                onSobreClick = {
                    scope.launch { drawerState.close() }
                    onSobreClick()
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    onLogoutClick()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "App Rotas",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /* Notificações */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificações"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                // Header com saudação personalizada
                HeaderSection(
                    greeting = greeting,
                    nomeMotorista = nomeMotorista,
                    date = dateFormatted
                )

                // Cards principais
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MotoristaCardProfessional(
                        title = "Rota do Dia",
                        description = "Visualize seus pontos de coleta e registre o progresso em tempo real",
                        icon = Icons.Default.Place,
                        gradientColors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF2E7D32)
                        ),
                        onClick = onRotaDiaClick
                    )

                    MotoristaCardProfessional(
                        title = "Relatórios",
                        description = "Acesse histórico completo e análises das suas rotas realizadas",
                        icon = Icons.Default.Menu,
                        gradientColors = listOf(
                            Color(0xFF2196F3),
                            Color(0xFF1565C0)
                        ),
                        onClick = onRelatoriosClick
                    )
                }

                // Cards de status e estatísticas
                StatusSection()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DrawerContent(
    nomeMotorista: String,
    onPerfilClick: () -> Unit,
    onRotaDiaClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onConfiguracoesClick: () -> Unit,
    onSobreClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp)
    ) {
        // Header do drawer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(32.dp)
                    )
                    .padding(16.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nomeMotorista,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Motorista",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Itens do menu principal
        DrawerMenuItem(
            icon = Icons.Default.Place,
            title = "Rota do Dia",
            onClick = onRotaDiaClick
        )

        DrawerMenuItem(
            icon = Icons.Default.Menu,
            title = "Relatórios",
            onClick = onRelatoriosClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        // Meu Perfil (abaixo da linha)
        DrawerMenuItem(
            icon = Icons.Default.Person,
            title = "Meu Perfil",
            onClick = onPerfilClick
        )

        // Itens secundários
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            title = "Configurações",
            onClick = onConfiguracoesClick
        )

        DrawerMenuItem(
            icon = Icons.Default.Info,
            title = "Sobre",
            onClick = onSobreClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout
        DrawerMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Sair",
            onClick = onLogoutClick,
            isDestructive = true
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        label = {
            Text(
                text = title,
                color = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun HeaderSection(
    greeting: String,
    nomeMotorista: String,
    date: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "$greeting, $nomeMotorista!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MotoristaCardProfessional(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun StatusSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AddCircle,
            title = "Veículo",
            status = "Operacional",
            color = Color(0xFF4CAF50)
        )

        StatusCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Info,
            title = "Rotas",
            status = "3 Hoje",
            color = Color(0xFF2196F3)
        )
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    status: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = status,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaHomeMotoristaProfissionalPreview() {
    MaterialTheme {
        TelaHomeMotorista(
            onRotaDiaClick = {},
            onRelatoriosClick = {},
            onLogoutClick = {}
        )
    }
}