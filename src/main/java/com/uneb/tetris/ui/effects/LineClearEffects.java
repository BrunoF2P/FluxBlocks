package com.uneb.tetris.ui.effects;

import javafx.animation.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LineClearEffects {
    private static final int PARTICLES_PER_LINE = 15;
    private static final Duration DISSOLVE_DURATION = Duration.millis(800);
    private static final Duration FLASH_DURATION = Duration.millis(150);
    private static final double LINE_PARTICLE_MAX_SIZE = 12;
    private static final double LINE_PARTICLE_MIN_SIZE = 4;
    private static final double SHAKE_INTENSITY_BASE = 3.0;
    private static final double SHAKE_INTENSITY_MULTIPLIER = 1.5;

    public static void applyLineClearEffect(Pane boardPane, double startY, double lineHeight) {
        double startX = 0;
        double lineWidth = boardPane.getWidth();
        double actualY = startY * lineHeight;

        // Configura o retângulo de flash
        Rectangle flash = new Rectangle(lineWidth, lineHeight);
        flash.setX(startX);
        flash.setY(actualY);
        flash.setFill(Color.web("#FFFFFF", 0.3));
        flash.setBlendMode(BlendMode.ADD);
        boardPane.getChildren().add(flash);

        // Screen shake
        StackPane boardRoot = (StackPane) boardPane.getParent();
        if (boardRoot != null) {
            applyScreenShake(boardRoot, 1);
        }

        // Efeito de brilho residual
        Rectangle glow = new Rectangle(lineWidth, lineHeight);
        glow.setX(startX);
        glow.setY(actualY);
        glow.setFill(Color.web("#ADD8E6", 0.2));
        glow.setBlendMode(BlendMode.ADD);
        boardPane.getChildren().add(glow);

        // Animações
        FadeTransition flashFade = new FadeTransition(FLASH_DURATION, flash);
        flashFade.setFromValue(0.3);
        flashFade.setToValue(0);
        flashFade.setOnFinished(e -> boardPane.getChildren().remove(flash));

        FadeTransition glowFade = new FadeTransition(DISSOLVE_DURATION, glow);
        glowFade.setFromValue(0.2);
        glowFade.setToValue(0);
        glowFade.setOnFinished(e -> boardPane.getChildren().remove(glow));

        for (int i = 0; i < PARTICLES_PER_LINE; i++) {
            createDissolvingParticle(boardPane, startX, actualY, lineWidth, lineHeight);
        }

        flashFade.play();
        glowFade.play();
    }

    private static void createDissolvingParticle(Pane boardPane, double startX, double startY,
                                               double lineWidth, double lineHeight) {
        if (EffectObjectPool.canCreateParticle()) return;

        double size = LINE_PARTICLE_MIN_SIZE + Math.random() * (LINE_PARTICLE_MAX_SIZE - LINE_PARTICLE_MIN_SIZE);
        Circle particle = EffectObjectPool.getParticle();

        particle.setRadius(size);
        double particleX = startX + Math.random() * lineWidth;
        double particleY = startY + Math.random() * lineHeight;
        particle.setTranslateX(particleX);
        particle.setTranslateY(particleY);

        particle.setFill(Color.web("#E6F3FF", 0.6 + Math.random() * 0.3));
        particle.setBlendMode(BlendMode.ADD);
        particle.setEffect(EffectObjectPool.getBlurEffect(Color.web("#E6F3FF")));
        particle.setUserData("line-clear-effect");

        boardPane.getChildren().add(particle);

        ParallelTransition pt = new ParallelTransition();

        double direction = Math.random() > 0.5 ? 1 : -1;
        double distance = 80 + Math.random() * 120;

        TranslateTransition move = new TranslateTransition(DISSOLVE_DURATION, particle);
        move.setByX(direction * distance);
        move.setByY((Math.random() - 0.5) * 20);
        move.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scale = new ScaleTransition(DISSOLVE_DURATION, particle);
        scale.setToX(0.1);
        scale.setToY(0.1);
        scale.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fade = new FadeTransition(DISSOLVE_DURATION, particle);
        fade.setFromValue(0.9);
        fade.setToValue(0);
        fade.setInterpolator(Interpolator.EASE_IN);

        pt.getChildren().addAll(move, scale, fade);
        pt.setOnFinished(event -> {
            boardPane.getChildren().remove(particle);
            EffectObjectPool.returnParticle(particle);
        });
        pt.play();
    }

    private static void applyScreenShake(StackPane boardRoot, int lineCount) {
        double intensity = SHAKE_INTENSITY_BASE + (lineCount - 1) * SHAKE_INTENSITY_MULTIPLIER;
        Duration shakeDuration = Duration.millis(100);

        TranslateTransition shake = new TranslateTransition(shakeDuration, boardRoot);
        shake.setByY(intensity);
        shake.setCycleCount(2);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> boardRoot.setTranslateY(0));
        shake.play();
    }
}
