package com.uneb.tetris.game.logic;

/**
 * Mantém e gerencia o estado atual do jogo Tetris.
 * 
 * <p>Esta classe é responsável por:</p>
 * <ul>
 *   <li>Controlar os estados de pausa e fim de jogo</li>
 *   <li>Definir constantes fundamentais do jogo</li>
 *   <li>Calcular velocidades baseadas no nível</li>
 * </ul>
 */
public class GameState {
    /** Estado de pausa do jogo */
    private boolean isPaused = false;
    
    /** Indica se o jogo terminou */
    private boolean isGameOver = false;

    /** Número de linhas necessárias para avançar de nível */
    public static final int LINES_PER_LEVEL = 10;
    
    /** Velocidade inicial de queda das peças em milissegundos */
    public static final double INITIAL_SPEED = 1000.0;

    /**
     * Reinicia o estado do jogo para seus valores padrão.
     * Remove estados de pausa e game over.
     */
    public void reset() {
        isPaused = false;
        isGameOver = false;
    }

    /**
     * Verifica se o jogo está pausado.
     *
     * @return true se o jogo estiver pausado, false caso contrário
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Verifica se o jogo terminou.
     *
     * @return true se for game over, false caso contrário
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Alterna o estado de pausa do jogo.
     * Se estiver pausado, despausa, e vice-versa.
     */
    public void togglePause() {
        isPaused = !isPaused;
    }

    /**
     * Define o estado de game over do jogo.
     *
     * @param gameOver true para indicar game over, false para continuar o jogo
     */
    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    /**
     * Calcula a velocidade de queda das peças com base no nível atual.
     * A velocidade aumenta exponencialmente com o nível.
     * 
     * <p>A fórmula utilizada é: velocidade = velocidade_inicial * (0.8 ^ (nível - 1))</p>
     * 
     * <p>Exemplos de velocidades:</p>
     * <ul>
     *   <li>Nível 1: 1000ms</li>
     *   <li>Nível 2: 800ms</li>
     *   <li>Nível 3: 640ms</li>
     *   <li>E assim por diante...</li>
     * </ul>
     *
     * @param currentLevel O nível atual do jogo
     * @return A velocidade calculada em milissegundos
     */
    public double calculateLevelSpeed(int currentLevel) {
        return INITIAL_SPEED * Math.pow(0.8, currentLevel - 1);
    }
}