package com.uneb.tetris.piece.collision;

import com.uneb.tetris.game.logic.GameBoard;
import com.uneb.tetris.piece.entities.Cell;
import com.uneb.tetris.piece.entities.Tetromino;

/**
 * Responsável por detectar colisões de peças com outras peças
 * ou com os limites do tabuleiro.
 */
public class CollisionDetector {
    private final GameBoard board;

    public CollisionDetector(GameBoard board) {
        this.board = board;
    }

    /**
     * Verifica se uma peça está numa posição válida no tabuleiro.
     *
     * @param piece A peça a ser verificada
     * @return true se a posição é válida, false caso contrário
     */
    public boolean isValidPosition(Tetromino piece) {
        if (piece == null || board == null) {
            return false;
        }

        return piece.getCells().stream()
                .noneMatch(this::isCellInvalid);
    }

    private boolean isCellInvalid(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();

        return !board.isValidPosition(x, y) || board.getCell(x, y) != 0;
    }

    /**
     * Verifica se a peça está numa posição de descanso válida
     * (sobre outra peça ou, no fundo do tabuleiro).
     *
     * @param piece A peça a verificar
     * @return true se a peça está numa posição de descanso
     */
    public boolean isAtRestingPosition(Tetromino piece) {
        if (piece == null) return false;

        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.move(0, 1);
        boolean wouldCollide = !isValidPosition(piece);

        piece.setPosition(originalX, originalY);

        return wouldCollide;
    }
}