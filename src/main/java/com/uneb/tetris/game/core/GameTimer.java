package com.uneb.tetris.game.core;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameState;
import javafx.animation.AnimationTimer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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

    /** Horário de início da partida */
    private LocalTime startTime;

    /** Última atualização do AnimationTimer */
    private long lastUpdate = 0;

    /** Última vez que o game loop foi executado */
    private long lastGameLoop = 0;

    /** Última vez que o clock foi atualizado */
    private long lastClockUpdate = 0;

    /** Velocidade atual do jogo em nanosegundos */
    private long gameSpeed = (long)(GameState.INITIAL_SPEED * 1_000_000);

    /** Intervalo do clock em nanosegundos (16.67ms = ~60fps) */
    private static final long CLOCK_INTERVAL = (long)(16.67 * 1_000_000);

    /**
     * Cria um novo controlador de tempo do jogo.
     *
     * @param mediator O mediador para comunicação entre componentes
     * @param gameState O estado atual do jogo
     */
    public GameTimer(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;
        subscribeToEvents();
    }

    /**
     * Registra os eventos necessários para o funcionamento do timer.
     */
    private void subscribeToEvents() {
        mediator.receiver(GameplayEvents.UPDATE_SPEED, this::onSpeedUpdate);
        mediator.receiver(GameplayEvents.RESTART, unused -> restartGame());
    }

    @Override
    public void handle(long now) {
        if (lastUpdate == 0) {
            lastUpdate = now;
            lastGameLoop = now;
            lastClockUpdate = now;
            return;
        }

        if (!gameState.isPaused() && !gameState.isGameOver()) {
            if (now - lastGameLoop >= gameSpeed) {
                onGameLoopTick();
                lastGameLoop = now;
            }
        }

        if (!gameState.isGameOver()) {
            if (now - lastClockUpdate >= CLOCK_INTERVAL) {
                onClockTick();
                lastClockUpdate = now;
            }
        }

        lastUpdate = now;
    }

    /**
     * Inicia o timer do jogo.
     * @throws IllegalStateException se o timer já estiver iniciado
     */
    public void startTimer() {
        validateStart();
        startTime = LocalTime.now();
        start();
    }

    /**
     * Para completamente o timer do jogo.
     */
    public void stopTimer() {
        stop();
        startTime = null;
    }

    /**
     * Gerencia o estado de pausa do timer.
     * O AnimationTimer continua rodando, mas os ticks são ignorados
     * baseado no estado do jogo.
     *
     * @param isPaused true para pausar, false para resumir
     */
    public void handlePauseState(boolean isPaused) {
    }

    /**
     * Atualiza a velocidade do loop do jogo.
     *
     * @param newSpeedMs Nova velocidade em milissegundos
     */
    private void onSpeedUpdate(double newSpeedMs) {
        gameSpeed = (long)(newSpeedMs * 1_000_000);
    }

    /**
     * Processa um tick do loop principal do jogo.
     */
    private void onGameLoopTick() {
        mediator.emit(GameplayEvents.AUTO_MOVE_DOWN, null);
    }

    /**
     * Processa um tick do cronômetro.
     */
    private void onClockTick() {
        updateElapsedTime();
    }

    /**
     * Atualiza e emite o tempo decorrido de jogo.
     */
    private void updateElapsedTime() {
        if (startTime == null) return;

        LocalTime now = LocalTime.now();
        long millisElapsed = ChronoUnit.MILLIS.between(startTime, now);
        String timeFormatted = formatElapsedTime(millisElapsed);
        mediator.emit(UiEvents.TIME_UPDATE, timeFormatted);
    }

    /**
     * Formata o tempo decorrido em minutos, segundos e milliseconds.
     *
     * @param millisElapsed Total de milliseconds decorridos
     * @return String formatada no padrão "MM:SS:mmm"
     */
    private String formatElapsedTime(long millisElapsed) {
        long milliseconds = millisElapsed % 1000;
        long totalSeconds = millisElapsed / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;

        char[] result = new char[9];

        final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        result[0] = DIGITS[(int)((minutes / 10) % 10)];
        result[1] = DIGITS[(int)(minutes % 10)];
        result[2] = ':';

        result[3] = DIGITS[(int)((seconds / 10) % 10)];
        result[4] = DIGITS[(int)(seconds % 10)];
        result[5] = ':';

        result[6] = DIGITS[(int)((milliseconds / 100) % 10)];
        result[7] = DIGITS[(int)((milliseconds / 10) % 10)];
        result[8] = DIGITS[(int)(milliseconds % 10)];

        return new String(result);
    }


    /**
     * Reinicia o jogo emitindo o evento apropriado.
     */
    private void restartGame() {
        mediator.emit(GameplayEvents.RESTART_GAME, null);
    }

    /**
     * Valida se o timer pode ser iniciado.
     * @throws IllegalStateException se o timer já estiver iniciado
     */
    private void validateStart() {
        if (startTime != null) {
            throw new IllegalStateException("Timer já iniciado");
        }
    }
}