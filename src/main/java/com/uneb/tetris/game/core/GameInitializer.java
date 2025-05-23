package com.uneb.tetris.game.core;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.UIScreenHandler;
import javafx.util.Duration;

public final class GameInitializer {
    private GameInitializer() {}

    public static GameMediator create(GameScene gameScene) {
        GameMediator mediator = new GameMediator();

        new UIScreenHandler(gameScene, mediator);

        FXGL.getGameTimer().runOnceAfter(() -> {
            mediator.emit(UiEvents.START, null);
        }, Duration.millis(100));

        return mediator;
    }
}