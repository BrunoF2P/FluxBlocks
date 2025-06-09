package com.uneb.tetris.ui.effects;

import javafx.animation.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ParticleEffects {
    private static final Duration FIREFLY_DURATION = Duration.seconds(12);
    private static final double FIREFLY_SIZE = 8;
    private static final double SQUARE_SIZE = 60;
    private static final double SQUARE_STROKE_WIDTH = 2;

    /**
     * Cria e anima uma partícula flutuante estilo vagalume.
     */
    public static void createFireflyParticle(Pane container, double width, double height) {
        if (EffectObjectPool.canCreateParticle()) return;

        Circle particle = EffectObjectPool.getParticle();
        particle.setRadius(FIREFLY_SIZE);
        particle.setFill(Color.web("#fcd34d", 0.2));
        particle.setBlendMode(BlendMode.ADD);
        particle.getProperties().put("particle-type", "firefly");

        double startX = Math.random() * width;
        double startY = Math.random() * height;
        particle.setTranslateX(startX);
        particle.setTranslateY(startY);

        TranslateTransition move = new TranslateTransition(FIREFLY_DURATION, particle);
        move.setByX((Math.random() - 0.5) * width * 0.7);
        move.setByY((Math.random() - 0.5) * height * 0.7);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(3), particle);
        fade.setFromValue(0);
        fade.setToValue(0.4);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);

        move.setOnFinished(e -> {
            container.getChildren().remove(particle);
            EffectObjectPool.returnParticle(particle);
        });

        move.play();
        fade.play();

        container.getChildren().add(particle);
    }

    /**
     * Cria e anima um quadrado flutuante decorativo.
     */
    public static void createSquareParticle(Pane container, double width, double height) {
        if (!EffectObjectPool.canCreateTrail()) return;

        Rectangle square = EffectObjectPool.getTrail();
        square.setWidth(SQUARE_SIZE);
        square.setHeight(SQUARE_SIZE);
        square.setFill(Color.TRANSPARENT);
        square.setStroke(Color.web("#fcd34d", 0.3));
        square.setStrokeWidth(SQUARE_STROKE_WIDTH);
        square.setBlendMode(BlendMode.ADD);
        square.setArcHeight(4);
        square.setArcWidth(4);
        square.getProperties().put("particle-type", "square");

        double startX = Math.random() * (width - SQUARE_SIZE);
        double startY = Math.random() * (height - SQUARE_SIZE);
        square.setTranslateX(startX);
        square.setTranslateY(startY);

        TranslateTransition move = new TranslateTransition(Duration.seconds(15), square);
        move.setByX((Math.random() - 0.5) * width * 0.6);
        move.setByY((Math.random() - 0.5) * height * 0.5);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);

        RotateTransition rotate = new RotateTransition(Duration.seconds(20), square);
        rotate.setByAngle((Math.random() - 0.5) * 180);
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(8), square);
        fade.setFromValue(0);
        fade.setToValue(0.4);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);

        move.setOnFinished(e -> {
            container.getChildren().remove(square);
            EffectObjectPool.returnTrail(square);
        });

        move.play();
        rotate.play();
        fade.play();

        container.getChildren().add(square);
    }
}
