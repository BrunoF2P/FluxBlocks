package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;


public class BoardCanvas {

    private final Entity boardEntity;
    private final BoardRenderComponent renderComponent;
    private final Canvas canvas;
    private final int width;
    private final int height;
    private final int cellSize;

    /**
     * Construtor da classe BoardCanvas
     * Inicializa o canvas e a entidade do tabuleiro com um grid vazio.
     *
     * @param width    largura do tabuleiro em células
     * @param height   altura do tabuleiro em células
     * @param cellSize tamanho de cada célula em pixels
     */
    public BoardCanvas(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.canvas = new Canvas(width * cellSize, height * cellSize);
        
        this.canvas.setCache(true);
        this.canvas.setCacheHint(CacheHint.SPEED);

        int[][] initialGrid = new int[height][width];

        this.renderComponent = new BoardRenderComponent(initialGrid, canvas, cellSize);

        this.boardEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(renderComponent)
                .buildAndAttach();
    }

    /**
     * Atualiza o estado visual do canvas com base no grid atual de jogo.
     *
     * @param grid matriz bidimensional representando o estado do tabuleiro
     */
    public void updateBoard(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return;
        }
        renderComponent.updateGrid(grid);
    }

    /**
     * Limpa o conteúdo do canvas, restaurando o fundo e redesenhando todas as células como vazias.
     */
    public void clearBoard() {
        renderComponent.clearBoard();
    }

    /**
     * Retorna o canvas JavaFX para uso na interface.
     *
     * @return Canvas JavaFX
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Remove a entidade do mundo FXGL.
     */
    public void destroy() {
        if (boardEntity != null && boardEntity.isActive()) {
            boardEntity.removeFromWorld();
        }
    }

    /**
     * Retorna a largura do tabuleiro em células.
     *
     * @return Largura do tabuleiro
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retorna a altura do tabuleiro em células.
     *
     * @return Altura do tabuleiro
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retorna o tamanho de cada célula em pixels.
     *
     * @return Tamanho da célula
     */
    public int getCellSize() {
        return cellSize;
    }
}