package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.ui.theme.BlockShapeColors;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Componente responsável por renderizar o tabuleiro do jogo.
 * Desenha as células do tabuleiro com base no estado atual do grid.
 */
public class BoardRenderComponent extends Component {

    private final int[][] grid;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int cellSize;
    private final int width;
    private final int height;

    private boolean firstDraw = true;
    private final int[][] previousGrid;

    public BoardRenderComponent(int[][] grid, Canvas canvas, int cellSize) {
        this.grid = grid;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.cellSize = cellSize;
        this.width = grid[0].length;
        this.height = grid.length;

        this.previousGrid = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.previousGrid[y][x] = -1;
            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (firstDraw) {
            drawBackground();
            firstDraw = false;
        }

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (previousGrid[r][c] != grid[r][c]) {
                    drawCell(c, r, grid[r][c]);
                    previousGrid[r][c] = grid[r][c];
                }
            }
        }
    }

    private void drawBackground() {
        // Fundo
        gc.setFill(Color.web("#15202b"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#1e2b38")),
                new Stop(1, Color.web("#14202c"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(gradient);
        gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 15, 15);

        // Borda
        gc.setStroke(Color.web("#2c3e50"));
        gc.setLineWidth(10);
        gc.strokeRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 15, 15);
    }

    private void drawCell(int x, int y, int cellType) {
        int pixelX = x * cellSize;
        int pixelY = y * cellSize;

        // Desenho da célula
        int spacing = 1;
        int innerSize = cellSize - (spacing * 2);

        // Fundo da célula
        gc.setFill(Color.web("#15202b"));
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        if (cellType != 0) {
            Color tetroColor = BlockShapeColors.getColor(cellType);
            gc.setFill(tetroColor);
            gc.fillRoundRect(pixelX + spacing, pixelY + spacing, innerSize, innerSize, 10, 10);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(0.5);
            gc.strokeRoundRect(pixelX + spacing, pixelY + spacing, innerSize, innerSize, 10, 10);
        } else {
            gc.setFill(Color.web("#15202b"));
            gc.fillRoundRect(pixelX + spacing, pixelY + spacing, innerSize, innerSize, 8, 8);
            gc.setStroke(Color.rgb(255, 255, 255, 0.05));
            gc.setLineWidth(0.5);
            gc.strokeRoundRect(pixelX + spacing, pixelY + spacing, innerSize, innerSize, 8, 8);
        }
    }

    /**
     * Limpa o tabuleiro e redesenhando todas as células como vazias.
     */
    public void clearBoard() {
        drawBackground();
        firstDraw = false;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                drawCell(x, y, 0); // Desenhar células vazias
                previousGrid[y][x] = 0;
            }
        }
    }

    /**
     * Atualiza o grid interno do componente.
     *
     * @param newGrid nova matriz do tabuleiro
     */
    public void updateGrid(int[][] newGrid) {
        for (int y = 0; y < height; y++) {
            if (width >= 0) System.arraycopy(newGrid[y], 0, this.grid[y], 0, width);
        }
    }
}