package com.uneb.fluxblocks.ui.effects;

import com.uneb.fluxblocks.configuration.GameConfig;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LineClearEffects {
    private static final int PARTICLES_PER_LINE = 10;
    private static final Duration DISSOLVE_DURATION = Duration.millis(600);
    private static final Duration FLASH_DURATION = Duration.millis(100);
    private static final double LINE_PARTICLE_MAX_SIZE = 12;
    private static final double LINE_PARTICLE_MIN_SIZE = 4;
    private static final double SHAKE_INTENSITY_BASE = 3.0;
    private static final double SHAKE_INTENSITY_MULTIPLIER = 1.5;


    private static void setupParticle(Circle particle, double size, double startX, double startY,
                                    double lineWidth, double lineHeight) {
        particle.setRadius(size);
        double particleX = startX + Math.random() * lineWidth;
        double particleY = startY + Math.random() * lineHeight;
        particle.setTranslateX(particleX);
        particle.setTranslateY(particleY);

        particle.setFill(Color.web("#E6F3FF", 0.6 + Math.random() * 0.3));
        particle.setBlendMode(BlendMode.ADD);
        particle.setEffect(EffectObjectPool.getBlurEffect(Color.web("#E6F3FF")));
        particle.setUserData("line-clear-effect");
        particle.getProperties().put("creation-time", System.currentTimeMillis());
    }

    public static void applyLineClearEffect(Pane boardPane, double startY, double lineHeight) {
        int maxParticles = Math.min(PARTICLES_PER_LINE, PARTICLES_PER_LINE - (int)(startY / lineHeight));

        double startX = 0;
        double lineWidth = boardPane.getWidth();
        double actualY = startY * lineHeight;

        Rectangle flash = new Rectangle(lineWidth, lineHeight);
        flash.setX(startX);
        flash.setY(actualY);
        flash.setFill(Color.web("#FFFFFF", 0.3));
        flash.setBlendMode(BlendMode.ADD);
        if (GameConfig.ENABLE_EFFECTS_CACHE) {
            flash.setCache(true);
            flash.setCacheHint(GameConfig.getCacheHint());
        }
        boardPane.getChildren().add(flash);

        StackPane boardRoot = (StackPane) boardPane.getParent();
        if (boardRoot != null) {
            applyScreenShake(boardRoot, Math.min(2, (int)(startY / lineHeight)));
        }

        Rectangle glow = new Rectangle(lineWidth, lineHeight);
        glow.setX(startX);
        glow.setY(actualY);
        glow.setFill(Color.web("#ADD8E6", 0.2));
        glow.setBlendMode(BlendMode.ADD);
        if (GameConfig.ENABLE_EFFECTS_CACHE) {
            glow.setCache(true);
            glow.setCacheHint(GameConfig.getCacheHint());
        }
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

        for (int i = 0; i < maxParticles; i++) {
            createDissolvingParticle(boardPane, startX, actualY, lineWidth, lineHeight);
        }

        flashFade.play();
        glowFade.play();
    }

    private static void createDissolvingParticle(Pane boardPane, double startX, double startY,
                                               double lineWidth, double lineHeight) {
        Platform.runLater(() -> {
            Circle particle = EffectObjectPool.getParticle();
            if (particle == null) return;

            double size = LINE_PARTICLE_MIN_SIZE + Math.random() * (LINE_PARTICLE_MAX_SIZE - LINE_PARTICLE_MIN_SIZE);
            setupParticle(particle, size, startX, startY, lineWidth, lineHeight);

            boardPane.getChildren().add(particle);

            ParallelTransition pt = createParticleAnimation(particle, startX, startY);

            particle.getProperties().put("animation", pt);

            pt.setOnFinished(event -> {
                boardPane.getChildren().remove(particle);
                EffectObjectPool.returnParticle(particle);
                particle.getProperties().remove("animation");
            });

            pt.play();
        });
    }

    private static ParallelTransition createParticleAnimation(Circle particle, double startX, double startY) {
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
        return pt;
    }

    private static void applyScreenShake(StackPane boardRoot, int lineCount) {
        double intensity = SHAKE_INTENSITY_BASE + (lineCount - 1) * SHAKE_INTENSITY_MULTIPLIER;
        Duration shakeDuration = Duration.millis(100);

        Animation currentShake = (Animation) boardRoot.getProperties().get("shake-animation");
        if (currentShake != null) {
            currentShake.stop();
        }

        TranslateTransition shake = new TranslateTransition(shakeDuration, boardRoot);
        shake.setByY(intensity);
        shake.setCycleCount(2);
        shake.setAutoReverse(true);

        boardRoot.getProperties().put("shake-animation", shake);

        shake.setOnFinished(e -> {
            boardRoot.setTranslateY(0);
            boardRoot.getProperties().remove("shake-animation");
        });

        shake.play();
    }

    public static void clearEffects(Pane boardPane) {
        if (boardPane == null) return;

        boardPane.getChildren().stream()
            .filter(node -> "line-clear-effect".equals(node.getUserData()))
            .forEach(node -> {
                Animation animation = (Animation) node.getProperties().get("animation");
                if (animation != null) {
                    animation.stop();
                }
                boardPane.getChildren().remove(node);
                if (node instanceof Circle) {
                    EffectObjectPool.returnParticle((Circle) node);
                }
            });
    }
}
