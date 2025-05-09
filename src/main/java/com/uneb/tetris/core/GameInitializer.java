package com.uneb.tetris.core;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.tetris.ui.UIManager;

public final class GameInitializer {
    private GameInitializer() {}

    public static GameMediator create(GameScene gameScene) {
        GameMediator mediator = new GameMediator();

        UIManager uiManager = new UIManager(gameScene, mediator);

        mediator.emit(GameEvents.UiEvents.START, null);

        return mediator;
    }


}
