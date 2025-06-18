# Pontual - Sistema de Gerenciamento de Entregas

Este é um aplicativo Android para gerenciamento completo de entregas, incluindo cadastro de pontos de entrega, criação de rotas, gestão de motoristas e acompanhamento de entregas em tempo real.

## 🚚 Funcionalidades

### Sistema de Autenticação
- **Login seguro** com JWT token
- **Diferenciação de roles** (admin/usuário)
- **Refresh token** para sessões persistentes
- **Logout** com limpeza de dados

### Gestão de Pontos de Entrega
- **Cadastro completo** de pontos com coordenadas GPS
- **Informações de contato** (nome, telefone)
- **Notas e observações** para cada ponto
- **Status ativo/inativo**

### Criação e Gestão de Rotas
- **Sequenciamento de pontos** em ordem definida
- **Cálculo automático** de distância e tempo estimado
- **Atribuição a motoristas**
- **Edição e reordenação** de pontos na rota

### Gestão de Motoristas
- **Cadastro completo** com dados pessoais e veículo
- **Licença e informações** do veículo
- **Status ativo/inativo**
- **Visualização de rotas** atribuídas

### Acompanhamento de Entregas
- **Status em tempo real** (pendente, em andamento, concluída, falhou)
- **Captura de assinatura** digital
- **Notas e observações** por entrega
- **Filtros por status, motorista e rota**

## 🛠️ Tecnologias Utilizadas

- **Jetpack Compose** - Interface moderna e declarativa
- **Material 3** - Design system atualizado
- **Retrofit** - Cliente HTTP para APIs
- **OkHttp** - Cliente HTTP base
- **Gson** - Serialização JSON
- **SharedPreferences** - Armazenamento local seguro
- **Coroutines** - Programação assíncrona

## 📱 Interface do Usuário

### Tela de Login
- Campos para email e senha
- Validação em tempo real
- Indicador de carregamento
- Tratamento de erros

### Tela Principal (Admin)
- Dashboard com estatísticas
- Acesso rápido às funcionalidades
- Painel administrativo
- Gestão de usuários e relatórios

### Tela Principal (Motorista)
- Lista de rotas atribuídas
- Status das entregas
- Navegação para pontos
- Captura de assinaturas

## 🔧 Configuração

### 1. URL da API

Edite o arquivo `app/src/main/java/com/example/pontual/api/ApiConfig.kt`:

```kotlin
// Para emulador Android
const val BASE_URL = BASE_URL_EMULATOR // http://10.0.2.2:3000/api/v1/

// Para dispositivo físico
const val BASE_URL = BASE_URL_DEVICE // http://192.168.1.100:3000/api/v1/

// Para produção
const val BASE_URL = BASE_URL_PRODUCTION // https://api.pontual.com/api/v1/
```

### 2. Estrutura da API

A API deve implementar todos os endpoints documentados em `API_DOCUMENTATION.md`:

- **Autenticação**: Login, logout, refresh token
- **Pontos de Entrega**: CRUD completo
- **Rotas**: Criação, edição, atribuição
- **Motoristas**: Gestão completa
- **Entregas**: Acompanhamento e status

## 📊 Estrutura do Projeto

```
app/src/main/java/com/example/pontual/
├── MainActivity.kt              # Activity principal
├── LoginScreen.kt              # Tela de login
├── HomeScreen.kt               # Tela principal
├── PreferenceManager.kt        # Gerenciador de preferências
├── api/
│   ├── ApiConfig.kt           # Configurações da API
│   ├── ApiClient.kt           # Cliente HTTP
│   ├── ApiService.kt          # Interface da API
│   ├── LoginRequest.kt        # Modelo de requisição
│   ├── LoginResponse.kt       # Modelo de resposta
│   ├── RefreshTokenRequest.kt # Refresh token
│   ├── DebugInterceptor.kt    # Interceptor para debug
│   └── models/
│       ├── DeliveryPoint.kt   # Modelo de ponto de entrega
│       ├── Route.kt          # Modelo de rota
│       ├── Driver.kt         # Modelo de motorista
│       └── Delivery.kt       # Modelo de entrega
└── repository/
    └── AuthRepository.kt      # Repositório de autenticação
```

## 🔐 Segurança

- **JWT Token** para autenticação
- **Refresh Token** para renovação automática
- **SharedPreferences** para armazenamento seguro
- **Bypass SSL** configurado para desenvolvimento
- **Logout** limpa todos os dados sensíveis

## 🚀 Como Usar

1. **Configure a URL da API** no `ApiConfig.kt`
2. **Compile e execute** o aplicativo
3. **Faça login** com credenciais válidas
4. **Acesse as funcionalidades** baseadas no seu role:
   - **Admin**: Gestão completa do sistema
   - **Motorista**: Visualização de rotas e entregas

## 📋 Funcionalidades por Role

### Administrador
- ✅ Gestão de pontos de entrega
- ✅ Criação e edição de rotas
- ✅ Atribuição de rotas a motoristas
- ✅ Gestão de motoristas
- ✅ Acompanhamento de todas as entregas
- ✅ Relatórios e estatísticas

### Motorista
- ✅ Visualização de rotas atribuídas
- ✅ Acompanhamento de entregas
- ✅ Atualização de status
- ✅ Captura de assinaturas
- ✅ Notas e observações

## 🐛 Solução de Problemas

### Erro de SSL
- O app está configurado para ignorar certificados SSL em desenvolvimento
- Para produção, remova as configurações de bypass SSL

### Erro de Conectividade
1. Verifique se a API está rodando
2. Confirme a URL no `ApiConfig.kt`
3. Para dispositivo físico, use o IP correto da sua máquina
4. Verifique os logs no Android Studio (filtre por "API_DEBUG")

### Debug
- Os logs de requisição aparecem no Logcat com tag "API_DEBUG"
- Use o filtro "API_DEBUG" no Android Studio

## 📚 Documentação da API

Consulte o arquivo `API_DOCUMENTATION.md` para:
- Todos os endpoints disponíveis
- Exemplos de request/response
- Códigos de status HTTP
- Configurações de autenticação
- Exemplos de paginação e upload

## 🔄 Próximos Passos

1. **Implementar telas específicas** para cada funcionalidade
2. **Adicionar mapa** para visualização de rotas
3. **Implementar notificações push**
4. **Adicionar sincronização offline**
5. **Implementar relatórios detalhados**
6. **Adicionar biometria** para login
7. **Implementar upload de fotos** de entrega

## 📄 Licença

Este projeto é desenvolvido para o sistema Pontual de gerenciamento de entregas. 