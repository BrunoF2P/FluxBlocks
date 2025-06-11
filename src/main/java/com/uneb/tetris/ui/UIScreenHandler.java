package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.game.core.GameController;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.screens.GameScreen;
import com.uneb.tetris.ui.screens.MenuScreen;
import com.uneb.tetris.ui.screens.OptionScreen;
import javafx.scene.layout.StackPane;

public class UIScreenHandler {
    private final GameMediator mediator;
    private final GameScene gameScene;
    private final StackPane currentScreen = new StackPane();
    private GameScreen gameScreen;
    private GameController gameManager;
    private MenuScreen menuScreen;
    private OptionScreen optionScreen;

    public UIScreenHandler(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        registerEvents();
        startUi();
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.START, unused -> showMenuScreen());
        mediator.receiver(UiEvents.PLAY_GAME, unused -> {
            if (gameManager != null) {
                gameManager.restart();
            } else {
                gameManager = new GameController(mediator);
            }
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
        currentScreen.getChildren().add(menuScreen.getNode());
        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    private void showGameScreen() {
        clearCurrentScreen();

        gameScene.clearUINodes();

        if (gameManager == null) {
            gameManager = new GameController(mediator);
        }

        gameScreen = new GameScreen(mediator, gameManager.getGameState());
        gameScreen.initialize();

        currentScreen.getChildren().setAll(gameScreen.getNode());
        gameScene.addUINode(currentScreen);
        mediator.emit(UiEvents.GAME_STARTED, null);
    }

    private void showOptionScreen() {
        clearCurrentScreen();

        optionScreen = new OptionScreen(mediator);
        currentScreen.getChildren().add(optionScreen.getNode());
        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    private void clearCurrentScreen() {
        if (gameScreen != null) {
            gameScreen.destroy();
            gameScreen = null;
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
}