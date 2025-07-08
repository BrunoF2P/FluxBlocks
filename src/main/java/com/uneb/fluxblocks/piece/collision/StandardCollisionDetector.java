package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;

/**
 * Implementação padrão do detector de colisão.
 * Baseada na implementação original do CollisionDetector.
 */
public class StandardCollisionDetector implements CollisionDetector {
    private final GameBoard board;
    private boolean active = true;

    public StandardCollisionDetector(GameBoard board) {
        this.board = board;
    }

    @Override
    public boolean checkCollision(BlockShape shape, GameBoard board) {
        return !isValidPosition(shape);
    }

    @Override
    public boolean checkPieceCollision(BlockShape shape, GameBoard board) {
        if (shape == null || board == null) {
            return false;
        }

        return shape.getCells().stream()
                .anyMatch(this::isCellInvalid);
    }

    @Override
    public boolean checkWallCollision(BlockShape shape, int boardWidth, int boardHeight) {
        if (shape == null) return false;

        return shape.getCells().stream()
                .anyMatch(cell -> {
                    int x = cell.getX();
                    int y = cell.getY();
                    return x < 0 || x >= boardWidth || y >= boardHeight;
                });
    }

    @Override
    public boolean isWithinBounds(BlockShape shape, int boardWidth, int boardHeight) {
        if (shape == null) return false;

        return shape.getCells().stream()
                .allMatch(cell -> {
                    int x = cell.getX();
                    int y = cell.getY();
                    return x >= 0 && x < boardWidth && y >= -4 && y < boardHeight;
                });
    }

    @Override
    public int getDistanceToCollision(BlockShape shape, GameBoard board, int directionX, int directionY) {
        if (shape == null || board == null) return -1;

        int distance = 0;
        BlockShape testShape = new BlockShape(shape.getCells(), shape.getType(), shape.isGlass());
        testShape.setPosition(shape.getX(), shape.getY());
        
        while (true) {
            testShape.move(directionX, directionY);
            if (!isValidPosition(testShape)) {
                return distance;
            }
            distance++;
        }
    }

    @Override
    public String getName() {
        return "StandardCollisionDetector";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Verifica se uma peça está numa posição válida no tabuleiro.
     * Método original mantido para compatibilidade.
     */
    public boolean isValidPosition(BlockShape piece) {
        if (piece == null || board == null) {
            return false;
        }

        return piece.getCells().stream()
                .noneMatch(this::isCellInvalid);
    }

    private boolean isCellInvalid(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();

        if (x < 0 || x >= board.getWidth()) {
            return true;
        }

        if (y < -4) {
            return true;
        }

        if (board.isValidPosition(x, y)) {
            return board.getCell(x, y) != 0;
        }

        return y >= board.getHeight() - GameConfig.BOARD_VISIBLE_ROW;
    }

    /**
     * Verifica se a peça está numa posição de descanso válida
     * (sobre outra peça ou no fundo do tabuleiro).
     * Método original mantido para compatibilidade.
     */
    public boolean isAtRestingPosition(BlockShape piece) {
        if (piece == null) return false;

        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.move(0, 1);
        boolean wouldCollide = !isValidPosition(piece);

        piece.setPosition(originalX, originalY);

        return wouldCollide;
    }

    /**
     * Verifica se é possível spawnar uma peça na posição especificada.
     * Método original mantido para compatibilidade.
     */
    public boolean canSpawn(BlockShape piece, int spawnX, int spawnY) {
        if (piece == null) return false;

        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.setPosition(spawnX, spawnY);
        boolean canSpawn = isValidPosition(piece);

        piece.setPosition(originalX, originalY);

        return canSpawn;
    }

    /**
     * Retorna o tabuleiro associado a este detector de colisão.
     * Método original mantido para compatibilidade.
     */
    public GameBoard getBoard() {
        return board;
    }
} 