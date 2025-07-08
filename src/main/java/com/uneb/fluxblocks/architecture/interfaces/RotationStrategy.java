package com.uneb.fluxblocks.architecture.interfaces;

import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Interface para abstrair o sistema de rotação de peças.
 * Permite diferentes estratégias de rotação (SRS, Super Rotation System, etc.).
 */
public interface RotationStrategy {
    
    /**
     * Resultado de uma tentativa de rotação.
     */
    enum RotationResult {
        SUCCESS,        // Rotação realizada com sucesso
        COLLISION,      // Colisão detectada
        OUT_OF_BOUNDS,  // Fora dos limites do tabuleiro
        INVALID_STATE,  // Estado inválido para rotação
        WALL_KICK_LEFT, // Rotação com wall kick para esquerda
        WALL_KICK_RIGHT // Rotação com wall kick para direita
    }
    
    /**
     * Rotaciona a peça no sentido horário.
     * @param piece A peça a ser rotacionada
     * @return Resultado da tentativa de rotação
     */
    RotationResult rotateClockwise(BlockShape piece);
    
    /**
     * Rotaciona a peça no sentido anti-horário.
     * @param piece A peça a ser rotacionada
     * @return Resultado da tentativa de rotação
     */
    RotationResult rotateCounterClockwise(BlockShape piece);
    
    /**
     * Rotaciona a peça 180 graus.
     * @param piece A peça a ser rotacionada
     * @return Resultado da tentativa de rotação
     */
    RotationResult rotate180(BlockShape piece);
    
    /**
     * Verifica se a peça pode ser rotacionada.
     * @param piece A peça a ser verificada
     * @return true se pode rotacionar, false caso contrário
     */
    boolean canRotate(BlockShape piece);
    
    /**
     * Valida uma rotação sem executá-la.
     * @param piece A peça a ser validada
     * @param clockwise true para rotação horária, false para anti-horária
     * @return Resultado da validação
     */
    RotationResult validateRotation(BlockShape piece, boolean clockwise);
    
    /**
     * Aplica wall kick se necessário para a rotação.
     * @param piece A peça a ser rotacionada
     * @param clockwise true para rotação horária, false para anti-horária
     * @return Resultado da tentativa de wall kick
     */
    RotationResult applyWallKick(BlockShape piece, boolean clockwise);
    
    /**
     * Obtém a rotação atual da peça.
     * @param piece A peça
     * @return Índice da rotação atual (0-3)
     */
    int getCurrentRotation(BlockShape piece);
    
    /**
     * Define a rotação da peça.
     * @param piece A peça
     * @param rotation Índice da rotação (0-3)
     */
    void setRotation(BlockShape piece, int rotation);
    
    /**
     * Obtém a forma da peça para uma rotação específica.
     * @param piece A peça
     * @param rotation Índice da rotação (0-3)
     * @return Matriz da forma da peça
     */
    int[][] getShapeForRotation(BlockShape piece, int rotation);
    
    /**
     * Verifica se a rotação resultaria em colisão.
     * @param piece A peça a ser verificada
     * @param rotation Índice da rotação a ser testada
     * @return true se haveria colisão, false caso contrário
     */
    boolean wouldCollide(BlockShape piece, int rotation);
    
    /**
     * Reseta o estado da rotação.
     */
    void reset();
    
    /**
     * Limpa recursos da rotação.
     */
    void cleanup();
} 