package com.uneb.tetris.piece.scoring;

/**
 * Calcula pontuações para diferentes ações do jogo.
 */
public class ScoreCalculator {
    /** Nível atual do jogo */
    private int currentLevel = 1;

    /** Total de linhas eliminadas durante o jogo */
    private int linesClearedTotal = 0;

    /**
     * Atualiza o nível atual.
     *
     * @param level Novo nível
     */
    public void updateLevel(int level) {
        this.currentLevel = level;
    }

    /**
     * Calcula a pontuação para linhas completadas.
     *
     * @param linesCleared Número de linhas completadas
     * @return Pontuação calculada
     */
    public int calculateLinesClearedScore(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> 40 * (currentLevel + 1);
            case 2 -> 100 * (currentLevel + 1);
            case 3 -> 300 * (currentLevel + 1);
            case 4 -> 1200 * (currentLevel + 1);
            default -> 0;
        };
    }

    /**
     * Calcula pontuação para soft drop.
     *
     * @return Pontuação para soft drop
     */
    public int calculateSoftDropScore() {
        return 20 * currentLevel;
    }

    /**
     * Calcula pontuação para hard drop.
     *
     * @param distance Distância percorrida durante o hard drop
     * @return Pontuação para hard drop
     */
    public int calculateHardDropScore(int distance) {
        return distance * 2;
    }

    /**
     * Atualiza o contador de linhas totais eliminadas.
     *
     * @param linesCleared Número de linhas eliminadas
     */
    public void updateTotalClearedLines(int linesCleared) {
        if (linesCleared > 0) {
            linesClearedTotal += linesCleared;
        }
    }

    /**
     * Retorna o total de linhas eliminadas.
     *
     * @return Número total de linhas eliminadas
     */
    public int getLinesClearedTotal() {
        return linesClearedTotal;
    }
}