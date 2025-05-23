package com.uneb.tetris.ui.controllers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.InputEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameState;
import javafx.scene.input.KeyCode;

/**
 * Gerencia toda a entrada do usuário para o jogo Tetris.
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
    private final GameState gameState;

    /** Flag indicando se a tecla de movimento esquerdo está atualmente pressionada */
    private boolean leftKeyPressed = false;

    /** Flag indicando se a tecla de movimento direito está atualmente pressionada */
    private boolean rightKeyPressed = false;

    /** Acompanha a última tecla de direção horizontal pressionada para lidar com entradas sobrepostas */
    private KeyCode lastHorizontalKeyPressed = null;

    /** Atraso inicial em segundos antes do início das ações repetidas */
    private static final double INITIAL_DELAY = 0.2; // 200ms

    /** Atraso entre movimentos horizontais repetidos em segundos */
    private static final double HORIZONTAL_REPEAT_DELAY = 0.1; // 100ms

    /** Atraso entre movimentos de queda suave em segundos */
    private static final double SOFT_DROP_DELAY = 0.03; // 30ms

    /** Atraso inicial para queda suave em segundos */
    private static final double SOFT_DROP_INITIAL_DELAY = 0.05; // 50ms

    /**
     * Cria um novo InputHandler com o mediador e estado do jogo especificados.
     *
     * @param mediator O mediador do jogo para notificar sobre eventos de entrada
     * @param gameState O estado atual do jogo para verificar o tratamento de entrada válido
     */
    public InputHandler(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;
    }

    /**
     * Configura todos os manipuladores de entrada para o jogo.
     * Deve ser chamado uma vez durante a inicialização do jogo.
     */
    public void setupInputHandling() {
        setupMoveLeftAction();
        setupMoveRightAction();
        setupSoftDropAction();
        setupRotateAction();
        setupHardDropAction();
        setupPauseAction();
        setupRestartAction();
    }

    /**
     * Verifica se o jogo não está em um estado jogável.
     *
     * @return true se o jogo estiver pausado ou terminado, false caso contrário
     */
    private boolean isGameNotPlayable() {
        return gameState.isPaused() || gameState.isGameOver();
    }

    /**
     * Configura a ação de movimento para a esquerda.
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupMoveLeftAction() {
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
                if (isGameNotPlayable()) return;
                if (lastHorizontalKeyPressed != KeyCode.LEFT) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? INITIAL_DELAY : HORIZONTAL_REPEAT_DELAY;

                if (isFirstMove || (now - lastMoveTime >= delay)) {
                    mediator.emit(GameplayEvents.MOVE_LEFT, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                leftKeyPressed = false;
                isFirstMove = true;
                mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, null);
                if (!rightKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = KeyCode.RIGHT;
                }
            }
        }, KeyCode.LEFT);
    }

    /**
     * Configura a ação de movimento para a direita.
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupMoveRightAction() {
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
                if (isGameNotPlayable()) return;
                if (lastHorizontalKeyPressed != KeyCode.RIGHT) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? INITIAL_DELAY : HORIZONTAL_REPEAT_DELAY;

                if (isFirstMove || (now - lastMoveTime >= delay)) {
                    mediator.emit(GameplayEvents.MOVE_RIGHT, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                rightKeyPressed = false;
                isFirstMove = true;
                mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, null);
                if (!leftKeyPressed) {
                    lastHorizontalKeyPressed = null;
                } else {
                    lastHorizontalKeyPressed = KeyCode.LEFT;
                }
            }
        }, KeyCode.RIGHT);
    }

    /**
     * Configura a ação de queda suave (movimento acelerado para baixo).
     * Trata tanto o pressionamento inicial quanto o movimento contínuo com atrasos apropriados.
     */
    private void setupSoftDropAction() {
        FXGL.getInput().addAction(new UserAction("Soft Drop") {
            private double lastMoveTime = 0;
            private boolean isFirstMove = true;

            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;

                mediator.emit(GameplayEvents.MOVE_DOWN, null);
                lastMoveTime = FXGL.getGameTimer().getNow();
                isFirstMove = false;
            }

            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;

                double now = FXGL.getGameTimer().getNow();
                double delay = isFirstMove ? SOFT_DROP_INITIAL_DELAY : SOFT_DROP_DELAY;

                if (now - lastMoveTime >= delay) {
                    mediator.emit(GameplayEvents.MOVE_DOWN, null);
                    lastMoveTime = now;
                    isFirstMove = false;
                }
            }

            @Override
            protected void onActionEnd() {
                isFirstMove = true;
            }
        }, KeyCode.DOWN);
    }

    /**
     * Configura a ação de rotação para o tetrominó atual.
     */
    private void setupRotateAction() {
        FXGL.getInput().addAction(new UserAction("Rotate") {
            @Override
            protected void onAction() {
                if (isGameNotPlayable()) return;
                mediator.emit(GameplayEvents.ROTATE, null);
            }

            @Override
            protected void onActionEnd() {
                if (isGameNotPlayable()) return;
                mediator.emit(InputEvents.ROTATE_RESET, null);
            }
        }, KeyCode.UP);
    }

    /**
     * Configura a ação de queda rápida (queda instantânea).
     */
    private void setupHardDropAction() {
        FXGL.getInput().addAction(new UserAction("Hard Drop") {
            @Override
            protected void onActionBegin() {
                if (isGameNotPlayable()) return;

                mediator.emit(GameplayEvents.DROP, null);
            }
        }, KeyCode.SPACE);
    }

    /**
     * Configura a ação de pausa do jogo.
     */
    private void setupPauseAction() {
        FXGL.getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                mediator.emit(GameplayEvents.PAUSE, null);
            }
        }, KeyCode.P);
    }

    /**
     * Configura a ação de reinício do jogo.
     */
    private void setupRestartAction() {
        FXGL.getInput().addAction(new UserAction("Restart") {
            @Override
            protected void onActionBegin() {
                mediator.emit(GameplayEvents.RESTART, null);
            }
        }, KeyCode.R);
    }
}