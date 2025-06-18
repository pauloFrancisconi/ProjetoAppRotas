package com.example.pontual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.Driver
import com.example.pontual.api.models.DriverRequest
import com.example.pontual.repository.DriverRepository
import kotlinx.coroutines.launch
import com.example.pontual.api.models.RegisterDriverRequest
import com.example.pontual.api.ApiClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverFormScreen(
    driverId: Int? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DriverRepository() }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(driverId != null) }

    fun loadDriver(id: Int) {
        scope.launch {
            isLoading = true
            repository.getDriver(id).fold(
                onSuccess = { driver ->
                    name = driver.name
                    email = driver.email
                    phone = driver.phone ?: ""
                    licenseNumber = driver.licenseNumber ?: ""
                    vehiclePlate = driver.vehiclePlate ?: ""
                    vehicleModel = driver.vehicleModel ?: ""
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro ao carregar motorista"
                }
            )
            isLoading = false
        }
    }

    fun saveDriver() {
        if (name.isBlank() || email.isBlank()) {
            showError = true
            errorMessage = "Nome e email são obrigatórios"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError = true
            errorMessage = "Email inválido"
            return
        }
        if (!isEditing && (password.isBlank() || confirmPassword.isBlank())) {
            showError = true
            errorMessage = "Senha e confirmação são obrigatórias"
            return
        }
        if (!isEditing && password != confirmPassword) {
            showError = true
            errorMessage = "As senhas não conferem"
            return
        }
        scope.launch {
            isLoading = true
            showError = false
            if (isEditing) {
                val request = DriverRequest(
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim().takeIf { it.isNotBlank() },
                    licenseNumber = licenseNumber.trim().takeIf { it.isNotBlank() },
                    vehiclePlate = vehiclePlate.trim().takeIf { it.isNotBlank() },
                    vehicleModel = vehicleModel.trim().takeIf { it.isNotBlank() }
                )
                val result = repository.updateDriver(driverId!!, request)
                result.fold(
                    onSuccess = { onNavigateBack() },
                    onFailure = { exception ->
                        showError = true
                        errorMessage = exception.message ?: "Erro ao salvar motorista"
                    }
                )
            } else {
                try {
                    val response = ApiClient.apiService.registerDriver(
                        RegisterDriverRequest(
                            name = name.trim(),
                            email = email.trim(),
                            password = password,
                            confirmPassword = confirmPassword,
                            phone = phone.trim().takeIf { it.isNotBlank() },
                            licenseNumber = licenseNumber.trim().takeIf { it.isNotBlank() },
                            vehiclePlate = vehiclePlate.trim().takeIf { it.isNotBlank() },
                            vehicleModel = vehicleModel.trim().takeIf { it.isNotBlank() }
                        )
                    )
                    if (response.isSuccessful) {
                        onNavigateBack()
                    } else {
                        showError = true
                        errorMessage = response.errorBody()?.string() ?: "Erro ao cadastrar motorista"
                    }
                } catch (e: Exception) {
                    showError = true
                    errorMessage = e.message ?: "Erro ao cadastrar motorista"
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(driverId) {
        if (driverId != null) {
            loadDriver(driverId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Motorista" else "Novo Motorista") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { saveDriver() },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Salvar")
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
                .verticalScroll(rememberScrollState())
        ) {
            if (showError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome Completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isEditing) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("Número da CNH") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = vehiclePlate,
                onValueChange = { vehiclePlate = it },
                label = { Text("Placa do Veículo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Modelo do Veículo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { saveDriver() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditing) "Atualizar" else "Criar")
                }
            }
        }
    }
} 