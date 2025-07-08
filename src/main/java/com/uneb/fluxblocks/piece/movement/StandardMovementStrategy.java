package com.uneb.fluxblocks.piece.movement;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.interfaces.MovementStrategy;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.collision.StandardCollisionDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.timing.LockDelayHandler;

/**
 * Implementação padrão do MovementStrategy baseada no PieceMovementHandler original.
 */
public class StandardMovementStrategy implements MovementStrategy {
    
    private final CollisionDetector collisionDetector;
    private final LockDelayHandler lockDelayHandler;
    private final GameMediator mediator;
    private final int playerId;
    
    private boolean isSoftDropping = false;
    private int softDropDistance = 0;
    
    public StandardMovementStrategy(CollisionDetector collisionDetector, 
                                  LockDelayHandler lockDelayHandler, 
                                  GameMediator mediator, 
                                  int playerId) {
        this.collisionDetector = collisionDetector;
        this.lockDelayHandler = lockDelayHandler;
        this.mediator = mediator;
        this.playerId = playerId;
    }
    
    @Override
    public boolean canMove(BlockShape piece, int deltaX, int deltaY) {
        if (piece == null) return false;
        
        int originalX = piece.getX();
        int originalY = piece.getY();
        
        piece.move(deltaX, deltaY);
        boolean isValid = ((StandardCollisionDetector) collisionDetector).isValidPosition(piece);
        
        piece.setPosition(originalX, originalY);
        return isValid;
    }
    
    @Override
    public MovementResult move(BlockShape piece, int deltaX, int deltaY) {
        if (piece == null) return MovementResult.INVALID_STATE;
        
        if (!canMove(piece, deltaX, deltaY)) {
            return MovementResult.COLLISION;
        }
        
        piece.move(deltaX, deltaY);
        return MovementResult.SUCCESS;
    }
    
    @Override
    public MovementResult validateMovement(BlockShape piece, int deltaX, int deltaY) {
        if (piece == null) return MovementResult.INVALID_STATE;
        
        if (canMove(piece, deltaX, deltaY)) {
            return MovementResult.SUCCESS;
        } else {
            return MovementResult.COLLISION;
        }
    }
    
    @Override
    public MovementResult moveLeft(BlockShape piece) {
        MovementResult result = move(piece, -1, 0);
        
        if (result == MovementResult.SUCCESS) {
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
            boolean isAtRest = ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece);
            lockDelayHandler.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(UiEvents.PIECE_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
        }
        
        return result;
    }
    
    @Override
    public MovementResult moveRight(BlockShape piece) {
        MovementResult result = move(piece, 1, 0);
        
        if (result == MovementResult.SUCCESS) {
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            boolean isAtRest = ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece);
            lockDelayHandler.resetLockDelay(piece, isAtRest);
        } else {
            mediator.emit(UiEvents.PIECE_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
        }
        
        return result;
    }
    
    @Override
    public MovementResult moveDown(BlockShape piece) {
        if (piece == null) return MovementResult.INVALID_STATE;
        
        isSoftDropping = true;
        MovementResult result = move(piece, 0, 1);
        
        if (result == MovementResult.SUCCESS) {
            lockDelayHandler.resetLockDelay(piece, ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece));
            softDropDistance++;
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
            mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
        }
        
        return result;
    }
    
    @Override
    public int hardDrop(BlockShape piece) {
        if (piece == null) return 0;
        
        int distance = 0;
        
        while (move(piece, 0, 1) == MovementResult.SUCCESS) {
            int[] dimensions = calculatePieceDimensions(piece);
            int pieceWidth = dimensions[0];
            int pieceHeight = dimensions[1];
            
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
    
    @Override
    public boolean isTouchingWall(BlockShape piece) {
        if (piece == null) return false;
        
        // Verifica se está tocando parede esquerda ou direita
        return !canMove(piece, -1, 0) || !canMove(piece, 1, 0);
    }
    
    @Override
    public boolean isTouchingGround(BlockShape piece) {
        if (piece == null) return false;
        
        // Verifica se está tocando o chão
        return !canMove(piece, 0, 1);
    }
    
    @Override
    public boolean isColliding(BlockShape piece) {
        if (piece == null) return false;
        
        return !((StandardCollisionDetector) collisionDetector).isValidPosition(piece);
    }
    
    @Override
    public void reset() {
        resetSoftDropTracking();
    }
    
    @Override
    public void cleanup() {
        reset();
    }
    
    private int[] calculatePieceDimensions(BlockShape piece) {
        if (piece.getCells().isEmpty()) {
            return new int[]{1, 1};
        }
        
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (var cell : piece.getCells()) {
            int relativeX = cell.getX() - piece.getX();
            int relativeY = cell.getY() - piece.getY();
            
            minX = Math.min(minX, relativeX);
            maxX = Math.max(maxX, relativeX);
            minY = Math.min(minY, relativeY);
            maxY = Math.max(maxY, relativeY);
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        return new int[]{width, height};
    }
    
    private void resetSoftDropTracking() {
        isSoftDropping = false;
        softDropDistance = 0;
    }
    
    public boolean isSoftDropping() {
        return isSoftDropping;
    }
    
    public int getSoftDropDistance() {
        return softDropDistance;
    }
} 