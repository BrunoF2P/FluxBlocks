package com.uneb.fluxblocks.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.components.BackgroundComponent;
import com.uneb.fluxblocks.ui.managers.GameManager;
import com.uneb.fluxblocks.ui.managers.GameOverManager;
import com.uneb.fluxblocks.ui.managers.InputManager;
import com.uneb.fluxblocks.ui.managers.ScreenManager;

/**
 * Classe responsável por coordenar os diferentes managers do jogo.
 */
public class UIScreenHandler {
    private final GameMediator mediator;
    private final ScreenManager screenManager;
    private final InputManager inputManager;
    private final GameManager gameManager;
    private final GameOverManager gameOverManager;
    private final BackgroundComponent backgroundComponent;

    public UIScreenHandler(GameScene gameScene, GameMediator mediator) {
        this.mediator = mediator;
        this.backgroundComponent = new BackgroundComponent();
        
        this.screenManager = new ScreenManager(gameScene, mediator);
        this.inputManager = new InputManager(mediator);
        this.gameManager = new GameManager(mediator, inputManager, screenManager, backgroundComponent);
        this.gameOverManager = new GameOverManager(mediator, screenManager, inputManager, gameManager);

        registerEvents();
        showMenuScreen();
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.PLAY_GAME, event -> showGameModeScreen());
        mediator.receiver(UiEvents.START_SINGLE_PLAYER, event -> startSinglePlayerGame());
        mediator.receiver(UiEvents.START_LOCAL_MULTIPLAYER, event -> startLocalMultiplayerGame());
        mediator.receiver(UiEvents.OPTIONS, event -> showOptionsScreen());
        mediator.receiver(UiEvents.RANKING, event -> showRankingScreen());
        mediator.receiver(UiEvents.BACK_TO_MENU, event -> showMenuScreen());
        mediator.receiver(UiEvents.OPEN_VIDEO_CONFIG, event -> showVideoConfigScreen());
        mediator.receiver(UiEvents.GAME_OVER, event -> handleGameOver(event));
        mediator.receiver(UiEvents.GAME_OVER_MULTIPLAYER, event -> handleGameOverMultiplayer(event));
    }

    public void showMenuScreen() {
        gameOverManager.resetVictories();
        screenManager.showMenuScreen();
    }

    public void showGameModeScreen() {
        screenManager.showGameModeScreen();
    }

    public void showOptionsScreen() {
        screenManager.showOptionsScreen();
    }

    public void showVideoConfigScreen() {
        screenManager.showVideoConfigScreen();
    }

    public void showRankingScreen() {
        screenManager.showRankingScreen();
    }

    private void startSinglePlayerGame() {
        cleanup();
        gameOverManager.reset();
        gameManager.startSinglePlayerGame();
    }

    private void startLocalMultiplayerGame() {
        cleanup();
        gameOverManager.reset();
        gameManager.startLocalMultiplayerGame();
    }

    private void handleGameOver(UiEvents.GameOverEvent event) {
        gameOverManager.handleGameOver(event, gameManager.getTotalPlayers());
    }

    private void handleGameOverMultiplayer(UiEvents.GameOverMultiplayerEvent event) {
        gameOverManager.handleGameOverMultiplayer(event);
    }

    /**
     * Limpa todos os recursos e reinicializa o sistema.
     */
    private void cleanup() {
        gameManager.cleanup();
        gameOverManager.cleanup();
        
        // Limpa todos os listeners do mediator para evitar interferência entre partidas
        mediator.clearAllListeners();

        registerEvents();
    }
}