package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.game.logic.GameBoard;

/**
 * Interface para detectores de colisão do jogo.
 * Permite implementar diferentes algoritmos de detecção de colisão.
 */
public interface CollisionDetector {
    
    /**
     * Verifica se uma peça colide com o tabuleiro
     * @param shape A peça a ser verificada
     * @param board O tabuleiro do jogo
     * @return true se houver colisão
     */
    boolean checkCollision(BlockShape shape, GameBoard board);
    
    /**
     * Verifica se uma peça colide com outras peças no tabuleiro
     * @param shape A peça a ser verificada
     * @param board O tabuleiro do jogo
     * @return true se houver colisão com outras peças
     */
    boolean checkPieceCollision(BlockShape shape, GameBoard board);
    
    /**
     * Verifica se uma peça colide com as bordas do tabuleiro
     * @param shape A peça a ser verificada
     * @param boardWidth Largura do tabuleiro
     * @param boardHeight Altura do tabuleiro
     * @return true se houver colisão com as bordas
     */
    boolean checkWallCollision(BlockShape shape, int boardWidth, int boardHeight);
    
    /**
     * Verifica se uma peça está dentro dos limites do tabuleiro
     * @param shape A peça a ser verificada
     * @param boardWidth Largura do tabuleiro
     * @param boardHeight Altura do tabuleiro
     * @return true se a peça estiver dentro dos limites
     */
    boolean isWithinBounds(BlockShape shape, int boardWidth, int boardHeight);
    
    /**
     * Calcula a distância até a próxima colisão na direção especificada
     * @param shape A peça a ser verificada
     * @param board O tabuleiro do jogo
     * @param directionX Direção X (-1, 0, 1)
     * @param directionY Direção Y (-1, 0, 1)
     * @return Distância até a colisão, ou -1 se não houver colisão
     */
    int getDistanceToCollision(BlockShape shape, GameBoard board, int directionX, int directionY);
    
    /**
     * Retorna o nome do detector
     * @return Nome do detector
     */
    String getName();
    
    /**
     * Verifica se o detector está ativo
     * @return true se o detector estiver ativo
     */
    boolean isActive();
    
    /**
     * Ativa/desativa o detector
     * @param active true para ativar, false para desativar
     */
    void setActive(boolean active);
}