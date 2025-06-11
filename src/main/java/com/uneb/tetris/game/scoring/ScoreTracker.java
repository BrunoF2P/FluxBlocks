package com.uneb.tetris.game.scoring;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameState;
import com.uneb.tetris.ui.effects.FloatingTextEffect;
import javafx.application.Platform;

/**
 * Gerencia eventos relacionados à pontuação e níveis.
 * Delega o armazenamento do estado ao GameState.
 */
public class ScoreTracker {
    private final GameMediator mediator;
    private final GameState gameState;

    public ScoreTracker(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;
        registerEvents();
    }

    private void registerEvents() {
        mediator.receiver(GameplayEvents.SCORE_UPDATED, this::handleScoreUpdate);
        mediator.receiver(GameplayEvents.LINE_CLEARED, this::handleLinesCleared);
    }

    public void reset() {
        gameState.reset();
        updateUI();
    }

    private void handleScoreUpdate(int points) {
        gameState.addScore(points);
        Platform.runLater(() ->
            mediator.emit(UiEvents.SCORE_UPDATE, gameState.getScore())
        );
    }

    private void handleLinesCleared(int lines) {
        if (gameState.processLinesCleared(lines)) {
            handleLevelUp();
        }
    }

    private void handleLevelUp() {
        int newLevel = gameState.getCurrentLevel();
        double newSpeed = gameState.calculateCurrentSpeed();

        Platform.runLater(() -> {
            FloatingTextEffect.updateLevel(newLevel);
            mediator.emit(UiEvents.LEVEL_UPDATE, newLevel);
        });
        mediator.emit(GameplayEvents.UPDATE_SPEED, newSpeed);
    }

    /**
     * Retorna a pontuação atual do jogo.
     */
    public int getScore() {
        return gameState.getScore();
    }

    private void updateUI() {
        Platform.runLater(() -> {
            FloatingTextEffect.updateLevel(gameState.getCurrentLevel());
            mediator.emit(UiEvents.SCORE_UPDATE, gameState.getScore());
            mediator.emit(UiEvents.LEVEL_UPDATE, gameState.getCurrentLevel());
        });
    }
}