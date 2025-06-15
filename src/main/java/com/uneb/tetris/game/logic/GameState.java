package com.uneb.tetris.game.logic;

import com.uneb.tetris.configuration.GameConfig;

/**
 * Mantém e gerencia o estado atual do jogo Tetris.
 * Esta classe é a única fonte de verdade para todo o estado do jogo,
 * seguindo o padrão Single Source of Truth.
 */
public class GameState {
    /** Estado de pausa do jogo */
    private boolean isPaused = false;
    
    /** Indica se o jogo terminou */
    private boolean isGameOver = false;

    /** Nível atual do jogo */
    private int currentLevel = 1;

    /** Número total de linhas eliminadas */
    private int totalLinesCleared = 0;

    /** Pontuação atual */
    private int score = 0;

    /** Tempo de jogo atual em formato "MM:SS:mmm" */
    private String gameTime = "00:00:000";

    /** Número de linhas eliminadas no nível atual */
    private int linesInCurrentLevel = 0;

    /**
     * Reinicia o estado do jogo para seus valores padrão.
     */
    public void reset() {
        isPaused = false;
        isGameOver = false;
        currentLevel = 1;
        totalLinesCleared = 0;
        linesInCurrentLevel = 0;
        score = 0;
        gameTime = "00:00:000";
    }

    /**
     * Adiciona pontos à pontuação.
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Processa linhas completadas e verifica progressão de nível.
     * @return true se houve avanço de nível
     */
    public boolean processLinesCleared(int lines) {
        if (lines <= 0) return false;

        int oldLevel = currentLevel;
        totalLinesCleared += lines;
        linesInCurrentLevel += lines;

        if (linesInCurrentLevel >= GameConfig.LINES_PER_LEVEL) {
            currentLevel++;
            linesInCurrentLevel = linesInCurrentLevel % GameConfig.LINES_PER_LEVEL;
        }

        return currentLevel > oldLevel;
    }



    /**
     * Atualiza o tempo de jogo.
     */
    public void updateGameTime(String newTime) {
        this.gameTime = newTime;
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public boolean isPaused() { return isPaused; }
    public boolean isGameOver() { return isGameOver; }
    public int getCurrentLevel() { return currentLevel; }
    public int getScore() { return score; }
    public String getGameTime() { return gameTime; }
    public int getLinesInCurrentLevel() { return linesInCurrentLevel; }
    public int getLinesCleared() { return totalLinesCleared; }


    // Setters
    public void setPaused(boolean paused) { this.isPaused = paused; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; }

    /**
     * Calcula a velocidade de queda das peças com base no nível atual.
     */
    public double calculateCurrentSpeed() {
        return GameConfig.INITIAL_GAME_SPEED * Math.pow(0.8, currentLevel - 1);
    }


}