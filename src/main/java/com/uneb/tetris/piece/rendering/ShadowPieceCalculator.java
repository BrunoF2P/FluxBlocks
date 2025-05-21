package com.uneb.tetris.piece.rendering;

import com.uneb.tetris.piece.Cell;
import com.uneb.tetris.piece.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.collision.CollisionDetector;

/**
 * Calcula a posição da peça fantasma (shadow piece).
 */
public class ShadowPieceCalculator {
    private final CollisionDetector collisionDetector;

    public ShadowPieceCalculator(CollisionDetector collisionDetector) {
        this.collisionDetector = collisionDetector;
    }

    /**
     * Calcula a peça fantasma para a peça atual.
     *
     * @param currentPiece A peça atual
     * @return Uma peça fantasma na posição mais baixa possível
     */
    public Tetromino calculateShadowPiece(Tetromino currentPiece) {
        if (currentPiece == null) return null;

        Tetromino shadow = TetrominoFactory.createTetromino(
                Tetromino.Type.values()[currentPiece.getType()]);
        shadow.setPosition(currentPiece.getX(), currentPiece.getY());

        copyTetrominoState(currentPiece, shadow);

        while (true) {
            shadow.move(0, 1);
            if (!collisionDetector.isValidPosition(shadow)) {
                shadow.move(0, -1);
                break;
            }
        }

        return shadow;
    }

    /**
     * Copia o estado (orientação/rotação) de uma peça para outra.
     *
     * @param source Peça fonte
     * @param target Peça destino
     */
    private void copyTetrominoState(Tetromino source, Tetromino target) {
        for (int i = 0; i < source.getCells().size(); i++) {
            Cell originalCell = source.getCells().get(i);
            Cell shadowCell = target.getCells().get(i);

            shadowCell.setRelativeX(originalCell.getRelativeX());
            shadowCell.setRelativeY(originalCell.getRelativeY());
        }
    }
}