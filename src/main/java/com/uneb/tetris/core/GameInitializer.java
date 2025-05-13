package com.uneb.tetris.core;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.ui.UIManager;
import javafx.util.Duration;

public final class GameInitializer {
    private GameInitializer() {}

    public static GameMediator create(GameScene gameScene) {
        GameMediator mediator = new GameMediator();

        new UIManager(gameScene, mediator);

        FXGL.getGameTimer().runOnceAfter(() -> {
            mediator.emit(GameEvents.UiEvents.START, null);
        }, Duration.millis(100));

        return mediator;
    }
}