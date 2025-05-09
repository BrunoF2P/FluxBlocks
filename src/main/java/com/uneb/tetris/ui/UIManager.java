package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.screens.MenuScreen;

public class UIManager {
    private final GameMediator mediator;
    private final GameScene gameScene;

    public UIManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        registerEvents();
    }

    private void registerEvents() {
        startUi();
    }

    private void startUi() {
        mediator.receiver(GameEvents.UiEvents.START, unused -> {
            MenuScreen menuScreen = new MenuScreen(mediator);
            gameScene.addUINode(menuScreen.getNode());
        });
    }
}