package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Responsável por detectar colisões de peças com outras peças
 * ou com os limites do tabuleiro.
 * Implementa lógica compatível com TETR.IO.
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
     * (sobre outra peça ou, no fundo do tabuleiro).
     *
     * @param piece A peça a verificar
     * @return true se a peça está numa posição de descanso
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
     * Esta é a verificação principal para game over no estilo TETR.IO.
     *
     * @param piece A peça a ser spawnada
     * @param spawnX Posição X de spawn
     * @param spawnY Posição Y de spawn
     * @return true se é possível spawnar, false se causaria game over
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
}