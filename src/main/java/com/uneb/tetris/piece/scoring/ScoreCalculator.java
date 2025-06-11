package com.uneb.tetris.piece.scoring;

import com.uneb.tetris.configuration.GameConfig;

/**
 * Classe utilitária para cálculos de pontuação.
 * Não mantém estado, apenas realiza cálculos baseados nos parâmetros recebidos.
 */
public class ScoreCalculator {

    /**
     * Calcula a pontuação para linhas completadas.
     *
     * @param linesCleared Número de linhas completadas
     * @param level Nível atual do jogo
     * @return Pontuação calculada
     */
    public static int calculateLinesClearedScore(int linesCleared, int level) {
        return switch (linesCleared) {
            case 1 -> GameConfig.SCORE_SINGLE_LINE * level;
            case 2 -> GameConfig.SCORE_DOUBLE_LINE * level;
            case 3 -> GameConfig.SCORE_TRIPLE_LINE * level;
            case 4 -> GameConfig.SCORE_TETRIS * level;
            default -> 0;
        };
    }

    /**
     * Calcula pontuação para soft drop.
     *
     * @param level Nível atual do jogo
     * @return Pontuação para soft drop
     */
    public static int calculateSoftDropScore(int level) {
        return GameConfig.SCORE_SOFT_DROP * level;
    }

    /**
     * Calcula pontuação para hard drop.
     *
     * @param distance Distância percorrida durante o hard drop
     * @return Pontuação para hard drop
     */
    public static int calculateHardDropScore(int distance) {
        return distance * GameConfig.SCORE_HARD_DROP;
    }
}