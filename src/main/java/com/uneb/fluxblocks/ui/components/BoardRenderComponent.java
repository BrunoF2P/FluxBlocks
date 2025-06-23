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

    // Cores e estilos do tabuleiro
    private static final Color BOARD_BACKGROUND_COLOR = Color.web("#15202b");
    private static final Color BOARD_GRADIENT_START = Color.web("#1e2b38");
    private static final Color BOARD_GRADIENT_END = Color.web("#14202c");
    private static final Color CELL_EMPTY_BACKGROUND = Color.web("#15202b");
    private static final Color CELL_EMPTY_STROKE = Color.rgb(255, 255, 255, 0.05);
    private static final Color CELL_FILLED_STROKE = Color.rgb(0, 0, 0, 0.2);
    
    // Dimensões e espaçamentos
    private static final double CELL_SPACING = 1.0;
    private static final double CELL_CORNER_RADIUS = 10.0;
    private static final double CELL_EMPTY_CORNER_RADIUS = 8.0;
    private static final double CELL_STROKE_WIDTH = 0.5;

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
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Fundo base
        gc.setFill(BOARD_BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Gradiente de fundo
        Stop[] stops = new Stop[]{
                new Stop(0, BOARD_GRADIENT_START),
                new Stop(1, BOARD_GRADIENT_END)
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(gradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawCell(int x, int y, int cellType) {
        int pixelX = x * cellSize;
        int pixelY = y * cellSize;

        // Desenho da célula
        double innerSize = cellSize - (CELL_SPACING * 2);

        // Fundo da célula
        gc.setFill(CELL_EMPTY_BACKGROUND);
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        if (cellType != 0) {
            // Célula preenchida com peça
            Color tetroColor = BlockShapeColors.getColor(cellType);
            
            // Aplica a cor sólida da peça
            gc.setFill(tetroColor);
            gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            
            // Borda da célula preenchida
            gc.setStroke(CELL_FILLED_STROKE);
            gc.setLineWidth(CELL_STROKE_WIDTH);
            gc.strokeRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            
        } else {
            // Célula vazia
            gc.setFill(CELL_EMPTY_BACKGROUND);
            gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_EMPTY_CORNER_RADIUS, CELL_EMPTY_CORNER_RADIUS);
            
            // Borda sutil da célula vazia
            gc.setStroke(CELL_EMPTY_STROKE);
            gc.setLineWidth(CELL_STROKE_WIDTH);
            gc.strokeRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_EMPTY_CORNER_RADIUS, CELL_EMPTY_CORNER_RADIUS);
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