package com.uneb.fluxblocks.architecture.interfaces;

import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Interface para abstrair o sistema de movimento de peças.
 * Permite diferentes estratégias de movimento (padrão, suave, rígido, etc.).
 */
public interface MovementStrategy {
    
    /**
     * Resultado de uma tentativa de movimento.
     */
    enum MovementResult {
        SUCCESS,        // Movimento realizado com sucesso
        COLLISION,      // Colisão detectada
        OUT_OF_BOUNDS,  // Fora dos limites do tabuleiro
        INVALID_STATE   // Estado inválido para movimento
    }
    
    /**
     * Verifica se a peça pode se mover na direção especificada.
     * @param piece A peça a ser movida
     * @param deltaX Deslocamento horizontal (-1 = esquerda, +1 = direita)
     * @param deltaY Deslocamento vertical (-1 = cima, +1 = baixo)
     * @return true se pode mover, false caso contrário
     */
    boolean canMove(BlockShape piece, int deltaX, int deltaY);
    
    /**
     * Move a peça na direção especificada.
     * @param piece A peça a ser movida
     * @param deltaX Deslocamento horizontal
     * @param deltaY Deslocamento vertical
     * @return Resultado da tentativa de movimento
     */
    MovementResult move(BlockShape piece, int deltaX, int deltaY);
    
    /**
     * Valida um movimento sem executá-lo.
     * @param piece A peça a ser validada
     * @param deltaX Deslocamento horizontal
     * @param deltaY Deslocamento vertical
     * @return Resultado da validação
     */
    MovementResult validateMovement(BlockShape piece, int deltaX, int deltaY);
    
    /**
     * Move a peça para a esquerda.
     * @param piece A peça a ser movida
     * @return Resultado da tentativa de movimento
     */
    MovementResult moveLeft(BlockShape piece);
    
    /**
     * Move a peça para a direita.
     * @param piece A peça a ser movida
     * @return Resultado da tentativa de movimento
     */
    MovementResult moveRight(BlockShape piece);
    
    /**
     * Move a peça para baixo (soft drop).
     * @param piece A peça a ser movida
     * @return Resultado da tentativa de movimento
     */
    MovementResult moveDown(BlockShape piece);
    
    /**
     * Realiza hard drop da peça (move até colidir).
     * @param piece A peça a ser movida
     * @return Número de células movidas para baixo
     */
    int hardDrop(BlockShape piece);
    
    /**
     * Verifica se a peça está tocando uma parede.
     * @param piece A peça a ser verificada
     * @return true se está tocando parede, false caso contrário
     */
    boolean isTouchingWall(BlockShape piece);
    
    /**
     * Verifica se a peça está tocando o chão.
     * @param piece A peça a ser verificada
     * @return true se está tocando o chão, false caso contrário
     */
    boolean isTouchingGround(BlockShape piece);
    
    /**
     * Verifica se a peça está colidindo com outras peças.
     * @param piece A peça a ser verificada
     * @return true se está colidindo, false caso contrário
     */
    boolean isColliding(BlockShape piece);
    
    /**
     * Reseta o estado do movimento.
     */
    void reset();
    
    /**
     * Limpa recursos do movimento.
     */
    void cleanup();
} 