package com.uneb.fluxblocks.game.scoring;

import com.uneb.fluxblocks.game.logic.GameState;

/**
 * Implementação padrão da estratégia de pontuação do FluxBlocks.
 * Baseada no sistema atual de pontuação do jogo.
 */
public class StandardScoringStrategy implements ScoringStrategy {
    
    @Override
    public int calculateLineClearScore(int linesCleared, int level, int combo) {
        int baseScore = switch (linesCleared) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
        return baseScore * level + (combo * 50);
    }
    
    @Override
    public int calculateRotationScore(int level) {
        return 0; // Rotação não dá pontos no sistema padrão
    }
    
    @Override
    public int calculateMovementScore(int level) {
        return 0; // Movimento não dá pontos no sistema padrão
    }
    
    @Override
    public int calculateHardDropScore(int distance, int level) {
        return distance * 2; // 2 pontos por célula percorrida
    }
    
    @Override
    public int calculateTSpinScore(int level, boolean isMini) {
        if (isMini) {
            return 100 * level; // Mini T-Spin
        } else {
            return 400 * level; // T-Spin normal
        }
    }
    
    @Override
    public int calculateComboScore(int comboCount, int level) {
        return comboCount * 50 * level; // 50 pontos por combo, multiplicado pelo nível
    }
    
    @Override
    public String getName() {
        return "StandardScoringStrategy";
    }
    
    @Override
    public void applyToGameState(GameState gameState) {
        // Implementação padrão vazia - o GameState já gerencia sua própria lógica
    }
} 