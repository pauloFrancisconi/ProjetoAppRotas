package com.example.projetoapprotas.ui.telas.gerente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projetoapprotas.service.EnderecoResponse
import com.example.projetoapprotas.service.ViaCepService
import com.example.projetoapprotas.ui.componentes.BotaoVoltar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaAdicionarPonto(
    onVoltarClick: () -> Unit
) {
    var cep by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var coordenadas by remember { mutableStateOf<Pair<Double, Double>?>(null) } // Se quiser usar depois
    var isLoading by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service = remember { retrofit.create(ViaCepService::class.java) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            BotaoVoltar(onVoltarClick)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Adicionar novo ponto", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cep,
                onValueChange = { cep = it },
                label = { Text("CEP") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    erro = null
                    service.buscarEndereco(cep).enqueue(object : Callback<EnderecoResponse> {
                        override fun onResponse(
                            call: Call<EnderecoResponse>,
                            response: Response<EnderecoResponse>
                        ) {
                            isLoading = false
                            if (response.isSuccessful) {
                                val endereco = response.body()
                                nome = endereco?.logradouro ?: ""
                                descricao = listOfNotNull(
                                    endereco?.bairro,
                                    endereco?.localidade,
                                    endereco?.uf
                                ).joinToString(", ")
                            } else {
                                erro = "CEP inválido"
                            }
                        }

                        override fun onFailure(call: Call<EnderecoResponse>, t: Throwable) {
                            isLoading = false
                            erro = "Erro ao buscar o CEP"
                        }
                    })
                }
            ) {
                Text("Buscar Endereço")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }

            erro?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do ponto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    println("Salvando ponto: nome=$nome, desc=$descricao, coord=$coordenadas")
                    onVoltarClick()
                },
                enabled = nome.isNotBlank() && descricao.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Ponto")
            }
        }
    }
}
