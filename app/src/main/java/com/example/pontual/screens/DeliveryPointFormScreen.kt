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
import androidx.compose.ui.unit.dp
import com.example.pontual.api.models.DeliveryPoint
import com.example.pontual.api.models.DeliveryPointRequest
import com.example.pontual.repository.DeliveryPointRepository
import com.example.pontual.components.ValidationError
import com.example.pontual.components.SuccessMessage
import com.example.pontual.components.LoadingButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPointFormScreen(
    pointId: Int? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DeliveryPointRepository() }
    
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(pointId != null) }

    fun loadDeliveryPoint(id: Int) {
        scope.launch {
            isLoading = true
            repository.getDeliveryPoint(id).fold(
                onSuccess = { point ->
                    name = point.name
                    address = point.address
                    contactName = point.contactName ?: ""
                    contactPhone = point.contactPhone ?: ""
                    notes = point.notes ?: ""
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro ao carregar ponto"
                }
            )
            isLoading = false
        }
    }

    fun saveDeliveryPoint() {
        if (name.isBlank() || address.isBlank()) {
            showError = true
            errorMessage = "Nome e endereço são obrigatórios"
            return
        }

        scope.launch {
            isLoading = true
            showError = false
            showSuccess = false
            
            val request = DeliveryPointRequest(
                name = name.trim(),
                address = address.trim(),
                contactName = contactName.trim().takeIf { it.isNotBlank() },
                contactPhone = contactPhone.trim().takeIf { it.isNotBlank() },
                notes = notes.trim().takeIf { it.isNotBlank() }
            )

            val result = if (isEditing) {
                repository.updateDeliveryPoint(pointId!!, request)
            } else {
                repository.createDeliveryPoint(request)
            }

            result.fold(
                onSuccess = {
                    showSuccess = true
                    successMessage = if (isEditing) "Ponto atualizado com sucesso!" else "Ponto criado com sucesso!"
                    kotlinx.coroutines.delay(1500)
                    onNavigateBack()
                },
                onFailure = { exception ->
                    showError = true
                    errorMessage = exception.message ?: "Erro ao salvar ponto"
                }
            )
            isLoading = false
        }
    }

    LaunchedEffect(pointId) {
        if (pointId != null) {
            loadDeliveryPoint(pointId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Ponto" else "Novo Ponto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { saveDeliveryPoint() },
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
                ValidationError(message = errorMessage)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (showSuccess) {
                SuccessMessage(message = successMessage)
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Endereço *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contactName,
                onValueChange = { contactName = it },
                label = { Text("Nome do Contato") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = { Text("Telefone do Contato") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoadingButton(
                text = if (isEditing) "Atualizar" else "Criar",
                isLoading = isLoading,
                onClick = { saveDeliveryPoint() }
            )
        }
    }
} 