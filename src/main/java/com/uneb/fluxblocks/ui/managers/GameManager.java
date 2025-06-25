package com.uneb.fluxblocks.ui.managers;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.core.GameController;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.components.BackgroundComponent;
import com.uneb.fluxblocks.ui.components.PlayerContainer;
import com.uneb.fluxblocks.ui.controllers.InputHandler;
import com.uneb.fluxblocks.ui.screens.GameScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia a criação e controle de jogos single player e multiplayer.
 */
public class GameManager {
    private final GameMediator mediator;
    private final InputManager inputManager;
    private final ScreenManager screenManager;
    private final BackgroundComponent backgroundComponent;
    
    private final List<GameController> gameControllers = new ArrayList<>();
    
    // Referências aos GameScreens para manipular overlays
    private GameScreen screenP1 = null;
    private GameScreen screenP2 = null;

    public GameManager(GameMediator mediator, InputManager inputManager, 
                      ScreenManager screenManager, BackgroundComponent backgroundComponent) {
        this.mediator = mediator;
        this.inputManager = inputManager;
        this.screenManager = screenManager;
        this.backgroundComponent = backgroundComponent;
    }

    /**
     * Inicia um jogo single player.
     */
    public void startSinglePlayerGame() {
        cleanup();
        
        GameState gameState = new GameState();
        GameScreen gameScreen = new GameScreen(mediator, gameState, 1, backgroundComponent);
        
        InputHandler handler = inputManager.getOrCreateHandler(gameState, 1);
        GameController controller = new GameController(mediator, gameScreen.getGameBoardScreen(), 1, gameState, handler);

        gameScreen.initialize();
        gameControllers.add(controller);

        PlayerContainer playerContainer = new PlayerContainer("Jogador", gameScreen, false, 0.9);

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playerContainer.getContainer());

        screenManager.clearScreen();
        screenManager.addUINode(gameContainer);
    }

    /**
     * Inicia um jogo multiplayer local.
     */
    public void startLocalMultiplayerGame() {
        cleanup();
        
        GameState gameState1 = new GameState();
        GameState gameState2 = new GameState();

        screenP1 = new GameScreen(mediator, gameState1, 1, backgroundComponent);
        screenP2 = new GameScreen(mediator, gameState2, 2, backgroundComponent);

        InputHandler handler1 = inputManager.getOrCreateHandler(gameState1, 1);
        InputHandler handler2 = inputManager.getOrCreateHandler(gameState2, 2);

        GameController controller1 = new GameController(mediator, screenP1.getGameBoardScreen(), 1, gameState1, handler1);
        GameController controller2 = new GameController(mediator, screenP2.getGameBoardScreen(), 2, gameState2, handler2);

        screenP1.initialize();
        screenP2.initialize();

        gameControllers.add(controller1);
        gameControllers.add(controller2);

        PlayerContainer player1Container = new PlayerContainer("Jogador 1", screenP1, true, 0.7);
        PlayerContainer player2Container = new PlayerContainer("Jogador 2", screenP2, true, 0.7);

        HBox playersContainer = createPlayersContainer(player1Container, player2Container);

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playersContainer);

        screenManager.clearScreen();
        screenManager.addUINode(gameContainer);
    }

    /**
     * Mostra overlay de espera para um jogador específico.
     */
    public void showWaitingOverlay(int playerId, String message) {
        if (playerId == 1 && screenP1 != null) {
            screenP1.showWaitingOverlay(message);
        } else if (playerId == 2 && screenP2 != null) {
            screenP2.showWaitingOverlay(message);
        }
    }

    /**
     * Limpa overlay de espera para um jogador específico.
     */
    public void clearWaitingOverlay(int playerId) {
        if (playerId == 1 && screenP1 != null) {
            screenP1.showWaitingOverlay(null);
        } else if (playerId == 2 && screenP2 != null) {
            screenP2.showWaitingOverlay(null);
        }
    }

    /**
     * Limpa todos os overlays de espera.
     */
    public void clearAllWaitingOverlays() {
        if (screenP1 != null) screenP1.showWaitingOverlay(null);
        if (screenP2 != null) screenP2.showWaitingOverlay(null);
        screenP1 = null;
        screenP2 = null;
    }

    /**
     * Obtém o número total de jogadores ativos.
     */
    public int getTotalPlayers() {
        return gameControllers.size();
    }

    /**
     * Limpa todos os game controllers e recursos.
     */
    public void cleanup() {
        for (GameController controller : gameControllers) {
            if (controller != null) {
                controller.cleanup();
            }
        }
        gameControllers.clear();

        inputManager.cleanup();

        clearAllWaitingOverlays();
    }

    private HBox createPlayersContainer(PlayerContainer player1Container, PlayerContainer player2Container) {
        HBox container = new HBox(300);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        container.getChildren().addAll(player1Container.getContainer(), player2Container.getContainer());
        return container;
    }
} 