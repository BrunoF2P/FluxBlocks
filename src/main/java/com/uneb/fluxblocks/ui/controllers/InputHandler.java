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
 * Gerencia toda a entrada do usuário para o jogo FluxBlocks.
 * <p>
 * Esta classe lida com entradas de teclado para ações do jogo como mover peças,
 * rotação, queda, pausa e reinício do jogo. Aplica controles de tempo apropriados
 * para ações contínuas e garante que os eventos de entrada sejam gerenciados
 * adequadamente com base no estado atual do jogo.
 */
public class InputHandler {
    /** O mediador do jogo para transmitir eventos */
    private final GameMediator mediator;

    /** O estado atual do jogo */
    private GameState gameState;

    /** Flag indicando se a tecla de movimento esquerdo está atualmente pressionada */
    private boolean leftKeyPressed = false;

    /** Flag indicando se a tecla de movimento direito está atualmente pressionada */
    private boolean rightKeyPressed = false;

    /** Acompanha a última tecla de direção horizontal pressionada para lidar com entradas sobrepostas */
    private KeyCode lastHorizontalKeyPressed = null;
    private final int playerId;

    private final KeyCode keyLeft;
    private final KeyCode keyRight;
    private final KeyCode keyDown;
    private final KeyCode keyRotate;
    private final KeyCode keyDrop;
    private final KeyCode keyPause;
    private final KeyCode keyRestart;

    private boolean actionsRegistered = false;

    private final String uniqueId = UUID.randomUUID().toString();

    private boolean inputEnabled = true;

    /**
     * Cria um novo InputHandler com o mediador e estado do jogo especificados.
     *
     * @param mediator O mediador central para comunicação entre componentes
     * @param gameState O estado atual do jogo
     * @param playerId O ID do jogador (1 ou 2)
     */
    public InputHandler(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;

        if (playerId == 1) {
            keyLeft    = KeyCode.A;
            keyRight   = KeyCode.D;
            keyDown    = KeyCode.S;
            keyRotate  = KeyCode.W;
            keyDrop    = KeyCode.SPACE;
            keyPause   = KeyCode.ESCAPE;
            keyRestart = KeyCode.R;

        } else {
            keyLeft    = KeyCode.LEFT;
            keyRight   = KeyCode.RIGHT;
            keyDown    = KeyCode.DOWN;
            keyRotate  = KeyCode.UP;
            keyDrop    = KeyCode.ENTER;
            keyPause   = KeyCode.P;
            keyRestart = KeyCode.BACK_SPACE;
        }
    }

    /**
     * Configura todos os manipuladores de entrada para o jogo.
     * Deve ser chamado uma vez durante a inicialização do jogo.
     */
    public void setupInputHandling() {
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

    /**
     * Verifica se o jogo não está em um estado jogável.
     *
     * @return true se o jogo estiver pausado ou terminado, false caso contrário
     */
    private boolean isGameNotPlayable() {
        return gameState.isPaused() || gameState.isGameOver() || !inputEnabled;
    }

    /**
     * Configura a ação de movimento para a esquerda.
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupMoveLeftAction() {
        FXGL.getInput().addAction(new UserAction("Move Left P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                leftKeyPressed = true;
                lastHorizontalKeyPressed = keyLeft;
                mediator.emit(GameplayEvents.MOVE_LEFT, new GameplayEvents.MoveEvent(playerId));
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
                    mediator.emit(GameplayEvents.MOVE_LEFT, new GameplayEvents.MoveEvent(playerId));
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

    /**
     * Configura a ação de movimento para a direita.
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupMoveRightAction() {
        FXGL.getInput().addAction(new UserAction("Move Right P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                rightKeyPressed = true;
                lastHorizontalKeyPressed = keyRight;
                mediator.emit(GameplayEvents.MOVE_RIGHT, new GameplayEvents.MoveEvent(playerId));
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
                    mediator.emit(GameplayEvents.MOVE_RIGHT, new GameplayEvents.MoveEvent(playerId));
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

    /**
     * Configura a ação de queda suave (movimento acelerado para baixo).
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupSoftDropAction() {
        FXGL.getInput().addAction(new UserAction("Soft Drop P"+ playerId + " " + uniqueId) {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;

                mediator.emit(GameplayEvents.MOVE_DOWN, new GameplayEvents.MoveEvent(playerId));
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
                    mediator.emit(GameplayEvents.MOVE_DOWN, new GameplayEvents.MoveEvent(playerId));
                    lastMoveTime = now;
                }
            }

            @Override
            protected void onActionEnd() {
                isFirstMove = true;
            }
        }, keyDown);
    }

    /**
     * Configura a ação de rotação para o tetrominó atual.
     */
    private void setupRotateAction() {
        FXGL.getInput().addAction(new UserAction("Rotate P"+ playerId + " " + uniqueId) {
            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;
                mediator.emit(GameplayEvents.ROTATE, new GameplayEvents.MoveEvent(playerId));
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "UP"));
            }

            @Override
            protected void onActionEnd() {
                if (isGameNotPlayable()) return;
                mediator.emit(InputEvents.ROTATE_RESET, new InputEvents.MoveEvent(playerId));
            }
        }, keyRotate);
    }

    /**
     * Configura a ação de queda rápida (queda instantânea).
     */
    private void setupHardDropAction() {
        FXGL.getInput().addAction(new UserAction("Hard Drop P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;

                mediator.emit(GameplayEvents.DROP, new GameplayEvents.MoveEvent(playerId));
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "SPACE"));
            }
        }, keyDrop);
    }

    /**
     * Configura a ação de pausa do jogo.
     */
    private void setupPauseAction() {
        FXGL.getInput().addAction(new UserAction("Pause P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                mediator.emit(GameplayEvents.PAUSE, null);
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "ESCAPE"));
            }
        }, keyPause);
    }

    /**
     * Configura a ação de reinício do jogo.
     */
    private void setupRestartAction() {
        FXGL.getInput().addAction(new UserAction("Restart P"+ playerId + " " + uniqueId) {
            @Override
            protected void onActionBegin() {
                mediator.emit(GameplayEvents.RESTART, null);
                mediator.emit(InputEvents.KEY_PRESSED, new InputEvents.KeyPressEvent(playerId, "BACK_SPACE"));
            }
        }, keyRestart);
    }

    /**
     * Reseta todas as flags e estados internos do InputHandler.
     * Deve ser chamado ao iniciar ou terminar uma partida para evitar resíduos de entradas anteriores.
     */
    public void reset() {
        leftKeyPressed = false;
        rightKeyPressed = false;
        lastHorizontalKeyPressed = null;
        inputEnabled = true;
    }

    /**
     * Atualiza o GameState associado a este InputHandler.
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Limpa todos os recursos utilizados pelo InputHandler.
     * Mantém as ações registradas para reutilização.
     */
    public void cleanup() {
        reset();
    }

    /**
     * Habilita ou desabilita o input para este jogador.
     *
     * @param enabled true para habilitar, false para desabilitar
     */
    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
    }
}