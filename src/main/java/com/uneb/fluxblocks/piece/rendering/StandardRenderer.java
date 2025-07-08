package com.uneb.fluxblocks.piece.rendering;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.game.logic.GameBoard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Implementação padrão do renderizador do FluxBlocks.
 * Baseada no sistema atual de renderização do jogo.
 */
public class StandardRenderer implements Renderer {
    
    private boolean active = true;
    private String style = "standard";
    
    @Override
    public void renderPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize) {
        if (!active || shape == null) return;
        
        for (var cell : shape.getCells()) {
            double cellX = x + (cell.getX() * cellSize);
            double cellY = y + (cell.getY() * cellSize);
            
            // Renderiza a célula com cor baseada no tipo
            Color cellColor = getCellColor(cell.getType());
            gc.setFill(cellColor);
            gc.fillRect(cellX, cellY, cellSize, cellSize);
            
            // Borda da célula
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(cellX, cellY, cellSize, cellSize);
        }
    }
    
    @Override
    public void renderBoard(GameBoard board, GraphicsContext gc, double x, double y, double cellSize) {
        if (!active || board == null) return;
        
        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                int cellValue = board.getCell(col, row);
                if (cellValue != 0) {
                    double cellX = x + (col * cellSize);
                    double cellY = y + (row * cellSize);
                    renderCell(cellValue, gc, cellX, cellY, cellSize);
                }
            }
        }
    }
    
    @Override
    public void renderCell(int cellValue, GraphicsContext gc, double x, double y, double cellSize) {
        if (!active) return;
        
        if (cellValue == 10) {
            Color glassColor = getCellColor(cellValue);
            gc.setFill(glassColor);
            gc.fillRect(x, y, cellSize, cellSize);
            
            // Gradiente de brilho
            gc.save();
            gc.setGlobalAlpha(0.35);
            javafx.scene.paint.LinearGradient shine = new javafx.scene.paint.LinearGradient(
                x, y, x, y + cellSize / 2, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.rgb(255, 255, 255, 0.9)),
                new javafx.scene.paint.Stop(1, Color.rgb(255, 255, 255, 0.0))
            );
            gc.setFill(shine);
            gc.fillRect(x, y, cellSize, cellSize / 2);
            gc.restore();
            
            // Rachaduras
            gc.save();
            gc.setStroke(Color.rgb(255, 255, 255, 0.25));
            gc.setLineWidth(0.8);
            
            double cx = x + cellSize / 2;
            double cy = y + cellSize / 2;
            int cracks = 6;
            double radius = cellSize / 2;
            
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
            Color cellColor = getCellColor(cellValue);
            gc.setFill(cellColor);
            gc.fillRect(x, y, cellSize, cellSize);
        }
        
        // Borda da célula
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, cellSize, cellSize);
    }
    
    @Override
    public void renderShadow(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize) {
        if (!active || shape == null) return;
        
        for (var cell : shape.getCells()) {
            double cellX = x + (cell.getX() * cellSize);
            double cellY = y + (cell.getY() * cellSize);
            
            // Sombra com transparência
            gc.setFill(Color.GRAY.deriveColor(0, 1, 1, 0.3));
            gc.fillRect(cellX, cellY, cellSize, cellSize);
            
            // Borda da sombra
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);
            gc.strokeRect(cellX, cellY, cellSize, cellSize);
        }
    }
    
    @Override
    public void renderNextPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize) {
        renderPiece(shape, gc, x, y, cellSize);
    }
    
    @Override
    public void renderHoldPiece(BlockShape shape, GraphicsContext gc, double x, double y, double cellSize) {
        renderPiece(shape, gc, x, y, cellSize);
    }
    
    @Override
    public void clearArea(GraphicsContext gc, double x, double y, double width, double height) {
        if (!active) return;
        
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(x, y, width, height);
    }
    
    @Override
    public String getName() {
        return "StandardRenderer";
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void setStyle(String style) {
        this.style = style;
    }
    
    @Override
    public String getStyle() {
        return style;
    }
    
    /**
     * Retorna a cor baseada no tipo da célula.
     */
    private Color getCellColor(int cellType) {
        return switch (cellType) {
            case 1 -> Color.CYAN;    // I
            case 2 -> Color.BLUE;     // J
            case 3 -> Color.ORANGE;   // L
            case 4 -> Color.YELLOW;   // O
            case 5 -> Color.GREEN;    // S
            case 6 -> Color.PURPLE;   // T
            case 7 -> Color.RED;      // Z
            case 8 -> Color.MAGENTA;  // X
            case 10 -> Color.WHITE;   // Glass
            default -> Color.GRAY;
        };
    }
} 