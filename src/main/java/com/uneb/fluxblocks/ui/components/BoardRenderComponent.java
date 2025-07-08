package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.configuration.GameConfig;
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
                    if (r < GameConfig.BOARD_VISIBLE_ROW) {
                        drawBufferCell(c, r, grid[r][c]);
                    } else {
                        drawCell(c, r, grid[r][c]);
                    }
                    previousGrid[r][c] = grid[r][c];
                }
            }
        }
    }

    private void drawBufferCell(int x, int y, int cellType) {
        int pixelX = x * cellSize;
        int pixelY = y * cellSize;
        double innerSize = cellSize - (CELL_SPACING * 2);

        gc.clearRect(pixelX, pixelY, cellSize, cellSize);
        gc.setFill(Color.rgb(21, 32, 43, 0.3));
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        if (cellType != 0) {
            if (cellType == 9) { 
                // Peça fantasma na área de buffer
                Color shadowColor = BlockShapeColors.getColor(cellType);
                gc.save();
                gc.setGlobalAlpha(0.4); 
                gc.setFill(shadowColor.deriveColor(1, 1, 1, 0.7));
                gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING,
                        innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
                gc.restore();
                
                // Borda da peça fantasma na área de buffer
                gc.setStroke(shadowColor.deriveColor(1, 1, 1, 0.5));
                gc.setLineWidth(1);
                gc.strokeRoundRect(pixelX + CELL_SPACING + 1, pixelY + CELL_SPACING + 1,
                        innerSize - 2, innerSize - 2, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            } else if (cellType == 10) {
                Color glassColor = BlockShapeColors.getGlassColor();
                gc.setFill(glassColor);
                gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            
                // Gradiente de brilho
                gc.save();
                gc.setGlobalAlpha(0.35);
                LinearGradient shine = new LinearGradient(
                    pixelX + CELL_SPACING, pixelY + CELL_SPACING, 
                    pixelX + CELL_SPACING, pixelY + CELL_SPACING + innerSize / 2, 
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                    new Stop(1, Color.rgb(255, 255, 255, 0.0))
                );
                gc.setFill(shine);
                gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize / 2, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
                gc.restore();
            
                // Rachaduras
                gc.save();
                gc.setStroke(Color.rgb(255, 255, 255, 0.25));
                gc.setLineWidth(0.8);
            
                double cx = pixelX + CELL_SPACING + innerSize / 2;
                double cy = pixelY + CELL_SPACING + innerSize / 2;
                int cracks = 6;
                double radius = innerSize / 2;
            
                for (int i = 0; i < cracks; i++) {
                    double angle = Math.toRadians(360.0 / cracks * i + Math.random() * 15 - 7.5);
                    double ex = cx + Math.cos(angle) * radius;
                    double ey = cy + Math.sin(angle) * radius;
                    gc.strokeLine(cx, cy, ex, ey);
            
                    double branchAngle1 = angle + Math.toRadians(20 + Math.random() * 10);
                    double bx1 = cx + Math.cos(branchAngle1) * (radius * 0.5);
                    double by1 = cy + Math.sin(branchAngle1) * (radius * 0.5);
                    gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx1, by1);
            
                    double branchAngle2 = angle - Math.toRadians(20 + Math.random() * 10);
                    double bx2 = cx + Math.cos(branchAngle2) * (radius * 0.4);
                    double by2 = cy + Math.sin(branchAngle2) * (radius * 0.4);
                    gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx2, by2);
                }
            
                gc.restore();
            } else {
                Color pieceColor = BlockShapeColors.getColor(cellType);
                gc.setFill(pieceColor);
                gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING,
                        innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);

                gc.setStroke(CELL_FILLED_STROKE);
                gc.setLineWidth(CELL_STROKE_WIDTH);
                gc.strokeRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING,
                        innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            }
        }
    }


    private void drawBackground() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Área principal do tabuleiro
        gc.setFill(BOARD_BACKGROUND_COLOR);
        gc.fillRect(0, GameConfig.BOARD_VISIBLE_ROW * cellSize,
                canvas.getWidth(), canvas.getHeight() - (GameConfig.BOARD_VISIBLE_ROW * cellSize));

        // Área do buffer (parte superior) com transparência
        gc.setFill(Color.rgb(21, 32, 43, 0.0)); // Cor mais transparente para a área do buffer
        gc.fillRect(0, 0, canvas.getWidth(), GameConfig.BOARD_VISIBLE_ROW * cellSize);
    }

    private void drawCell(int x, int y, int cellType) {
        int pixelX = x * cellSize;
        int pixelY = y * cellSize;
        double innerSize = cellSize - (CELL_SPACING * 2);

        // Fundo da célula
        gc.setFill(CELL_EMPTY_BACKGROUND);
        gc.fillRect(pixelX, pixelY, cellSize, cellSize);

        if (cellType == 9) { // 9 é o código para a sombra
            // Célula de sombra com aparência mais clara
            Color shadowColor = BlockShapeColors.getColor(cellType);
            gc.save();
            gc.setGlobalAlpha(0.4); // Aumenta a opacidade geral
            gc.setFill(shadowColor.deriveColor(1, 1, 1, 0.8)); // Aumenta o brilho do preenchimento
            gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING,
                    innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            gc.restore();
            
            // Borda da sombra mais clara
            gc.setStroke(shadowColor.deriveColor(1, 1, 1, 0.6)); // Borda mais visível
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(pixelX + CELL_SPACING + 1, pixelY + CELL_SPACING + 1,
                    innerSize - 2, innerSize - 2, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);

        } else if (cellType == 10) {
            // Fundo translúcido azul
            Color glassColor = BlockShapeColors.getGlassColor();
            gc.setFill(glassColor);
            gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
        
            // Gradiente de brilho
            gc.save();
            gc.setGlobalAlpha(0.35);
            LinearGradient shine = new LinearGradient(
                pixelX, pixelY, pixelX, pixelY + innerSize / 2, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                new Stop(1, Color.rgb(255, 255, 255, 0.0))
            );
            gc.setFill(shine);
            gc.fillRoundRect(pixelX + CELL_SPACING, pixelY + CELL_SPACING, innerSize, innerSize / 2, CELL_CORNER_RADIUS, CELL_CORNER_RADIUS);
            gc.restore();
        
            // Rachaduras
            gc.save();
            gc.setStroke(Color.rgb(255, 255, 255, 0.25));
            gc.setLineWidth(0.8);
        
            double cx = pixelX + CELL_SPACING + innerSize / 2;
            double cy = pixelY + CELL_SPACING + innerSize / 2;
            int cracks = 6;
            double radius = innerSize / 2;
        
            for (int i = 0; i < cracks; i++) {
                double angle = Math.toRadians(360.0 / cracks * i + Math.random() * 15 - 7.5);
                double ex = cx + Math.cos(angle) * radius;
                double ey = cy + Math.sin(angle) * radius;
                gc.strokeLine(cx, cy, ex, ey);
        
                double branchAngle1 = angle + Math.toRadians(20 + Math.random() * 10);
                double bx1 = cx + Math.cos(branchAngle1) * (radius * 0.5);
                double by1 = cy + Math.sin(branchAngle1) * (radius * 0.5);
                gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx1, by1);
        
                double branchAngle2 = angle - Math.toRadians(20 + Math.random() * 10);
                double bx2 = cx + Math.cos(branchAngle2) * (radius * 0.4);
                double by2 = cy + Math.sin(branchAngle2) * (radius * 0.4);
                gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx2, by2);
            }
        
            gc.restore();
                
        } else if (cellType != 0) {
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