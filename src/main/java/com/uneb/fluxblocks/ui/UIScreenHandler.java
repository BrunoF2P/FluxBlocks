package com.uneb.fluxblocks.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.events.UserEvents;
import com.uneb.fluxblocks.architecture.events.UserEventTypes;
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
    private boolean modalAutomaticoHabilitado = true;

    public UIScreenHandler(GameScene gameScene, GameMediator mediator) {
        this.mediator = mediator;
        this.backgroundComponent = new BackgroundComponent();
        
        this.screenManager = new ScreenManager(gameScene, mediator);
        this.inputManager = new InputManager(mediator);
        
        GameOverManager tempGameOverManager = new GameOverManager(mediator, screenManager, inputManager, null);
        this.gameManager = new GameManager(mediator, inputManager, screenManager, backgroundComponent, tempGameOverManager);
        this.gameOverManager = new GameOverManager(mediator, screenManager, inputManager, gameManager);

        registerEvents();
        showMenuScreen();
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.START, event -> {
            modalAutomaticoHabilitado = true;
            emitirVerificacaoSessao();
        });
        mediator.receiver(UiEvents.PLAY_GAME, event -> {
            modalAutomaticoHabilitado = false;
            showGameModeScreen();
        });
        mediator.receiver(UiEvents.SHOW_GAME_MODE_SCREEN, event -> {
            modalAutomaticoHabilitado = false;
            showGameModeScreen();
        });
        mediator.receiver(UiEvents.START_SINGLE_PLAYER, event -> startSinglePlayerGame());
        mediator.receiver(UiEvents.START_LOCAL_MULTIPLAYER, event -> startLocalMultiplayerGame());
        mediator.receiver(UiEvents.OPTIONS, event -> {
            modalAutomaticoHabilitado = false;
            showOptionsScreen();
        });
        mediator.receiver(UiEvents.RANKING, event -> {
            modalAutomaticoHabilitado = false;
            showRankingScreen();
        });
        mediator.receiver(UiEvents.BACK_TO_MENU, event -> {
            modalAutomaticoHabilitado = false; // Desabilita modal automático ao voltar
            showMenuScreen();
        });
        mediator.receiver(UiEvents.OPEN_VIDEO_CONFIG, event -> {
            modalAutomaticoHabilitado = false;
            showVideoConfigScreen();
        });
        mediator.receiver(UiEvents.OPEN_CONTROL_CONFIG, event -> {
            modalAutomaticoHabilitado = false;
            showControlConfigScreen();
        });
        mediator.receiver(UiEvents.GAME_OVER, event -> handleGameOver(event));
        mediator.receiver(UiEvents.GAME_OVER_MULTIPLAYER, event -> handleGameOverMultiplayer(event));
        mediator.receiver(UiEvents.SHOW_USER_LOGIN_MODAL, event -> showUserLoginModal());
        mediator.receiver(UiEvents.HIDE_USER_LOGIN_MODAL, event -> hideUserLoginModal());
        
        mediator.receiver(UserEventTypes.CHECK_SESSION_RESPONSE, 
                         this::onCheckSessionResponse);
    }

    /**
     * Emite o evento de verificação de sessão de usuário
     */
    private void emitirVerificacaoSessao() {
        mediator.emit(UserEventTypes.CHECK_SESSION_REQUEST, new UserEvents.CheckSessionRequestEvent());
    }

    /**
     * Handler para resposta da verificação de sessão
     */
    private void onCheckSessionResponse(UserEvents.CheckSessionResponseEvent event) {
        if (!event.hasValidSession() && modalAutomaticoHabilitado) {
            showUserLoginModal();
        }
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

    public void showControlConfigScreen() {
        screenManager.showControlConfigScreen();
    }

    public void showRankingScreen() {
        screenManager.showRankingScreen();
    }

    public void showUserLoginModal() {
        screenManager.showUserLoginModal();
    }

    public void hideUserLoginModal() {
        screenManager.hideUserLoginModal();
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
        
        gameManager.registerPauseEvents();
    }
}