package com.uneb.fluxblocks.architecture.interfaces;

/**
 * Interface para abstrair o sistema de gerenciamento de estado do jogo.
 * Permite diferentes estratégias de gerenciamento de estado.
 */
public interface GameStateManager {
    
    /**
     * Estados possíveis do jogo.
     */
    enum GameState {
        MENU,           // Menu principal
        PLAYING,        // Jogando
        PAUSED,         // Pausado
        GAME_OVER,      // Fim de jogo
        COUNTDOWN,      // Contagem regressiva
        MULTIPLAYER     // Modo multiplayer
    }
    
    /**
     * Informações do estado do jogo.
     */
    class StateInfo {
        private final GameState state;
        private final int score;
        private final int level;
        private final int lines;
        private final long gameTime;
        private final boolean isPaused;
        private final boolean isGameOver;
        
        public StateInfo(GameState state, int score, int level, int lines, 
                        long gameTime, boolean isPaused, boolean isGameOver) {
            this.state = state;
            this.score = score;
            this.level = level;
            this.lines = lines;
            this.gameTime = gameTime;
            this.isPaused = isPaused;
            this.isGameOver = isGameOver;
        }
        
        public GameState getState() { return state; }
        public int getScore() { return score; }
        public int getLevel() { return level; }
        public int getLines() { return lines; }
        public long getGameTime() { return gameTime; }
        public boolean isPaused() { return isPaused; }
        public boolean isGameOver() { return isGameOver; }
    }
    
    /**
     * Obtém o estado atual do jogo.
     * @return Estado atual
     */
    GameState getCurrentState();
    
    /**
     * Define o estado do jogo.
     * @param state Novo estado
     */
    void setState(GameState state);
    
    /**
     * Obtém informações completas do estado atual.
     * @return Informações do estado
     */
    StateInfo getStateInfo();
    
    /**
     * Obtém a pontuação atual.
     * @return Pontuação
     */
    int getScore();
    
    /**
     * Adiciona pontos à pontuação.
     * @param points Pontos a adicionar
     */
    void addScore(int points);
    
    /**
     * Define a pontuação.
     * @param score Nova pontuação
     */
    void setScore(int score);
    
    /**
     * Obtém o nível atual.
     * @return Nível atual
     */
    int getLevel();
    
    /**
     * Define o nível.
     * @param level Novo nível
     */
    void setLevel(int level);
    
    /**
     * Obtém o número de linhas eliminadas.
     * @return Número de linhas
     */
    int getLines();
    
    /**
     * Adiciona linhas eliminadas.
     * @param lines Número de linhas a adicionar
     */
    void addLines(int lines);
    
    /**
     * Obtém o tempo de jogo em milissegundos.
     * @return Tempo de jogo
     */
    long getGameTime();
    
    /**
     * Define o tempo de jogo.
     * @param timeMs Tempo em milissegundos
     */
    void setGameTime(long timeMs);
    
    /**
     * Verifica se o jogo está pausado.
     * @return true se pausado, false caso contrário
     */
    boolean isPaused();
    
    /**
     * Alterna o estado de pausa.
     */
    void togglePause();
    
    /**
     * Define o estado de pausa.
     * @param paused true para pausar, false para resumir
     */
    void setPaused(boolean paused);
    
    /**
     * Verifica se o jogo acabou.
     * @return true se acabou, false caso contrário
     */
    boolean isGameOver();
    
    /**
     * Define o estado de fim de jogo.
     * @param gameOver true se acabou, false caso contrário
     */
    void setGameOver(boolean gameOver);
    
    /**
     * Processa linhas eliminadas e atualiza o estado.
     * @param linesCleared Número de linhas eliminadas
     * @return true se subiu de nível, false caso contrário
     */
    boolean processLinesCleared(int linesCleared);
    
    /**
     * Calcula a velocidade atual baseada no nível.
     * @return Velocidade em milissegundos
     */
    double calculateCurrentSpeed();
    
    /**
     * Reseta o estado para o início.
     */
    void reset();
    
    /**
     * Salva o estado atual.
     */
    void saveState();
    
    /**
     * Carrega o estado salvo.
     */
    void loadState();
    
    /**
     * Limpa recursos do gerenciador de estado.
     */
    void cleanup();
} 