package com.uneb.fluxblocks.piece.rendering;

import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;

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
    public BlockShape calculateShadowPiece(BlockShape currentPiece) {
        if (currentPiece == null) return null;

        BlockShape shadow = BlockShapeFactory.createBlockShape(
                BlockShape.Type.values()[currentPiece.getType()]);
        shadow.setPosition(currentPiece.getX(), currentPiece.getY());

        copyBlockShapeState(currentPiece, shadow);

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
    private void copyBlockShapeState(BlockShape source, BlockShape target) {
        for (int i = 0; i < source.getCells().size(); i++) {
            Cell originalCell = source.getCells().get(i);
            Cell shadowCell = target.getCells().get(i);

            shadowCell.setRelativeX(originalCell.getRelativeX());
            shadowCell.setRelativeY(originalCell.getRelativeY());
        }
    }
}