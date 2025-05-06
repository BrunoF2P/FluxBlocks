package com.uneb.tetris;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.core.GameInitializer;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.UiManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends GameApplication {
    private GameMediator mediator;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Tetris");
        settings.setVersion("1.0");
        settings.setScaleAffectedOnResize(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);

    }

    @Override
    protected void initGame() {
        mediator = GameInitializer.create(FXGL.getGameScene());
    }

    public static void main(String[] args) {

        launch(args);
    }
}