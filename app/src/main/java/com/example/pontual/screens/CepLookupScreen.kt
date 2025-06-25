package com.example.pontual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.ViaCepAddress
import com.example.pontual.repository.ViaCepRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CepLookupScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cep by remember { mutableStateOf("") }
    var address by remember { mutableStateOf<ViaCepAddress?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val repository = remember { ViaCepRepository() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    fun searchAddress() {
        if (!repository.isValidCep(cep)) {
            errorMessage = "CEP deve ter 8 dígitos"
            return
        }
        
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            address = null
            
            try {
                val response = repository.getAddress(cep)
                if (response.isSuccessful) {
                    val addressData = response.body()
                    if (addressData?.erro == true) {
                        errorMessage = "CEP não encontrado"
                    } else {
                        address = addressData
                    }
                } else {
                    errorMessage = "Erro ao consultar CEP: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
                keyboardController?.hide()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultar CEP") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Digite o CEP para consultar o endereço",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { newValue ->
                            // Limita a 9 caracteres (incluindo hífen) e permite apenas dígitos e hífen
                            if (newValue.length <= 9 && newValue.all { it.isDigit() || it == '-' }) {
                                cep = newValue
                                errorMessage = null
                            }
                        },
                        label = { Text("CEP") },
                        placeholder = { Text("00000-000") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { searchAddress() }
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { searchAddress() },
                                enabled = !isLoading && cep.isNotEmpty()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                                }
                            }
                        },
                        singleLine = true
                    )
                    
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            address?.let { addr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Endereço Encontrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        AddressField("CEP", addr.cep)
                        AddressField("Logradouro", addr.logradouro)
                        if (addr.complemento.isNotEmpty()) {
                            AddressField("Complemento", addr.complemento)
                        }
                        AddressField("Bairro", addr.bairro)
                        AddressField("Cidade", addr.localidade)
                        AddressField("Estado", "${addr.uf} - ${addr.estado}")
                        AddressField("Região", addr.regiao)
                        if (addr.ddd.isNotEmpty()) {
                            AddressField("DDD", addr.ddd)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun AddressField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
} 