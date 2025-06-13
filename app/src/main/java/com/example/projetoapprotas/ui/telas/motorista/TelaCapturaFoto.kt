package com.example.projetoapprotas.ui.telas.motorista

import android.content.Context
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class FotoComLocalizacao(
    val uri: Uri,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long,
    val pontoId: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCapturaFoto(
    pontoId: String,
    nomePonto: String,
    onFotoCapturada: (FotoComLocalizacao) -> Unit,
    onVoltarClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localizacaoGPS = remember { LocalizacaoGPS(context) }

    var statusCaptura by remember { mutableStateOf("Pronto para tirar foto") }
    var carregandoLocalizacao by remember { mutableStateOf(false) }
    var fotoCapturada by remember { mutableStateOf<Uri?>(null) }
    var localizacaoAtual by remember { mutableStateOf<Location?>(null) }
    var permissoesOK by remember { mutableStateOf(false) }

    // Criar arquivo temporário para a foto
    val fotoFile = remember {
        val imageDir = File(context.getExternalFilesDir(null), "fotos")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        File(
            imageDir,
            "foto_ponto_${pontoId}_${System.currentTimeMillis()}.jpg"
        )
    }

    val fotoUri = remember {
        try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                fotoFile
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao criar URI da foto: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    // Launcher para captura de foto
    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso && fotoFile.exists()) {
            fotoCapturada = fotoUri
            statusCaptura = "Foto capturada! Obtendo localização..."

            // Obter localização após capturar a foto
            scope.launch {
                carregandoLocalizacao = true
                try {
                    localizacaoAtual = localizacaoGPS.obterLocalizacaoAtual()

                    val fotoComLocalizacao = FotoComLocalizacao(
                        uri = fotoUri!!,
                        latitude = localizacaoAtual?.latitude,
                        longitude = localizacaoAtual?.longitude,
                        timestamp = System.currentTimeMillis(),
                        pontoId = pontoId
                    )

                    statusCaptura = if (localizacaoAtual != null) {
                        "✅ Foto e localização salvas com sucesso!"
                    } else {
                        "⚠️ Foto salva, localização não disponível"
                    }

                    // Chama o callback com os dados da foto
                    onFotoCapturada(fotoComLocalizacao)

                    // Mostra mensagem de sucesso
                    Toast.makeText(
                        context,
                        "Foto do ponto salva com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {
                    statusCaptura = "❌ Erro ao obter localização: ${e.message}"
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    carregandoLocalizacao = false
                }
            }
        } else {
            statusCaptura = "❌ Captura de foto cancelada"
            Toast.makeText(context, "Falha ao capturar foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para permissões
    val launcherPermissoes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val cameraPermitida = permissoes[android.Manifest.permission.CAMERA] ?: false
        val localizacaoPermitida = permissoes[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        permissoesOK = cameraPermitida && localizacaoPermitida

        if (permissoesOK) {
            statusCaptura = "✅ Permissões concedidas. Pronto para tirar foto!"
        } else {
            statusCaptura = "❌ Permissões necessárias não foram concedidas"
            Toast.makeText(
                context,
                "É necessário conceder permissões para câmera e localização",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Solicitar permissões ao iniciar
    LaunchedEffect(Unit) {
        launcherPermissoes.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Capturar Foto do Ponto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Voltar"
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card com informações do ponto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = nomePonto,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ID: $pontoId",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status da captura
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (carregandoLocalizacao) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = when {
                                fotoCapturada != null && localizacaoAtual != null -> Icons.Default.CheckCircle
                                fotoCapturada != null -> Icons.Default.Warning
                                permissoesOK -> Icons.Default.ThumbUp
                                else -> Icons.Default.Clear
                            },
                            contentDescription = null,
                            tint = when {
                                fotoCapturada != null && localizacaoAtual != null -> Color(0xFF4CAF50)
                                fotoCapturada != null -> Color(0xFFFF9800)
                                permissoesOK -> MaterialTheme.colorScheme.primary
                                else -> Color(0xFFF44336)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = statusCaptura,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão para tirar foto
            Button(
                onClick = {
                    if (fotoUri != null && permissoesOK) {
                        statusCaptura = "📸 Abrindo câmera..."
                        try {
                            launcherCamera.launch(fotoUri)
                        } catch (e: Exception) {
                            statusCaptura = "❌ Erro ao abrir câmera: ${e.message}"
                            Toast.makeText(context, "Erro ao abrir câmera: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else if (!permissoesOK) {
                        Toast.makeText(context, "Conceda as permissões primeiro", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erro: URI da foto não disponível", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !carregandoLocalizacao && fotoUri != null && permissoesOK && fotoCapturada == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (fotoCapturada != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (fotoCapturada != null) Icons.Default.CheckCircle else Icons.Default.Face,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when {
                        carregandoLocalizacao -> "Processando..."
                        fotoCapturada != null -> "Foto Capturada!"
                        else -> "Tirar Foto"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botão para voltar após capturar a foto
            if (fotoCapturada != null && !carregandoLocalizacao) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onVoltarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voltar para Rota")
                }
            }

            // Informações sobre localização se disponível
            localizacaoAtual?.let { location ->
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Localização GPS Capturada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Latitude: ${String.format("%.6f", location.latitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Longitude: ${String.format("%.6f", location.longitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        Text(
                            text = "Capturada em: ${dateFormat.format(Date())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}