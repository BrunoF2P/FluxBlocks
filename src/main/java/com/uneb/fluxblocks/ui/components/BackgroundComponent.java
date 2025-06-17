package com.uneb.fluxblocks.ui.components;

import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.effects.Effects;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

public class BackgroundComponent {
    private final Pane background;
    private Timeline particleTimeline;

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
        for (int i = 0; i < 6; i++) {
            Effects.createSquareParticle(background, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }
        for (int i = 0; i < 12; i++) {
            Effects.createFireflyParticle(background, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }
    }

    public void destroy() {
        if (particleTimeline != null) {
            particleTimeline.stop();
            particleTimeline = null;
        }
        if (background != null) {
            Effects.clearAllEffects(background);
        }
    }

    public Pane getBackground() {
        return background;
    }
}

