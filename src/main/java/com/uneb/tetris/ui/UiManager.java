package com.uneb.tetris.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import javafx.fxml.FXML;

public class UiManager {
    private final GameMediator mediator;
    private final GameScene gameScene;

    public UiManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        registerEvents();
    }

    private void registerEvents() {
        startUi();
        audioStart();
    }

    private void startUi() {
        mediator.receiver(GameEvents.UiEvents.START, unused -> {
            MenuScreen menuScreen = new MenuScreen(mediator);
            gameScene.addUINode(menuScreen.getNode());
        });
    }

    public void audioStart() {
        mediator.receiver(GameEvents.UiEvents.PLAY_BUTTON_CLICKED, unused -> {
            System.out.println("Botao funcionando 2");
        }, 1);
    }
}