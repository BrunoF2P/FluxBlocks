package com.uneb.fluxblocks.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class DropTrailEffect {
    private static final double TRAIL_DURATION = 0.35;
    private static final double BLUR_RADIUS = 2.0;
    private static final double MIN_OPACITY = 0.1;
    private static final double MAX_OPACITY = 0.25;
    private static final double PULSE_SCALE = 1.05;

    public static void createTrailEffect(Pane effectsLayer, int x, int y, int width, int height,
                                       Color color, int distance) {
        double opacity = Math.min(MAX_OPACITY, Math.max(MIN_OPACITY, distance / 15.0));

        Rectangle trail = new Rectangle(width, height);
        trail.setX(x);
        trail.setY(y);

        Color enhancedColor = color.deriveColor(0, 1.1, 1.0, opacity);
        trail.setFill(enhancedColor);
        trail.setArcWidth(8);
        trail.setArcHeight(8);

        effectsLayer.getChildren().add(trail);

        GaussianBlur blur = new GaussianBlur(BLUR_RADIUS);
        trail.setEffect(blur);

        FadeTransition fade = new FadeTransition(Duration.seconds(TRAIL_DURATION), trail);
        fade.setFromValue(opacity);
        fade.setToValue(0);

        ScaleTransition scaleY = new ScaleTransition(Duration.seconds(TRAIL_DURATION), trail);
        scaleY.setFromY(1.0);
        scaleY.setToY(0.9);

        ScaleTransition scaleX = new ScaleTransition(Duration.seconds(TRAIL_DURATION), trail);
        scaleX.setFromX(PULSE_SCALE);
        scaleX.setToX(0.98);

        ParallelTransition parallel = new ParallelTransition(fade, scaleX, scaleY);
        parallel.setOnFinished(event -> effectsLayer.getChildren().remove(trail));
        parallel.play();
    }
}
