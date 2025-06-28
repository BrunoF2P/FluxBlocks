package com.uneb.fluxblocks.piece.rendering;

import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import java.util.List;

/**
 * Calcula a posição da peça fantasma (shadow piece).
 */
public class ShadowPieceCalculator {
    private final CollisionDetector collisionDetector;
    private static final int MAX_FALL_DISTANCE = 40;

    public ShadowPieceCalculator(CollisionDetector collisionDetector) {
        if (collisionDetector == null) {
            throw new IllegalArgumentException("CollisionDetector não pode ser null");
        }
        this.collisionDetector = collisionDetector;
    }

    /**
     * Calcula a peça fantasma para a peça atual.
     *
     * @param currentPiece A peça atual
     * @return Uma peça fantasma na posição mais baixa possível, ou null se a peça for null
     */
    public BlockShape calculateShadowPiece(BlockShape currentPiece) {
        if (currentPiece == null) return null;

        // Cria a peça fantasma com o mesmo tipo
        BlockShape shadow = BlockShapeFactory.createBlockShape(
                BlockShape.Type.values()[currentPiece.getType()]);
        
        // Copia a posição e estado da peça atual
        shadow.setPosition(currentPiece.getX(), currentPiece.getY());
        copyBlockShapeState(currentPiece, shadow);

        // Move a peça fantasma para baixo até encontrar colisão
        int fallDistance = 0;
        while (fallDistance < MAX_FALL_DISTANCE) {
            shadow.move(0, 1);
            if (!collisionDetector.isValidPosition(shadow)) {
                shadow.move(0, -1); // Volta uma posição
                break;
            }
            fallDistance++;
        }

        // Se atingiu o limite máximo, algo está errado
        if (fallDistance >= MAX_FALL_DISTANCE) {
            return null;
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
        List<Cell> sourceCells = source.getCells();
        List<Cell> targetCells = target.getCells();
        
        int cellCount = Math.min(sourceCells.size(), targetCells.size());
        
        for (int i = 0; i < cellCount; i++) {
            Cell originalCell = sourceCells.get(i);
            Cell shadowCell = targetCells.get(i);

            shadowCell.setRelativeX(originalCell.getRelativeX());
            shadowCell.setRelativeY(originalCell.getRelativeY());
        }
    }
}