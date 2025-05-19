package com.example.projetoapprotas.ui.telas.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun TelaLogin(
    viewModel: LoginViewModel,
    onCadastroClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val senha by viewModel.senha.collectAsState()
    val cargoSelecionado by viewModel.cargoSelecionado.collectAsState()
    val isLoginEnabled = email.isNotBlank() && senha.isNotBlank() && cargoSelecionado != null

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bem-vindo de volta!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    placeholder = { Text("exemplo@email.com") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { viewModel.onSenhaChange(it) },
                    label = { Text("Senha") },
                    placeholder = { Text("Digite sua senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val cargos = listOf("Gerente", "Motorista")
                    cargos.forEach { cargo ->
                        val isSelecionado = cargoSelecionado == cargo

                        OutlinedButton(
                            onClick = { viewModel.onCargoSelecionadoChange(cargo) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelecionado)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                                contentColor = if (isSelecionado)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(text = cargo)
                        }
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {viewModel.fazerLogin {
                        onLoginSuccess()
                    }},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entrar")
                }

                Spacer(modifier = Modifier.height(24.dp))

                val annotatedText = buildAnnotatedString {
                    append("Não possui uma conta? ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("Cadastre-se")
                    }
                }

                Text(
                    text = annotatedText,
                    modifier = Modifier.clickable { onCadastroClick() }
                )
            }
        }
    }
}


