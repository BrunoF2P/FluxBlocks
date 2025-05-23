package com.uneb.tetris.piece.movement;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.collision.CollisionDetector;
import com.uneb.tetris.piece.timing.LockDelayHandler;

/**
 * Gerencia a rotação das peças com sistema de wall kicks.
 */
public class PieceRotationHandler {
    private final CollisionDetector collisionDetector;
    private final LockDelayHandler lockDelayHandler;

    /** Tempo inicial entre rotações consecutivas */
    private static final double ROTATE_INITIAL_DELAY = 100.0;

    /** Tempo entre rotações subsequentes após a primeira */
    private static final double ROTATE_REPEAT_DELAY = 200.0;

    /** Timestamp da última rotação realizada */
    private double lastRotateTime = 0;

    /** Flag que indica se é a primeira rotação de uma sequência */
    private boolean isFirstRotate = true;

    public PieceRotationHandler(CollisionDetector collisionDetector,
                                LockDelayHandler lockDelayHandler) {
        this.collisionDetector = collisionDetector;
        this.lockDelayHandler = lockDelayHandler;
    }

    /**
     * Rotaciona a peça atual no sentido horário com sistema de wall kicks.
     *
     * @param piece A peça a rotacionar
     * @return true se a rotação foi bem-sucedida
     */
    public boolean rotate(Tetromino piece) {
        if (!canRotate(piece)) return false;

        RotationResult result = attemptRotationWithWallKicks(piece);

        if (!result.success) {
            resetToOriginalPosition(piece, result.originalX, result.originalY);
            return false;
        }

        completeSuccessfulRotation(piece);
        return true;
    }

    /**
     * Verifica se a peça pode ser rotacionada.
     *
     * @param piece A peça a verificar
     * @return true se a peça pode ser rotacionada
     */
    private boolean canRotate(Tetromino piece) {
        if (piece == null) return false;
        if (isRotationOnCooldown()) return false;
        return true;
    }

    /**
     * Verifica se a rotação está em cooldown.
     *
     * @return true se a rotação está em cooldown
     */
    private boolean isRotationOnCooldown() {
        double currentTime = FXGL.getGameTimer().getNow() * 1000;
        double requiredDelay = isFirstRotate ? ROTATE_INITIAL_DELAY : ROTATE_REPEAT_DELAY;
        return currentTime - lastRotateTime < requiredDelay;
    }

    /**
     * Tenta rotacionar a peça com wall kicks.
     *
     * @param piece A peça a rotacionar
     * @return Resultado da tentativa de rotação
     */
    private RotationResult attemptRotationWithWallKicks(Tetromino piece) {
        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.rotate();
        if (collisionDetector.isValidPosition(piece)) {
            return new RotationResult(true, originalX, originalY);
        }

        return tryWallKickMoves(piece, originalX, originalY);
    }

    /**
     * Tenta aplicar wall kicks para permitir a rotação.
     *
     * @param piece A peça atual
     * @param originalX Posição X original
     * @param originalY Posição Y original
     * @return Resultado da tentativa de rotação
     */
    private RotationResult tryWallKickMoves(Tetromino piece, int originalX, int originalY) {
        piece.move(1, 0);
        if (collisionDetector.isValidPosition(piece)) {
            return new RotationResult(true, originalX, originalY);
        }

        piece.move(-2, 0);
        if (collisionDetector.isValidPosition(piece)) {
            return new RotationResult(true, originalX, originalY);
        }

        piece.move(1, -1);
        if (collisionDetector.isValidPosition(piece)) {
            return new RotationResult(true, originalX, originalY);
        }

        return new RotationResult(false, originalX, originalY);
    }

    /**
     * Retorna a peça à posição original caso a rotação falhe.
     *
     * @param piece A peça atual
     * @param x Posição X original
     * @param y Posição Y original
     */
    private void resetToOriginalPosition(Tetromino piece, int x, int y) {
        // Reverte a rotação (3 rotações no sentido horário = 1 no anti-horário)
        piece.rotate();
        piece.rotate();
        piece.rotate();
        piece.setPosition(x, y);
    }

    /**
     * Completa a rotação bem-sucedida e atualiza o estado.
     *
     * @param piece A peça atual
     */
    private void completeSuccessfulRotation(Tetromino piece) {
        boolean isAtRest = collisionDetector.isAtRestingPosition(piece);
        lockDelayHandler.resetLockDelay(piece, isAtRest);

        lastRotateTime = FXGL.getGameTimer().getNow() * 1000;
        isFirstRotate = false;
    }

    /**
     * Reinicia o delay de rotação quando o botão é solto.
     */
    public void resetRotateDelay() {
        isFirstRotate = true;
    }

    /**
     * Classe interna para armazenar o resultado de uma tentativa de rotação.
     */
    private static class RotationResult {
        boolean success;
        int originalX;
        int originalY;

        RotationResult(boolean success, int originalX, int originalY) {
            this.success = success;
            this.originalX = originalX;
            this.originalY = originalY;
        }
    }
}