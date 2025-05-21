package com.uneb.tetris.piece.movement;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.piece.Tetromino;
import com.uneb.tetris.piece.collision.CollisionDetector;
import com.uneb.tetris.piece.timing.LockDelayManager;

/**
 * Gerencia a movimentação de peças (esquerda, direita, baixo, hard drop).
 */
public class PieceMovementHandler {
    private final CollisionDetector collisionDetector;
    private final LockDelayManager lockDelayManager;
    private final GameMediator mediator;

    /** Flag que indica se o jogador está realizando soft drop */
    private boolean isSoftDropping = false;

    /** Distância percorrida durante o soft drop atual */
    private int softDropDistance = 0;

    /**
     * Constrói um novo manipulador de movimento de peças.
     *
     * @param collisionDetector Detector de colisões para verificar posições válidas
     * @param lockDelayManager Gerenciador de atraso de bloqueio
     * @param mediator O mediador para comunicação entre componentes do jogo
     */
    public PieceMovementHandler(CollisionDetector collisionDetector, LockDelayManager lockDelayManager, GameMediator mediator) { // Modificado
        this.collisionDetector = collisionDetector;
        this.lockDelayManager = lockDelayManager;
        this.mediator = mediator;
    }

    /**
     * Move a peça para a esquerda se possível.
     *
     * @param piece A peça a ser movida (não pode ser nula)
     * @return true se o movimento foi bem-sucedido
     * @throws NullPointerException se a peça for nula
     */
    public boolean moveLeft(Tetromino piece) {
        boolean moved = tryMove(piece, -1, 0);

        if (moved) {
            boolean isAtRest = collisionDetector.isAtRestingPosition(piece);
            lockDelayManager.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(GameEvents.UiEvents.COLLISION_LEFT, null);
        }

        return moved;
    }

    /**
     * Move a peça para a direita se possível.
     *
     * @param piece A peça a ser movida
     * @return true se o movimento foi bem-sucedido
     */
    public boolean moveRight(Tetromino piece) {
        boolean moved = tryMove(piece, 1, 0);

        if (moved) {
            boolean isAtRest = collisionDetector.isAtRestingPosition(piece);
            lockDelayManager.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(GameEvents.UiEvents.COLLISION_RIGHT, null);
        }

        return moved;
    }

    /**
     * Move a peça para baixo (soft drop).
     *
     * @param piece A peça a ser movida (pode ser nula)
     * @return true se o movimento foi bem-sucedido
     */
    public boolean moveDown(Tetromino piece) {
        if (piece == null) return false;

        isSoftDropping = true;
        boolean moved = tryMove(piece, 0, 1);

        if (moved) {
            lockDelayManager.resetLockDelay(piece, collisionDetector.isAtRestingPosition(piece));
            softDropDistance++;
        }

        return moved;
    }

    /**
     * Realiza um hard drop da peça.
     *
     * @param piece A peça a ser dropped (pode ser nula)
     * @return A distância percorrida durante o drop (0 se a peça for nula)
     */
    public int hardDrop(Tetromino piece) {
        if (piece == null) return 0;

        int distance = 0;
        while (tryMove(piece, 0, 1)) {
            distance++;
        }

        resetSoftDropTracking();
        return distance;
    }

    /**
     * Tenta mover a peça na direção especificada.
     *
     * @param piece A peça a ser movida
     * @param deltaX Movimento horizontal
     * @param deltaY Movimento vertical
     * @return true se o movimento foi possível
     */
    private boolean tryMove(Tetromino piece, int deltaX, int deltaY) {
        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.move(deltaX, deltaY);
        boolean isValid = collisionDetector.isValidPosition(piece);

        if (!isValid) {
            piece.setPosition(originalX, originalY);
        }

        return isValid;
    }

    /**
     * Reseta o tracking de soft drop.
     */
    public void resetSoftDropTracking() {
        isSoftDropping = false;
        softDropDistance = 0;
    }

    /**
     * Retorna se está ocorrendo um soft drop.
     *
     * @return true se estiver ocorrendo um soft drop
     */
    public boolean isSoftDropping() {
        return isSoftDropping;
    }

    /**
     * Retorna a distância percorrida durante o soft drop atual.
     *
     * @return a distância do soft drop atual
     */
    public int getSoftDropDistance() {
        return softDropDistance;
    }
}