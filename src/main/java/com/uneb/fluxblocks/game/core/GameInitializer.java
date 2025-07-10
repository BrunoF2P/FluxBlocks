package com.uneb.fluxblocks.game.core;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.UIScreenHandler;
import javafx.util.Duration;

public final class GameInitializer {
    private GameInitializer() {}

    public static GameMediator create(GameScene gameScene) {
        GameMediator mediator = new GameMediator();

        new UIScreenHandler(gameScene, mediator);
        
        new GameStartListener(mediator);

        FXGL.getGameTimer().runOnceAfter(() -> {
            mediator.emit(UiEvents.START, null);
        }, Duration.millis(50));

        return mediator;
    }
}