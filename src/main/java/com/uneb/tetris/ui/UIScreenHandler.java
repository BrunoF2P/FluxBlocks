package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.ui.components.BackgroundComponent;
import com.uneb.tetris.game.core.GameController;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameState;
import com.uneb.tetris.ui.components.PlayerContainer;
import com.uneb.tetris.ui.effects.Effects;
import com.uneb.tetris.ui.screens.GameScreen;
import com.uneb.tetris.ui.screens.MenuScreen;
import com.uneb.tetris.ui.screens.OptionScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

public class UIScreenHandler {
    private final GameScene gameScene;
    private final GameMediator mediator;
    private final StackPane currentScreen = new StackPane();
    private BackgroundComponent backgroundComponent;

    // Screens
    private MenuScreen menuScreen;
    private OptionScreen optionScreen;
    private GameScreen screen1;
    private GameScreen screen2;

    // Controllers
    private GameController controller1;
    private GameController controller2;

    public UIScreenHandler(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;

        configurarStackPanePrincipal();
        initializeBackground();
        registerEvents();
        startUi();
    }

    private void configurarStackPanePrincipal() {
        currentScreen.setAlignment(Pos.CENTER);
        currentScreen.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
    }

    private void initializeBackground() {
        backgroundComponent = new BackgroundComponent();
        currentScreen.getChildren().clear();
        currentScreen.getChildren().add(backgroundComponent.getBackground());
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.START, unused -> showMenuScreen());

        mediator.receiver(UiEvents.PLAY_GAME, unused -> {
            showGameScreen();
        });

        mediator.receiver(UiEvents.OPTIONS, unused -> showOptionScreen());
    }

    private void startUi() {
        showMenuScreen();
    }

    private void showMenuScreen() {
        clearCurrentScreen();
        menuScreen = new MenuScreen(mediator);
        initializeBackground();
        currentScreen.getChildren().add(menuScreen.getNode());
        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    /**
     * Versão simplificada da tela de jogo com background único
     */
    private void showGameScreen() {
        clearCurrentScreen();
        gameScene.clearUINodes();

        GameState gameState1 = new GameState();
        GameState gameState2 = new GameState();

        screen1 = new GameScreen(mediator, gameState1, 1);
        screen2 = new GameScreen(mediator, gameState2, 2);

        controller1 = new GameController(mediator, screen1.getGameBoardScreen(), 1, gameState1);
        controller2 = new GameController(mediator, screen2.getGameBoardScreen(), 2, gameState2);

        screen1.initialize();
        screen2.initialize();

        controller1.restart();
        controller2.restart();

        initializeBackground();
        HBox playersContainer = createPlayersContainer();
        currentScreen.getChildren().add(playersContainer);

        gameScene.addUINode(currentScreen);
        mediator.emit(UiEvents.GAME_STARTED, null);
    }

    /**
     * Cria o container principal que contém as telas dos jogadores
     *
     * @return HBox contendo os containers dos jogadores
     */
    private HBox createPlayersContainer() {
        HBox playersContainer = new HBox(300);
        playersContainer.setAlignment(Pos.CENTER);
        playersContainer.setPadding(new Insets(20));

        PlayerContainer player1Container = new PlayerContainer("Jogador 1", screen1);
        PlayerContainer player2Container = new PlayerContainer("Jogador 2", screen2);

        playersContainer.getChildren().addAll(player1Container.getNode(), player2Container.getNode());
        return playersContainer;
    }

    private void showOptionScreen() {
        clearCurrentScreen();
        optionScreen = new OptionScreen(mediator);

        initializeBackground();
        currentScreen.getChildren().add(optionScreen.getNode());

        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    private void clearCurrentScreen() {
        if (screen1 != null) {
            screen1.destroy();
            screen1 = null;
        }

        if (screen2 != null) {
            screen2.destroy();
            screen2 = null;
        }

        if (menuScreen != null) {
            menuScreen.destroy();
            menuScreen = null;
        }

        if (optionScreen != null) {
            optionScreen.destroy();
            optionScreen = null;
        }

        currentScreen.getChildren().clear();
    }

    /**
     * Limpa todos os efeitos do background quando necessário
     */
    public void destroy() {
        clearCurrentScreen();
        if (backgroundComponent != null) {
            Effects.clearAllEffects(backgroundComponent.getBackground());
        }
    }
}