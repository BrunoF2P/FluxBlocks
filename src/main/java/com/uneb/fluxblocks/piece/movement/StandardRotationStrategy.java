package com.uneb.fluxblocks.piece.movement;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.architecture.interfaces.RotationStrategy;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.collision.SpinDetector;
import com.uneb.fluxblocks.piece.collision.TripleSpinDetector;
import com.uneb.fluxblocks.piece.collision.StandardCollisionDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.timing.LockDelayHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação padrão do RotationStrategy baseada no PieceRotationHandler original.
 * Implementa o sistema SRS (Super Rotation System) completo.
 */
public class StandardRotationStrategy implements RotationStrategy {
    
    private final CollisionDetector collisionDetector;
    private final SpinDetector spinDetector;
    private final TripleSpinDetector tripleSpinDetector;
    private final LockDelayHandler lockDelayHandler;
    
    private double lastRotateTime = 0;
    private boolean isFirstRotate = true;
    private SpinDetector.SpinType lastSpin = SpinDetector.SpinType.NONE;
    private TripleSpinDetector.TripleSpinType lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
    
    // Tabelas de wall kicks do SRS para cada tipo de peça
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
            // 0>>1
            {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            // 1>>2
            {0, 0}, {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            // 2>>3
            {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            // 3>>0
            {0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}
        }
    };
    
    public StandardRotationStrategy(CollisionDetector collisionDetector, LockDelayHandler lockDelayHandler) {
        this.collisionDetector = collisionDetector;
        this.spinDetector = new SpinDetector(((StandardCollisionDetector) collisionDetector).getBoard());
        this.tripleSpinDetector = new TripleSpinDetector(((StandardCollisionDetector) collisionDetector).getBoard());
        this.lockDelayHandler = lockDelayHandler;
    }
    
    @Override
    public RotationResult rotateClockwise(BlockShape piece) {
        if (!canRotate(piece)) return RotationResult.INVALID_STATE;
        
        int originalX = piece.getX();
        int originalY = piece.getY();
        int[] originalPosition = {originalX, originalY};
        
        RotationResult result = attemptRotationWithSRS(piece);
        
        if (result == RotationResult.SUCCESS) {
            lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
            lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);
            completeSuccessfulRotation(piece);
        } else {
            lastSpin = SpinDetector.SpinType.NONE;
            lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
        }
        
        return result;
    }
    
    @Override
    public RotationResult rotateCounterClockwise(BlockShape piece) {
        if (!canRotate(piece)) return RotationResult.INVALID_STATE;
        
        int originalX = piece.getX();
        int originalY = piece.getY();
        int[] originalPosition = {originalX, originalY};
        
        // Para rotação anti-horária, fazemos 3 rotações horárias
        piece.rotate();
        piece.rotate();
        piece.rotate();
        
        if (((StandardCollisionDetector) collisionDetector).isValidPosition(piece)) {
            lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
            lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);
            completeSuccessfulRotation(piece);
            return RotationResult.SUCCESS;
        }
        
        // Tenta wall kicks se a posição não for válida
        RotationResult result = trySRSWallKicks(piece, originalX, originalY);
        
        if (result == RotationResult.SUCCESS) {
            lastSpin = spinDetector.detectSpin(piece, true, originalPosition);
            lastTripleSpin = tripleSpinDetector.detectTripleSpin(piece, true, originalPosition);
            completeSuccessfulRotation(piece);
        } else {
            // Reverte as 3 rotações
            piece.rotate();
            piece.rotate();
            piece.rotate();
            lastSpin = SpinDetector.SpinType.NONE;
            lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
        }
        
        return result;
    }
    
    @Override
    public RotationResult rotate180(BlockShape piece) {
        if (!canRotate(piece)) return RotationResult.INVALID_STATE;
        
        // Faz duas rotações horárias para 180 graus
        RotationResult result1 = rotateClockwise(piece);
        if (result1 != RotationResult.SUCCESS) return result1;
        
        return rotateClockwise(piece);
    }
    
    @Override
    public boolean canRotate(BlockShape piece) {
        if (piece == null) return false;
        return !isRotationOnCooldown();
    }
    
    @Override
    public RotationResult validateRotation(BlockShape piece, boolean clockwise) {
        if (!canRotate(piece)) return RotationResult.INVALID_STATE;
        
        int originalX = piece.getX();
        int originalY = piece.getY();
        
        if (clockwise) {
            piece.rotate();
        } else {
            piece.rotate();
            piece.rotate();
            piece.rotate();
        }
        
        boolean isValid = ((StandardCollisionDetector) collisionDetector).isValidPosition(piece);
        piece.setPosition(originalX, originalY);
        
        return isValid ? RotationResult.SUCCESS : RotationResult.COLLISION;
    }
    
    @Override
    public RotationResult applyWallKick(BlockShape piece, boolean clockwise) {
        if (!canRotate(piece)) return RotationResult.INVALID_STATE;
        
        int originalX = piece.getX();
        int originalY = piece.getY();
        
        if (clockwise) {
            piece.rotate();
        } else {
            piece.rotate();
            piece.rotate();
            piece.rotate();
        }
        
        return trySRSWallKicks(piece, originalX, originalY);
    }
    
    @Override
    public int getCurrentRotation(BlockShape piece) {
        // BlockShape não mantém rotação como número, mas sim como estado das células
        // Retorna 0 como padrão, já que a rotação é gerenciada pelas células
        return 0;
    }
    
    @Override
    public void setRotation(BlockShape piece, int rotation) {
        // BlockShape não suporta definir rotação por número
        // A rotação é gerenciada pelo método rotate()
        if (piece == null) return;
        
        // Reseta para posição inicial e aplica rotações
        piece.resetRotation();
        for (int i = 0; i < rotation; i++) {
            piece.rotate();
        }
    }
    
    @Override
    public int[][] getShapeForRotation(BlockShape piece, int rotation) {
        // Retorna a forma da peça para uma rotação específica
        // Esta é uma implementação simplificada baseada nas posições das células
        if (piece == null) return new int[0][0];
        
        List<Cell> cells = piece.getCells();
        int[][] shape = new int[cells.size()][2];
        
        for (int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            shape[i][0] = cell.getRelativeX();
            shape[i][1] = cell.getRelativeY();
        }
        
        return shape;
    }
    
    @Override
    public boolean wouldCollide(BlockShape piece, int rotation) {
        if (piece == null) return true;
        
        // Salva estado atual
        List<int[]> originalPositions = new ArrayList<>();
        for (Cell cell : piece.getCells()) {
            originalPositions.add(new int[]{cell.getRelativeX(), cell.getRelativeY()});
        }
        
        // Aplica rotação
        piece.resetRotation();
        for (int i = 0; i < rotation; i++) {
            piece.rotate();
        }
        
        boolean wouldCollide = !((StandardCollisionDetector) collisionDetector).isValidPosition(piece);
        
        // Restaura estado original
        for (int i = 0; i < piece.getCells().size(); i++) {
            Cell cell = piece.getCells().get(i);
            int[] originalPos = originalPositions.get(i);
            cell.setRelativePosition(originalPos[0], originalPos[1]);
        }
        // Atualiza posições das células movendo a peça para a posição atual
        piece.setPosition(piece.getX(), piece.getY());
        
        return wouldCollide;
    }
    
    @Override
    public void reset() {
        lastRotateTime = 0;
        isFirstRotate = true;
        lastSpin = SpinDetector.SpinType.NONE;
        lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
    }
    
    @Override
    public void cleanup() {
        reset();
    }
    
    private RotationResult attemptRotationWithSRS(BlockShape piece) {
        int originalX = piece.getX();
        int originalY = piece.getY();
        
        piece.rotate();
        if (((StandardCollisionDetector) collisionDetector).isValidPosition(piece)) {
            return RotationResult.SUCCESS;
        }
        
        return trySRSWallKicks(piece, originalX, originalY);
    }
    
    private RotationResult trySRSWallKicks(BlockShape piece, int originalX, int originalY) {
        int[][] wallKicks = getWallKicksForPiece(piece);
        
        for (int[] kick : wallKicks) {
            piece.setPosition(originalX + kick[0], originalY + kick[1]);
            
            if (((StandardCollisionDetector) collisionDetector).isValidPosition(piece)) {
                return RotationResult.SUCCESS;
            }
        }
        
        piece.setPosition(originalX, originalY);
        return RotationResult.COLLISION;
    }
    
    private int[][] getWallKicksForPiece(BlockShape piece) {
        int pieceType = piece.getType();
        
        if (pieceType == 1) { // I-Piece
            return SRS_WALL_KICKS[0];
        } else if (pieceType == 10) { // X-Piece (Glass)
            return SRS_WALL_KICKS[2];
        } else { // J, L, S, T, Z
            return SRS_WALL_KICKS[1];
        }
    }
    
    private boolean isRotationOnCooldown() {
        double currentTime = FXGL.getGameTimer().getNow();
        double timeSinceLastRotate = currentTime - lastRotateTime;
        
        if (isFirstRotate) {
            return timeSinceLastRotate < GameConfig.ROTATE_INITIAL_DELAY / 1000.0;
        } else {
            return timeSinceLastRotate < GameConfig.ROTATE_REPEAT_DELAY / 1000.0;
        }
    }
    
    private void completeSuccessfulRotation(BlockShape piece) {
        double currentTime = FXGL.getGameTimer().getNow();
        lastRotateTime = currentTime;
        isFirstRotate = false;
        
        boolean isAtRest = ((StandardCollisionDetector) collisionDetector).isAtRestingPosition(piece);
        lockDelayHandler.resetLockDelay(piece, isAtRest);
    }
    
    public void resetRotateDelay() {
        lastRotateTime = 0;
        isFirstRotate = true;
    }
    
    public SpinDetector.SpinType getLastSpin() {
        return lastSpin;
    }
    
    public void resetLastSpin() {
        lastSpin = SpinDetector.SpinType.NONE;
    }
    
    public TripleSpinDetector.TripleSpinType getLastTripleSpin() {
        return lastTripleSpin;
    }
    
    public void resetLastTripleSpin() {
        lastTripleSpin = TripleSpinDetector.TripleSpinType.NONE;
    }
} 