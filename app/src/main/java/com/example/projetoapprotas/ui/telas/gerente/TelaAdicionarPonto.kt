/* ui/telas/gerente/TelaAdicionarPonto.kt */
package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetoapprotas.data.remote.RetrofitFactory
import com.example.projetoapprotas.data.repository.PontoRepository
import com.example.projetoapprotas.ui.componentes.BotaoVoltar
import com.example.projetoapprotas.viewmodel.AdicionarPontoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarPonto(
    baseCepUrl: String = "https://viacep.com.br/ws/",
    onVoltarClick: () -> Unit,
    onPontoSalvo: () -> Unit = {}
) {
    val cepApi = remember(baseCepUrl) {
        RetrofitFactory.createViaCepService(baseCepUrl)
    }
    val repo = remember { PontoRepository(cepApi) }
    val viewModel: AdicionarPontoViewModel =
        viewModel(factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(c: Class<T>): T = AdicionarPontoViewModel(repo) as T
        })/* -------------------------------------------------------------- */

    val uiState by viewModel.state.collectAsState()
    val scroll = rememberScrollState()
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            navigationIcon = { BotaoVoltar(onVoltarClick) },
            title = { Text("Cadastro de Ponto") })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            /* ----------------------- Formulário -------------------- */
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        OutlinedTextField(
                        value = uiState.cep,
                        onValueChange = viewModel::onCepChange,
                        label = { Text("CEP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.buscarEndereco() },
                        enabled = uiState.canSearchCep,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.loadingCep) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Buscando…")
                        } else {
                            Icon(Icons.Default.Search, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Buscar Endereço")
                        }
                    }

                   OutlinedTextField(
                        value = uiState.nome,
                        onValueChange = viewModel::onNomeChange,
                        label = { Text("Nome do Ponto") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions {
                            focus.moveFocus(FocusDirection.Down)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.descricao,
                        onValueChange = viewModel::onDescricaoChange,
                        label = { Text("Descrição/Localização") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions {
                            focus.moveFocus(FocusDirection.Down)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                     OutlinedTextField(
                        value = uiState.observacoes,
                        onValueChange = viewModel::onObservacoesChange,
                        label = { Text("Observações (Opcional)") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    uiState.error?.let { mensagem ->
                        AssistChip(
                            onClick = viewModel::dismissMessage,
                            label = { Text(mensagem) },
                            leadingIcon = { Icon(Icons.Default.Warning, null) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }

                    uiState.sucesso?.let { mensagem ->
                        AssistChip(
                            onClick = viewModel::dismissMessage,
                            label = { Text(mensagem) },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, null) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = .15f),
                                labelColor = Color(0xFF256029)
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onVoltarClick, modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        scope.launch {
                            val ok = viewModel.salvarPonto()
                            if (ok) onPontoSalvo()
                        }
                    },
                    enabled = uiState.formValido && !uiState.salvando,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    if (uiState.salvando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Salvando…")
                    } else {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Salvar Ponto")
                    }
                }
            }
        }
    }
}
