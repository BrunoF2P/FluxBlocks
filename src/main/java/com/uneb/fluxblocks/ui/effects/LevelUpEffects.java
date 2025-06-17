package com.uneb.fluxblocks.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;

public class LevelUpEffects {
    public static void applyLevelUpEffect(Pane container) {
        container.getChildren().stream()
            .filter(node -> node.getProperties().containsKey("particle-type"))
            .forEach(particle -> {
                double originalOpacity = particle.getOpacity();

                ScaleTransition pulse = new ScaleTransition(EffectConstants.LEVEL_UP_CYCLE_DURATION, particle);
                pulse.setFromX(1.0);
                pulse.setFromY(1.0);
                pulse.setToX(EffectConstants.LEVEL_UP_SCALE_FACTOR);
                pulse.setToY(EffectConstants.LEVEL_UP_SCALE_FACTOR);
                pulse.setCycleCount(EffectConstants.LEVEL_UP_CYCLES * 2);
                pulse.setAutoReverse(true);

                FadeTransition glow = new FadeTransition(EffectConstants.LEVEL_UP_CYCLE_DURATION, particle);
                glow.setFromValue(originalOpacity);
                glow.setToValue(EffectConstants.LEVEL_UP_GLOW_INTENSITY);
                glow.setCycleCount(EffectConstants.LEVEL_UP_CYCLES * 2);
                glow.setAutoReverse(true);
                glow.setOnFinished(e -> particle.setOpacity(originalOpacity));

                RotateTransition spin = new RotateTransition(
                    EffectConstants.LEVEL_UP_CYCLE_DURATION.multiply(2), particle);
                spin.setByAngle(360);
                spin.setCycleCount(EffectConstants.LEVEL_UP_CYCLES);
                spin.setAutoReverse(false);

                pulse.play();
                glow.play();
                spin.play();
            });
    }
}
