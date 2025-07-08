package com.uneb.fluxblocks.architecture.interfaces;

import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Interface para abstrair o sistema de detecção de spins.
 * Permite diferentes algoritmos de detecção de spins.
 */
public interface SpinDetector {
    
    /**
     * Tipos de spin possíveis.
     */
    enum SpinType {
        NONE,           // Nenhum spin detectado
        MINI_SPIN,      // Mini spin (T-spin mini)
        SPIN,           // Spin normal (T-spin)
        TRIPLE_SPIN     // Triple spin (T-spin triple)
    }
    
    /**
     * Resultado da detecção de spin.
     */
    class SpinResult {
        private final SpinType spinType;
        private final boolean isValid;
        private final int linesCleared;
        private final String description;
        
        public SpinResult(SpinType spinType, boolean isValid, int linesCleared, String description) {
            this.spinType = spinType;
            this.isValid = isValid;
            this.linesCleared = linesCleared;
            this.description = description;
        }
        
        public SpinType getSpinType() { return spinType; }
        public boolean isValid() { return isValid; }
        public int getLinesCleared() { return linesCleared; }
        public String getDescription() { return description; }
    }
    
    /**
     * Detecta se uma peça realizou um spin.
     * @param piece A peça que foi colocada
     * @param linesCleared Número de linhas eliminadas
     * @return Resultado da detecção de spin
     */
    SpinResult detectSpin(BlockShape piece, int linesCleared);
    
    /**
     * Verifica se uma peça está em posição de spin.
     * @param piece A peça a ser verificada
     * @return true se está em posição de spin, false caso contrário
     */
    boolean isInSpinPosition(BlockShape piece);
    
    /**
     * Verifica se uma peça pode realizar um spin.
     * @param piece A peça a ser verificada
     * @return true se pode realizar spin, false caso contrário
     */
    boolean canSpin(BlockShape piece);
    
    /**
     * Obtém o tipo de spin baseado na posição da peça.
     * @param piece A peça a ser analisada
     * @return Tipo de spin detectado
     */
    SpinType getSpinType(BlockShape piece);
    
    /**
     * Verifica se uma peça está em posição de T-spin.
     * @param piece A peça a ser verificada
     * @return true se está em posição de T-spin, false caso contrário
     */
    boolean isTSpinPosition(BlockShape piece);
    
    /**
     * Verifica se uma peça está em posição de T-spin mini.
     * @param piece A peça a ser verificada
     * @return true se está em posição de T-spin mini, false caso contrário
     */
    boolean isTSpinMiniPosition(BlockShape piece);
    
    /**
     * Verifica se uma peça está em posição de T-spin triple.
     * @param piece A peça a ser verificada
     * @return true se está em posição de T-spin triple, false caso contrário
     */
    boolean isTSpinTriplePosition(BlockShape piece);
    
    /**
     * Obtém a pontuação baseada no tipo de spin.
     * @param spinType Tipo de spin
     * @param linesCleared Número de linhas eliminadas
     * @param level Nível atual do jogo
     * @return Pontuação do spin
     */
    int getSpinScore(SpinType spinType, int linesCleared, int level);
    
    /**
     * Reseta o estado do detector de spin.
     */
    void reset();
    
    /**
     * Limpa recursos do detector de spin.
     */
    void cleanup();
} 