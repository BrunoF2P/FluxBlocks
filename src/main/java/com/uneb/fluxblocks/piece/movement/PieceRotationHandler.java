package com.uneb.fluxblocks.piece.movement;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.collision.SpinDetector;
import com.uneb.fluxblocks.piece.collision.TripleSpinDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.timing.LockDelayHandler;

/**
 * Gerencia a rotação das peças no jogo FluxBlocks.
 * 
 * <p>Esta classe implementa o sistema SRS (Super Rotation System) completo,
 * incluindo wall kicks específicos para cada tipo de peça e detecção de Spins.</p>
 * 
 * <p>O sistema SRS permite que as peças sejam rotacionadas mesmo quando
 * há obstáculos próximos, desde que haja espaço suficiente após o wall kick.</p>
 */
public class PieceRotationHandler {
    private final CollisionDetector collisionDetector;
    private final SpinDetector spinDetector;
    private final TripleSpinDetector tripleSpinDetector;
    private final LockDelayHandler lockDelayHandler;

    /** Timestamp da última rotação realizada */
    private double lastRotateTime = 0;

    /** Flag que indica se é a primeira rotação de uma sequência */
    private boolean isFirstRotate = true;

    /** Último Spin detectado */
    private SpinDetector.SpinType lastSpin = SpinDetector.SpinType.NONE;

    /** Último Triple Spin detectado */
    private TripleSpinDetector.TripleSpinType lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;

    /** Tabelas de wall kicks do SRS para cada tipo de peça */
    private static final int[][][] SRS_WALL_KICKS = {
        // I-Piece (específico)
        {
            // 0>>1
            {0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2},
            // 1>>2
            {0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1},
            // 2>>3
            {0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2},
            // 3>>0
            {0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}
        },
        // J, L, S, T, Z (compartilham a mesma tabela)
        {
            // 0>>1
            {0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2},
            // 1>>2
            {0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2},
            // 2>>3
            {0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2},
            // 3>>0
            {0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}
        },
        // X-Piece (específico - wall kicks mais conservadores)
        {
            // 0>>1 (rotação 0 para 1)
            {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            // 1>>2 (rotação 1 para 2)
            {0, 0}, {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            // 2>>3 (rotação 2 para 3)
            {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            // 3>>0 (rotação 3 para 0)
            {0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}
        }
    };

    /**
     * Cria um novo gerenciador de rotação de peças.
     *
     * @param collisionDetector Detector de colisão para verificar posições válidas
     */
    public PieceRotationHandler(CollisionDetector collisionDetector,
                                LockDelayHandler lockDelayHandler) {
        this.collisionDetector = collisionDetector;
        this.spinDetector = new SpinDetector(collisionDetector.getBoard());
        this.tripleSpinDetector = new TripleSpinDetector(collisionDetector.getBoard());
        this.lockDelayHandler = lockDelayHandler;
    }

    /**
     * Rotaciona uma peça no sentido horário.
     *
     * @param piece A peça a ser rotacionada
     * @return true se a rotação foi bem-sucedida
     */
    public boolean rotateClockwise(BlockShape piece) {
        if (!canRotate(piece)) return false;

        // Guarda a posição original para detecção de Spin
        int originalX = piece.getX();
        int originalY = piece.getY();
        int[] originalPosition = {originalX, originalY};

        RotationResult result = attemptRotationWithSRS(piece);
        
        if (!result.success) {
            resetToOriginalPosition(piece, result.originalX, result.originalY);
            lastSpin = SpinDetector.SpinType.NONE;
            lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
            return false;
        }

        // Detecta Spin após rotação bem-sucedida
        lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
        
        // Detecta Triple Spin
        lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);

        completeSuccessfulRotation(piece);
        return true;
    }

    /**
     * Rotaciona uma peça no sentido anti-horário.
     *
     * @param piece A peça a ser rotacionada
     * @return true se a rotação foi bem-sucedida
     */
    public boolean rotateCounterClockwise(BlockShape piece) {
        if (!canRotate(piece)) return false;

        // Guarda a posição original para detecção de Spin
        int originalX = piece.getX();
        int originalY = piece.getY();
        int[] originalPosition = {originalX, originalY};

        // Para rotação anti-horária, fazemos 3 rotações horárias
        piece.rotate();
        piece.rotate();
        piece.rotate();

        // Verifica se a nova posição é válida
        if (collisionDetector.isValidPosition(piece)) {
            // Detecta Spin após rotação bem-sucedida
            lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
            lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);
            completeSuccessfulRotation(piece);
            return true;
        }

        // Tenta wall kicks se a posição não for válida
        RotationResult result = trySRSWallKicks(piece, originalX, originalY);
        
        if (!result.success) {
            // Reverte as 3 rotações
            piece.rotate();
            piece.rotate();
            piece.rotate();
            resetToOriginalPosition(piece, result.originalX, result.originalY);
            lastSpin = SpinDetector.SpinType.NONE;
            lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
            return false;
        }

        // Detecta Spin após rotação bem-sucedida
        lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
        lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);
        completeSuccessfulRotation(piece);
        return true;
    }

    /**
     * Tenta rotacionar a peça com sistema SRS completo.
     *
     * @param piece A peça a rotacionar
     * @return Resultado da tentativa de rotação
     */
    private RotationResult attemptRotationWithSRS(BlockShape piece) {
        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.rotate();
        if (collisionDetector.isValidPosition(piece)) {
            return new RotationResult(true, originalX, originalY);
        }

        return trySRSWallKicks(piece, originalX, originalY);
    }

    /**
     * Tenta aplicar wall kicks do SRS para permitir a rotação.
     *
     * @param piece A peça atual
     * @param originalX Posição X original
     * @param originalY Posição Y original
     * @return Resultado da tentativa de rotação
     */
    private RotationResult trySRSWallKicks(BlockShape piece, int originalX, int originalY) {
        int[][] wallKicks = getWallKicksForPiece(piece);
        
        for (int[] kick : wallKicks) {
            piece.move(kick[0], kick[1]);
            if (collisionDetector.isValidPosition(piece)) {
                return new RotationResult(true, originalX, originalY);
            }
            piece.move(-kick[0], -kick[1]); // Volta à posição anterior
        }

        return new RotationResult(false, originalX, originalY);
    }

    /**
     * Obtém a tabela de wall kicks apropriada para o tipo de peça.
     *
     * @param piece A peça atual
     * @return Array de wall kicks para testar
     */
    private int[][] getWallKicksForPiece(BlockShape piece) {
        if (piece.getType() == BlockShape.Type.I.getValue()) {
            return SRS_WALL_KICKS[0]; // I-piece tem tabela específica
        } else if (piece.getType() == BlockShape.Type.O.getValue()) {
            return new int[0][0]; // O-piece não tem wall kicks (não rotaciona)
        } else if (piece.getType() == BlockShape.Type.X.getValue()) {
            return SRS_WALL_KICKS[2]; // X-piece tem tabela específica
        } else {
            return SRS_WALL_KICKS[1]; // J, L, S, T, Z compartilham tabela
        }
    }

    /**
     * Retorna a peça à posição original caso a rotação falhe.
     *
     * @param piece A peça atual
     * @param x Posição X original
     * @param y Posição Y original
     */
    private void resetToOriginalPosition(BlockShape piece, int x, int y) {
        // Reverte a rotação (3 rotações no sentido horário = 1 no anti-horário)
        piece.rotate();
        piece.rotate();
        piece.rotate();
        piece.setPosition(x, y);
    }

    /**
     * Verifica se a peça pode ser rotacionada.
     *
     * @param piece A peça a verificar
     * @return true se a peça pode ser rotacionada
     */
    private boolean canRotate(BlockShape piece) {
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
        double currentTime = FXGL.getGameTimer().getNow();
        double requiredDelay = isFirstRotate ? GameConfig.ROTATE_INITIAL_DELAY / 1000.0 : GameConfig.ROTATE_REPEAT_DELAY / 1000.0;
        return currentTime - lastRotateTime < requiredDelay;
    }

    /**
     * Completa a rotação bem-sucedida e atualiza o estado.
     *
     * @param piece A peça atual
     */
    private void completeSuccessfulRotation(BlockShape piece) {
        boolean isAtRest = collisionDetector.isAtRestingPosition(piece);
        lockDelayHandler.resetLockDelay(piece, isAtRest);

        lastRotateTime = FXGL.getGameTimer().getNow();
        isFirstRotate = false;
    }

    /**
     * Reinicia o delay de rotação quando o botão é solto.
     */
    public void resetRotateDelay() {
        isFirstRotate = true;
    }

    /**
     * Obtém o último Spin detectado.
     *
     * @return O tipo do último Spin detectado
     */
    public SpinDetector.SpinType getLastSpin() {
        return lastSpin;
    }

    /**
     * Reseta o último Spin detectado.
     */
    public void resetLastSpin() {
        lastSpin = SpinDetector.SpinType.NONE;
    }

    /**
     * Obtém o último Triple Spin detectado.
     *
     * @return O tipo do último Triple Spin detectado
     */
    public TripleSpinDetector.TripleSpinType getLastTripleSpin() {
        return lastTripleSpin;
    }

    /**
     * Reseta o último Triple Spin detectado.
     */
    public void resetLastTripleSpin() {
        lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
        tripleSpinDetector.resetConsecutiveSpins();
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