package com.uneb.fluxblocks.ui.controllers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.InputEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameState;
import javafx.scene.input.KeyCode;

import java.util.UUID;

/**
 * Implementação padrão do InputHandler usando teclado.
 * Baseada na implementação original do InputHandler.
 */
public class KeyboardInputHandler implements InputHandler {
    private final GameMediator mediator;
    private GameState gameState;
    private final int playerId;
    
    private final KeyCode keyLeft;
    private final KeyCode keyRight;
    private final KeyCode keyDown;
    private final KeyCode keyRotate;
    private final KeyCode keyDrop;
    private final KeyCode keyPause;
    private final KeyCode keyRestart;
    
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private KeyCode lastHorizontalKeyPressed = null;
    private boolean actionsRegistered = false;
    private final String uniqueId = UUID.randomUUID().toString();
    private boolean inputEnabled = true;

    public KeyboardInputHandler(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;

        if (playerId == 1) {
            keyLeft    = KeyCode.valueOf(GameConfig.P1_KEY_LEFT);
            keyRight   = KeyCode.valueOf(GameConfig.P1_KEY_RIGHT);
            keyDown    = KeyCode.valueOf(GameConfig.P1_KEY_DOWN);
            keyRotate  = KeyCode.valueOf(GameConfig.P1_KEY_ROTATE);
            keyDrop    = KeyCode.valueOf(GameConfig.P1_KEY_DROP);
            keyPause   = KeyCode.valueOf(GameConfig.P1_KEY_PAUSE);
            keyRestart = KeyCode.valueOf(GameConfig.P1_KEY_RESTART);
        } else {
            keyLeft    = KeyCode.valueOf(GameConfig.P2_KEY_LEFT);
            keyRight   = KeyCode.valueOf(GameConfig.P2_KEY_RIGHT);
            keyDown    = KeyCode.valueOf(GameConfig.P2_KEY_DOWN);
            keyRotate  = KeyCode.valueOf(GameConfig.P2_KEY_ROTATE);
            keyDrop    = KeyCode.valueOf(GameConfig.P2_KEY_DROP);
            keyPause   = KeyCode.valueOf(GameConfig.P2_KEY_PAUSE);
            keyRestart = KeyCode.valueOf(GameConfig.P2_KEY_RESTART);
        }
    }

    @Override
    public void initialize(GameMediator mediator, GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void registerInputEvents() {
        if (actionsRegistered) {
            return;
        }
        setupMoveLeftAction();
        setupMoveRightAction();
        setupSoftDropAction();
        setupRotateAction();
        setupHardDropAction();
        setupPauseAction();
        setupRestartAction();
        actionsRegistered = true;
    }

    @Override
    public void unregisterInputEvents() {
        inputEnabled = false;
    }

    @Override
    public void handleMoveLeft() {
        if (isGameNotPlayable()) return;
        mediator.emit(GameplayEvents.MOVE_LEFT, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handleMoveRight() {
        if (isGameNotPlayable()) return;
        mediator.emit(GameplayEvents.MOVE_RIGHT, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handleMoveDown() {
        if (isGameNotPlayable()) return;
        mediator.emit(GameplayEvents.MOVE_DOWN, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handleRotate() {
        if (isGameNotPlayable()) return;
        mediator.emit(GameplayEvents.ROTATE, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handleHardDrop() {
        if (isGameNotPlayable()) return;
        mediator.emit(GameplayEvents.DROP, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handleHold() {
        if (isGameNotPlayable()) return;
        // Evento HOLD removido pois não existe em GameplayEvents
        // mediator.emit(GameplayEvents.HOLD, new GameplayEvents.MoveEvent(playerId));
    }

    @Override
    public void handlePause() {
        mediator.emit(GameplayEvents.PAUSE, null);
    }

    @Override
    public void handleRestart() {
        mediator.emit(GameplayEvents.RESTART, null);
    }

    @Override
    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
    }

    @Override
    public void cleanup() {
        reset();
    }

    @Override
    public String getName() {
        return "KeyboardInputHandler";
    }

    @Override
    public boolean isInputEnabled() {
        return inputEnabled;
    }

    private boolean isGameNotPlayable() {
        return gameState.isPaused() || gameState.isGameOver() || !inputEnabled;
    }

    private void setupMoveLeftAction() {
        FXGL.getInput().addAction(new UserAction("Move Left P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;
                
                leftKeyPressed = true;
                lastHorizontalKeyPressed = keyLeft;
                handleMoveLeft();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "LEFT"));
                lastMoveTime = FXGL.getGameTimer().getNow();
                isFirstMove = false;
            }

            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;
                if (lastHorizontalKeyPressed != keyLeft) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? GameConfig.MOVE_INITIAL_DELAY / 1000.0 : GameConfig.MOVE_REPEAT_DELAY / 1000.0;

                if (now - lastMoveTime >= delay) {
                    handleMoveLeft();
                    lastMoveTime = now;
                }
            }

            @Override
            protected void onActionEnd() {
                leftKeyPressed = false;
                isFirstMove = true;
                mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
                if (!rightKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = keyRight;
                }
            }
        }, keyLeft);
    }

    private void setupMoveRightAction() {
        FXGL.getInput().addAction(new UserAction("Move Right P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;
                
                rightKeyPressed = true;
                lastHorizontalKeyPressed = keyRight;
                handleMoveRight();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "RIGHT"));
                lastMoveTime = FXGL.getGameTimer().getNow();
                isFirstMove = false;
            }

            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;
                if (lastHorizontalKeyPressed != keyRight) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? GameConfig.MOVE_INITIAL_DELAY / 1000.0 : GameConfig.MOVE_REPEAT_DELAY / 1000.0;

                if (now - lastMoveTime >= delay) {
                    handleMoveRight();
                    lastMoveTime = now;
                }
            }

            @Override
            protected void onActionEnd() {
                rightKeyPressed = false;
                isFirstMove = true;
                mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));
                if (!leftKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = keyLeft;
                }
            }
        }, keyRight);
    }

    private void setupSoftDropAction() {
        FXGL.getInput().addAction(new UserAction("Soft Drop P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;
                handleMoveDown();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "DOWN"));
                lastMoveTime = FXGL.getGameTimer().getNow();
                isFirstMove = false;
            }

            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? GameConfig.SOFT_DROP_INITIAL_DELAY / 1000.0 : GameConfig.SOFT_DROP_DELAY / 1000.0;

                if (now - lastMoveTime >= delay) {
                    handleMoveDown();
                    lastMoveTime = now;
                }
            }

            @Override
            protected void onActionEnd() {
                isFirstMove = true;
            }
        }, keyDown);
    }

    private void setupRotateAction() {
        FXGL.getInput().addAction(new UserAction("Rotate P"+ playerId + " " + uniqueId) {
            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;
                handleRotate();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "UP"));
            }

            @Override
            protected void onActionEnd() {
                if (isGameNotPlayable()) return;
                mediator.emit(InputEvents.ROTATE_RESET, new InputEvents.MoveEvent(playerId));
            }
        }, keyRotate);
    }

    private void setupHardDropAction() {
        FXGL.getInput().addAction(new UserAction("Hard Drop P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;
                handleHardDrop();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "SPACE"));
            }
        }, keyDrop);
    }

    private void setupPauseAction() {
        FXGL.getInput().addAction(new UserAction("Pause P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                handlePause();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "ESCAPE"));
            }
        }, keyPause);
    }

    private void setupRestartAction() {
        FXGL.getInput().addAction(new UserAction("Restart P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                handleRestart();
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "BACK_SPACE"));
            }
        }, keyRestart);
    }

    public void reset() {
        leftKeyPressed = false;
        rightKeyPressed = false;
        lastHorizontalKeyPressed = null;
        inputEnabled = true;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setupInputHandling() {
        registerInputEvents();
    }
} 