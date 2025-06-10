package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.game.core.GameController;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.screens.GameScreen;
import com.uneb.tetris.ui.screens.MenuScreen;
import javafx.scene.layout.StackPane;

public class UIScreenHandler {
    private final GameMediator mediator;
    private final GameScene gameScene;
    private final StackPane currentScreen = new StackPane();
    private GameScreen gameScreen;
    private GameController gameManager;
    private MenuScreen menuScreen;

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
    }


    private void startUi() {
        showMenuScreen();
    }

    private void showMenuScreen() {
        // Limpa a tela atual
        if (gameScreen != null) {
            gameScreen.destroy();
            gameScreen = null;
        }

        currentScreen.getChildren().clear();
        menuScreen = new MenuScreen(mediator);
        currentScreen.getChildren().add(menuScreen.getNode());
        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    private void showGameScreen() {
        // Limpa a tela anterior
        if (menuScreen != null) {
            menuScreen.destroy();
            menuScreen = null;
        }

        gameScene.clearUINodes();

        gameScreen = new GameScreen(mediator);
        gameScreen.initialize();

        currentScreen.getChildren().setAll(gameScreen.getNode());
        gameScene.addUINode(currentScreen);
        mediator.emit(UiEvents.GAME_STARTED, null);
    }

}