package com.example.projetoapprotas.ui.telas.motorista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class PerfilMotorista(
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val cnh: String = "",
    val cpf: String = "",
    val endereco: String = "",
    val cidade: String = "",
    val estado: String = "",
    val cep: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilMotorista(
    onVoltarClick: () -> Unit = {},
    onSalvarClick: (PerfilMotorista) -> Unit = {},
    perfilAtual: PerfilMotorista = PerfilMotorista()
) {
    var perfil by remember { mutableStateOf(perfilAtual) }
    var isEditMode by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltarClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                onSalvarClick(perfil)
                                isEditMode = false
                                showSuccessMessage = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Salvar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                perfil = perfilAtual
                                isEditMode = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { isEditMode = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Mensagem de sucesso
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Perfil atualizado com sucesso!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Avatar e informações básicas
            ProfileHeaderCard(
                nome = perfil.nome.ifEmpty { "João Silva" },
                email = perfil.email.ifEmpty { "joao.silva@email.com" },
                isEditMode = isEditMode
            )

            // Dados Pessoais
            DadosPessoaisCard(
                perfil = perfil,
                isEditMode = isEditMode,
                onPerfilChange = { perfil = it }
            )

            // Dados de Contato
            DadosContatoCard(
                perfil = perfil,
                isEditMode = isEditMode,
                onPerfilChange = { perfil = it }
            )

            // Endereço
            EnderecoCard(
                perfil = perfil,
                isEditMode = isEditMode,
                onPerfilChange = { perfil = it }
            )

            // Seção de Segurança
            if (!isEditMode) {
                SecurityCard(
                    onChangePasswordClick = { showPasswordDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog para alteração de senha
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = {
                showPasswordDialog = false
                showSuccessMessage = true
            }
        )
    }
}

@Composable
fun ProfileHeaderCard(
    nome: String,
    email: String,
    isEditMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(40.dp)
                    )
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nome,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditMode) {
                Text(
                    text = "Modo de Edição",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Motorista",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun DadosPessoaisCard(
    perfil: PerfilMotorista,
    isEditMode: Boolean,
    onPerfilChange: (PerfilMotorista) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Dados Pessoais",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Nome Completo",
                value = perfil.nome,
                onValueChange = { onPerfilChange(perfil.copy(nome = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Person
            )

            ProfileTextField(
                label = "CPF",
                value = perfil.cpf,
                onValueChange = { onPerfilChange(perfil.copy(cpf = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Info,
                keyboardType = KeyboardType.Number
            )

            ProfileTextField(
                label = "CNH",
                value = perfil.cnh,
                onValueChange = { onPerfilChange(perfil.copy(cnh = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Star
            )
        }
    }
}

@Composable
fun DadosContatoCard(
    perfil: PerfilMotorista,
    isEditMode: Boolean,
    onPerfilChange: (PerfilMotorista) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Dados de Contato",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "E-mail",
                value = perfil.email,
                onValueChange = { onPerfilChange(perfil.copy(email = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            ProfileTextField(
                label = "Telefone",
                value = perfil.telefone,
                onValueChange = { onPerfilChange(perfil.copy(telefone = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone
            )
        }
    }
}

@Composable
fun EnderecoCard(
    perfil: PerfilMotorista,
    isEditMode: Boolean,
    onPerfilChange: (PerfilMotorista) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Endereço",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Endereço",
                value = perfil.endereco,
                onValueChange = { onPerfilChange(perfil.copy(endereco = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.Home
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileTextField(
                    label = "Cidade",
                    value = perfil.cidade,
                    onValueChange = { onPerfilChange(perfil.copy(cidade = it)) },
                    enabled = isEditMode,
                    leadingIcon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f)
                )

                ProfileTextField(
                    label = "UF",
                    value = perfil.estado,
                    onValueChange = { onPerfilChange(perfil.copy(estado = it)) },
                    enabled = isEditMode,
                    leadingIcon = Icons.Default.MoreVert,
                    modifier = Modifier.weight(0.5f)
                )
            }

            ProfileTextField(
                label = "CEP",
                value = perfil.cep,
                onValueChange = { onPerfilChange(perfil.copy(cep = it)) },
                enabled = isEditMode,
                leadingIcon = Icons.Default.LocationOn,
                keyboardType = KeyboardType.Number
            )
        }
    }
}

@Composable
fun SecurityCard(
    onChangePasswordClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Segurança",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onChangePasswordClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Alterar Senha")
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (enabled)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var mostrarSenhaAtual by remember { mutableStateOf(false) }
    var mostrarNovaSenha by remember { mutableStateOf(false) }
    var mostrarConfirmarSenha by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Alterar Senha",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (erro.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = erro,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                PasswordTextField(
                    label = "Senha Atual",
                    value = senhaAtual,
                    onValueChange = { senhaAtual = it },
                    isPasswordVisible = mostrarSenhaAtual,
                    onTogglePasswordVisibility = { mostrarSenhaAtual = !mostrarSenhaAtual }
                )

                PasswordTextField(
                    label = "Nova Senha",
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    isPasswordVisible = mostrarNovaSenha,
                    onTogglePasswordVisibility = { mostrarNovaSenha = !mostrarNovaSenha }
                )

                PasswordTextField(
                    label = "Confirmar Nova Senha",
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    isPasswordVisible = mostrarConfirmarSenha,
                    onTogglePasswordVisibility = { mostrarConfirmarSenha = !mostrarConfirmarSenha }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty() -> {
                            erro = "Todos os campos são obrigatórios"
                        }
                        novaSenha.length < 6 -> {
                            erro = "A nova senha deve ter pelo menos 6 caracteres"
                        }
                        novaSenha != confirmarSenha -> {
                            erro = "As senhas não coincidem"
                        }
                        else -> {
                            onConfirm()
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Alterar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha"
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TelaPerfilMotoristaPreview() {
    MaterialTheme {
        TelaPerfilMotorista(
            perfilAtual = PerfilMotorista(
                nome = "João Silva",
                email = "joao.silva@email.com",
                telefone = "(11) 99999-9999",
                cnh = "12345678901",
                cpf = "123.456.789-00",
                endereco = "Rua das Flores, 123",
                cidade = "São Paulo",
                estado = "SP",
                cep = "01234-567"
            )
        )
    }
}