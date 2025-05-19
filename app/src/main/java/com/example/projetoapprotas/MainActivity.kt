package com.example.projetoapprotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projetoapprotas.ui.telas.login.LoginViewModel
import com.example.projetoapprotas.ui.telas.login.TelaLogin
import com.example.projetoapprotas.ui.theme.ProjetoAppRotasTheme
import androidx.lifecycle.viewmodel.compose.viewModel



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val loginViewModel: LoginViewModel = viewModel()
            TelaLogin(
                viewModel = loginViewModel,
                onCadastroClick = {
                },
                onLoginSuccess = {
                }
            )
        }
    }
}
