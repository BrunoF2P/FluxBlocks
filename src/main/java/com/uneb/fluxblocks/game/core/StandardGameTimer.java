package com.uneb.fluxblocks.game.core;

import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.interfaces.GameTimer;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameState;
import javafx.animation.AnimationTimer;

/**
 * Implementação padrão do GameTimer usando AnimationTimer.
 * Adaptado do GameTimer original.
 */
public class StandardGameTimer implements GameTimer {
    
    private final GameMediator mediator;
    private final GameState gameState;
    private final int playerId;
    private final AnimationTimer animationTimer;
    
    private long gameStartTime = 0;
    private long lastUpdate = 0;
    private long lastGameLoop = 0;
    private long lastClockUpdate = 0;
    private long startTime = 0;
    
    private long gameSpeed = (long)(GameConfig.INITIAL_GAME_SPEED * 1_000_000);
    private static final long CLOCK_INTERVAL = (long)(GameConfig.GAME_TICK_INTERVAL * 1_000_000);
    
    private boolean isRunning = false;
    private boolean isPaused = false;
    private double speed = 1.0;
    
    public StandardGameTimer(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;
        
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isRunning) return;
                
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
                
                if (!isPaused && !gameState.isGameOver()) {
                    if (now - lastGameLoop >= gameSpeed) {
                        onGameLoopTick();
                        lastGameLoop = now;
                    }
                }
                
                lastUpdate = now;
            }
        };
        
        subscribeToEvents();
    }
    
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
    
    private void updateGameTime(long currentTimeNanos) {
        if (gameStartTime == 0) return;
        
        long deltaNanos = (long)((currentTimeNanos - lastClockUpdate) * speed);
        long deltaMs = deltaNanos / 1_000_000;
        
        long currentTimeMs = gameState.getGameTimeMs() + deltaMs;
        gameState.setGameTimeMs(currentTimeMs);
    }
    
    private void onClockTick(long currentTimeNanos) {
        mediator.emit(UiEvents.TIME_UPDATE, gameState.getGameTime());
    }
    
    private void onSpeedUpdate(GameplayEvents.UpdateSpeedEvent ev) {
        if (ev.playerId() != this.playerId) return;
        this.gameSpeed = (long)(ev.newSpeed() * 1_000_000);
    }
    
    private void onGameLoopTick() {
        mediator.emit(GameplayEvents.AUTO_MOVE_DOWN, new GameplayEvents.MoveEvent(playerId));
    }
    
    private void restartGame() {
        gameStartTime = 0;
        lastUpdate = 0;
        lastGameLoop = 0;
        lastClockUpdate = 0;
        mediator.emit(GameplayEvents.RESTART_GAME, null);
    }
    
    @Override
    public void start() {
        if (!isRunning) {
            isRunning = true;
            isPaused = false;
            startTime = System.currentTimeMillis();
            gameStartTime = 0;
            lastUpdate = 0;
            lastGameLoop = 0;
            lastClockUpdate = 0;
            animationTimer.start();
        }
    }
    
    @Override
    public void stop() {
        isRunning = false;
        isPaused = false;
        animationTimer.stop();
        gameStartTime = 0;
        lastUpdate = 0;
        lastGameLoop = 0;
        lastClockUpdate = 0;
        startTime = 0;
    }
    
    @Override
    public void pause() {
        if (isRunning && !isPaused) {
            isPaused = true;
        }
    }
    
    @Override
    public void resume() {
        if (isRunning && isPaused) {
            isPaused = false;
        }
    }
    
    @Override
    public void setSpeed(double speed) {
        this.speed = Math.max(0.1, Math.min(10.0, speed));
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public boolean isPaused() {
        return isPaused;
    }
    
    @Override
    public long getElapsedTime() {
        if (startTime == 0) return 0;
        return System.currentTimeMillis() - startTime;
    }
    
    @Override
    public long getGameTime() {
        return gameState.getGameTimeMs();
    }
    
    @Override
    public void setGameTime(long timeMs) {
        gameState.setGameTimeMs(timeMs);
    }
    
    @Override
    public void reset() {
        stop();
        gameState.setGameTimeMs(0);
        startTime = 0;
        lastUpdate = 0;
        lastGameLoop = 0;
        lastClockUpdate = 0;
        gameStartTime = 0;
        speed = 1.0;
    }
    
    @Override
    public void cleanup() {
        stop();
    }
} 