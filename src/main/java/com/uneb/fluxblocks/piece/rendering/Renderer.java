package com.uneb.fluxblocks.piece.rendering;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.game.logic.GameBoard;
import javafx.scene.canvas.GraphicsContext;

/**
 * Interface para renderizadores do jogo.
 * Permite implementar diferentes estratégias de renderização.
 */
public interface Renderer {
    
    /**
     * Renderiza uma peça no contexto gráfico especificado
     * @param shape A peça a ser renderizada
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Renderiza o tabuleiro no contexto gráfico especificado
     * @param board O tabuleiro a ser renderizado
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderBoard(GameBoard board, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Renderiza uma célula específica
     * @param cellValue Valor da célula
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderCell(int cellValue, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Renderiza a sombra de uma peça
     * @param shape A peça para renderizar a sombra
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderShadow(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Renderiza a próxima peça
     * @param shape A próxima peça
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderNextPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Renderiza a peça em hold
     * @param shape A peça em hold
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param cellSize Tamanho da célula
     */
    void renderHoldPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize);
    
    /**
     * Limpa a área de renderização
     * @param gc O contexto gráfico
     * @param x Posição X
     * @param y Posição Y
     * @param width Largura
     * @param height Altura
     */
    void clearArea(GraphicsContext gc, double x, double y, double width, double height);
    
    /**
     * Retorna o nome do renderizador
     * @return Nome do renderizador
     */
    String getName();
    
    /**
     * Verifica se o renderizador está ativo
     * @return true se o renderizador estiver ativo
     */
    boolean isActive();
    
    /**
     * Ativa/desativa o renderizador
     * @param active true para ativar, false para desativar
     */
    void setActive(boolean active);
    
    /**
     * Configura o estilo de renderização
     * @param style Nome do estilo
     */
    void setStyle(String style);
    
    /**
     * Retorna o estilo atual
     * @return Nome do estilo atual
     */
    String getStyle();
} 