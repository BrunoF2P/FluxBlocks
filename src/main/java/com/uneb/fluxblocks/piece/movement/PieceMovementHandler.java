package com.uneb.fluxblocks.piece.movement;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.collision.StandardCollisionDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.timing.LockDelayHandler;

/**
 * Gerencia a movimentação de peças (esquerda, direita, baixo, hard drop).
 */
public class PieceMovementHandler {
    private final CollisionDetector collisionDetector;
    private final LockDelayHandler lockDelayHandler;
    private final GameMediator mediator;
    private final int playerId;

    /** Flag que indica se o jogador está realizando soft drop */
    private boolean isSoftDropping = false;

    /** Distância percorrida durante o soft drop atual */
    private int softDropDistance = 0;

    /**
     * Constrói um novo manipulador de movimento de peças.
     *
     * @param collisionDetector Detector de colisões para verificar posições válidas
     * @param lockDelayHandler Gerenciador de atraso de bloqueio
     * @param mediator O mediador para comunicação entre componentes do jogo
     */
    public PieceMovementHandler(CollisionDetector collisionDetector, LockDelayHandler lockDelayHandler, GameMediator mediator, int playerId) {
        this.collisionDetector = collisionDetector;
        this.lockDelayHandler = lockDelayHandler;
        this.mediator = mediator;
        this.playerId = playerId;
    }

    /**
     * Move a peça para a esquerda se possível.
     *
     * @param piece A peça a ser movida (não pode ser nula)
     * @return true se o movimento foi bem-sucedido
     * @throws NullPointerException se a peça for nula
     */
    public boolean moveLeft(BlockShape piece) {
        boolean moved = tryMove(piece, -1, 0);

        if (moved) {
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
            boolean isAtRest = ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece);
            lockDelayHandler.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(UiEvents.PIECE_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
        }
        return moved;
    }

    /**
     * Move a peça para a direita se possível.
     *
     * @param piece A peça a ser movida
     * @return true se o movimento foi bem-sucedido
     */
    public boolean moveRight(BlockShape piece) {
        boolean moved = tryMove(piece, 1, 0);

        if (moved) {
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            boolean isAtRest = ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece);
            lockDelayHandler.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(UiEvents.PIECE_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
        }
        return moved;
    }

    /**
     * Move a peça para baixo (soft drop).
     *
     * @param piece A peça a ser movida (pode ser nula)
     * @return true se o movimento foi bem-sucedido
     */
    public boolean moveDown(BlockShape piece) {
        if (piece == null) return false;

        isSoftDropping = true;
        boolean moved = tryMove(piece, 0, 1);

        if (moved) {
            lockDelayHandler.resetLockDelay(piece, ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece));
            softDropDistance++;
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
        }
        return moved;
    }

    /**
     * Realiza um hard drop da peça.
     *
     * @param piece A peça a ser dropped (pode ser nula)
     * @return A distância percorrida durante o drop (0 se a peça for nula)
     */
    public int hardDrop(BlockShape piece) {
        if (piece == null) return 0;

        int distance = 0;

        while (tryMove(piece, 0, 1)) {
            // Calcula as dimensões reais da peça baseado nas células
            int[] dimensions = calculatePieceDimensions(piece);
            int pieceWidth = dimensions[0];
            int pieceHeight = dimensions[1];

            // Emite evento para criar o efeito de rastro a cada movimento
            mediator.emit(UiEvents.PIECE_TRAIL_EFFECT, new UiEvents.PieceTrailEffectEvent(
                    this.playerId,
                    new int[]{
                            piece.getX(),
                            piece.getY(),
                            piece.getType(),
                            distance,
                            pieceWidth,
                            pieceHeight
                    }
            ));
            distance++;
        }
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(this.playerId));
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(this.playerId));

        resetSoftDropTracking();
        return distance;
    }

    /**
     * Calcula as dimensões reais da peça baseado na posição das suas células.
     *
     * @param piece A peça para calcular as dimensões
     * @return Array com [largura, altura] da peça
     */
    private int[] calculatePieceDimensions(BlockShape piece) {
        if (piece.getCells().isEmpty()) {
            return new int[]{1, 1}; // Fallback
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Encontra os limites da peça baseado nas posições relativas das células
        for (var cell : piece.getCells()) {
            int relativeX = cell.getX() - piece.getX(); // Posição relativa ao centro
            int relativeY = cell.getY() - piece.getY(); // Posição relativa ao centro

            minX = Math.min(minX, relativeX);
            maxX = Math.max(maxX, relativeX);
            minY = Math.min(minY, relativeY);
            maxY = Math.max(maxY, relativeY);
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        return new int[]{width, height};
    }

    /**
     * Tenta mover a peça na direção especificada.
     *
     * @param piece A peça a ser movida
     * @param deltaX Movimento horizontal
     * @param deltaY Movimento vertical
     * @return true se o movimento foi possível
     */
    private boolean tryMove(BlockShape piece, int deltaX, int deltaY) {
        int originalX = piece.getX();
        int originalY = piece.getY();

        piece.move(deltaX, deltaY);
        boolean isValid = ((StandardCollisionDetector) collisionDetector).isValidPosition(piece);

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

    /**
     * Reseta o estado de "empurrar paredes".
     * Chamado quando uma nova peça é gerada ou rotacionada.
     */
    public void resetWallPushState() {
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
    }
}