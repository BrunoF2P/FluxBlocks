package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameManager;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.screens.GameScreen;
import com.uneb.tetris.ui.screens.MenuScreen;
import javafx.scene.layout.StackPane;

public class UIManager {
    private final GameMediator mediator;
    private final GameScene gameScene;
    private final StackPane currentScreen = new StackPane();
    private GameScreen gameScreen;
    private GameManager gameManager;

    public UIManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        registerEvents();
        startUi();
    }

    private void registerEvents() {
        mediator.receiver(GameEvents.UiEvents.START, unused -> showMenuScreen());
        mediator.receiver(GameEvents.UiEvents.PLAY_GAME, unused -> {
            if (gameManager != null) {
                gameManager.restart();
            } else {
                gameManager = new GameManager(mediator);
            }
            showGameScreen();
        });
    }


    private void startUi() {
        showMenuScreen();
    }

    private void showMenuScreen() {
        currentScreen.getChildren().clear();
        currentScreen.getChildren().add(new MenuScreen(mediator).getNode());
        gameScene.clearUINodes();
        gameScene.addUINode(currentScreen);
    }

    private void showGameScreen() {
        gameScene.clearUINodes();

        gameScreen = new GameScreen(mediator);
        gameScreen.initialize();

        currentScreen.getChildren().setAll(gameScreen.getNode());
        gameScene.addUINode(currentScreen);
        mediator.emit(GameEvents.UiEvents.GAME_STARTED, null);
    }

}