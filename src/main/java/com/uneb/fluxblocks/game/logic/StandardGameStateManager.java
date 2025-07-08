package com.uneb.fluxblocks.game.logic;

import com.uneb.fluxblocks.architecture.interfaces.GameStateManager;
import com.uneb.fluxblocks.configuration.GameConfig;

/**
 * Implementação padrão do GameStateManager baseada no GameState original.
 * Mantém e gerencia o estado atual do jogo FluxBlocks.
 */
public class StandardGameStateManager implements GameStateManager {
    
    private volatile boolean isPaused = true;
    private boolean isGameOver = false;
    private int currentLevel = 1;
    private int totalLinesCleared = 0;
    private int score = 0;
    private long gameTimeMs = 0;
    private int linesInCurrentLevel = 0;
    private GameState currentState = GameState.MENU;
    
    @Override
    public GameState getCurrentState() {
        if (isGameOver) return GameState.GAME_OVER;
        if (isPaused) return GameState.PAUSED;
        return GameState.PLAYING;
    }
    
    @Override
    public void setState(GameState state) {
        this.currentState = state;
        
        switch (state) {
            case PLAYING:
                isPaused = false;
                isGameOver = false;
                break;
            case PAUSED:
                isPaused = true;
                break;
            case GAME_OVER:
                isGameOver = true;
                isPaused = true;
                break;
            case COUNTDOWN:
                isPaused = true;
                break;
            default:
                break;
        }
    }
    
    @Override
    public StateInfo getStateInfo() {
        return new StateInfo(
            getCurrentState(),
            score,
            currentLevel,
            totalLinesCleared,
            gameTimeMs,
            isPaused,
            isGameOver
        );
    }
    
    @Override
    public int getScore() {
        return score;
    }
    
    @Override
    public void addScore(int points) {
        score += points;
    }
    
    @Override
    public void setScore(int score) {
        this.score = Math.max(0, score);
    }
    
    @Override
    public int getLevel() {
        return currentLevel;
    }
    
    @Override
    public void setLevel(int level) {
        this.currentLevel = Math.max(1, level);
    }
    
    @Override
    public int getLines() {
        return totalLinesCleared;
    }
    
    @Override
    public void addLines(int lines) {
        totalLinesCleared += lines;
    }
    
    @Override
    public long getGameTime() {
        return gameTimeMs;
    }
    
    @Override
    public void setGameTime(long timeMs) {
        this.gameTimeMs = Math.max(0, timeMs);
    }
    
    @Override
    public boolean isPaused() {
        return isPaused;
    }
    
    @Override
    public void togglePause() {
        isPaused = !isPaused;
    }
    
    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }
    
    @Override
    public boolean isGameOver() {
        return isGameOver;
    }
    
    @Override
    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }
    
    @Override
    public boolean processLinesCleared(int linesCleared) {
        if (linesCleared <= 0) return false;
        
        int oldLevel = currentLevel;
        totalLinesCleared += linesCleared;
        linesInCurrentLevel += linesCleared;
        
        if (linesInCurrentLevel >= GameConfig.LINES_PER_LEVEL) {
            currentLevel++;
            linesInCurrentLevel = linesInCurrentLevel % GameConfig.LINES_PER_LEVEL;
        }
        
        return currentLevel > oldLevel;
    }
    
    @Override
    public double calculateCurrentSpeed() {
        return GameConfig.INITIAL_GAME_SPEED * Math.pow(0.8, currentLevel - 1);
    }
    
    @Override
    public void reset() {
        isPaused = false;
        isGameOver = false;
        currentLevel = 1;
        totalLinesCleared = 0;
        linesInCurrentLevel = 0;
        score = 0;
        gameTimeMs = 0;
        currentState = GameState.PLAYING;
    }
    
    @Override
    public void saveState() {
        // Implementação básica - em uma versão mais avançada
        // poderia salvar em arquivo ou banco de dados
    }
    
    @Override
    public void loadState() {
        // Implementação básica - em uma versão mais avançada
        // poderia carregar de arquivo ou banco de dados
    }
    
    @Override
    public void cleanup() {
        // Não há recursos específicos para limpar
    }
    
    // Métodos de conveniência para compatibilidade com o GameState original
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public int getLinesInCurrentLevel() {
        return linesInCurrentLevel;
    }
    
    public int getLinesCleared() {
        return totalLinesCleared;
    }
    
    public long getGameTimeMs() {
        return gameTimeMs;
    }
    
    public String getGameTimeFormatted() {
        long totalSeconds = gameTimeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;
        long milliseconds = (gameTimeMs % 1000) / 10;
        
        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
    }
    
    public void setGameTimeMs(long gameTimeMs) {
        this.gameTimeMs = Math.max(0, gameTimeMs);
    }
} 