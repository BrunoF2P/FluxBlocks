package com.uneb.fluxblocks.piece.scoring;

import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.game.scoring.ScoringStrategy;
import com.uneb.fluxblocks.piece.collision.SpinDetector;
import com.uneb.fluxblocks.piece.collision.TripleSpinDetector;

/**
 * Calculadora de pontuação para o jogo FluxBlocks.
 * 
 * <p>Esta classe implementa o sistema de pontuação oficial do Tetris,
 * incluindo pontuação para linhas limpas, Spins e combos.</p>
 */
public class StandardScoreCalculator implements ScoringStrategy {
    
    public StandardScoreCalculator() {}

    /**
     * Calcula pontuação base para linhas completadas.
     *
     * @param linesCleared Número de linhas completadas
     * @param level Nível atual do jogo
     * @return Pontuação base calculada
     */
    public static int calculateBaseScore(int linesCleared, int level) {
        if (linesCleared <= 0) return 0;
        
        int baseScore = switch (linesCleared) {
            case 1 -> GameConfig.SCORE_SINGLE_LINE;
            case 2 -> GameConfig.SCORE_DOUBLE_LINE;
            case 3 -> GameConfig.SCORE_TRIPLE_LINE; 
            case 4 -> GameConfig.SCORE_QUADRA_LINE; 
            default -> 0;
        };
        
        return baseScore * level;
    }
    
    /**
     * Calcula pontuação para Spin com linhas completadas.
     *
     * @param spinType Tipo de Spin realizado
     * @param linesCleared Número de linhas completadas
     * @param level Nível atual do jogo
     * @return Pontuação calculada para Spin
     */
    public static int calculateSpinScore(SpinDetector.SpinType spinType, int linesCleared, int level) {
        if (spinType == SpinDetector.SpinType.NONE) {
            return 0;
        }
        
        int baseScore = switch (spinType) {
            case SPIN -> switch (linesCleared) {
                case 0 -> GameConfig.SCORE_SPIN_BASE;
                case 1 -> GameConfig.SCORE_SPIN_SINGLE;
                case 2 -> GameConfig.SCORE_SPIN_DOUBLE;
                case 3 -> GameConfig.SCORE_SPIN_TRIPLE; 
                default -> 0;
            };
            case SPIN_MINI -> switch (linesCleared) {
                case 0 -> GameConfig.SCORE_SPIN_MINI_BASE;
                case 1 -> GameConfig.SCORE_SPIN_MINI_SINGLE;
                case 2 -> GameConfig.SCORE_SPIN_MINI_DOUBLE;
                default -> 0;
            };
            case NONE -> 0;
        };
        
        return baseScore * level;
    }

    /**
     * Calcula pontuação para Triple Spin com linhas completadas.
     *
     * @param tripleSpinType Tipo de Triple Spin realizado
     * @param linesCleared Número de linhas completadas
     * @param level Nível atual do jogo
     * @return Pontuação calculada para Triple Spin
     */
    public static int calculateTripleSpinScore(TripleSpinDetector.TripleSpinType tripleSpinType, int linesCleared, int level) {
        if (tripleSpinType == TripleSpinDetector.TripleSpinType.NONE) {
            return 0;
        }
        
        int baseScore = switch (tripleSpinType) {
            case TRIPLE_SPIN -> switch (linesCleared) {
                case 0 -> GameConfig.SCORE_TRIPLE_SPIN_BASE;
                case 1 -> GameConfig.SCORE_TRIPLE_SPIN_SINGLE;
                case 2 -> GameConfig.SCORE_TRIPLE_SPIN_DOUBLE;
                case 3 -> GameConfig.SCORE_TRIPLE_SPIN_TRIPLE; 
                default -> 0;
            };
            case TRIPLE_SPIN_MINI -> switch (linesCleared) {
                case 0 -> GameConfig.SCORE_TRIPLE_SPIN_MINI_BASE;
                case 1 -> GameConfig.SCORE_TRIPLE_SPIN_MINI_SINGLE;
                case 2 -> GameConfig.SCORE_TRIPLE_SPIN_MINI_DOUBLE;
                default -> 0;
            };
            case NONE -> 0;
        };
        
        return baseScore * level;
    }
    
    /**
     * Calcula pontuação total incluindo Spin.
     *
     * @param linesCleared Número de linhas completadas
     * @param spinType Tipo de Spin realizado
     * @param level Nível atual do jogo
     * @return Pontuação total calculada
     */
    public static int calculateTotalScore(int linesCleared, SpinDetector.SpinType spinType, int level) {
        int baseScore = calculateBaseScore(linesCleared, level);
        int spinScore = calculateSpinScore(spinType, linesCleared, level);
        
        return baseScore + spinScore;
    }

    /**
     * Calcula pontuação total incluindo Triple Spin.
     *
     * @param linesCleared Número de linhas completadas
     * @param tripleSpinType Tipo de Triple Spin realizado
     * @param level Nível atual do jogo
     * @return Pontuação total calculada
     */
    public static int calculateTotalScoreWithTripleSpin(int linesCleared, TripleSpinDetector.TripleSpinType tripleSpinType, int level) {
        int baseScore = calculateBaseScore(linesCleared, level);
        int tripleSpinScore = calculateTripleSpinScore(tripleSpinType, linesCleared, level);
        
        return baseScore + tripleSpinScore;
    }
    
    /**
     * Calcula pontuação para soft drop.
     *
     * @param level Nível atual do jogo
     * @return Pontuação por célula de soft drop
     */
    public static int calculateSoftDropScore(int level) {
        return GameConfig.SCORE_SOFT_DROP * level;
    }
    

    
    // Implementação dos métodos da interface ScoringStrategy
    
    @Override
    public int calculateLineClearScore(int linesCleared, int level, int combo) {
        int baseScore = calculateBaseScore(linesCleared, level);
        return baseScore + (combo * 50); // Bônus de combo
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
        return distance * GameConfig.SCORE_HARD_DROP * level;
    }
    
    @Override
    public int calculateTSpinScore(int level, boolean isMini) {
        if (isMini) {
            return GameConfig.SCORE_SPIN_MINI_BASE * level;
        } else {
            return GameConfig.SCORE_SPIN_BASE * level;
        }
    }
    
    @Override
    public int calculateComboScore(int comboCount, int level) {
        return comboCount * 50 * level; // 50 pontos por combo, multiplicado pelo nível
    }
    
    @Override
    public String getName() {
        return "StandardScoreCalculator";
    }
    
    @Override
    public void applyToGameState(GameState gameState) {
        // Implementação padrão vazia - o GameState já gerencia sua própria lógica
    }
}