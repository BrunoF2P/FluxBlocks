package com.uneb.tetris.core;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.ui.UiManager;

public final class GameInitializer {
    private GameInitializer() {}

    public static GameMediator create(GameScene gameScene) {
        GameMediator mediator = new GameMediator();

        UiManager uiManager = new UiManager(gameScene, mediator);

        mediator.emit(GameEvents.UiEvents.START, null);

        return mediator;
    }


}
