# 🎮 FluxBlocks

<div align="center">

![FluxBlocks Logo](src/main/resources/assets/ui/icons/mipmap-mdpi/ic_game.png)

**Um jogo de Tetris moderno e inovador desenvolvido em Java com FXGL**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![FXGL](https://img.shields.io/badge/FXGL-11.0-blue.svg)](https://almasb.github.io/FXGL/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Gradle-lightgrey.svg)](https://gradle.org/)

</div>

---

## 🌟 Sobre o Projeto

**FluxBlocks** é uma reimaginação moderna do clássico Tetris, desenvolvida com foco em arquitetura limpa, extensibilidade e experiência de usuário excepcional. O projeto combina a nostalgia dos jogos de puzzle com tecnologias modernas e padrões de design robustos.

### ✨ Características Principais

- **🎯 Gameplay Clássico**: Implementação fiel das regras oficiais do Tetris
- **🎨 Interface Moderna**: UI elegante com animações fluidas e efeitos visuais
- **👥 Multiplayer Local**: Suporte para 2 jogadores na mesma tela
- **🔧 Arquitetura Modular**: Sistema baseado em interfaces e padrões de design
- **🎛️ Controles Customizáveis**: Configuração completa de teclas
- **📊 Sistema de Pontuação Avançado**: Suporte a T-Spins, combos e mais
- **🎭 Efeitos Visuais**: Partículas, animações e feedback visual rico

---

## 🎮 Modos de Jogo

### 🎯 Single Player
- **Modo Clássico**: Tetris tradicional com progressão de nível
- **Sistema de Pontuação**: T-Spins, combos, e pontuação baseada em linhas
- **Estatísticas Detalhadas**: Acompanhe seu progresso e recordes

### 👥 Multiplayer Local
- **Competição Local**: 2 jogadores na mesma tela
- **Sistema de Vitórias**: Contagem de vitórias por sessão
- **Interface Dividida**: Layout otimizado para multiplayer

### 🌐 Multiplayer Online *(Em Desenvolvimento)*
- **Jogos Online**: Conecte-se com jogadores ao redor do mundo
- **Ranking Global**: Compita pelos melhores scores

---

## 🧩 Peças Especiais

### 🎲 Peças Clássicas
- **I-Piece**: A clássica linha reta
- **J-Piece**: Formato em L invertido
- **L-Piece**: Formato em L
- **O-Piece**: O quadrado perfeito
- **S-Piece**: Formato em S
- **T-Piece**: Formato em T (suporte a T-Spins)
- **Z-Piece**: Formato em Z


### 💎 Peça de Vidro *(Exclusiva)*
- **+-Pece**:  Formato em + (Cruz)
- **Glass-Piece**: Peça especial com aparência de vidro
- **Efeitos Visuais Únicos**: Gradientes e brilhos especiais
- **Mecânica Diferenciada**: Comportamento único no jogo

---

## 🏗️ Arquitetura do Projeto

### 📦 Estrutura Modular
```
src/main/java/com/uneb/fluxblocks/
├── architecture/          # Padrões arquiteturais
│   ├── events/           # Sistema de eventos
│   ├── mediators/        # Padrão Mediator
│   └── interfaces/       # Interfaces do sistema
├── configuration/         # Configurações do jogo
├── game/                 # Lógica principal do jogo
│   ├── core/            # Controladores principais
│   ├── logic/           # Lógica de negócio
│   ├── scoring/         # Sistema de pontuação
│   └── statistics/      # Estatísticas do jogo
├── piece/               # Sistema de peças
│   ├── collision/       # Detecção de colisão
│   ├── entities/        # Entidades das peças
│   ├── factory/         # Fábrica de peças
│   ├── movement/        # Movimento e rotação
│   ├── rendering/       # Renderização
│   ├── scoring/         # Pontuação por peça
│   └── timing/          # Controle de tempo
└── ui/                  # Interface do usuário
    ├── components/      # Componentes reutilizáveis
    ├── controllers/     # Controladores de input
    ├── effects/         # Efeitos visuais
    ├── managers/        # Gerenciadores de UI
    ├── screens/         # Telas do jogo
    └── theme/           # Temas visuais
```

### 🔧 Padrões de Design Implementados

- **🏗️ Factory Pattern**: Criação de peças e componentes
- **🎭 Mediator Pattern**: Comunicação entre componentes
- **🎯 Strategy Pattern**: Diferentes algoritmos de movimento
- **👁️ Observer Pattern**: Sistema de eventos
- **🎨 Template Method**: Estrutura base para telas
- **🔌 Plugin Architecture**: Sistema extensível

---

## 🚀 Como Executar

### 📋 Pré-requisitos
- **Java 17** ou superior
- **Gradle 7.0** ou superior

### ⚡ Execução Rápida

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/FluxBlocks.git
cd FluxBlocks

# Execute o jogo
./gradlew run
```

### 🔧 Desenvolvimento

```bash
# Compile o projeto
./gradlew build

# Execute os testes
./gradlew test

# Gere a documentação
./gradlew javadoc
```

---

## 📦 Notas sobre Build e Distribuição

- **AppImage**: Atualmente, a geração do instalador AppImage depende do pacote `.deb` criado pelo `jpackage`. O workflow extrai os arquivos do `.deb` para montar o AppImage. Futuras versões do `jpackage` podem permitir a criação direta do AppImage (acompanhe as atualizações da ferramenta).

---

## 🎮 Controles

### 🎯 Jogador 1
- **Movimento**: `A` (esquerda) / `D` (direita)
- **Rotação**: `W`
- **Soft Drop**: `S`
- **Hard Drop**: `SPACE`
- **Pausa**: `ESC`
- **Reiniciar**: `R`

### 🎯 Jogador 2 *(Multiplayer)*
- **Movimento**: `←` / `→`
- **Rotação**: `↑`
- **Soft Drop**: `↓`
- **Hard Drop**: `ENTER`
- **Pausa**: `P`
- **Reiniciar**: `BACKSPACE`

---

## ⚙️ Configurações

### 🎛️ Personalização
- **Resolução**: Suporte a múltiplas resoluções
- **Fullscreen**: Modo tela cheia opcional
- **Controles**: Teclas completamente customizáveis
- **Performance**: Configurações de cache e otimização

### 📁 Arquivo de Configuração
```properties
# config.properties
video.fullscreen=true
video.resolution_width=1920
video.resolution_height=1080

controls.p1.left=A
controls.p1.right=D
controls.p1.rotate=W
# ... mais configurações
```

---

## 🎯 Sistema de Pontuação

### 📊 Pontuação Base
- **Single Line**: 40 pontos
- **Double Line**: 100 pontos
- **Triple Line**: 300 pontos
- **Tetris (4 linhas)**: 1200 pontos

### 🌀 T-Spins
- **T-Spin**: 400 pontos base
- **T-Spin Single**: 800 pontos
- **T-Spin Double**: 1200 pontos
- **T-Spin Triple**: 1600 pontos

### 🎭 Spins Especiais
- **Mini T-Spin**: 100 pontos base
- **Mini T-Spin Single**: 200 pontos
- **Mini T-Spin Double**: 400 pontos

---

## 🔧 Desenvolvimento

### 🛠️ Tecnologias Utilizadas
- **Java 17**: Linguagem principal
- **FXGL 11.0**: Framework de jogos
- **JavaFX**: Interface gráfica
- **Gradle**: Build system
- **CSS**: Estilização avançada


### 🧪 Testes
```bash
# Execute todos os testes
./gradlew test

# Execute testes específicos
./gradlew test --tests "com.uneb.fluxblocks.game.*"
```

---

## 🤝 Contribuição

### 📝 Como Contribuir
1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. Abra um **Pull Request**

### 🎯 Áreas para Contribuição
- **🎨 UI/UX**: Melhorias na interface
- **🎮 Gameplay**: Novos modos de jogo
- **🔧 Performance**: Otimizações
- **🧪 Testes**: Cobertura de testes
- **📚 Documentação**: Melhorias na documentação

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- **Alexey Almasov** pelo framework FXGL
- **Contribuidores** que ajudaram no desenvolvimento

---

## 📞 Contato

- **GitHub**: [@BrunoF2P](https://github.com/BrunoF2P)
- **Email**: brunobispos.saints@gmail.com

---

<div align="center">

**⭐ Se este projeto te ajudou, considere dar uma estrela! ⭐**

</div> 