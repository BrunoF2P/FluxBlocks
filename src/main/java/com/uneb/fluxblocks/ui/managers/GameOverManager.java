package com.uneb.fluxblocks.ui.managers;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.ui.screens.GameOverScreen;
import com.uneb.fluxblocks.ui.screens.GameOverMultiplayerScreen;

/**
 * Gerencia as telas de game over para single player e multiplayer.
 */
public class GameOverManager {
    private final GameMediator mediator;
    private final ScreenManager screenManager;
    private final InputManager inputManager;
    private final GameManager gameManager;
    
    private boolean gameOverHandled = false;
    
    // Estatísticas temporárias do multiplayer
    private GameStatistics statsP1 = null;
    private GameStatistics statsP2 = null;
    private int victoriesP1 = 0;
    private int victoriesP2 = 0;

    public GameOverManager(GameMediator mediator, ScreenManager screenManager, 
                          InputManager inputManager, GameManager gameManager) {
        this.mediator = mediator;
        this.screenManager = screenManager;
        this.inputManager = inputManager;
        this.gameManager = gameManager;
    }

    /**
     * Reseta o estado do game over manager.
     */
    public void reset() {
        gameOverHandled = false;
        statsP1 = null;
        statsP2 = null;
    }

    /**
     * Reseta as vitórias dos jogadores.
     */
    public void resetVictories() {
        victoriesP1 = 0;
        victoriesP2 = 0;
    }

    /**
     * Processa evento de game over.
     */
    public void handleGameOver(UiEvents.GameOverEvent event, int totalPlayers) {
        if (totalPlayers == 1) {
            handleSinglePlayerGameOver(event);
        } else {
            handleMultiplayerGameOver(event);
        }
    }

    /**
     * Processa evento de game over multiplayer.
     */
    public void handleGameOverMultiplayer(UiEvents.GameOverMultiplayerEvent event) {
        if (gameOverHandled) return;
        gameOverHandled = true;
        
        GameOverMultiplayerScreen screen = new GameOverMultiplayerScreen(
            mediator, event.statsP1(), event.statsP2(), event.victoriesP1(), event.victoriesP2()
        );
        screenManager.showGameOverMultiplayerScreen(screen);
    }

    private void handleSinglePlayerGameOver(UiEvents.GameOverEvent event) {
        if (gameOverHandled) return;
        gameOverHandled = true;
        
        GameOverScreen screen = new GameOverScreen(mediator, event.statistics());
        screenManager.showGameOverScreen(screen);
    }

    private void handleMultiplayerGameOver(UiEvents.GameOverEvent event) {
        if (event.playerId() == 1) {
            statsP1 = event.statistics();
            inputManager.disableInput(1);
            gameManager.showWaitingOverlay(1, "Aguardando o outro jogador...");
        } else if (event.playerId() == 2) {
            statsP2 = event.statistics();
            inputManager.disableInput(2);
            gameManager.showWaitingOverlay(2, "Aguardando o outro jogador...");
        }
        
        if (statsP1 != null && statsP2 != null) {
            processMultiplayerResults();
        }
    }

    private void processMultiplayerResults() {
        int scoreP1 = statsP1.getScore();
        int scoreP2 = statsP2.getScore();
        
        if (scoreP1 > scoreP2) {
            victoriesP1++;
        } else if (scoreP2 > scoreP1) {
            victoriesP2++;
        }
        
        mediator.emit(UiEvents.GAME_OVER_MULTIPLAYER, 
            new UiEvents.GameOverMultiplayerEvent(statsP1, statsP2, victoriesP1, victoriesP2)
        );
        
        statsP1 = null;
        statsP2 = null;
        gameManager.clearAllWaitingOverlays();
    }

    /**
     * Limpa recursos do game over manager.
     */
    public void cleanup() {
        screenManager.destroyGameOverScreens();
    }
} 