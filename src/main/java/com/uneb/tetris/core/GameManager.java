package com.uneb.tetris.core;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.uneb.tetris.board.GameBoard;
import com.uneb.tetris.piece.PieceManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class GameManager {
    private final GameMediator mediator;
    private final GameBoard gameBoard;
    private final PieceManager pieceManager;

    private Timeline gameLoop;
    private Timeline gameTimer;
    private LocalTime startTime;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private KeyCode lastHorizontalKeyPressed = null;
    private int score = 0;
    private int level = 1;

    public GameManager(GameMediator mediator) {
        this.mediator = mediator;
        this.gameBoard = new GameBoard(mediator);
        this.pieceManager = new PieceManager(mediator, gameBoard);

        registerEvents();
        setupGameLoop();
        setupGameTimer();
        setupFXGLInputHandling();

        start();
    }

    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.GAME_OVER, unused -> handleGameOver());
        mediator.receiver(GameEvents.GameplayEvents.PAUSE, unused -> togglePause());
        mediator.receiver(GameEvents.GameplayEvents.SCORE_UPDATED, this::updateScore);

        mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, pieceManager.getNextPiece());
    }

    private void setupGameLoop() {
        double initialSpeed = 1000.0;
        gameLoop = new Timeline(new KeyFrame(Duration.millis(initialSpeed), e -> {
            if (!isPaused && !isGameOver) {
                pieceManager.moveDown();
            }
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
    }

    private void setupGameTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!isPaused && !isGameOver) {
                updateGameTime();
            }
        }));
        gameTimer.setCycleCount(Animation.INDEFINITE);
    }

    private void setupFXGLInputHandling() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                leftKeyPressed = true;
                lastHorizontalKeyPressed = KeyCode.LEFT;
            }

            @Override
            protected void onAction() {
                if (isPaused || isGameOver) return;

                if (lastHorizontalKeyPressed != KeyCode.LEFT) return;

                double now = FXGL.getGameTimer().getNow();
                double repeatDelay = 100.0 / 1000.0;
                double initialDelay = 200.0 / 1000.0;
                double delay = isFirstMove ? initialDelay : repeatDelay;

                if (isFirstMove || (now - lastMoveTime >= delay)) {
                    mediator.emit(GameEvents.GameplayEvents.MOVE_LEFT, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                leftKeyPressed = false;
                isFirstMove = true;
                if (!rightKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = KeyCode.RIGHT;
                }
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                rightKeyPressed = true;
                lastHorizontalKeyPressed = KeyCode.RIGHT;
            }

            @Override
            protected void onAction() {
                if (isPaused || isGameOver) return;

                if (lastHorizontalKeyPressed != KeyCode.RIGHT) return;

                double now = FXGL.getGameTimer().getNow();
                double initialDelay = 200.0 / 1000.0;
                double repeatDelay = 100.0 / 1000.0;
                double delay = isFirstMove ? initialDelay : repeatDelay;

                if (isFirstMove || (now - lastMoveTime >= delay)) {
                    mediator.emit(GameEvents.GameplayEvents.MOVE_RIGHT, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                rightKeyPressed = false;
                isFirstMove = true;
                if (!leftKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = KeyCode.LEFT;
                }
            }
        }, KeyCode.RIGHT);

        FXGL.getInput().addAction(new UserAction("Soft Drop") {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (!isPaused && !isGameOver) {
                    mediator.emit(GameEvents.GameplayEvents.MOVE_DOWN, null);
                    lastMoveTime = FXGL.getGameTimer().getNow();
                    isFirstMove = false;
                }
            }

            @Override
            protected void onAction() {
                if (isPaused || isGameOver) return;

                double now = FXGL.getGameTimer().getNow();
                double initialDelay = 50.0 / 1000.0;
                double repeatDelay = 30.0 / 1000.0;
                double delay = isFirstMove ? initialDelay : repeatDelay;

                if (now - lastMoveTime >= delay) {
                    mediator.emit(GameEvents.GameplayEvents.MOVE_DOWN, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                isFirstMove = true;
            }
        }, KeyCode.DOWN);

        FXGL.getInput().addAction(new UserAction("Rotate") {
            @Override
            protected void onAction() {
                if (!isPaused && !isGameOver) {
                    mediator.emit(GameEvents.GameplayEvents.ROTATE, null);
                }
            }

            @Override
            protected void onActionEnd() {
                mediator.emit(GameEvents.InputEvents.ROTATE_RESET, null);
            }
        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Hard Drop") {
            @Override
            protected void onActionBegin() {
                if (!isPaused && !isGameOver) {
                    mediator.emit(GameEvents.GameplayEvents.DROP, null);
                }
            }
        }, KeyCode.SPACE);

        FXGL.getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                togglePause();
            }
        }, KeyCode.P);

        FXGL.getInput().addAction(new UserAction("Restart") {
            @Override
            protected void onActionBegin() {
                restart();
            }
        }, KeyCode.R);
    }

    public void start() {
        gameBoard.clearGrid();
        isGameOver = false;
        isPaused = false;
        score = 0;
        level = 1;
        startTime = LocalTime.now();

        gameLoop.play();
        gameTimer.play();

        mediator.emit(GameEvents.UiEvents.GAME_STARTED, null);
    }

    public void togglePause() {
        isPaused = !isPaused;
        mediator.emit(GameEvents.UiEvents.GAME_PAUSED, isPaused);
    }

    public void restart() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (gameTimer != null) {
            gameTimer.stop();
        }
        start();
    }

    private void handleGameOver() {
        isGameOver = true;
        gameLoop.stop();
        gameTimer.stop();
        mediator.emit(GameEvents.UiEvents.GAME_OVER, score);
    }

    public void updateScore(int points) {
        score += points;
        mediator.emit(GameEvents.UiEvents.SCORE_UPDATE, score);
    }
    
    private void levelUp(int newLevel) {
        level = newLevel;

        double speedFactor = Math.pow(0.8, level - 1);
        double newSpeed = 1000.0 * speedFactor;

        gameLoop.stop();
        gameLoop = new Timeline(
                new KeyFrame(Duration.millis(newSpeed), e -> {
                    if (!isPaused && !isGameOver) {
                        pieceManager.moveDown();
                    }
                })
        );
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        mediator.emit(GameEvents.UiEvents.LEVEL_UPDATE, level);
    }

    private void updateGameTime() {
        LocalTime now = LocalTime.now();
        long elapsedSeconds = ChronoUnit.SECONDS.between(startTime, now);
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        mediator.emit(GameEvents.UiEvents.TIME_UPDATE, timeString);
    }
}