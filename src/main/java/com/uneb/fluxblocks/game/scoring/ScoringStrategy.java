package com.uneb.fluxblocks.game.scoring;

import com.uneb.fluxblocks.game.logic.GameState;

/**
 * Interface para estratégias de pontuação do jogo.
 * Permite implementar diferentes algoritmos de scoring sem modificar o código existente.
 */
public interface ScoringStrategy {
    
    /**
     * Calcula a pontuação para uma linha limpa
     * @param linesCleared Número de linhas limpas
     * @param level Nível atual do jogo
     * @param combo Combo atual
     * @return Pontuação calculada
     */
    int calculateLineClearScore(int linesCleared, int level, int combo);
    
    /**
     * Calcula pontuação para rotação de peça
     * @param level Nível atual do jogo
     * @return Pontuação calculada
     */
    int calculateRotationScore(int level);
    
    /**
     * Calcula pontuação para movimento de peça
     * @param level Nível atual do jogo
     * @return Pontuação calculada
     */
    int calculateMovementScore(int level);
    
    /**
     * Calcula pontuação para drop hard (queda rápida)
     * @param distance Distância percorrida
     * @param level Nível atual do jogo
     * @return Pontuação calculada
     */
    int calculateHardDropScore(int distance, int level);
    
    /**
     * Calcula pontuação para T-Spin
     * @param level Nível atual do jogo
     * @param isMini Se é um mini T-Spin
     * @return Pontuação calculada
     */
    int calculateTSpinScore(int level, boolean isMini);
    
    /**
     * Calcula pontuação para combo
     * @param comboCount Número de combos
     * @param level Nível atual do jogo
     * @return Pontuação calculada
     */
    int calculateComboScore(int comboCount, int level);
    
    /**
     * Retorna o nome da estratégia
     * @return Nome da estratégia
     */
    String getName();
    
    /**
     * Aplica a estratégia ao estado do jogo
     * @param gameState Estado atual do jogo
     */
    default void applyToGameState(GameState gameState) {
        // Implementação padrão vazia
    }
} 