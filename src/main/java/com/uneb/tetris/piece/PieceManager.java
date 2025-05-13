package com.uneb.tetris.piece;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.board.GameBoard;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import javafx.util.Duration;

import java.util.List;

public class PieceManager {
    private final GameMediator mediator;
    private final GameBoard board;

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentLevel = 1;

    private static final double LOCK_DELAY = 500.0;
    private static final double ROTATE_INITIAL_DELAY = 100.0;
    private static final double ROTATE_REPEAT_DELAY = 200.0;
    private double lastRotateTime = 0;
    private boolean isFirstRotate = true;
    private double lockTimer = 0;
    private boolean lockPending = false;
    private int lastLandedY = -1;
    private boolean isSoftDropping = false;
    private int softDropDistance = 0;
    private int linesClearedTotal = 0;

    public PieceManager(GameMediator mediator, GameBoard board) {
        this.mediator = mediator;
        this.board = board;

        initialize();
        registerEvents();
    }

    private void initialize() {
        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        spawnNewPiece();
    }

    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.MOVE_LEFT, unused -> moveLeft());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_RIGHT, unused -> moveRight());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameEvents.GameplayEvents.ROTATE, unused -> rotate());
        mediator.receiver(GameEvents.GameplayEvents.DROP, unused -> hardDrop());
        mediator.receiver(GameEvents.InputEvents.ROTATE_RESET, unused -> resetRotateDelay());
        mediator.receiver(GameEvents.UiEvents.LEVEL_UPDATE, level -> updateLevel((int)level));
        mediator.receiver(GameEvents.UiEvents.GAME_STARTED, unused -> {
            mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);
        });
        FXGL.getGameTimer().runAtInterval(this::checkLockDelay, Duration.millis(16.67)); // 60 FPS (1000ms/60)
    }

    private void updateLevel(int level) {
        this.currentLevel = level;
    }

    private void checkLockDelay() {
        if (!lockPending) return;

        double currentTime = FXGL.getGameTimer().getNow();

        if (currentTime - lockTimer >= LOCK_DELAY / 1000.0) {
            lockPiece();
            lockPending = false;
        }
    }

    private void resetLockDelay() {
        if (isAtValidRestingPosition()) {
            int currentY = currentPiece.getY();

            if (currentY != lastLandedY) {
                lockTimer = FXGL.getGameTimer().getNow();
                lastLandedY = currentY;
            }
        } else {
            lockPending = false;
        }
    }

    private boolean isAtValidRestingPosition() {
        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.move(0, 1);
        boolean wouldCollide = !isValidPosition(currentPiece);

        currentPiece.setPosition(originalX, originalY);

        return wouldCollide;
    }

    public void spawnNewPiece() {
        currentPiece = nextPiece;

        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);

        if (!isValidPosition(currentPiece)) {
            mediator.emit(GameEvents.GameplayEvents.GAME_OVER, null);
            return;
        }

        lockPending = false;
        lastLandedY = -1;
        isSoftDropping = false;
        softDropDistance = 0;

        updateBoardWithCurrentPiece();
    }

    private Tetromino calculateShadowPiece() {
        if (currentPiece == null) return null;

        Tetromino shadow = TetrominoFactory.createTetromino(Tetromino.Type.values()[currentPiece.getType()]);
        shadow.setPosition(currentPiece.getX(), currentPiece.getY());

        for (int i = 0; i < currentPiece.getCells().size(); i++) {
            Cell originalCell = currentPiece.getCells().get(i);
            Cell shadowCell = shadow.getCells().get(i);
            shadowCell.setRelativeX(originalCell.getRelativeX());
            shadowCell.setRelativeY(originalCell.getRelativeY());
        }

        while (true) {
            shadow.move(0, 1);
            if (!isValidPosition(shadow)) {
                shadow.move(0, -1);
                break;
            }
        }

        return shadow;
    }

    private void updateBoardWithCurrentPiece() {
        if (currentPiece == null) return;

        int[][] grid = new int[board.getHeight()][board.getWidth()];

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.isValidPosition(x, y)) {
                    grid[y][x] = board.getCell(x, y);
                }
            }
        }

        Tetromino shadowPiece = calculateShadowPiece();
        if (shadowPiece != null) {
            for (Cell cell : shadowPiece.getCells()) {
                int x = cell.getX();
                int y = cell.getY();

                if (y >= 0 && y < board.getHeight() && x >= 0 && x < board.getWidth()) {
                    grid[y][x] = 8;
                }
            }
        }

        for (Cell cell : currentPiece.getCells()) {
            int x = cell.getX();
            int y = cell.getY();

            if (y >= 0 && y < board.getHeight() && x >= 0 && x < board.getWidth()) {
                grid[y][x] = cell.getType();
            }
        }

        mediator.emit(GameEvents.UiEvents.BOARD_UPDATE, grid);
    }

    private boolean isValidPosition(Tetromino piece) {
        if (piece == null) return false;

        for (Cell cell : piece.getCells()) {
            int x = cell.getX();
            int y = cell.getY();

            if (!board.isValidPosition(x, y)) {
                return false;
            }

            if (board.getCell(x, y) != 0) {
                return false;
            }
        }
        return true;
    }

    public void lockPiece() {

        if (currentPiece == null) return;

        List<Cell> cells = currentPiece.getCells();
        for (Cell cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            if (board.isValidPosition(x, y)) {
                board.setCell(x, y, cell.getType());
            }
        }

        if (isSoftDropping && softDropDistance > 0) {
            int softDropScore = 20 * currentLevel;
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, softDropScore);
        }

        int linesCleared = board.removeCompletedLines();

        if (linesCleared > 0) {
            linesClearedTotal += linesCleared;

            int scoreIncrease = calculateScore(linesCleared);
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, scoreIncrease);
        }

        spawnNewPiece();

    }

    private int calculateScore(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> 40 * (currentLevel + 1);
            case 2 -> 100 * (currentLevel + 1);
            case 3 -> 300 * (currentLevel + 1);
            case 4 -> 1200 * (currentLevel + 1);
            default -> 0;
        };
    }

    public void moveLeft() {
        if (tryMove(-1, 0)) {
            resetLockDelay();
        }
    }

    public void moveRight() {
        if (tryMove(1, 0)) {
            resetLockDelay();
        }
    }

    public void moveDown() {
        isSoftDropping = true;
        if (tryMove(0, 1)) {
            resetLockDelay();
            softDropDistance++;
        } else {
            if (!lockPending) {
                lockPending = true;
                lockTimer = FXGL.getGameTimer().getNow();
                lastLandedY = currentPiece.getY();
            }
        }
    }

    public void hardDrop() {
        if (currentPiece == null) return;

        int distanceDropped = 0;
        while (tryMove(0, 1)) {
            distanceDropped++;
        }

        lockPiece();

        isSoftDropping = false;
        softDropDistance = 0;

        if (distanceDropped > 0) {
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, distanceDropped * 2);
        }
    }

    public void rotate() {
        if (currentPiece == null) return;

        double currentTime = FXGL.getGameTimer().getNow() * 1000;

        double requiredDelay = isFirstRotate ? ROTATE_INITIAL_DELAY : ROTATE_REPEAT_DELAY;
        if (currentTime - lastRotateTime < requiredDelay) {
            return;
        }

        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.rotate();

        if (!isValidPosition(currentPiece)) {
            currentPiece.move(1, 0);
            if (!isValidPosition(currentPiece)) {
                currentPiece.move(-2, 0);
                if (!isValidPosition(currentPiece)) {
                    currentPiece.move(1, -1);
                    if (!isValidPosition(currentPiece)) {
                        currentPiece.rotate();
                        currentPiece.rotate();
                        currentPiece.rotate();
                        currentPiece.setPosition(originalX, originalY);
                        return;
                    }
                }
            }
        }

        updateBoardWithCurrentPiece();
        resetLockDelay();

        lastRotateTime = currentTime;
        isFirstRotate = false;
    }

    public void resetRotateDelay() {
        isFirstRotate = true;
    }

    private boolean tryMove(int deltaX, int deltaY) {
        if (currentPiece == null) return false;

        int originalX = currentPiece.getX();
        int originalY = currentPiece.getY();

        currentPiece.move(deltaX, deltaY);

        if (isValidPosition(currentPiece)) {
            updateBoardWithCurrentPiece();
            return true;
        } else {
            currentPiece.setPosition(originalX, originalY);
            return false;
        }
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getNextPiece() {
        return nextPiece;
    }
}