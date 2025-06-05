# 📍 ProjetoAppRotas

Aplicativo Android desenvolvido com **Kotlin** e **Jetpack Compose**, que permite o gerenciamento de rotas de transporte para motoristas e administradores (gerentes). O sistema é dividido em perfis de usuário e oferece funcionalidades como cadastro de pontos, criação de rotas e acompanhamento de execução.

## ✨ Funcionalidades

### 👤 Autenticação
- Tela de login com validação de email e senha.
- Identificação do tipo de usuário (Motorista ou Administrador) após login.
- Redirecionamento automático para a tela inicial conforme o tipo de usuário.

### 🛠️ Funcionalidades do Administrador (Gerente)
- **Cadastro de Pontos**:
  - Nome do ponto, observações e coordenadas via mapa.
- **Cadastro de Rotas**:
  - Seleção de motorista, pontos e dias da semana.
- **Relatórios**:
  - Visualização de rotas finalizadas com fotos, observações e localização.

### 🚚 Funcionalidades do Motorista
- **Rota do Dia**:
  - Lista de pontos da rota atual com possibilidade de marcar como concluído, adicionar observações e enviar fotos.
- **Relatórios**:
  - Histórico das rotas já executadas.
- **Mensagens informativas**:
  - Exibição de mensagem caso não haja rota no dia atual.
  - Confirmação de rota finalizada.

### 🧭 Navegação
- Implementação de navegação com **NavController**.
- Menu lateral (Drawer) para navegação entre telas de cada perfil.

## 🧱 Estrutura de Pastas

```
com.example.projetoapprotas
├── navigation           # Rotas e gráficos de navegação
├── service              # Serviços (como ViaCep)
├── ui
│   ├── components       # Componentes reutilizáveis (mapa, botões)
│   ├── telas
│   │   ├── gerente      # Telas do administrador
│   │   ├── login        # Tela de login
│   │   └── motorista    # Telas do motorista
│   └── theme            # Cores, Tipografia e Tema do app
└── MainActivity.kt      # Ponto de entrada do app
```

## 🚀 Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/pauloFrancisconi/ProjetoAppRotas.git
   ```

2. Abra o projeto no **Android Studio**.

3. Execute o projeto iniciando por:
   ```kotlin
   com.example.projetoapprotas.MainActivity
   ```

4. Selecione um emulador ou dispositivo físico com Android 8.0+ e clique em **Run** ▶️.

## 🗂️ Tecnologias Utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Material3**
- **Navigation Compose**
- **Retrofit**
- **MVVM Pattern**
