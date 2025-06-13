package com.example.projetoapprotas.ui.telas.motorista

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ConfiguracaoPermissao(
    val titulo: String,
    val descricao: String,
    val icon: ImageVector,
    val permissao: String,
    val isEssencial: Boolean = false,
    val corPrimaria: Color,
    val corSecundaria: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConfiguracoes(
    onVoltarClick: () -> Unit
) {
    val context = LocalContext.current
    val permissoesMotorista = remember { PermissoesMotorista() }

    // Estados para controlar as permissões com animação
    var permissaoCamera by remember {
        mutableStateOf(permissoesMotorista.verificarPermissaoCamera(context))
    }
    var permissaoLocalizacao by remember {
        mutableStateOf(permissoesMotorista.verificarPermissaoLocalizacao(context))
    }

    // Lista de configurações de permissões com cores personalizadas
    val configuracoesPermissoes = listOf(
        ConfiguracaoPermissao(
            titulo = "Câmera",
            descricao = "Capture fotos dos pontos de coleta com qualidade profissional",
            icon = Icons.Default.AccountBox,
            permissao = PermissoesMotorista.CAMERA_PERMISSION,
            isEssencial = true,
            corPrimaria = Color(0xFF6366F1),
            corSecundaria = Color(0xFF8B5CF6)
        ),
        ConfiguracaoPermissao(
            titulo = "Localização",
            descricao = "Navegação GPS precisa e rastreamento em tempo real das rotas",
            icon = Icons.Default.LocationOn,
            permissao = PermissoesMotorista.LOCATION_FINE_PERMISSION,
            isEssencial = true,
            corPrimaria = Color(0xFF10B981),
            corSecundaria = Color(0xFF059669)
        )
    )

    // Launcher para solicitar permissões
    val launcherPermissoes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        permissaoCamera = permissoesMotorista.verificarPermissaoCamera(context)
        permissaoLocalizacao = permissoesMotorista.verificarPermissaoLocalizacao(context)
    }

    // Estado para mostrar diálogos
    var mostrarDialogoConfiguracoes by remember { mutableStateOf(false) }
    var permissaoParaConfigurar by remember { mutableStateOf<ConfiguracaoPermissao?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFEFF6FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header personalizado
            ModernHeader(onVoltarClick = onVoltarClick)

            Spacer(modifier = Modifier.height(24.dp))

            // Cards de permissões
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedPermissionCard(
                    configuracao = configuracoesPermissoes[0],
                    isPermitido = permissaoCamera,
                    onTogglePermission = {
                        if (permissaoCamera) {
                            permissaoParaConfigurar = configuracoesPermissoes[0]
                            mostrarDialogoConfiguracoes = true
                        } else {
                            launcherPermissoes.launch(arrayOf(PermissoesMotorista.CAMERA_PERMISSION))
                        }
                    }
                )

                AnimatedPermissionCard(
                    configuracao = configuracoesPermissoes[1],
                    isPermitido = permissaoLocalizacao,
                    onTogglePermission = {
                        if (permissaoLocalizacao) {
                            permissaoParaConfigurar = configuracoesPermissoes[1]
                            mostrarDialogoConfiguracoes = true
                        } else {
                            launcherPermissoes.launch(
                                arrayOf(
                                    PermissoesMotorista.LOCATION_FINE_PERMISSION,
                                    PermissoesMotorista.LOCATION_COARSE_PERMISSION
                                )
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status geral modernizado
            ModernStatusSection(
                permissaoCamera = permissaoCamera,
                permissaoLocalizacao = permissaoLocalizacao,
                onVerificarTudo = {
                    launcherPermissoes.launch(PermissoesMotorista.PERMISSOES_NECESSARIAS)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Seção de dicas
            ModernTipsSection()

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Diálogo moderno
    if (mostrarDialogoConfiguracoes && permissaoParaConfigurar != null) {
        ModernAlertDialog(
            permissao = permissaoParaConfigurar!!,
            onDismiss = {
                mostrarDialogoConfiguracoes = false
                permissaoParaConfigurar = null
            },
            onConfirm = {
                abrirConfiguracoesApp(context)
                mostrarDialogoConfiguracoes = false
                permissaoParaConfigurar = null
            }
        )
    }
}

@Composable
fun ModernHeader(onVoltarClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
                .clickable(onClick = { onVoltarClick() }) // Removed ripple and indication
                .padding(10.dp), // Add padding for visual feedback
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF475569),
                modifier = Modifier.size(20.dp)
            )
        }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Configurações",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    fontSize = 26.sp
                )
                Text(
                    text = "Gerencie suas permissões",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    fontSize = 16.sp
                )
            }

        }
    }
}

@Composable
fun AnimatedPermissionCard(
    configuracao: ConfiguracaoPermissao,
    isPermitido: Boolean,
    onTogglePermission: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPermitido) 1f else 0.98f,
        animationSpec = tween(300),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onTogglePermission() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPermitido) 12.dp else 8.dp
        )
    ) {
        Box {
            // Background gradient sutil
            if (isPermitido) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    configuracao.corPrimaria.copy(alpha = 0.05f),
                                    configuracao.corSecundaria.copy(alpha = 0.08f)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone moderno
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (isPermitido) {
                                        listOf(
                                            configuracao.corPrimaria.copy(alpha = 0.2f),
                                            configuracao.corSecundaria.copy(alpha = 0.1f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFEF4444).copy(alpha = 0.15f),
                                            Color(0xFFDC2626).copy(alpha = 0.1f)
                                        )
                                    }
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = configuracao.icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = if (isPermitido) configuracao.corPrimaria else Color(0xFFEF4444)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = configuracao.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                fontSize = 20.sp
                            )

                            if (configuracao.isEssencial) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "Essencial",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFD97706),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = configuracao.descricao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status com animação
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (isPermitido) Color(0xFF10B981) else Color(0xFFEF4444),
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (isPermitido) "Ativo" else "Inativo",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isPermitido) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontSize = 14.sp
                        )
                    }

                    // Botão de ação moderno
                    Button(
                        onClick = onTogglePermission,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPermitido) configuracao.corPrimaria else Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (isPermitido) "Gerenciar" else "Ativar",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStatusSection(
    permissaoCamera: Boolean,
    permissaoLocalizacao: Boolean,
    onVerificarTudo: () -> Unit
) {
    val todasPermitidas = permissaoCamera && permissaoLocalizacao

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box {
            if (todasPermitidas) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF10B981).copy(alpha = 0.08f),
                                    Color(0xFF059669).copy(alpha = 0.05f)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícone central
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (todasPermitidas) {
                                    listOf(
                                        Color(0xFF10B981).copy(alpha = 0.2f),
                                        Color(0xFF059669).copy(alpha = 0.1f)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFEF4444).copy(alpha = 0.2f),
                                        Color(0xFFDC2626).copy(alpha = 0.1f)
                                    )
                                }
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (todasPermitidas) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = if (todasPermitidas) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (todasPermitidas) "Tudo Pronto!" else "Configuração Necessária",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (todasPermitidas)
                        "Todas as permissões estão ativas e funcionando perfeitamente"
                    else
                        "Configure as permissões para usar todos os recursos do app",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                if (!todasPermitidas) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onVerificarTudo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Configurar Todas",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernTipsSection() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dicas Importantes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            fontSize = 20.sp
        )

        ModernTipCard(
            icon = Icons.Default.Lock,
            titulo = "Privacidade Protegida",
            descricao = "Suas informações são mantidas seguras e usadas apenas para o funcionamento do app."
        )

        ModernTipCard(
            icon = Icons.Default.Check,
            titulo = "Performance Otimizada",
            descricao = "As permissões melhoram significativamente a velocidade e precisão do app."
        )
    }
}

@Composable
fun ModernTipCard(
    icon: ImageVector,
    titulo: String,
    descricao: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF3B82F6).copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ModernAlertDialog(
    permissao: ConfiguracaoPermissao,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        permissao.corPrimaria.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = permissao.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = permissao.corPrimaria
                )
            }
        },
        title = {
            Text(
                text = "Gerenciar ${permissao.titulo}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        },
        text = {
            Text(
                text = "Você será direcionado para as configurações do sistema onde poderá gerenciar esta permissão.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
                lineHeight = 24.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = permissao.corPrimaria
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Ir para Configurações",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF64748B)
                )
            ) {
                Text(
                    text = "Cancelar",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

// Função utilitária para abrir as configurações do app
fun abrirConfiguracoesApp(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun TelaConfiguracoesPreview() {
    MaterialTheme {
        TelaConfiguracoes(
            onVoltarClick = {}
        )
    }
}