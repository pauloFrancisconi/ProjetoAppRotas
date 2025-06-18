package com.example.projetoapprotas.ui.feature.addpoint

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.projetoapprotas.ui.feature.addpoint.component.BasicTextField
import com.example.projetoapprotas.ui.feature.addpoint.component.FeedbackChip
import com.example.projetoapprotas.ui.feature.addpoint.component.PrimaryButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPointScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: AddPointViewModel = hiltViewModel()
) {
    val ui by vm.state.collectAsState()
    val focus = LocalFocusManager.current
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } },
            title = { Text("Cadastro de Ponto") }
        )
    }) { pad ->
        Column(
            Modifier.padding(pad)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    OutlinedTextField(
                        value = ui.cep,
                        onValueChange = vm::onCepChange,
                        label = { Text("CEP") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                    PrimaryButton(
                        text = if (ui.loadingCep) "Buscando…" else "Buscar Endereço",
                        icon = Icons.Default.Search,
                        enabled = ui.canSearchCep,
                        loading = ui.loadingCep,
                        onClick = vm::searchCep
                    )
                    BasicTextField(
                        value = ui.name,
                        onValueChange = vm::onNameChange,
                        label = "Nome do Ponto",
                        icon = Icons.Default.Edit,
                        onNext = { focus.moveFocus(FocusDirection.Down) }
                    )
                    BasicTextField(
                        value = ui.description,
                        onValueChange = vm::onDescChange,
                        label = "Descrição/Localização",
                        icon = Icons.Default.Info,
                        onNext = { focus.moveFocus(FocusDirection.Down) }
                    )
                    BasicTextField(
                        value = ui.notes,
                        onValueChange = vm::onNotesChange,
                        label = "Observações (Opcional)",
                        icon = Icons.Default.Info,
                        lines = 3,
                        ime = ImeAction.Done,
                        onDone = { focus.clearFocus() }
                    )

                    ui.error?.let {
                        FeedbackChip(it, Icons.Default.Warning, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, vm::clearMessage)
                    }
                    ui.success?.let {
                        FeedbackChip(it, Icons.Default.CheckCircle, Color(0xFF4CAF50).copy(alpha=.15f), Color(0xFF256029), vm::clearMessage)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) { Text("Cancelar") }

                PrimaryButton(
                    text = if (ui.saving) "Salvando…" else "Salvar Ponto",
                    icon = Icons.Default.Check,
                    enabled = ui.formValid && !ui.saving,
                    loading = ui.saving,
                    onClick = {
                        scope.launch { vm.save(onSaved) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
