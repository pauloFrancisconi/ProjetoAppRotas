package com.example.projetoapprotas.ui.telas.motorista

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PermissoesMotorista {
    companion object {
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val LOCATION_FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val LOCATION_COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

        val PERMISSOES_NECESSARIAS = arrayOf(
            CAMERA_PERMISSION,
            LOCATION_FINE_PERMISSION,
            LOCATION_COARSE_PERMISSION
        )
    }

    fun verificarPermissaoCamera(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun verificarPermissaoLocalizacao(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            LOCATION_FINE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    LOCATION_COARSE_PERMISSION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun verificarTodasPermissoes(context: Context): Boolean {
        return PERMISSOES_NECESSARIAS.all { permissao ->
            ContextCompat.checkSelfPermission(context, permissao) == PackageManager.PERMISSION_GRANTED
        }
    }
}

@Composable
fun GerenciadorPermissoes(
    onPermissoesObtidas: () -> Unit,
    onPermissaoNegada: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissoesMotorista = remember { PermissoesMotorista() }

    var permissoesConcedidas by remember {
        mutableStateOf(permissoesMotorista.verificarTodasPermissoes(context))
    }

    val launcherPermissoes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val todasConcedidas = permissoes.values.all { it }
        permissoesConcedidas = todasConcedidas

        if (todasConcedidas) {
            onPermissoesObtidas()
        } else {
            onPermissaoNegada()
        }
    }

    LaunchedEffect(Unit) {
        if (!permissoesConcedidas) {
            launcherPermissoes.launch(PermissoesMotorista.PERMISSOES_NECESSARIAS)
        }
    }

    if (permissoesConcedidas) {
        content()
    }
}

// Classe para obter localização GPS
class LocalizacaoGPS(private val context: Context) {

    suspend fun obterLocalizacaoAtual(): Location? = suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!PermissoesMotorista().verificarPermissaoLocalizacao(context)) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        try {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            when {
                isGPSEnabled -> {
                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    continuation.resume(lastKnownLocation)
                }
                isNetworkEnabled -> {
                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    continuation.resume(lastKnownLocation)
                }
                else -> {
                    continuation.resume(null)
                }
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
}