package com.example.projetoapprotas.ui.telas.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdminHome(
    onCadastroPontosClick: () -> Unit,
    onCadastroRotasClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onConfiguracoesClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    nomeAdmin: String = "Admin"
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompactScreen = screenWidth < 600.dp

    val currentTime = Calendar.getInstance()
    val greeting = when (currentTime.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
    val dateFormatted = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        .format(currentTime.time)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            // Header com informações do usuário
            AdminHeaderSection(
                greeting = greeting,
                nomeAdmin = nomeAdmin,
                date = dateFormatted,
                onPerfilClick = onPerfilClick,
                onLogoutClick = onLogoutClick,
                isCompactScreen = isCompactScreen
            )

            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = if (isCompactScreen) 16.dp else 24.dp)
            ) {
                // Seção de estatísticas rápidas
                AdminStatsSection(isCompactScreen = isCompactScreen)

                Spacer(modifier = Modifier.height(if (isCompactScreen) 24.dp else 32.dp))

                // Título das funcionalidades
                Text(
                    text = "Painel de Controle",
                    style = if (isCompactScreen)
                        MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                    else
                        MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Cards principais de funcionalidades
                Column(
                    verticalArrangement = Arrangement.spacedBy(if (isCompactScreen) 12.dp else 16.dp)
                ) {
                    AdminCardProfessional(
                        title = "Cadastro de Pontos",
                        description = "Cadastre novos pontos de coleta com localização precisa e informações detalhadas",
                        icon = Icons.Default.Place,
                        gradientColors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF2E7D32)
                        ),
                        onClick = onCadastroPontosClick,
                        stats = "12 pontos ativos",
                        isCompactScreen = isCompactScreen
                    )

                    AdminCardProfessional(
                        title = "Cadastro de Rotas",
                        description = "Defina rotas otimizadas e atribua motoristas para cada dia da semana",
                        icon = Icons.Default.Build,
                        gradientColors = listOf(
                            Color(0xFF2196F3),
                            Color(0xFF1565C0)
                        ),
                        onClick = onCadastroRotasClick,
                        stats = "8 rotas configuradas",
                        isCompactScreen = isCompactScreen
                    )

                    AdminCardProfessional(
                        title = "Relatórios Executivos",
                        description = "Visualize relatórios detalhados de performance e análises das operações",
                        icon = Icons.Default.Menu,
                        gradientColors = listOf(
                            Color(0xFF9C27B0),
                            Color(0xFF6A1B9A)
                        ),
                        onClick = onRelatoriosClick,
                        stats = "Últimos 30 dias",
                        isCompactScreen = isCompactScreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AdminHeaderSection(
    greeting: String,
    nomeAdmin: String,
    date: String,
    onPerfilClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isCompactScreen: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isCompactScreen) 16.dp else 24.dp),
        shape = RoundedCornerShape(if (isCompactScreen) 16.dp else 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompactScreen) 8.dp else 12.dp)
    ) {
        if (isCompactScreen) {
            // Layout compacto para telas pequenas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Primeira linha: Avatar + Info básica + Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar/Ícone do usuário
                    Card(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Informações do usuário
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$greeting, $nomeAdmin!",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Sistema de Gestão de Rotas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    // Botão de logout
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Sair",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Segunda linha: Data
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = date.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        } else {
            // Layout original para telas maiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar/Ícone do usuário
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Informações do usuário
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$greeting, $nomeAdmin!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = date.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Sistema de Gestão de Rotas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }

                // Menu de ações
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Sair",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatsSection(isCompactScreen: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (isCompactScreen) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(if (isCompactScreen) 16.dp else 20.dp)
        ) {
            Text(
                text = "Resumo do Sistema",
                style = if (isCompactScreen)
                    MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                else
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = if (isCompactScreen) 12.dp else 16.dp)
            )

            if (isCompactScreen) {
                // Layout em coluna para telas pequenas
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocationOn,
                            title = "Pontos",
                            value = "12",
                            subtitle = "Ativos",
                            color = Color(0xFF4CAF50),
                            isCompactScreen = isCompactScreen
                        )

                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Settings,
                            title = "Rotas",
                            value = "8",
                            subtitle = "Configuradas",
                            color = Color(0xFF2196F3),
                            isCompactScreen = isCompactScreen
                        )
                    }

                    AdminStatCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Person,
                        title = "Motoristas",
                        value = "5",
                        subtitle = "Cadastrados",
                        color = Color(0xFF9C27B0),
                        isCompactScreen = isCompactScreen
                    )
                }
            } else {
                // Layout original em linha para telas maiores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocationOn,
                        title = "Pontos",
                        value = "12",
                        subtitle = "Ativos",
                        color = Color(0xFF4CAF50),
                        isCompactScreen = isCompactScreen
                    )

                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Settings,
                        title = "Rotas",
                        value = "8",
                        subtitle = "Configuradas",
                        color = Color(0xFF2196F3),
                        isCompactScreen = isCompactScreen
                    )

                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Person,
                        title = "Motoristas",
                        value = "5",
                        subtitle = "Cadastrados",
                        color = Color(0xFF9C27B0),
                        isCompactScreen = isCompactScreen
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    isCompactScreen: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(if (isCompactScreen) 10.dp else 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        if (isCompactScreen) {
            // Layout horizontal para telas pequenas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "$title $subtitle",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Layout vertical original para telas maiores
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AdminCardProfessional(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    stats: String,
    modifier: Modifier = Modifier,
    isCompactScreen: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = if (isCompactScreen) 6.dp else 8.dp,
                shape = RoundedCornerShape(if (isCompactScreen) 16.dp else 20.dp),
                spotColor = gradientColors.first().copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(if (isCompactScreen) 16.dp else 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.1f) }
                    )
                )
        ) {
            if (isCompactScreen) {
                // Layout compacto para telas pequenas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone principal
                        Card(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = gradientColors.first()
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Título e ícone de navegação
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descrição
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Estatística
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = gradientColors.first().copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = stats,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Medium,
                            color = gradientColors.first(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            } else {
                // Layout original para telas maiores
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone principal
                    Card(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = gradientColors.first()
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Conteúdo do card
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Estatística
                        Card(
                            modifier = Modifier.padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = gradientColors.first().copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = stats,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = gradientColors.first(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Ícone de navegação
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaAdminHomePreview() {
    MaterialTheme {
        TelaAdminHome(
            onCadastroPontosClick = {},
            onCadastroRotasClick = {},
            onRelatoriosClick = {},
            onPerfilClick = {},
            onConfiguracoesClick = {},
            onLogoutClick = {},
            nomeAdmin = "João Silva"
        )
    }
}