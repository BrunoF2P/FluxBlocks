package com.uneb.tetris.ui.components;


import com.uneb.tetris.configuration.GameConfig;
import com.uneb.tetris.ui.effects.Effects;
import javafx.scene.layout.Pane;

public class BackgroundComponent {
    private final Pane background;

    public BackgroundComponent() {
        background = new Pane();
        initializeBackground();

    }

    private void initializeBackground() {
        background.getStyleClass().add("game-bg");
        background.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        createParticles();
    }

    private void createParticles() {
        for (int i = 0; i < 8; i++) {
            Effects.createSquareParticle(background, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }
    }

    public Pane getBackground() {
        return background;
    }
}
