package com.uneb.fluxblocks.game.core;

import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameState;

import javafx.animation.AnimationTimer;

/**
 * Controlador de tempo do jogo, responsável pelo loop principal e cronômetro.
 *
 * <p>Esta classe gerencia dois aspectos temporais do jogo:</p>
 * <ul>
 *   <li>Loop principal: Controla a queda automática das peças</li>
 *   <li>Cronômetro: Mantém o registro do tempo de jogo</li>
 * </ul>
 */
public class GameTimer extends AnimationTimer {
    /** Mediador para comunicação com outros componentes */
    private final GameMediator mediator;

    /** Estado atual do jogo */
    private final GameState gameState;

    private long gameStartTime = 0;
    private long lastUpdate = 0;
    private long lastGameLoop = 0;
    private long lastClockUpdate = 0;

    private long gameSpeed = (long)(GameConfig.INITIAL_GAME_SPEED * 1_000_000);
    private static final long CLOCK_INTERVAL = (long)(GameConfig.GAME_TICK_INTERVAL * 1_000_000);

    private final int playerId;

    /**
     * Cria um novo controlador de tempo do jogo.
     *
     * @param mediator O mediador para comunicação entre componentes
     * @param gameState O estado atual do jogo
     */
    public GameTimer(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;
        subscribeToEvents();
    }

    /**
     * Registra os eventos necessários para o funcionamento do timer.
     */
    private void subscribeToEvents() {
        mediator.receiver(GameplayEvents.UPDATE_SPEED, this::onSpeedUpdate);
        mediator.receiver(GameplayEvents.RESTART, unused -> restartGame());
        mediator.receiver(UiEvents.LEVEL_UPDATE, ev -> {
            if (ev.playerId() == playerId) {
                updateGameSpeed();
            }
        });
    }

    private void updateGameSpeed() {
        double speed = gameState.calculateCurrentSpeed();
        gameSpeed = (long)(speed * 1_000_000);

    }

    @Override
    public void handle(long now) {
        if (lastUpdate == 0) {
            lastUpdate = now;
            lastGameLoop = now;
            lastClockUpdate = now;
            gameStartTime = now;
            return;
        }

        // Sempre atualiza o tempo do jogo, mesmo quando pausado
        if (!gameState.isGameOver()) {
            if (now - lastClockUpdate >= CLOCK_INTERVAL) {
                updateGameTime(now);
                onClockTick(now);
                lastClockUpdate = now;
            }
        }

        if (!gameState.isPaused() && !gameState.isGameOver()) {
            if (now - lastGameLoop >= gameSpeed) {
                onGameLoopTick();
                lastGameLoop = now;
            }
        }

        lastUpdate = now;
    }

    private void updateGameTime(long currentTimeNanos) {
        if (gameStartTime == 0) return;

        long deltaNanos = currentTimeNanos - lastClockUpdate;
        long deltaMs = deltaNanos / 1_000_000;

        long currentTimeMs = gameState.getGameTimeMs() + deltaMs;
        gameState.setGameTimeMs(currentTimeMs);
    }

    private void onClockTick(long currentTimeNanos) {
        mediator.emit(UiEvents.TIME_UPDATE, gameState.getGameTime());
    }



    /**
     * Inicia o timer do jogo.
     * @throws IllegalStateException se o timer já estiver iniciado
     */
    public void startTimer() {
        validateStart();
        start();
    }

    /**
     * Para completamente o timer do jogo.
     */
    public void stopTimer() {
        stop();
        gameStartTime = 0;

    }

    /**
     * Gerencia o estado de pausa do timer.
     * O timer continua rodando para manter o tempo, mas não executa o loop do jogo.
     *
     * @param isPaused true para pausar, false para resumir
     */
    public void handlePauseState(boolean isPaused) {

    }

    /**
     * Atualiza a velocidade do loop do jogo.
     *
     * @param ev Nova velocidade em milissegundos
     */
    private void onSpeedUpdate(GameplayEvents.UpdateSpeedEvent ev) {
        if (ev.playerId() != this.playerId) return;
        this.gameSpeed = (long)(ev.newSpeed() * 1_000_000);
    }

    /**
     * Processa um tick do loop principal do jogo.
     */
    private void onGameLoopTick() {
        mediator.emit(GameplayEvents.AUTO_MOVE_DOWN, new GameplayEvents.MoveEvent(playerId));
    }


    /**
     * Reinicia o jogo emitindo o evento apropriado.
     */
    private void restartGame() {
        gameStartTime = 0;
        lastUpdate = 0;
        lastGameLoop = 0;
        lastClockUpdate = 0;

        mediator.emit(GameplayEvents.RESTART_GAME, null);
    }

    /**
     * Valida se o timer pode ser iniciado.
     * @throws IllegalStateException se o timer já estiver iniciado
     */
    private void validateStart() {
        if (gameStartTime != 0) {
            throw new IllegalStateException("Timer já iniciado");
        }
    }
}