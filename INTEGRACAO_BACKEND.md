# Integração e Backend - App Rotas

## Funcionalidades Implementadas

### 1. Autenticação e Role Management

#### AuthRepository
- **Login integrado com API**: Autentica usuários e armazena tokens
- **Verificação de roles**: Sistema hierárquico (MOTORISTA, GERENTE, ADMIN)
- **Refresh token**: Renovação automática de tokens expirados
- **Armazenamento seguro**: Dados salvos em SharedPreferences

#### Funcionalidades:
```kotlin
// Login
val resultado = authRepository.login(email, senha)

// Verificar role
val temPermissao = authRepository.hasRole(UserRole.GERENTE)

// Logout
authRepository.logout()
```

### 2. Registro de Pontos

#### RegistroRepository
- **Armazenamento local**: Dados salvos offline primeiro
- **Sincronização automática**: Envio para servidor quando há conexão
- **Upload de fotos**: Suporte a múltiplas imagens
- **Coordenadas GPS**: Captura de localização precisa

#### Funcionalidades:
```kotlin
// Registrar ponto
registroRepository.registrarPonto(
    pontoColetaId = "123",
    coordenadas = Coordenadas(-23.5505, -46.6333),
    fotos = listOf("foto1.jpg", "foto2.jpg"),
    observacoes = "Coleta realizada com sucesso",
    status = StatusRegistro.COLETADO,
    motoristaId = "motorista123"
)

// Sincronizar registros pendentes
registroRepository.sincronizarRegistrosPendentes()
```

### 3. Relatórios com Filtros

#### RelatorioRepository
- **Filtros avançados**: Por motorista, data, status, ponto de coleta
- **Estatísticas**: Percentuais de conclusão, totais por status
- **Exportação**: PDF, Excel, CSV
- **Dashboard**: Dados em tempo real

#### Funcionalidades:
```kotlin
// Relatório filtrado
val filtro = FiltroRelatorio(
    motoristaId = "123",
    dataInicio = "2024-01-01",
    dataFim = "2024-01-31",
    status = StatusRegistro.COLETADO
)
relatorioRepository.obterRelatorioFiltrado(filtro)

// Estatísticas
relatorioRepository.obterEstatisticasGerais()

// Exportar
relatorioRepository.exportarRelatorio("PDF", motoristaId = "123")
```

## Estrutura de Dados

### Modelos Principais

#### Usuario
```kotlin
data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val role: UserRole,
    val ativo: Boolean = true,
    val criadoEm: String? = null
)
```

#### RegistroPonto
```kotlin
data class RegistroPonto(
    val id: String,
    val motoristaId: String,
    val pontoColetaId: String,
    val coordenadas: Coordenadas,
    val timestamp: String,
    val fotos: List<String> = emptyList(),
    val observacoes: String? = null,
    val status: StatusRegistro,
    val sincronizado: Boolean = false
)
```

#### FiltroRelatorio
```kotlin
data class FiltroRelatorio(
    val motoristaId: String? = null,
    val dataInicio: String? = null,
    val dataFim: String? = null,
    val status: StatusRegistro? = null,
    val pontoColetaId: String? = null
)
```

## ViewModels

### LoginViewModel
- Gerencia autenticação
- Controla estado de login
- Verifica permissões de role

### RegistroViewModel
- Controla formulário de registro
- Gerencia fotos e coordenadas
- Valida dados antes do envio

### RelatorioViewModel
- Aplica filtros
- Carrega dados de relatórios
- Controla exportação

## Sincronização de Dados

### SyncManager
- **Sincronização automática**: A cada 30 segundos
- **Retry com backoff**: Tentativas com delay crescente
- **Verificação de rede**: Só sincroniza com internet
- **Indicadores visuais**: Status de sincronização na UI

#### Uso:
```kotlin
val syncManager = SyncManager(context)
syncManager.startAutoSync()

// Forçar sincronização
syncManager.forceSyncWithRetry()
```

## Configuração da API

### ApiClient
- **Base URL configurável**: Altere em `ApiClient.BASE_URL`
- **Interceptadores**: Autenticação automática e logging
- **Timeouts**: 30 segundos para conexão/leitura/escrita
- **Formato de data**: ISO 8601

### Endpoints Implementados

#### Autenticação
- `POST /auth/login` - Login
- `POST /auth/refresh` - Renovar token
- `GET /auth/verify` - Verificar token
- `POST /auth/logout` - Logout

#### Registros
- `POST /registros` - Criar registro
- `POST /registros/bulk` - Criar múltiplos registros
- `POST /fotos/upload` - Upload de fotos
- `GET /registros/motorista/{id}` - Registros do motorista

#### Relatórios
- `POST /relatorios/filtrar` - Relatório com filtros
- `GET /relatorios/motorista/{id}` - Relatório detalhado
- `GET /relatorios/estatisticas` - Estatísticas gerais
- `GET /relatorios/export` - Exportar relatório

## Como Usar

### 1. Configurar URL da API
```kotlin
// Em ApiClient.kt
private const val BASE_URL = "https://sua-api-backend.com/api/v1/"
```

### 2. Inicializar nos ViewModels
```kotlin
class MinhaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val loginViewModel = LoginViewModel(this)
            val registroViewModel = RegistroViewModel(this)
            val relatorioViewModel = RelatorioViewModel(this)
            
            // Usar ViewModels nas telas
        }
    }
}
```

### 3. Verificar Autenticação
```kotlin
LaunchedEffect(Unit) {
    loginViewModel.verificarTokenValido(
        onTokenValid = { role ->
            // Navegar para tela principal baseado no role
        },
        onTokenInvalid = {
            // Navegar para tela de login
        }
    )
}
```

### 4. Registrar Pontos
```kotlin
// Na tela de registro
Button(
    onClick = {
        registroViewModel.registrarPonto(
            pontoColetaId = pontoId,
            onSuccess = { registro ->
                // Sucesso
            },
            onError = { erro ->
                // Tratar erro
            }
        )
    }
) {
    Text("Registrar Ponto")
}
```

### 5. Gerar Relatórios
```kotlin
// Aplicar filtros
relatorioViewModel.aplicarFiltro(
    motoristaId = "123",
    dataInicio = "2024-01-01",
    dataFim = "2024-01-31"
)

// Observar resultado
val relatorio by relatorioViewModel.relatorioAtual.collectAsState()
```

## Tratamento de Erros

### Offline First
- Dados salvos localmente primeiro
- Sincronização quando há conexão
- Indicadores visuais de status

### Retry Logic
- Tentativas automáticas com backoff
- Máximo de 3 tentativas
- Logs de erro detalhados

### Validação
- Validação no cliente antes do envio
- Mensagens de erro claras
- Estados de loading apropriados

## Segurança

### Tokens
- Bearer tokens em todas as requisições
- Refresh automático quando expiram
- Limpeza ao fazer logout

### Dados Sensíveis
- Senhas não armazenadas localmente
- Tokens criptografados no SharedPreferences
- Validação de roles no servidor

## Monitoramento

### Logs
- HTTP logging interceptor para debug
- Logs de sincronização
- Tracking de erros

### Métricas
- Contadores de registros pendentes
- Tempo da última sincronização
- Taxa de sucesso das requisições

## Próximos Passos

1. **Implementar cache inteligente** para relatórios
2. **Adicionar compressão** para upload de fotos
3. **Implementar push notifications** para sincronização
4. **Adicionar analytics** para monitoramento de uso
5. **Implementar backup/restore** de dados locais 