package com.uneb.fluxblocks;

import java.util.List;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.core.GameInitializer;

public class Main extends GameApplication {
    private GameMediator mediator;

    @Override
    protected void initSettings(GameSettings settings) {
        GameConfig.loadConfig();
        
        settings.setTitle("FluxBlocks");
        settings.setVersion("1.0");

        settings.setWidth((int)GameConfig.SCREEN_WIDTH);
        settings.setHeight((int)GameConfig.SCREEN_HEIGHT);

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(GameConfig.FULLSCREEN);
        settings.setManualResizeEnabled(false);
        settings.setPreserveResizeRatio(true);
        settings.setScaleAffectedOnResize(true);
        settings.setGameMenuEnabled(false);

        settings.setCSSList(List.of("style.css"));
        settings.setFontGame("thatsoundsgreat.ttf");
        settings.setAppIcon("ui/icons/mipmap-mdpi/ic_game.png");

        settings.setTicksPerSecond(60);

        settings.setEntityPreloadEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        mediator = GameInitializer.create(FXGL.getGameScene());
    }

    public static void main(String[] args) {

        launch(args);
    }
}