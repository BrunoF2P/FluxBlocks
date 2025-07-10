package com.uneb.fluxblocks.game.scoring;

import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.effects.FloatingTextEffect;
import javafx.application.Platform;

/**
 * Gerencia eventos relacionados à pontuação e níveis.
 * Delega o armazenamento do estado ao GameState.
 */
public class ScoreTracker {
    private final GameMediator mediator;
    private final GameState gameState;
    private final int playerId;

    public ScoreTracker(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;
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

    private void handleScoreUpdate(GameplayEvents.ScoreEvent ev) {
        if (ev.playerId() != this.playerId) return;

        Platform.runLater(() ->
            mediator.emit(UiEvents.SCORE_UPDATE, new UiEvents.ScoreUiEvent(playerId, gameState.getScore()))
        );
    }

    private void handleLinesCleared(GameplayEvents.LineClearEvent ev) {
        if (ev.playerId() != this.playerId) return;
        Platform.runLater(() -> mediator.emit(UiEvents.SCORE_UPDATE,
            new UiEvents.ScoreUiEvent(playerId, gameState.getScore())));
    }

    private void handleLevelUp() {
        int newLevel = gameState.getCurrentLevel();
        double newSpeed = gameState.calculateCurrentSpeed();

        Platform.runLater(() -> {
            FloatingTextEffect.updateLevel(newLevel);
            mediator.emit(
                    UiEvents.LEVEL_UPDATE,
                    new UiEvents.LevelUiEvent(playerId, newLevel)
            );
        });
        mediator.emit(
                GameplayEvents.UPDATE_SPEED,
                new GameplayEvents.UpdateSpeedEvent(playerId, newSpeed)
        );
    }

    /**
     * Retorna a pontuação atual do jogo.
     */
    public int getScore() {
        return gameState.getScore();
    }

    /**
     * Limpa os recursos do ScoreTracker, removendo os listeners do mediator.
     */
    public void cleanup() {
        mediator.removeReceiver(GameplayEvents.SCORE_UPDATED, this::handleScoreUpdate);
        mediator.removeReceiver(GameplayEvents.LINE_CLEARED, this::handleLinesCleared);
    }

    private void updateUI() {
        Platform.runLater(() -> {
            FloatingTextEffect.updateLevel(gameState.getCurrentLevel());
            mediator.emit(
                    UiEvents.SCORE_UPDATE,
                    new UiEvents.ScoreUiEvent(playerId, gameState.getScore())
            );
            mediator.emit(
                    UiEvents.LEVEL_UPDATE,
                    new UiEvents.LevelUiEvent(playerId, gameState.getCurrentLevel())
            );
        });
    }
}