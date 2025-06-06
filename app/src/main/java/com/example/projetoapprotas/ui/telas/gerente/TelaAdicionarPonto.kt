package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projetoapprotas.service.EnderecoResponse
import com.example.projetoapprotas.service.ViaCepService
import com.example.projetoapprotas.ui.componentes.BotaoVoltar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarPonto(
    onVoltarClick: () -> Unit,
    onPontoSalvo: () -> Unit = {}
) {
    var cep by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var coordenadas by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var enderecoCompleto by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service = remember { retrofit.create(ViaCepService::class.java) }

    val isFormValid = nome.isNotBlank() &&
            descricao.isNotBlank() &&
            cep.length == 8 &&
            enderecoCompleto.isNotBlank()

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
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // Header com botão voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoVoltar(onVoltarClick)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Cadastro de Ponto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // Card principal do formulário
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Título do formulário
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Informações do Ponto",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        // Seção de localização
                        Text(
                            text = "LOCALIZAÇÃO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Campo CEP
                        OutlinedTextField(
                            value = cep,
                            onValueChange = {
                                if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                                    cep = it
                                    erro = null
                                    successMessage = null
                                }
                            },
                            label = { Text("CEP") },
                            placeholder = { Text("12345678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "CEP",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        // Botão buscar endereço
                        Button(
                            onClick = {
                                if (cep.length == 8) {
                                    isLoading = true
                                    erro = null
                                    successMessage = null

                                    service.buscarEndereco(cep).enqueue(object : Callback<EnderecoResponse> {
                                        override fun onResponse(
                                            call: Call<EnderecoResponse>,
                                            response: Response<EnderecoResponse>
                                        ) {
                                            isLoading = false
                                            if (response.isSuccessful) {
                                                val endereco = response.body()
                                                if (endereco?.erro == null) {
                                                    nome = endereco?.logradouro ?: ""
                                                    enderecoCompleto = listOfNotNull(
                                                        endereco?.logradouro,
                                                        endereco?.bairro,
                                                        endereco?.localidade,
                                                        endereco?.uf
                                                    ).joinToString(", ")
                                                    descricao = "${endereco?.bairro}, ${endereco?.localidade} - ${endereco?.uf}"
                                                    successMessage = "Endereço encontrado com sucesso!"
                                                } else {
                                                    erro = "CEP não encontrado"
                                                }
                                            } else {
                                                erro = "CEP inválido ou não encontrado"
                                            }
                                        }

                                        override fun onFailure(call: Call<EnderecoResponse>, t: Throwable) {
                                            isLoading = false
                                            erro = "Erro de conexão. Verifique sua internet."
                                        }
                                    })
                                } else {
                                    erro = "CEP deve conter 8 dígitos"
                                }
                            },
                            enabled = cep.length == 8 && !isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Buscando...")
                            } else {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Buscar Endereço")
                            }
                        }

                        // Mensagens de erro e sucesso
                        erro?.let { errorMsg ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMsg,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        successMessage?.let { successMsg ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = successMsg,
                                        color = Color(0xFF2E7D32),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        // Seção de detalhes
                        Text(
                            text = "DETALHES DO PONTO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Nome do ponto
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome do Ponto") },
                            placeholder = { Text("Ex: Rua das Flores, 123") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Nome",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        // Descrição/Localização
                        OutlinedTextField(
                            value = descricao,
                            onValueChange = { descricao = it },
                            label = { Text("Descrição/Localização") },
                            placeholder = { Text("Bairro, Cidade - Estado") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Descrição",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        // Observações
                        OutlinedTextField(
                            value = observacoes,
                            onValueChange = { observacoes = it },
                            label = { Text("Observações (Opcional)") },
                            placeholder = { Text("Informações adicionais sobre o ponto") },
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Observações",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão cancelar
                    OutlinedButton(
                        onClick = onVoltarClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Botão salvar
                    Button(
                        onClick = {
                            isSaving = true
                            // Simular salvamento
                            println("Salvando ponto: nome=$nome, desc=$descricao, cep=$cep, obs=$observacoes")

                            // Aqui você implementaria a lógica de salvamento real
                            // Por enquanto, apenas simula um delay e chama o callback
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                delay(1000) // Simula operação
                                isSaving = false
                                onPontoSalvo()
                            }
                        },
                        enabled = isFormValid && !isSaving,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salvando...")
                        } else {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Salvar Ponto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}