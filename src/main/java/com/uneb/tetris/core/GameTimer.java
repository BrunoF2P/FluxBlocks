package com.uneb.tetris.core;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
public class GameTimer {
    /** Mediador para comunicação com outros componentes */
    private final GameMediator mediator;
    
    /** Estado atual do jogo */
    private final GameState gameState;

    /** Timeline para o loop principal do jogo */
    private Timeline gameLoop;
    
    /** Timeline para o cronômetro do jogo */
    private Timeline gameClock;
    
    /** Horário de início da partida */
    private LocalTime startTime;

    /**
     * Cria um novo controlador de tempo do jogo.
     *
     * @param mediator O mediador para comunicação entre componentes
     * @param gameState O estado atual do jogo
     */
    public GameTimer(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;

        initializeGameLoop();
        initializeGameClock();
        subscribeToEvents();
    }

    /**
     * Registra os eventos necessários para o funcionamento do timer.
     */
    private void subscribeToEvents() {
        mediator.receiver(GameEvents.GameplayEvents.UPDATE_SPEED, this::onSpeedUpdate);
        mediator.receiver(GameEvents.GameplayEvents.RESTART, unused -> restartGame());
    }

    /**
     * Inicializa o loop principal do jogo com a velocidade inicial.
     */
    private void initializeGameLoop() {
        gameLoop = createGameLoop(GameState.INITIAL_SPEED);
    }

    /**
     * Cria um novo loop de jogo com o intervalo especificado.
     *
     * @param intervalMs Intervalo em milissegundos entre cada tick
     * @return Timeline configurada para o loop do jogo
     */
    private Timeline createGameLoop(double intervalMs) {
        Timeline loop = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(intervalMs), e -> onGameLoopTick());
        loop.getKeyFrames().add(keyFrame);
        loop.setCycleCount(Animation.INDEFINITE);
        return loop;
    }

    /**
     * Inicializa o cronômetro do jogo.
     */
    private void initializeGameClock() {
        gameClock = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), e -> onClockTick());
        gameClock.getKeyFrames().add(keyFrame);
        gameClock.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Inicia o timer do jogo.
     * @throws IllegalStateException se o timer já estiver iniciado
     */
    public void start() {
        validateStart();
        startTime = LocalTime.now();
        gameLoop.play();
        gameClock.play();
    }

    /**
     * Para completamente o timer do jogo.
     */
    public void stop() {
        gameLoop.stop();
        gameClock.stop();
    }

    /**
     * Gerencia o estado de pausa do timer.
     *
     * @param isPaused true para pausar, false para resumir
     */
    public void handlePauseState(boolean isPaused) {
        if (isPaused) {
            pause();
            return;
        }
        resume();
    }

    /**
     * Pausa temporariamente o timer.
     */
    private void pause() {
        gameLoop.pause();
        gameClock.pause();
    }

    /**
     * Retoma o timer após uma pausa.
     */
    private void resume() {
        gameLoop.play();
        gameClock.play();
    }

    /**
     * Atualiza a velocidade do loop do jogo.
     *
     * @param newSpeedMs Nova velocidade em milissegundos
     */
    private void onSpeedUpdate(double newSpeedMs) {
        gameLoop.stop();
        gameLoop = createGameLoop(newSpeedMs);
        gameLoop.play();
    }

    /**
     * Processa um tick do loop principal do jogo.
     */
    private void onGameLoopTick() {
        if (gameState.isPaused() || gameState.isGameOver()) return;
        mediator.emit(GameEvents.GameplayEvents.AUTO_MOVE_DOWN, null);
    }

    /**
     * Processa um tick do cronômetro.
     */
    private void onClockTick() {
        if (gameState.isPaused() || gameState.isGameOver()) return;
        updateElapsedTime();
    }

    /**
     * Atualiza e emite o tempo decorrido de jogo.
     */
    private void updateElapsedTime() {
        LocalTime now = LocalTime.now();
        long secondsElapsed = ChronoUnit.SECONDS.between(startTime, now);
        String timeFormatted = formatElapsedTime(secondsElapsed);
        mediator.emit(GameEvents.UiEvents.TIME_UPDATE, timeFormatted);
    }

    /**
     * Formata o tempo decorrido em minutos e segundos.
     *
     * @param secondsElapsed Total de segundos decorridos
     * @return String formatada no padrão "MM:SS"
     */
    private String formatElapsedTime(long secondsElapsed) {
        long minutes = secondsElapsed / 60;
        long seconds = secondsElapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Reinicia o jogo emitindo o evento apropriado.
     */
    private void restartGame() {
        mediator.emit(GameEvents.GameplayEvents.RESTART_GAME, null);
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